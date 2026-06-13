package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.*;

/**
 * SoreManager — trigger scoring and eruption sequences for Sore sites.
 * <p>
 * Sores are secondary infection sites spawned by the Worldbeast when it detects
 * concentrations of player activity (light, noise, livestock, villagers) in
 * loaded surface chunks far from existing breach sites.
 * <p>
 * Created as part of W2 of the Worldbeast Rework (§3.3).
 */
public class SoreManager {

    // ── Timing constants ────────────────────────────────────────────────────
    private static final int SCORE_INTERVAL_TICKS  = 1200;  // 1 minute (in 20-tick calls)
    private static final int NOISE_DECAY_INTERVAL  = 2400;  // 2 minutes
    private static final int ERUPTION_DURATION      = 240;   // 12 seconds in game ticks

    // ── Eruption phase tick offsets ──────────────────────────────────────────
    private static final int PHASE_TREMBLE     = 0;
    private static final int PHASE_INTENSIFY   = 60;
    private static final int PHASE_FRACTURE    = 120;
    private static final int PHASE_SCULK_BURST = 180;
    private static final int PHASE_PLACE_BLOCKS = 200;
    private static final int PHASE_VEIN_GROWTH = 220;
    private static final int PHASE_COMPLETE    = 240;

    // ── Block set flag ──────────────────────────────────────────────────────
    private static final int UPDATE_ALL = 3;

    // ── State ───────────────────────────────────────────────────────────────
    private final List<SoreData> sores = new ArrayList<>();
    private final Map<Long, Integer> noiseCounters = new HashMap<>();
    private final List<EruptionSequence> activeEruptions = new ArrayList<>();
    private final Random random = new Random();

    private int tickCounter = 0;
    private boolean firstSoreEver = true;

    // =====================================================================
    //  Inner class: SoreData
    // =====================================================================

    /**
     * Persisted data for a single Sore eruption site.
     */
    public static class SoreData {
        public BlockPos center;
        public long eruptedGameTime;
        public boolean active;
        public UUID breachId;  // reference to the BreachData created for this sore

        public SoreData() {}

        public SoreData(BlockPos center, long eruptedGameTime) {
            this.center = center;
            this.eruptedGameTime = eruptedGameTime;
            this.active = true;
            this.breachId = null;
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putLong("center", center.asLong());
            tag.putLong("eruptedGameTime", eruptedGameTime);
            tag.putBoolean("active", active);
            if (breachId != null) {
                tag.putUUID("breachId", breachId);
            }
            return tag;
        }

        public static SoreData load(CompoundTag tag) {
            SoreData data = new SoreData();
            data.center = BlockPos.of(tag.getLong("center"));
            data.eruptedGameTime = tag.getLong("eruptedGameTime");
            data.active = tag.getBoolean("active");
            if (tag.hasUUID("breachId")) {
                data.breachId = tag.getUUID("breachId");
            }
            return data;
        }
    }

    // =====================================================================
    //  Inner class: EruptionSequence (transient — active eruptions)
    // =====================================================================

    /**
     * Tracks an in-progress eruption sequence. Not persisted — if the server
     * stops mid-eruption the sequence is lost (acceptable for a 12s event).
     */
    private static class EruptionSequence {
        final BlockPos center;
        final long startTick;
        final float triggerScore;
        int lastPhase = -1;

        EruptionSequence(BlockPos center, long startTick, float triggerScore) {
            this.center = center;
            this.startTick = startTick;
            this.triggerScore = triggerScore;
        }
    }

    // =====================================================================
    //  Inner class: ChunkCandidate (for scoring pipeline)
    // =====================================================================

    private static class ChunkCandidate {
        final ChunkPos chunkPos;
        int placedLightCount;
        int noiseEvents;
        int livestockCount;
        int villagerCount;
        float cheapScore;
        float fullScore;

        ChunkCandidate(ChunkPos chunkPos) {
            this.chunkPos = chunkPos;
        }
    }

    // =====================================================================
    //  TICK — called every 20 game ticks (1 second)
    // =====================================================================

    /**
     * Main tick method. Must be called once per second from the beast tick loop.
     */
    public void tick(ServerLevel level) {
        tickCounter++;
        long gameTime = level.getGameTime();

        // ── Process active eruption sequences ───────────────────────────
        tickEruptions(level, gameTime);

        // ── Noise decay every 2 minutes ─────────────────────────────────
        if (tickCounter % (NOISE_DECAY_INTERVAL / 20) == 0) {
            decayNoise();
        }

        // ── Sore candidate evaluation every 1 minute ────────────────────
        if (tickCounter % (SCORE_INTERVAL_TICKS / 20) == 0) {
            evaluateSoreCandidates(level);
        }
    }

    // =====================================================================
    //  NOISE TRACKING
    // =====================================================================

    /**
     * Record a noise event near the given position. Called by ModEventHandlers
     * on block place, break, or explosion events.
     */
    public void recordNoise(BlockPos pos) {
        long chunkKey = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
        noiseCounters.merge(chunkKey, 1, Integer::sum);
    }

    /**
     * Decay all noise counters by 1. Called every 2400 ticks (2 min).
     * This gives a ~10-minute rolling window (5 decay cycles).
     */
    private void decayNoise() {
        Iterator<Map.Entry<Long, Integer>> it = noiseCounters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Integer> entry = it.next();
            int newVal = entry.getValue() - 1;
            if (newVal <= 0) {
                it.remove();
            } else {
                entry.setValue(newVal);
            }
        }
    }

    /**
     * Get the total noise count for a chunk and its 8 neighbors (3×3 chunk region).
     */
    private int getRegionNoiseCount(ChunkPos center) {
        int total = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                long key = ChunkPos.asLong(center.x + dx, center.z + dz);
                total += noiseCounters.getOrDefault(key, 0);
            }
        }
        return total;
    }

    // =====================================================================
    //  TRIGGER SCORING
    // =====================================================================

    /**
     * Evaluate loaded surface chunks for Sore eruption candidates.
     * Uses a two-pass prefiltering approach for performance.
     */
    private void evaluateSoreCandidates(ServerLevel level) {
        WorldbeastState beast = WorldbeastState.get(level);
        InfectionSavedData infData = InfectionSavedData.get(level);

        // Check active sore cap
        long activeSoreCount = sores.stream().filter(s -> s.active).count();
        if (activeSoreCount >= OthersideConfig.SERVER.maxActiveSores.get()) {
            return;
        }

        // Collect loaded chunk positions
        List<ChunkCandidate> candidates = new ArrayList<>();
        for (LevelChunk chunk : getLoadedChunks(level)) {
            ChunkPos cp = chunk.getPos();
            candidates.add(new ChunkCandidate(cp));
        }

        if (candidates.isEmpty()) return;

        // ── Pass 1: Cheap scoring (placed lights + noise) ───────────────
        for (ChunkCandidate candidate : candidates) {
            candidate.placedLightCount = countPlacedLights(level, beast, candidate.chunkPos);
            candidate.noiseEvents = getRegionNoiseCount(candidate.chunkPos);
            candidate.cheapScore = candidate.placedLightCount * 2.0f + candidate.noiseEvents;
        }

        // Sort by cheap score descending, take top 10
        candidates.sort(Comparator.comparingDouble((ChunkCandidate c) -> c.cheapScore).reversed());
        int topN = Math.min(10, candidates.size());
        List<ChunkCandidate> topCandidates = candidates.subList(0, topN);

        // ── Pass 2: Entity scan on top 10 only (expensive) ──────────────
        float massFactor = 1.0f + beast.getMass() / 60.0f;
        ChunkCandidate bestCandidate = null;
        float bestScore = 0;

        for (ChunkCandidate candidate : topCandidates) {
            ChunkPos cp = candidate.chunkPos;

            // Expanded scan area: chunk bounds +8 blocks each side (32×32)
            int minX = cp.getMinBlockX() - 8;
            int maxX = cp.getMaxBlockX() + 8;
            int minZ = cp.getMinBlockZ() - 8;
            int maxZ = cp.getMaxBlockZ() + 8;

            // Y range: surface heightmap ±16
            int centerX = (cp.getMinBlockX() + cp.getMaxBlockX()) / 2;
            int centerZ = (cp.getMinBlockZ() + cp.getMaxBlockZ()) / 2;
            int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, centerX, centerZ);
            int minY = surfaceY - 16;
            int maxY = surfaceY + 16;

            AABB scanBox = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);

            candidate.livestockCount = level.getEntitiesOfClass(Animal.class, scanBox).size();
            candidate.villagerCount = level.getEntitiesOfClass(Villager.class, scanBox).size();

            // Full score formula
            candidate.fullScore = (candidate.placedLightCount * 2
                    + candidate.noiseEvents
                    + candidate.livestockCount * 3
                    + candidate.villagerCount * 8) * massFactor;

            if (candidate.fullScore > bestScore) {
                bestScore = candidate.fullScore;
                bestCandidate = candidate;
            }
        }

        if (bestCandidate == null) return;

        // ── Check threshold ─────────────────────────────────────────────
        int threshold = OthersideConfig.SERVER.soreThreshold.get();
        if (bestScore < threshold) {
            return;
        }

        // ── Check minimum distance from all existing sites ──────────────
        int minDist = OthersideConfig.SERVER.soreMinDistance.get();
        ChunkPos cp = bestCandidate.chunkPos;
        int centerX = (cp.getMinBlockX() + cp.getMaxBlockX()) / 2;
        int centerZ = (cp.getMinBlockZ() + cp.getMaxBlockZ()) / 2;
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, centerX, centerZ);
        BlockPos soreCenter = new BlockPos(centerX, surfaceY - 1, centerZ);

        if (!isMinDistanceFromSites(soreCenter, infData, minDist)) {
            return;
        }

        // ── Request eruption ────────────────────────────────────────────
        OthersideMod.LOGGER.info("[SORE] Candidate scored {} at {}, requesting eruption",
                String.format("%.1f", bestScore), soreCenter);

        // W2_HOOK: OrderManager.requestBreakout(level, soreCenter, bestScore);
        // For now, trigger eruption directly
        triggerEruption(level, soreCenter, beast, bestScore);
    }

    /**
     * Count player-placed light-emitting blocks in a chunk's scan area.
     */
    private int countPlacedLights(ServerLevel level, WorldbeastState beast, ChunkPos cp) {
        int count = 0;
        int minX = cp.getMinBlockX() - 8;
        int maxX = cp.getMaxBlockX() + 8;
        int minZ = cp.getMinBlockZ() - 8;
        int maxZ = cp.getMaxBlockZ() + 8;

        // Sample rather than exhaustive scan for performance — every 2nd block
        for (int x = minX; x <= maxX; x += 2) {
            for (int z = minZ; z <= maxZ; z += 2) {
                if (!level.isLoaded(new BlockPos(x, 0, z))) continue;

                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                for (int y = surfaceY - 16; y <= surfaceY + 16; y += 2) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.getLightEmission(level, pos) > 0 && beast.isPlayerPlaced(pos)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Check if a position is at least {@code minDist} blocks from all existing
     * breach sites and other sore locations.
     */
    public boolean isMinDistanceFromSites(BlockPos pos, InfectionSavedData infData, int minDist) {
        long minDistSq = (long) minDist * minDist;

        // Check against all breaches
        for (BreachData breach : infData.getBreaches()) {
            BlockPos surface = breach.getSurfaceBreakout();
            if (surface != null && pos.distSqr(surface) < minDistSq) {
                return false;
            }
            BlockPos city = breach.getCityOrigin();
            if (city != null && pos.distSqr(city) < minDistSq) {
                return false;
            }
        }

        // Check against all existing sores
        for (SoreData sore : sores) {
            if (sore.center != null && pos.distSqr(sore.center) < minDistSq) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get loaded chunks in the overworld.
     */
    private List<LevelChunk> getLoadedChunks(ServerLevel level) {
        List<LevelChunk> chunks = new ArrayList<>();
        // Use player-loaded chunk range to find loaded chunks
        for (ServerPlayer player : level.players()) {
            ChunkPos playerChunk = player.chunkPosition();
            int viewDist = level.getServer().getPlayerList().getViewDistance();
            for (int dx = -viewDist; dx <= viewDist; dx++) {
                for (int dz = -viewDist; dz <= viewDist; dz++) {
                    ChunkPos cp = new ChunkPos(playerChunk.x + dx, playerChunk.z + dz);
                    if (level.hasChunk(cp.x, cp.z)) {
                        LevelChunk chunk = level.getChunk(cp.x, cp.z);
                        if (!chunks.contains(chunk)) {
                            chunks.add(chunk);
                        }
                    }
                }
            }
        }
        return chunks;
    }

    // =====================================================================
    //  ERUPTION SEQUENCE
    // =====================================================================

    /**
     * Begin the 12-second eruption sequence at the target position.
     */
    public void triggerEruption(ServerLevel level, BlockPos target, WorldbeastState beast, float triggerScore) {
        long gameTime = level.getGameTime();

        // Create SoreData
        SoreData soreData = new SoreData(target, gameTime);
        sores.add(soreData);

        // Start the eruption sequence
        activeEruptions.add(new EruptionSequence(target, gameTime, triggerScore));

        OthersideMod.LOGGER.info("[SORE] Eruption started at {} (score={})",
                target, String.format("%.1f", triggerScore));
    }

    /**
     * Process all active eruption sequences each tick.
     */
    private void tickEruptions(ServerLevel level, long gameTime) {
        Iterator<EruptionSequence> it = activeEruptions.iterator();
        while (it.hasNext()) {
            EruptionSequence seq = it.next();
            int elapsed = (int) (gameTime - seq.startTick);

            // Process phases in order
            if (elapsed >= PHASE_TREMBLE && seq.lastPhase < PHASE_TREMBLE) {
                phaseGroundTremble(level, seq.center);
                seq.lastPhase = PHASE_TREMBLE;
            }
            if (elapsed >= PHASE_INTENSIFY && seq.lastPhase < PHASE_INTENSIFY) {
                phaseIntensify(level, seq.center);
                seq.lastPhase = PHASE_INTENSIFY;
            }
            if (elapsed >= PHASE_FRACTURE && seq.lastPhase < PHASE_FRACTURE) {
                phaseGroundFracture(level, seq.center);
                seq.lastPhase = PHASE_FRACTURE;
            }
            if (elapsed >= PHASE_SCULK_BURST && seq.lastPhase < PHASE_SCULK_BURST) {
                phaseSculkBurst(level, seq.center);
                seq.lastPhase = PHASE_SCULK_BURST;
            }
            if (elapsed >= PHASE_PLACE_BLOCKS && seq.lastPhase < PHASE_PLACE_BLOCKS) {
                phasePlaceBlocks(level, seq.center);
                seq.lastPhase = PHASE_PLACE_BLOCKS;
            }
            if (elapsed >= PHASE_VEIN_GROWTH && seq.lastPhase < PHASE_VEIN_GROWTH) {
                phaseVeinGrowth(level, seq.center);
                seq.lastPhase = PHASE_VEIN_GROWTH;
            }
            if (elapsed >= PHASE_COMPLETE && seq.lastPhase < PHASE_COMPLETE) {
                phaseComplete(level, seq.center, seq.triggerScore);
                seq.lastPhase = PHASE_COMPLETE;
                it.remove();
            }
        }
    }

    // ── Phase 0: Ground trembles (tick 0) ───────────────────────────────

    private void phaseGroundTremble(ServerLevel level, BlockPos center) {
        // Spawn SCULK_CHARGE_POP particles in 7×7 area
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos particlePos = center.offset(dx, 1, dz);
                level.sendParticles(ParticleTypes.SCULK_CHARGE_POP,
                        particlePos.getX() + 0.5, particlePos.getY() + 0.5, particlePos.getZ() + 0.5,
                        3, 0.3, 0.3, 0.3, 0.01);
            }
        }

        level.playSound(null, center, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS,
                1.5f, 0.5f);

        OthersideMod.LOGGER.debug("[SORE] Phase TREMBLE at {}", center);
    }

    // ── Phase 1: Intensify (tick 60 / 3s) ───────────────────────────────

    private void phaseIntensify(ServerLevel level, BlockPos center) {
        // More particles, wider area
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos particlePos = center.offset(dx, 1, dz);
                level.sendParticles(ParticleTypes.SCULK_CHARGE_POP,
                        particlePos.getX() + 0.5, particlePos.getY() + 0.8, particlePos.getZ() + 0.5,
                        5, 0.4, 0.5, 0.4, 0.02);
            }
        }

        // Deeper sound
        level.playSound(null, center, SoundEvents.WARDEN_EMERGE, SoundSource.BLOCKS,
                2.0f, 0.3f);

        OthersideMod.LOGGER.debug("[SORE] Phase INTENSIFY at {}", center);
    }

    // ── Phase 2: Ground fracture (tick 120 / 6s) ────────────────────────

    private void phaseGroundFracture(ServerLevel level, BlockPos center) {
        // 3×3 center blocks break
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos breakPos = center.offset(dx, 0, dz);
                if (!level.isLoaded(breakPos)) continue;

                BlockState state = level.getBlockState(breakPos);
                if (!state.isAir()) {
                    // Spawn break particles
                    level.sendParticles(ParticleTypes.SCULK_CHARGE_POP,
                            breakPos.getX() + 0.5, breakPos.getY() + 0.5, breakPos.getZ() + 0.5,
                            10, 0.3, 0.3, 0.3, 0.05);
                    level.destroyBlock(breakPos, false);
                }
            }
        }

        level.playSound(null, center, SoundEvents.SCULK_BLOCK_BREAK, SoundSource.BLOCKS,
                2.0f, 0.4f);

        OthersideMod.LOGGER.debug("[SORE] Phase FRACTURE at {}", center);
    }

    // ── Phase 3: Sculk burst (tick 180 / 9s) ────────────────────────────

    private void phaseSculkBurst(ServerLevel level, BlockPos center) {
        // 7×7 sculk burst: convert surface blocks via ConversionMap
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos pos = center.offset(dx, 0, dz);
                if (!level.isLoaded(pos)) continue;

                // Scan vertically to find a surface block
                for (int dy = 2; dy >= -2; dy--) {
                    BlockPos scanPos = pos.offset(0, dy, 0);
                    BlockState state = level.getBlockState(scanPos);
                    if (!state.isAir() && ConversionMap.isConvertible(state)) {
                        BlockState converted = ConversionMap.getConversion(state);
                        level.setBlock(scanPos, converted, UPDATE_ALL);
                        break;
                    }
                }
            }
        }

        OthersideMod.LOGGER.debug("[SORE] Phase SCULK_BURST at {}", center);
    }

    // ── Phase 4: Place blocks (tick 200 / 10s) ──────────────────────────

    private void phasePlaceBlocks(ServerLevel level, BlockPos center) {
        // Place shrieker at center with can_summon=false EXPLICITLY
        BlockState shriekerState = Blocks.SCULK_SHRIEKER.defaultBlockState()
                .setValue(SculkShriekerBlock.CAN_SUMMON, false);
        level.setBlock(center, shriekerState, UPDATE_ALL);

        // Place 2 sensors at ±3 offset (east/west)
        BlockPos sensorEast = center.offset(3, 0, 0);
        BlockPos sensorWest = center.offset(-3, 0, 0);

        if (level.isLoaded(sensorEast)) {
            // Find surface
            BlockPos surfaceEast = findSurfaceForPlacement(level, sensorEast);
            if (surfaceEast != null) {
                level.setBlock(surfaceEast, Blocks.SCULK_SENSOR.defaultBlockState(), UPDATE_ALL);
            }
        }
        if (level.isLoaded(sensorWest)) {
            BlockPos surfaceWest = findSurfaceForPlacement(level, sensorWest);
            if (surfaceWest != null) {
                level.setBlock(surfaceWest, Blocks.SCULK_SENSOR.defaultBlockState(), UPDATE_ALL);
            }
        }

        OthersideMod.LOGGER.debug("[SORE] Phase PLACE_BLOCKS at {}", center);
    }

    /**
     * Find a valid surface position for block placement near the given pos.
     */
    private BlockPos findSurfaceForPlacement(ServerLevel level, BlockPos pos) {
        // Scan from the pos upward and downward for air above solid
        for (int dy = 2; dy >= -3; dy--) {
            BlockPos check = pos.offset(0, dy, 0);
            if (level.getBlockState(check).isAir() && !level.getBlockState(check.below()).isAir()) {
                return check;
            }
        }
        return pos; // fallback
    }

    // ── Phase 5: Vein growth (tick 220 / 11s) ───────────────────────────

    private void phaseVeinGrowth(ServerLevel level, BlockPos center) {
        // Start 3-6 vein growth tasks radiating outward from center
        int taskCount = 3 + random.nextInt(4); // 3-6
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST,
                // Diagonals represented as offset positions
        };

        for (int i = 0; i < taskCount; i++) {
            Direction dir = directions[i % directions.length];
            int distance = 12 + random.nextInt(20); // 12-31 blocks
            BlockPos target = center.relative(dir, distance);

            // Offset for diagonals if we have more than 4 tasks
            if (i >= 4) {
                int diagDist = 8 + random.nextInt(16);
                int offsetX = (i == 4) ? diagDist : -diagDist;
                int offsetZ = (i == 5) ? diagDist : -diagDist;
                target = center.offset(offsetX, 0, offsetZ);
            }

            VeinGrowth.startGrowthTask(level, center, target);
        }

        OthersideMod.LOGGER.debug("[SORE] Phase VEIN_GROWTH at {} — started {} growth tasks",
                center, taskCount);
    }

    // ── Phase 6: Complete (tick 240 / 12s) ──────────────────────────────

    private void phaseComplete(ServerLevel level, BlockPos center, float triggerScore) {
        // W4: spawn 2 bloom buds at the sore
        WorldbeastState.get(level).getListeningBloomManager().spawnBloomBuds(level, center, 2);

        WorldbeastState beast = WorldbeastState.get(level);
        InfectionSavedData infData = InfectionSavedData.get(level);

        // Create a new BreachData for the Sore (center as cityOrigin AND surfaceBreakout)
        BreachData soreBreach = new BreachData(center);
        soreBreach.setSurfaceBreakout(center);
        soreBreach.setActive(true);
        soreBreach.setColumnComplete(true);
        soreBreach.setColumnProgress(0);
        soreBreach.setColumnMaxHeight(0);

        // Sore breaches use a reduced budget fraction
        soreBreach.setBudgetFraction(OthersideConfig.SERVER.soreSpreadBudgetFraction.get().floatValue());

        // Seed initial frontier around the sore center
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos fp = center.offset(dx, 0, dz);
                soreBreach.getFrontier().add(fp.asLong());
            }
        }

        infData.addBreach(soreBreach);

        // Update SoreData with breach reference
        for (SoreData sore : sores) {
            if (sore.center.equals(center) && sore.breachId == null) {
                // We don't have a UUID on BreachData, so we use a generated one
                sore.breachId = UUID.randomUUID();
                break;
            }
        }

        // Director logging
        DirectorLog.log(level, "SORE_ERUPT", center,
                "score=" + String.format("%.1f", triggerScore)
                        + " mass=" + String.format("%.1f", beast.getMass())
                        + " breaches=" + infData.getBreaches().size());

        // First sore flags
        if (firstSoreEver) {
            firstSoreEver = false;
            DirectorLog.log(level, "FIRST_SORE", center,
                    "The Worldbeast's first sore eruption");
        }

        // Check FIRST_SORE_BASE — if within 48 blocks of any player-placed block
        if (isNearPlayerPlacedBlock(level, beast, center, 48)) {
            DirectorLog.log(level, "FIRST_SORE_BASE", center,
                    "Sore erupted within 48 blocks of a player-placed block");
        }

        OthersideMod.LOGGER.info("[SORE] Eruption complete at {} — breach created, score={}",
                center, String.format("%.1f", triggerScore));
    }

    /**
     * Check if any player-placed block exists within radius of the given position.
     */
    private boolean isNearPlayerPlacedBlock(ServerLevel level, WorldbeastState beast, BlockPos center, int radius) {
        // Sample a grid within the radius
        for (int dx = -radius; dx <= radius; dx += 4) {
            for (int dz = -radius; dz <= radius; dz += 4) {
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE,
                        center.getX() + dx, center.getZ() + dz);
                for (int dy = -4; dy <= 4; dy += 2) {
                    BlockPos check = new BlockPos(center.getX() + dx, surfaceY + dy, center.getZ() + dz);
                    if (beast.isPlayerPlaced(check)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // =====================================================================
    //  GETTERS
    // =====================================================================

    public List<SoreData> getSores() {
        return Collections.unmodifiableList(sores);
    }

    public int getActiveSoreCount() {
        return (int) sores.stream().filter(s -> s.active).count();
    }

    // =====================================================================
    //  NBT PERSISTENCE
    // =====================================================================

    /**
     * Save sore data into the WorldbeastState compound tag.
     */
    public void saveTo(CompoundTag tag) {
        // Sores list
        ListTag soreList = new ListTag();
        for (SoreData sore : sores) {
            soreList.add(sore.save());
        }
        tag.put("sores", soreList);

        // Noise counters
        CompoundTag noiseTag = new CompoundTag();
        for (Map.Entry<Long, Integer> entry : noiseCounters.entrySet()) {
            noiseTag.putInt(Long.toString(entry.getKey()), entry.getValue());
        }
        tag.put("noiseCounters", noiseTag);

        // Flags
        tag.putBoolean("firstSoreEver", firstSoreEver);
    }

    /**
     * Load sore data from the WorldbeastState compound tag.
     */
    public void loadFrom(CompoundTag tag) {
        sores.clear();
        noiseCounters.clear();

        // Sores list
        if (tag.contains("sores")) {
            ListTag soreList = tag.getList("sores", Tag.TAG_COMPOUND);
            for (int i = 0; i < soreList.size(); i++) {
                sores.add(SoreData.load(soreList.getCompound(i)));
            }
        }

        // Noise counters
        if (tag.contains("noiseCounters")) {
            CompoundTag noiseTag = tag.getCompound("noiseCounters");
            for (String key : noiseTag.getAllKeys()) {
                noiseCounters.put(Long.parseLong(key), noiseTag.getInt(key));
            }
        }

        // Flags
        if (tag.contains("firstSoreEver")) {
            firstSoreEver = tag.getBoolean("firstSoreEver");
        }
    }
}
