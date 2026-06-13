package com._jackoboy.otherside.block;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.dimension.DimensionRulesManager;
import com._jackoboy.otherside.dimension.LandingRuinBuilder;
import com._jackoboy.otherside.dimension.OthersideSavedData;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.portal.GuardianManager;
import com._jackoboy.otherside.portal.PortalSavedData;
import com._jackoboy.otherside.registry.ModSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The Otherside portal block — a thin, sculk-themed portal plane.
 * <p>
 * Modeled on vanilla's {@link net.minecraft.world.level.block.NetherPortalBlock},
 * this block validates its surrounding frame via the {@code otherside:portal_frame}
 * block tag and handles portal transition logic for the Otherside dimension.
 */
public class OthersidePortalBlock extends Block implements Portal {

    /** Horizontal axis property — determines whether the portal faces X or Z. */
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    /** Tag for blocks that can form a valid portal frame. */
    private static final TagKey<Block> PORTAL_FRAME_TAG =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("otherside", "portal_frame"));

    /** Thin slab shape when the portal plane runs along the X axis (narrow on Z). */
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);

    /** Thin slab shape when the portal plane runs along the Z axis (narrow on X). */
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    /**
     * Rate-limit map: tracks the last game tick each player was sent the
     * "the way is not yet open" message, to avoid chat spam.
     */
    private static final Map<UUID, Long> LAST_MESSAGE_TICK = new HashMap<>();

    /** Minimum ticks between repeated destination messages per player. */
    private static final long MESSAGE_COOLDOWN_TICKS = 100L;

    // ── Seal system ──

    /** Cache: BlockPos → portal center. Only caches the immutable mapping, NOT the guardian state. */
    private static final Map<Long, BlockPos> SEAL_CENTER_CACHE = new HashMap<>();

    /** Per-entity rejection cooldown: entity UUID → last rejection tick. */
    private static final Map<UUID, Long> SEAL_REJECT_COOLDOWN = new HashMap<>();
    private static final long SEAL_REJECT_COOLDOWN_TICKS = 20L;

    /** Per-player action bar message cooldown. */
    private static final Map<UUID, Long> SEAL_MESSAGE_COOLDOWN = new HashMap<>();
    private static final long SEAL_MESSAGE_COOLDOWN_TICKS = 60L;

    public OthersidePortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    // ------------------------------------------------------------------
    // Block state
    // ------------------------------------------------------------------

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    // ------------------------------------------------------------------
    // Shape & collision
    // ------------------------------------------------------------------

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.Z ? Z_AXIS_AABB : X_AXIS_AABB;
    }

    // ------------------------------------------------------------------
    // Frame validation — collapse to air if frame is broken
    // ------------------------------------------------------------------

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                     LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        // Only check neighbours along the portal's own plane (the axis it spans + up/down).
        Direction.Axis portalAxis = state.getValue(AXIS);

        // Directions that border the portal plane: for an X-axis portal the plane
        // extends along X and Y, so the thin edges face along Z (NORTH/SOUTH) — but
        // those are behind/in-front and not structural.  The structural neighbours
        // are UP, DOWN, and along the portal axis (EAST/WEST for X, NORTH/SOUTH for Z).
        boolean isStructuralNeighbour =
                (facing.getAxis() == portalAxis) || (facing.getAxis() == Direction.Axis.Y);

        if (isStructuralNeighbour) {
            // The neighbour must be either another portal block or a valid frame block.
            if (!facingState.is(this) && !facingState.is(PORTAL_FRAME_TAG)) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    // ------------------------------------------------------------------
    // Visual / audio effects
    // ------------------------------------------------------------------

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        // 1-in-4 chance: spawn a sculk soul particle drifting within the portal plane.
        if (random.nextInt(4) == 0) {
            Direction.Axis axis = state.getValue(AXIS);
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + 0.5D;

            if (axis == Direction.Axis.X) {
                // Portal spans X — offset along X, keep Z centred ±0.3
                x += (random.nextDouble() - 0.5D);
                z += (random.nextDouble() - 0.5D) * 0.6D; // ±0.3
            } else {
                // Portal spans Z — offset along Z, keep X centred ±0.3
                x += (random.nextDouble() - 0.5D) * 0.6D; // ±0.3
                z += (random.nextDouble() - 0.5D);
            }

            level.addParticle(ParticleTypes.SCULK_SOUL, x, y, z, 0.0D, 0.05D, 0.0D);
        }

        // 1-in-200 chance: play the ambient portal loop sound.
        if (random.nextInt(200) == 0) {
            level.playLocalSound(
                    pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                    ModSoundEvents.PORTAL_AMBIENT_LOOP.get(),
                    SoundSource.BLOCKS,
                    0.5F,
                    random.nextFloat() * 0.4F + 0.8F,
                    false
            );
        }
    }

    // ------------------------------------------------------------------
    // Entity interaction
    // ------------------------------------------------------------------

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // ── Seal check: reject living entities while guardian is active ──
        if (!level.isClientSide && entity instanceof LivingEntity living
                && OthersideConfig.SERVER.sealEnabled.get()) {
            if (isSealActive(level, pos)) {
                rejectEntity(level, state, pos, living);
                return; // Do NOT call setAsInsidePortal
            }
        }

        entity.setAsInsidePortal(this, pos);
    }

    /**
     * Checks if this portal block's guardian seal is active.
     * Reads guardian state LIVE from PortalSavedData — never cached.
     */
    private boolean isSealActive(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        // Cache only the pos → portal center mapping (immutable while portal exists)
        long packed = pos.asLong();
        BlockPos portalCenter = SEAL_CENTER_CACHE.get(packed);

        if (portalCenter == null) {
            PortalSavedData data = PortalSavedData.get(serverLevel);
            PortalSavedData.PortalEntry entry = data.findPortalContaining(
                    serverLevel.dimension(), pos);
            if (entry == null) return false;
            portalCenter = entry.center;
            SEAL_CENTER_CACHE.put(packed, portalCenter);
        }

        // Read state LIVE — never cache this
        PortalSavedData data = PortalSavedData.get((ServerLevel) level);
        return data.getState(((ServerLevel) level).dimension(), portalCenter)
                == PortalSavedData.GuardianState.ACTIVE;
    }

    /**
     * Rejects a living entity from the sealed portal with knockback, sound, particles,
     * and an action bar message.
     */
    private void rejectEntity(Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        long currentTick = level.getGameTime();
        UUID entityId = entity.getUUID();

        // Per-entity cooldown: no machine-gun knockback
        Long lastReject = SEAL_REJECT_COOLDOWN.get(entityId);
        if (lastReject != null && (currentTick - lastReject) < SEAL_REJECT_COOLDOWN_TICKS) {
            return;
        }
        SEAL_REJECT_COOLDOWN.put(entityId, currentTick);

        // Determine push direction: away from the portal plane
        Direction.Axis portalAxis = state.getValue(AXIS);
        Vec3 entityPos = entity.position();
        Vec3 blockCenter = Vec3.atCenterOf(pos);

        double pushX = 0, pushZ = 0;
        if (portalAxis == Direction.Axis.X) {
            // Portal is thin along Z — push along Z
            pushZ = entityPos.z >= blockCenter.z ? 0.8 : -0.8;
        } else {
            // Portal is thin along X — push along X
            pushX = entityPos.x >= blockCenter.x ? 0.8 : -0.8;
        }

        entity.setDeltaMovement(entity.getDeltaMovement().add(pushX, 0.2, pushZ));
        if (entity instanceof ServerPlayer player) {
            player.hurtMarked = true;
        }

        // Sound: warden sonic charge, high pitch
        level.playSound(null, pos, SoundEvents.WARDEN_SONIC_CHARGE,
                SoundSource.HOSTILE, 0.6F, 1.6F);

        // Particles: 6 sculk_soul at contact point
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    6, 0.3, 0.3, 0.3, 0.02);
        }

        // Action bar message (players only, rate-limited)
        if (entity instanceof ServerPlayer player) {
            Long lastMsg = SEAL_MESSAGE_COOLDOWN.get(entityId);
            if (lastMsg == null || (currentTick - lastMsg) >= SEAL_MESSAGE_COOLDOWN_TICKS) {
                SEAL_MESSAGE_COOLDOWN.put(entityId, currentTick);
                player.sendSystemMessage(
                        Component.literal("it will not let you pass.")
                                .withStyle(ChatFormatting.DARK_AQUA)
                                .withStyle(ChatFormatting.ITALIC),
                        true // action bar
                );
            }

            // Director log: first rejection per player
            if (level instanceof ServerLevel sl && GuardianManager.shouldLogSealReject(entityId)) {
                DirectorLog.log(sl, "SEAL_REJECT", pos,
                        "Player " + player.getName().getString() + " rejected by sealed portal");
            }
        }
    }

    // ------------------------------------------------------------------
    // Portal interface
    // ------------------------------------------------------------------

    @Override
    public int getPortalTransitionTime(ServerLevel level, Entity entity) {
        return entity instanceof Player player && player.isCreative() ? 0 : 60;
    }

    @Nullable
    @Override
    public DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
        // Determine source and target dimensions
        ResourceKey<Level> othersideKey = DimensionRulesManager.OTHERSIDE_DIM;
        boolean isInOtherside = level.dimension().equals(othersideKey);
        ResourceKey<Level> targetDimKey = isInOtherside ? Level.OVERWORLD : othersideKey;

        ServerLevel targetLevel = level.getServer().getLevel(targetDimKey);
        if (targetLevel == null) {
            OthersideMod.LOGGER.warn("Could not find target dimension: {}", targetDimKey.location());
            return null;
        }

        float yRot = entity.getYRot();
        float xRot = entity.getXRot();

        if (isInOtherside) {
            // ── Otherside → Overworld: 1:1 coordinate mapping, heightmap Y ──
            int targetX = pos.getX();
            int targetZ = pos.getZ();
            int targetY = targetLevel.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, targetX, targetZ) + 1;
            Vec3 targetPos = new Vec3(targetX + 0.5, targetY, targetZ + 0.5);
            return new DimensionTransition(targetLevel, targetPos, Vec3.ZERO, yRot, xRot,
                    DimensionTransition.DO_NOTHING);
        } else {
            // ── Overworld → Otherside: check first-arrival vs returning player ──
            if (entity instanceof ServerPlayer player) {
                OthersideSavedData savedData = OthersideSavedData.get(targetLevel);

                if (!savedData.hasArrived(player.getUUID())) {
                    // First arrival — send to landing ruin
                    // ArrivalScript detects arrival via PlayerChangedDimensionEvent and handles cinematic + markArrived
                    Vec3 targetPos = getOrBuildLandingRuin(targetLevel, savedData, pos);
                    return new DimensionTransition(targetLevel, targetPos, Vec3.ZERO, yRot, xRot,
                            DimensionTransition.DO_NOTHING);
                }
            }

            // Returning player or non-player entity: 1:1 coords with safe Y scan
            int targetX = pos.getX();
            int targetZ = pos.getZ();
            int targetY = findSafeYInOtherside(targetLevel, targetX, targetZ);
            Vec3 targetPos = new Vec3(targetX + 0.5, targetY, targetZ + 0.5);
            return new DimensionTransition(targetLevel, targetPos, Vec3.ZERO, yRot, xRot,
                    DimensionTransition.DO_NOTHING);
        }
    }

    /**
     * Get (or build) the landing ruin position.
     * If the ruin hasn't been placed yet, builds it at 1:1 coords from the portal.
     */
    private Vec3 getOrBuildLandingRuin(ServerLevel targetLevel, OthersideSavedData savedData, BlockPos sourcePos) {
        if (savedData.isLandingRuinPlaced() && savedData.getLandingRuinCenter() != null) {
            BlockPos center = savedData.getLandingRuinCenter();
            return new Vec3(center.getX() + 0.5, center.getY() + 1.0, center.getZ() + 0.5);
        }

        // Build the landing ruin at 1:1 coordinates from the source portal
        BlockPos ruinCenter = LandingRuinBuilder.build(targetLevel, sourcePos.getX(), sourcePos.getZ());
        savedData.setLandingRuinCenter(ruinCenter);
        savedData.setLandingRuinPlaced(true);

        // Spawn point is 1 block above the platform center
        return new Vec3(ruinCenter.getX() + 0.5, ruinCenter.getY() + 1.0, ruinCenter.getZ() + 0.5);
    }

    /**
     * Scan down from Y=120 in the Otherside to find the first solid block
     * with 2 air blocks above it. Falls back to Y=101 if nothing is found.
     */
    private int findSafeYInOtherside(ServerLevel level, int x, int z) {
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos(x, 0, z);
        for (int y = 120; y > 40; y--) {
            probe.setY(y);
            BlockState below = level.getBlockState(probe);
            if (!below.isAir() && !below.liquid()) {
                // Check 2 air blocks above
                probe.setY(y + 1);
                BlockState above1 = level.getBlockState(probe);
                probe.setY(y + 2);
                BlockState above2 = level.getBlockState(probe);
                if (above1.isAir() && above2.isAir()) {
                    return y + 1;
                }
            }
        }
        return 101; // Fallback
    }

}
