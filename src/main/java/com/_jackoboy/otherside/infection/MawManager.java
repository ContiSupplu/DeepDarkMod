package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.entity.MawTentacleEntity;
import com._jackoboy.otherside.registry.ModDamageTypes;
import com._jackoboy.otherside.registry.ModEntityTypes;
import com._jackoboy.otherside.infection.InfectionSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Manages the Maw lifecycle: eligibility → telegraph → open → grasp → swallow → seal.
 * Max 1 active Maw globally. Ticked from WorldbeastState.
 */
public class MawManager {

    // ── Maw state enum ───────────────────────────────────────────────
    public enum MawState {
        INACTIVE,       // No active Maw
        TELEGRAPHING,   // Order issued, waiting for arrival
        OPENING,        // Throat sequence (crack → widen), tentacles emerging
        ACTIVE,         // Grasp field live, tentacles hunting
        SEALING         // Tentacles retracting, throat healing
    }

    // ── Active Maw data ──────────────────────────────────────────────
    private MawState state = MawState.INACTIVE;
    private BlockPos throatPos = BlockPos.ZERO;
    @Nullable private UUID targetPlayerUUID = null;
    private long openTick = 0;
    private final List<Integer> tentacleEntityIds = new ArrayList<>();
    private final Set<Integer> graspedEntityIds = new HashSet<>();
    private int dismantleTimer = 0;

    // ── Persistence ──────────────────────────────────────────────────
    private long lastMawCloseTick = -100000;
    private boolean firstMawEver = true;

    // ── Throat animation ─────────────────────────────────────────────
    private int throatPhaseTimer = 0;
    private static final int THROAT_CRACK_TICKS = 20;   // 1s crack
    private static final int THROAT_WIDEN_TICKS = 40;    // 2s widen
    private static final int THROAT_TOTAL = THROAT_CRACK_TICKS + THROAT_WIDEN_TICKS;

    // ── Constants ────────────────────────────────────────────────────
    private static final double TENTACLE_RING_RADIUS = 4.0;
    private static final int SEAL_GRACE_TICKS = 60; // 3s for tentacles to retract
    private static final int DURATION_FLOOR_DEFAULT = 300; // 15s fallback

    private final Random random = new Random();

    // =====================================================================
    //  TICK (called every 20 game ticks from WorldbeastState)
    // =====================================================================

    public void tick(ServerLevel level, WorldbeastState beast) {
        switch (state) {
            case INACTIVE -> tickInactive(level, beast);
            case TELEGRAPHING -> tickTelegraphing(level, beast);
            case OPENING -> tickOpening(level, beast);
            case ACTIVE -> tickActive(level, beast);
            case SEALING -> tickSealing(level, beast);
        }
    }

    // ── INACTIVE: check eligibility ──────────────────────────────────
    private void tickInactive(ServerLevel level, WorldbeastState beast) {
        long gameTime = level.getGameTime();

        // Cheap gates first (M3)
        if (beast.getHunger() < OthersideConfig.SERVER.mawHungerGate.get()) return;
        if (beast.isSated()) return;
        if (gameTime - lastMawCloseTick < OthersideConfig.SERVER.mawCooldownTicks.get()) return;

        // Find a HUNTED player (near claimed territory OR near any breach)
        ServerPlayer target = findHuntedPlayer(level, beast);
        if (target == null) {
            // Debug: log why (only every 100 ticks to avoid spam)
            if (gameTime % 100 == 0) {
                OthersideMod.LOGGER.debug("[MAW] No eligible target: claimed_chunks={}",
                        beast.getClaimedChunks().size());
            }
            return;
        }

        // Find flat-ground site near the player
        BlockPos site = findMawSite(level, beast, target);
        if (site == null) {
            OthersideMod.LOGGER.debug("[MAW] No valid site found near {}",
                    target.blockPosition().toShortString());
            return;
        }

        // All gates passed — issue telegraph
        targetPlayerUUID = target.getUUID();
        throatPos = site;
        state = MawState.TELEGRAPHING;

        // Issue MAW order through OrderManager (gets sever-before-open for free)
        beast.getOrderManager().issueOrder(level, OrderManager.OrderType.MAW, site, beast);

        DirectorLog.log(level, "MAW_TELEGRAPH", site,
                "target=" + target.getName().getString()
                        + " hunger=" + String.format("%.1f", beast.getHunger()));
        OthersideMod.LOGGER.info("[MAW] Telegraph issued to {} for player {}",
                site.toShortString(), target.getName().getString());
    }

    // ── TELEGRAPHING: waiting for order arrival ──────────────────────
    private void tickTelegraphing(ServerLevel level, WorldbeastState beast) {
        // Check if the MAW order was severed (cancelled)
        boolean orderStillActive = false;
        for (OrderManager.VeinOrder order : beast.getOrderManager().getActiveOrders()) {
            if (order.type == OrderManager.OrderType.MAW
                    && order.target.equals(throatPos)) {
                orderStillActive = true;
                break;
            }
        }

        if (!orderStillActive) {
            // Severed or expired — cancel
            OthersideMod.LOGGER.info("[MAW] Telegraph severed/cancelled for {}", throatPos.toShortString());
            DirectorLog.log(level, "MAW_SEVERED", throatPos, "telegraph_cut");
            resetMaw();
        }
        // Arrival is handled via onMawOrderArrived(), called from OrderManager.executeArrival
    }

    /**
     * Called by OrderManager when the MAW order arrives (case MAW -> in executeArrival).
     */
    public void onMawOrderArrived(ServerLevel level, BlockPos target, WorldbeastState beast) {
        if (state != MawState.TELEGRAPHING) return;
        if (!target.equals(throatPos)) return;

        // Begin opening sequence
        state = MawState.OPENING;
        throatPhaseTimer = 0;
        openTick = level.getGameTime();
        OthersideMod.LOGGER.info("[MAW] Opening at {}", throatPos.toShortString());
    }

    // ── OPENING: throat crack→widen + tentacle emerge ────────────────
    // Throat scales with hunger: radius 1 (3×3) at hunger<50, radius 2 (5×5) at hunger≥70
    private int throatRadius(WorldbeastState beast) {
        float hunger = beast.getHunger();
        return hunger >= 70 ? 2 : 1;
    }

    private void tickOpening(ServerLevel level, WorldbeastState beast) {
        throatPhaseTimer++;

        if (throatPhaseTimer == 1) {
            // Phase 1: crack — play tremor sound, open center via dismantling
            level.playSound(null, throatPos, SoundEvents.SCULK_SHRIEKER_SHRIEK,
                    SoundSource.HOSTILE, 2.0f, 0.5f);
            // Also play at surface level to ensure audibility
            BlockPos surfaceAbove = throatPos.above();
            level.playSound(null, surfaceAbove, SoundEvents.SCULK_CATALYST_BLOOM,
                    SoundSource.HOSTILE, 3.0f, 0.3f);
            // Remove center block (respects amethyst)
            if (!MawTentacleEntity.isAmethystAnchored(throatPos, level)) {
                level.setBlock(throatPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }

        if (throatPhaseTimer == THROAT_CRACK_TICKS) {
            // Phase 2: widen — hunger-scaled aperture
            int radius = throatRadius(beast);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos p = throatPos.offset(dx, 0, dz);
                    BlockState bs = level.getBlockState(p);
                    if (!bs.isAir() && !MawTentacleEntity.isAmethystAnchored(p, level)) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }

            // Spawn tentacle ring
            spawnTentacleRing(level, beast);

            // Loud arrival sound
            level.playSound(null, throatPos, SoundEvents.WARDEN_EMERGE,
                    SoundSource.HOSTILE, 2.0f, 0.6f);

            DirectorLog.log(level, "MAW_OPEN", throatPos,
                    "tentacles=" + tentacleEntityIds.size() + " throat=" + (radius * 2 + 1) + "x" + (radius * 2 + 1));
        }

        if (throatPhaseTimer >= THROAT_TOTAL) {
            state = MawState.ACTIVE;
        }
    }

    // ── ACTIVE: grasp field + dismantling + swallow ───────────────────
    private void tickActive(ServerLevel level, WorldbeastState beast) {
        long gameTime = level.getGameTime();
        long elapsed = gameTime - openTick;

        // Hard cap — seal no matter what
        int maxDuration = OthersideConfig.SERVER.mawDurationTicks.get();
        if (elapsed >= maxDuration) {
            beginSeal(level, beast);
            return;
        }

        // Clean up dead tentacle references
        tentacleEntityIds.removeIf(id -> level.getEntity(id) == null);
        if (tentacleEntityIds.isEmpty()) {
            beginSeal(level, beast);
            return;
        }

        // Grasp field: pull entities toward throat
        tickGraspField(level, beast);

        // Base dismantling
        if (OthersideConfig.SERVER.baseDismantleEnabled.get()) {
            tickDismantle(level, beast);
        }

        // Check for swallows
        tickSwallow(level, beast);

        // Early-seal: once past the floor, seal if target gone AND no debris in flight
        int floor = OthersideConfig.SERVER.mawDurationFloorTicks.get();
        if (elapsed >= floor) {
            boolean targetGone = true;
            if (targetPlayerUUID != null) {
                for (var player : level.players()) {
                    if (player.getUUID().equals(targetPlayerUUID) && player.isAlive()) {
                        double dist = player.distanceToSqr(throatPos.getX() + 0.5,
                                throatPos.getY(), throatPos.getZ() + 0.5);
                        float graspR = OthersideConfig.SERVER.mawGraspBaseRadius.get()
                                + OthersideConfig.SERVER.mawGraspHungerRadius.get();
                        if (dist < graspR * graspR) {
                            targetGone = false; // still in range
                        }
                        break;
                    }
                }
            }

            boolean noDebris = graspedEntityIds.stream()
                    .noneMatch(id -> level.getEntity(id) != null);

            boolean noGrabs = tentacleEntityIds.stream()
                    .noneMatch(id -> {
                        Entity e = level.getEntity(id);
                        return e instanceof MawTentacleEntity t && t.isGrabbing();
                    });

            if (targetGone && noDebris && noGrabs) {
                DirectorLog.log(level, "MAW_EARLY_SEAL", throatPos,
                        "elapsed=" + elapsed + " reason=empty");
                beginSeal(level, beast);
            }
        }
    }

    private void tickGraspField(ServerLevel level, WorldbeastState beast) {
        float hunger = beast.getHunger();
        int baseR = OthersideConfig.SERVER.mawGraspBaseRadius.get();
        int hungerR = OthersideConfig.SERVER.mawGraspHungerRadius.get();
        double radius = baseR + hungerR * (hunger / 100.0);
        double pullSpeed = OthersideConfig.SERVER.mawPullPerTick.get();
        int cap = OthersideConfig.SERVER.mawDebrisCap.get();

        Vec3 throatCenter = new Vec3(throatPos.getX() + 0.5, throatPos.getY(), throatPos.getZ() + 0.5);
        AABB pullBox = new AABB(throatPos).inflate(radius);

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, pullBox,
                e -> !(e instanceof MawTentacleEntity)
                        && e.isAlive()
                        && !MawTentacleEntity.isAmethystProtected(e));

        int pulled = 0;
        for (Entity entity : entities) {
            if (pulled >= cap) break;

            // Tentacles handle their own grab targets
            if (isGrabbedByTentacle(entity)) continue;

            Vec3 direction = throatCenter.subtract(entity.position());
            double dist = direction.length();
            if (dist < 1.0) continue;

            Vec3 pull = direction.normalize().scale(Math.min(pullSpeed, dist * 0.1));

            // Add spiral feel
            double angle = (entity.tickCount % 60) * Math.PI * 2.0 / 60.0;
            pull = pull.add(Math.sin(angle) * 0.02, -0.015, Math.cos(angle) * 0.02);

            entity.setDeltaMovement(entity.getDeltaMovement().add(pull));
            entity.hurtMarked = true;

            graspedEntityIds.add(entity.getId());
            pulled++;
        }

        // Prune stale entries
        graspedEntityIds.removeIf(id -> {
            Entity e = level.getEntity(id);
            return e == null || !e.isAlive();
        });
    }

    private boolean isGrabbedByTentacle(Entity entity) {
        for (int tentId : tentacleEntityIds) {
            Entity tent = entity.level().getEntity(tentId);
            if (tent instanceof MawTentacleEntity tentacle
                    && tentacle.isGrabbing()
                    && entity.getUUID().equals(tentacle.getGrabTargetUUID())) {
                return true;
            }
        }
        return false;
    }

    private void tickSwallow(ServerLevel level, WorldbeastState beast) {
        Vec3 throatCenter = new Vec3(throatPos.getX() + 0.5, throatPos.getY(), throatPos.getZ() + 0.5);

        for (Entity entity : level.getEntitiesOfClass(Entity.class,
                new AABB(throatPos).inflate(2.0))) {
            if (entity instanceof MawTentacleEntity) continue;
            if (!entity.isAlive()) continue;
            if (MawTentacleEntity.isAmethystProtected(entity)) continue;

            double dist = entity.position().distanceTo(throatCenter);
            if (dist > 2.0) continue;

            // Swallow!
            if (entity instanceof LivingEntity living) {
                DamageSource source = new DamageSource(
                        level.registryAccess()
                                .registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                                .getHolderOrThrow(ModDamageTypes.SWALLOWED));
                living.hurt(source, Float.MAX_VALUE);

                DirectorLog.log(level, "MAW_SWALLOW", throatPos,
                        "entity=" + entity.getType().toShortString()
                                + " name=" + entity.getName().getString());

                // First swallow ever
                if (firstMawEver) {
                    firstMawEver = false;
                    DirectorLog.log(level, "FIRST_MAW", throatPos,
                            "entity=" + entity.getName().getString());
                }
            } else if (entity instanceof ItemEntity || entity instanceof FallingBlockEntity) {
                entity.discard();
            }
        }
    }

    // ── Base dismantling (Phase B) ───────────────────────────────────
    private void tickDismantle(ServerLevel level, WorldbeastState beast) {
        dismantleTimer++;
        if (dismantleTimer % 4 != 0) return; // Every 4 ticks = 5 blocks/second max

        float hunger = beast.getHunger();
        int baseR = OthersideConfig.SERVER.mawGraspBaseRadius.get();
        int hungerR = OthersideConfig.SERVER.mawGraspHungerRadius.get();
        int radius = (int)(baseR + hungerR * (hunger / 100.0));
        int cap = OthersideConfig.SERVER.mawDebrisCap.get();

        if (graspedEntityIds.size() >= cap) return;

        // Scan for blocks to pull — prefer player-placed
        List<BlockPos> candidates = new ArrayList<>();
        List<BlockPos> playerPlaced = new ArrayList<>();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) continue;
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE,
                        throatPos.getX() + dx, throatPos.getZ() + dz);
                for (int dy = 0; dy < 12; dy++) {
                    BlockPos pos = new BlockPos(throatPos.getX() + dx, surfaceY + dy, throatPos.getZ() + dz);
                    BlockState bs = level.getBlockState(pos);
                    if (bs.isAir() || bs.liquid()) continue;
                    if (MawTentacleEntity.isAmethystAnchored(pos, level)) continue;
                    if (isImmuneBlock(bs)) continue;

                    if (beast.isPlayerPlaced(pos)) {
                        playerPlaced.add(pos);
                    } else {
                        candidates.add(pos);
                    }
                }
            }
        }

        // Prioritize player-placed
        List<BlockPos> ordered = new ArrayList<>(playerPlaced);
        ordered.addAll(candidates);

        int pulled = 0;
        for (BlockPos pos : ordered) {
            if (pulled >= 3) break; // Max 3 blocks per tick cycle
            if (graspedEntityIds.size() >= cap) break;

            BlockState blockState = level.getBlockState(pos);
            if (blockState.isAir()) continue;

            // Remove block and spawn falling entity
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

            FallingBlockEntity falling = FallingBlockEntity.fall(level, pos, blockState);
            falling.setHurtsEntities(0, 0); // No hurt
            falling.dropItem = false;       // No item drops
            falling.time = 1;               // Skip initial delay

            // Override velocity toward throat
            Vec3 throatCenter = new Vec3(throatPos.getX() + 0.5,
                    throatPos.getY(), throatPos.getZ() + 0.5);
            Vec3 dir = throatCenter.subtract(falling.position()).normalize();
            double angle = (random.nextDouble() * Math.PI * 2);
            falling.setDeltaMovement(
                    dir.x * 0.3 + Math.sin(angle) * 0.05,
                    dir.y * 0.3 - 0.1,
                    dir.z * 0.3 + Math.cos(angle) * 0.05);
            falling.setNoGravity(true); // We control the motion
            falling.hurtMarked = true;

            graspedEntityIds.add(falling.getId());
            pulled++;

            // Director row (throttled)
            if (dismantleTimer % 20 == 0) {
                DirectorLog.log(level, "BASE_BLOCK_TAKEN", pos,
                        "block=" + blockState.getBlock().getName().getString());
            }
        }
    }

    private boolean isImmuneBlock(BlockState state) {
        Block b = state.getBlock();
        return b == Blocks.OBSIDIAN || b == Blocks.CRYING_OBSIDIAN
                || b == Blocks.REINFORCED_DEEPSLATE || b == Blocks.BEDROCK
                || b == Blocks.BARRIER || b == Blocks.END_PORTAL_FRAME;
    }

    // ── SEALING ──────────────────────────────────────────────────────
    private void beginSeal(ServerLevel level, WorldbeastState beast) {
        state = MawState.SEALING;
        throatPhaseTimer = 0;

        // Tell all tentacles to retract
        for (int id : tentacleEntityIds) {
            Entity e = level.getEntity(id);
            if (e instanceof MawTentacleEntity tentacle) {
                tentacle.requestRetract();
            }
        }
    }

    private void tickSealing(ServerLevel level, WorldbeastState beast) {
        throatPhaseTimer++;

        // Wait for tentacles to finish retracting
        boolean anyAlive = false;
        for (int id : tentacleEntityIds) {
            if (level.getEntity(id) != null) {
                anyAlive = true;
                break;
            }
        }

        if (!anyAlive || throatPhaseTimer >= SEAL_GRACE_TICKS) {
            // Clean up any stragglers
            for (int id : tentacleEntityIds) {
                Entity e = level.getEntity(id);
                if (e != null) e.discard();
            }

            // Heal throat to scar (sculk blocks) — same radius as opening
            int radius = throatRadius(beast);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos p = throatPos.offset(dx, 0, dz);
                    if (level.getBlockState(p).isAir()) {
                        level.setBlock(p, Blocks.SCULK.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }

            // Beast goes SATED
            beast.triggerSated();

            lastMawCloseTick = level.getGameTime();

            DirectorLog.log(level, "MAW_SEAL", throatPos, "sated=true");
            OthersideMod.LOGGER.info("[MAW] Sealed at {}, beast now SATED", throatPos.toShortString());

            resetMaw();
        }
    }

    // ── Bell force-close (Phase C) ───────────────────────────────────
    public void forceClose(ServerLevel level, BlockPos bellPos, WorldbeastState beast) {
        if (state != MawState.ACTIVE && state != MawState.OPENING) return;

        double dist = bellPos.distSqr(throatPos);
        float radius = OthersideConfig.SERVER.mawGraspBaseRadius.get()
                + OthersideConfig.SERVER.mawGraspHungerRadius.get();
        if (dist > radius * radius) return;

        DirectorLog.log(level, "MAW_FORCE_CLOSE", throatPos, "bell_at=" + bellPos.toShortString());

        // Issue RETALIATION through existing path
        beast.getOrderManager().issueOrder(level, OrderManager.OrderType.RETALIATION, throatPos, beast);

        beginSeal(level, beast);
    }

    // ── Tentacle spawning ────────────────────────────────────────────
    private void spawnTentacleRing(ServerLevel level, WorldbeastState beast) {
        tentacleEntityIds.clear();
        int count = OthersideConfig.SERVER.mawTentacleCount.get();

        // Irregular angular spacing: jitter each arm's angle for organic feel
        double baseStep = Math.PI * 2.0 / count;
        double[] angles = new double[count];
        for (int i = 0; i < count; i++) {
            // ±15% angular jitter (grown, not manufactured)
            double jitter = (random.nextDouble() - 0.5) * baseStep * 0.3;
            angles[i] = baseStep * i + jitter;
        }

        for (int i = 0; i < count; i++) {
            double angle = angles[i];
            // Slight radius variance (±0.5 blocks)
            double r = TENTACLE_RING_RADIUS + (random.nextDouble() - 0.5) * 1.0;
            double x = throatPos.getX() + 0.5 + Math.cos(angle) * r;
            double z = throatPos.getZ() + 0.5 + Math.sin(angle) * r;

            // Find surface Y at this position
            int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE,
                    (int) x, (int) z);

            MawTentacleEntity tentacle = ModEntityTypes.MAW_TENTACLE.get().create(level);
            if (tentacle == null) continue;

            tentacle.moveTo(x, surfaceY, z,
                    (float)(angle * 180.0 / Math.PI) + 180, 0);
            tentacle.setMawCenter(throatPos);
            level.addFreshEntity(tentacle);
            tentacleEntityIds.add(tentacle.getId());
        }

        OthersideMod.LOGGER.info("[MAW] Spawned {} tentacles (irregular ring) around {}",
                tentacleEntityIds.size(), throatPos.toShortString());
    }

    // ── Eligibility helpers ──────────────────────────────────────────
    @Nullable
    private ServerPlayer findHuntedPlayer(ServerLevel level, WorldbeastState beast) {
        int attentionGate = OthersideConfig.SERVER.mawAttentionGate.get();

        for (ServerPlayer player : level.players()) {
            float attention = beast.getPlayerAttention(player.getUUID());
            if (attention >= attentionGate) {
                // Check near claimed territory
                ChunkPos cp = new ChunkPos(player.blockPosition());
                boolean nearBody = false;
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        if (beast.getClaimedChunks().contains(
                                ChunkPos.asLong(cp.x + dx, cp.z + dz))) {
                            nearBody = true;
                            break;
                        }
                    }
                    if (nearBody) break;
                }
                if (nearBody) return player;

                // Fallback: near any breach surface breakout (within 32 blocks)
                InfectionSavedData infData = InfectionSavedData.get(level);
                for (var breach : infData.getBreaches()) {
                    BlockPos surface = breach.getSurfaceBreakout();
                    if (surface != null) {
                        double dist = player.blockPosition().distSqr(surface);
                        if (dist < 32 * 32) return player;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private BlockPos findMawSite(ServerLevel level, WorldbeastState beast, ServerPlayer target) {
        BlockPos playerPos = target.blockPosition();

        // Search around player for flat ground
        BlockPos bestSite = null;
        int bestScore = -1; // Start at -1 so score=0 sites still get selected

        for (int dx = -16; dx <= 16; dx += 4) {
            for (int dz = -16; dz <= 16; dz += 4) {
                int x = playerPos.getX() + dx;
                int z = playerPos.getZ() + dz;
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                // surfaceY is the Y of the first air block above ground;
                // the actual ground block is at surfaceY-1.
                // Place throat AT the ground block so it gets broken visibly.
                BlockPos candidate = new BlockPos(x, surfaceY - 1, z);

                if (!level.isLoaded(candidate)) continue;

                // Flatness check: 3×3 surface variance ≤ 2
                int minY = surfaceY, maxY = surfaceY;
                for (int cx = -1; cx <= 1; cx++) {
                    for (int cz = -1; cz <= 1; cz++) {
                        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x + cx, z + cz);
                        minY = Math.min(minY, y);
                        maxY = Math.max(maxY, y);
                    }
                }
                if (maxY - minY > 2) continue;

                // Score: count player-placed blocks in column above (bonus, not required)
                int score = 0;
                for (int dy = 0; dy < 16; dy++) {
                    if (beast.isPlayerPlaced(new BlockPos(x, surfaceY + dy, z))) {
                        score++;
                    }
                }

                // Prefer claimed territory but don't require it near breaches
                long chunkKey = ChunkPos.asLong(x >> 4, z >> 4);
                if (beast.getClaimedChunks().contains(chunkKey)) {
                    score += 10; // Strong bonus for being on claimed territory
                }

                if (score > bestScore) {
                    bestScore = score;
                    bestSite = candidate;
                }
            }
        }

        return bestSite;
    }

    // ── Reset ────────────────────────────────────────────────────────
    private void resetMaw() {
        state = MawState.INACTIVE;
        throatPos = BlockPos.ZERO;
        targetPlayerUUID = null;
        tentacleEntityIds.clear();
        graspedEntityIds.clear();
        throatPhaseTimer = 0;
        dismantleTimer = 0;
    }

    // ── Orphan sweep (I2) ────────────────────────────────────────────
    public void sweepOrphanTentacles(ServerLevel level) {
        for (Entity e : level.getAllEntities()) {
            if (e instanceof MawTentacleEntity tentacle) {
                if (!tentacleEntityIds.contains(tentacle.getId())) {
                    OthersideMod.LOGGER.info("[MAW] Removing orphan tentacle {} at {}",
                            tentacle.getId(), tentacle.blockPosition().toShortString());
                    tentacle.discard();
                }
            }
        }
    }

    // ── Persistence ──────────────────────────────────────────────────
    public void saveTo(CompoundTag tag) {
        CompoundTag maw = new CompoundTag();
        maw.putLong("lastMawCloseTick", lastMawCloseTick);
        maw.putBoolean("firstMawEver", firstMawEver);
        maw.putInt("state", state.ordinal());
        if (state != MawState.INACTIVE) {
            maw.putLong("throatPos", throatPos.asLong());
            if (targetPlayerUUID != null) {
                maw.putUUID("targetPlayer", targetPlayerUUID);
            }
            maw.putLong("openTick", openTick);
        }
        tag.put("mawManager", maw);
    }

    public void loadFrom(CompoundTag tag) {
        if (!tag.contains("mawManager")) return;
        CompoundTag maw = tag.getCompound("mawManager");
        lastMawCloseTick = maw.getLong("lastMawCloseTick");
        firstMawEver = maw.getBoolean("firstMawEver");

        int savedState = maw.getInt("state");
        if (savedState != MawState.INACTIVE.ordinal()) {
            // If server restarted mid-Maw, seal it rather than resuming
            OthersideMod.LOGGER.info("[MAW] Server restarted during active Maw — sealing on load");
            state = MawState.INACTIVE;
            // lastMawCloseTick stays as-is so cooldown continues
        }
    }

    // ── Getters ──────────────────────────────────────────────────────
    public MawState getState() { return state; }
    public boolean isActive() { return state != MawState.INACTIVE; }
    public BlockPos getThroatPos() { return throatPos; }
    @Nullable public UUID getTargetPlayerUUID() { return targetPlayerUUID; }
}
