package com._jackoboy.otherside.block;

import com._jackoboy.otherside.infection.OrderManager;
import com._jackoboy.otherside.infection.WorldbeastState;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Sculk Vein Cord — the Worldbeast's nervous system.
 * <p>
 * A flat, carpet-like block that connects horizontally to adjacent cords and
 * blocks in the {@code #otherside:vein_connectable} tag. Walking on it alerts
 * the Worldbeast. The {@code CHARGED} state is toggled externally to animate
 * signal pulses.
 */
public class SculkVeinCordBlock extends Block {
    public static final MapCodec<SculkVeinCordBlock> CODEC = simpleCodec(SculkVeinCordBlock::new);

    // ── Block-state properties ──────────────────────────────────────────────
    public static final BooleanProperty NORTH   = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH   = BooleanProperty.create("south");
    public static final BooleanProperty EAST    = BooleanProperty.create("east");
    public static final BooleanProperty WEST    = BooleanProperty.create("west");
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");

    // ── Tag for connectable neighbours ──────────────────────────────────────
    private static final TagKey<Block> VEIN_CONNECTABLE = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("otherside", "vein_connectable"));

    // ── VoxelShapes ─────────────────────────────────────────────────────────
    private static final VoxelShape NODE_SHAPE  = Block.box(4, 0, 4, 12, 3, 12);
    private static final VoxelShape ARM_NORTH   = Block.box(5, 0, 0, 11, 3, 5);
    private static final VoxelShape ARM_SOUTH   = Block.box(5, 0, 11, 11, 3, 16);
    private static final VoxelShape ARM_EAST    = Block.box(11, 0, 5, 16, 3, 11);
    private static final VoxelShape ARM_WEST    = Block.box(0, 0, 5, 5, 3, 11);

    /** Cached combined shapes for the 16 possible NSEW combinations. */
    private static final VoxelShape[] SHAPE_CACHE = buildShapeCache();

    // ── Per-player step cooldown (server only) ──────────────────────────────
    private final Map<UUID, Long> lastStepTime = new HashMap<>();

    // ── Constructor ─────────────────────────────────────────────────────────

    public SculkVeinCordBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH,   false)
                .setValue(SOUTH,   false)
                .setValue(EAST,    false)
                .setValue(WEST,    false)
                .setValue(CHARGED, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    // ── State definition ────────────────────────────────────────────────────

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, CHARGED);
    }

    // ── Placement & connection logic ────────────────────────────────────────

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        return this.defaultBlockState()
                .setValue(NORTH,   connectsTo(level, pos, Direction.NORTH))
                .setValue(SOUTH,   connectsTo(level, pos, Direction.SOUTH))
                .setValue(EAST,    connectsTo(level, pos, Direction.EAST))
                .setValue(WEST,    connectsTo(level, pos, Direction.WEST));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // Support check — pop if block below is gone
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) {
            popBlock(level, pos);
            return Blocks.AIR.defaultBlockState();
        }

        // Update horizontal connections
        if (direction.getAxis().isHorizontal()) {
            BooleanProperty prop = directionProperty(direction);
            if (prop != null) {
                state = state.setValue(prop, connectsTo(level, pos, direction));
            }
        }

        return state;
    }

    /**
     * Returns {@code true} when the neighbour in the given direction is
     * another vein cord or belongs to the {@code vein_connectable} tag.
     */
    private boolean connectsTo(LevelReader level, BlockPos pos, Direction dir) {
        BlockState neighbor = level.getBlockState(pos.relative(dir));
        return neighbor.getBlock() instanceof SculkVeinCordBlock
                || neighbor.is(VEIN_CONNECTABLE);
    }

    @Nullable
    private static BooleanProperty directionProperty(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST  -> EAST;
            case WEST  -> WEST;
            default    -> null;
        };
    }

    // ── VoxelShape ──────────────────────────────────────────────────────────

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        int index = shapeIndex(state);
        return SHAPE_CACHE[index];
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    private static int shapeIndex(BlockState state) {
        int idx = 0;
        if (state.getValue(NORTH)) idx |= 1;
        if (state.getValue(SOUTH)) idx |= 2;
        if (state.getValue(EAST))  idx |= 4;
        if (state.getValue(WEST))  idx |= 8;
        return idx;
    }

    private static VoxelShape[] buildShapeCache() {
        VoxelShape[] cache = new VoxelShape[16];
        for (int i = 0; i < 16; i++) {
            VoxelShape shape = NODE_SHAPE;
            if ((i & 1) != 0) shape = Shapes.or(shape, ARM_NORTH);
            if ((i & 2) != 0) shape = Shapes.or(shape, ARM_SOUTH);
            if ((i & 4) != 0) shape = Shapes.or(shape, ARM_EAST);
            if ((i & 8) != 0) shape = Shapes.or(shape, ARM_WEST);
            cache[i] = shape.optimize();
        }
        return cache;
    }

    // ── Support ─────────────────────────────────────────────────────────────

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    /**
     * Destroys the block with a sculk squelch sound and particles (no drops).
     */
    private void popBlock(LevelAccessor level, BlockPos pos) {
        if (level instanceof Level realLevel && !realLevel.isClientSide()) {
            // Sound
            realLevel.playSound(null, pos, SoundType.SCULK.getBreakSound(),
                    SoundSource.BLOCKS, 1.0F, 1.0F);

            // Particles
            if (realLevel instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                        pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5,
                        2, 0.3, 0.1, 0.3, 0.01);
            }

            realLevel.destroyBlock(pos, false);
        }
    }

    // ── Entity stepping ─────────────────────────────────────────────────────

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide()) return;
        if (!(entity instanceof ServerPlayer player)) return;

        long gameTime = level.getGameTime();
        UUID playerId = player.getUUID();
        Long lastTime = lastStepTime.get(playerId);

        if (lastTime != null && gameTime - lastTime < 10) return;

        lastStepTime.put(playerId, gameTime);

        // Alert the Worldbeast
        if (level instanceof ServerLevel serverLevel) {
            WorldbeastState.get(serverLevel).addAttention(playerId, 2.0f, gameTime);
        }
    }

    // ── Block removal ───────────────────────────────────────────────────────

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level instanceof ServerLevel serverLevel) {
                // Notify order system of cord break (guarded: only real removals)
                WorldbeastState beast = WorldbeastState.get(serverLevel);
                OrderManager om = beast.getOrderManager();
                if (om != null) {
                    om.onCordBroken(serverLevel, pos);
                }
            }

            // Clear per-player step cooldown entries to prevent stale data
            lastStepTime.clear();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    // ── Player attribution ──────────────────────────────────────────────────

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer sp) {
            // Severer attribution: +8 attention goes to breaker when full sever detected
            WorldbeastState beast = WorldbeastState.get(serverLevel);
            OrderManager om = beast.getOrderManager();
            if (om != null) {
                om.onCordBrokenByPlayer(serverLevel, pos, sp);
            }
        }
    }
}
