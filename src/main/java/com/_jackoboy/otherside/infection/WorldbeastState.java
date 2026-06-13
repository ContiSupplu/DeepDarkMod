package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;
import java.util.stream.Collectors;

/**
 * WorldbeastState — the organism's brain.
 * <p>
 * A {@link SavedData} singleton stored in the overworld that tracks the
 * Worldbeast's mass, hunger, acuity, per-player attention, pacing rails,
 * and player-placed block provenance.
 * <p>
 * Created as part of W1 of the Worldbeast Rework.
 */
public class WorldbeastState extends SavedData {

    private static final String DATA_NAME = "otherside_worldbeast";

    // Tag key for beast_body blocks — cached to avoid re-creation
    private static final TagKey<Block> BEAST_BODY_TAG =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("otherside", "beast_body"));

    // ── §2.1 MASS ───────────────────────────────────────────────────────────
    private float mass = 0;
    private final Set<Long> claimedChunks = new HashSet<>();
    private final Set<Long> exploredChunkSet = new HashSet<>();
    private float debugMassOverride = -1;

    // ── §2.1 Pacing rails — IN-GAME TIME ────────────────────────────────────
    private long baselineGameDay = -1;
    private float railsOverride = -1;
    private boolean railThrottled = false;

    // ── §2.2 HUNGER ─────────────────────────────────────────────────────────
    private float hunger = 0;
    private boolean sated = false;
    private int satedTicksRemaining = 0;
    private boolean hungerWasAbove50 = false;
    private float nutritionAccumulator = 0;

    // ── §2.3 ACUITY ─────────────────────────────────────────────────────────
    private float acuity = 0;
    private final Set<String> tastedBiomes = new HashSet<>();
    private final Set<String> tastedNovelties = new HashSet<>();

    // ── §2.4 ATTENTION per player ───────────────────────────────────────────
    private final Map<UUID, AttentionData> playerAttention = new HashMap<>();

    // ── Player-placed block tracking ────────────────────────────────────────
    private final Map<Long, Set<Long>> playerPlacedBlocks = new HashMap<>();

    // ── Migration ───────────────────────────────────────────────────────────
    private boolean migratedFromPhases = false;

    // ── Claim check queue ───────────────────────────────────────────────────
    private final Queue<Long> claimCheckQueue = new LinkedList<>();
    private long lastDailyLogDay = -1;

    // ── Tick bookkeeping ────────────────────────────────────────────────────
    private int tickCounter = 0; // counts 20-tick calls for mass sub-cycle

    // ── W2: Vein orders, sores, growth ───────────────────────────────────
    private final OrderManager orderManager = new OrderManager();
    private final SoreManager soreManager = new SoreManager();
    private final VeinGrowth veinGrowth = new VeinGrowth();
    private boolean veinNetworkSeeded = false;
    private boolean firstSoreEver = false;
    private boolean firstSoreNearBase = false;
    private boolean firstSeveredOrder = false;

    // =====================================================================
    //  Inner class: AttentionData
    // =====================================================================

    public static class AttentionData {
        public float attention;
        public AttentionTier currentTier = AttentionTier.UNNOTICED;
        public long lastTierChangeGameTime;
        public long lastActivityGameTime;
        public BlockPos lastKnownPos = BlockPos.ZERO;
        public boolean tierChanged = false;
        public AttentionTier previousTier = null;

        public float getAttention() { return attention; }
        public AttentionTier getCurrentTier() { return currentTier; }
        public BlockPos getLastKnownPos() { return lastKnownPos; }

        public void setLastKnownPos(BlockPos pos) { this.lastKnownPos = pos; }

        CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("attention", attention);
            tag.putInt("tier", currentTier.ordinal());
            tag.putLong("lastTierChange", lastTierChangeGameTime);
            tag.putLong("lastActivity", lastActivityGameTime);
            tag.putInt("posX", lastKnownPos.getX());
            tag.putInt("posY", lastKnownPos.getY());
            tag.putInt("posZ", lastKnownPos.getZ());
            return tag;
        }

        static AttentionData load(CompoundTag tag) {
            AttentionData d = new AttentionData();
            d.attention = tag.getFloat("attention");
            int tierOrd = tag.getInt("tier");
            d.currentTier = tierOrd >= 0 && tierOrd < AttentionTier.values().length
                    ? AttentionTier.values()[tierOrd]
                    : AttentionTier.UNNOTICED;
            d.lastTierChangeGameTime = tag.getLong("lastTierChange");
            d.lastActivityGameTime = tag.getLong("lastActivity");
            d.lastKnownPos = new BlockPos(tag.getInt("posX"), tag.getInt("posY"), tag.getInt("posZ"));
            return d;
        }
    }

    // =====================================================================
    //  Enum: AttentionTier
    // =====================================================================

    public enum AttentionTier {
        UNNOTICED(0, 25),
        HEARD(25, 55),
        KNOWN(55, 80),
        HUNTED(80, 100);

        public final int min;
        public final int max;

        AttentionTier(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public static AttentionTier getTierForValue(float value) {
            // Walk backwards so higher tiers take priority
            AttentionTier[] tiers = values();
            for (int i = tiers.length - 1; i >= 0; i--) {
                if (value >= tiers[i].min) return tiers[i];
            }
            return UNNOTICED;
        }
    }

    // =====================================================================
    //  Config helpers (forward-compatible — will use OthersideConfig fields
    //  once they are wired, falling back to defaults for now)
    // =====================================================================

    /** Base hunger-gain rate per real-minute. Config key: beast.hungerBaseRate */
    private static float cfgHungerBaseRate() {
        return OthersideConfig.SERVER.beastHungerBaseRate.get().floatValue();
    }

    /** Sated-state duration in ticks. Config key: beast.satedDurationTicks */
    private static int cfgSatedDurationTicks() {
        return OthersideConfig.SERVER.beastSatedDurationTicks.get();
    }

    /** Attention decay rate per minute. Config key: beast.attentionDecayRate */
    private static float cfgAttentionDecayRate() {
        return OthersideConfig.SERVER.beastAttentionDecayRate.get().floatValue();
    }

    /** Min distance from body for attention decay. Config key: beast.attentionDecayMinDist */
    private static int cfgAttentionDecayMinDist() {
        return OthersideConfig.SERVER.beastAttentionDecayMinDist.get();
    }

    /** Claim threshold (fraction of 16-point sample). Config key: beast.claimThreshold */
    private static float cfgClaimThreshold() {
        return OthersideConfig.SERVER.beastClaimThreshold.get().floatValue();
    }

    /**
     * Mass-rail caps parsed from config string.
     * Format: "wk1,wk2,wk3,wk4,perWeekAfter" e.g. "3,7,12,18,6"
     * Returns an int array of caps.
     */
    private static int[] cfgMassRails() {
        String railsStr = OthersideConfig.SERVER.beastMassRails.get();
        try {
            String[] parts = railsStr.split(",");
            int[] result = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Integer.parseInt(parts[i].trim());
            }
            return result;
        } catch (Exception e) {
            OthersideMod.LOGGER.warn("[BEAST] Failed to parse massRails config '{}', using defaults", railsStr);
            return new int[]{3, 7, 12, 18, 6};
        }
    }

    // =====================================================================
    //  TICK — called every 20 ticks (1 second)
    // =====================================================================

    /**
     * Master tick method. Must be called once per second (every 20 game ticks).
     */
    public void tickWorldbeast(ServerLevel level) {
        tickCounter++;

        // ── Initialise baseline day on first tick ───────────────────────
        if (baselineGameDay < 0) {
            baselineGameDay = level.getDayTime() / 24000;
            setDirty();
        }

        long gameTime = level.getDayTime();

        // ── HUNGER ──────────────────────────────────────────────────────
        if (!sated) {
            // Hunger gain: rate/60 * (1 + mass/40) * (1 + acuity/100)
            // rate is per real-minute → /60 gives per-second, and we tick every second
            float effectiveMass = getMass();
            float hungerGain = cfgHungerBaseRate() / 60.0f
                    * (1.0f + effectiveMass / 40.0f)
                    * (1.0f + acuity / 100.0f);
            hunger = Math.min(100.0f, hunger + hungerGain);

            if (hunger >= 50.0f) {
                hungerWasAbove50 = true;
            }
            // Entry into SATED: hunger drops below 20 after having been above 50
            if (hunger < 20.0f && hungerWasAbove50) {
                sated = true;
                satedTicksRemaining = cfgSatedDurationTicks();
                hungerWasAbove50 = false;
                OthersideMod.LOGGER.debug("Worldbeast entered SATED state ({} ticks)", satedTicksRemaining);
            }
        } else {
            // Decrement sated timer (20 ticks per call)
            satedTicksRemaining -= 20;
            if (satedTicksRemaining <= 0) {
                sated = false;
                satedTicksRemaining = 0;
                OthersideMod.LOGGER.debug("Worldbeast SATED state expired");
            }
        }

        // ── MASS — every 10th call (200 ticks / 10 seconds) ────────────
        if (tickCounter % 10 == 0) {
            processClaimQueue(level);
            recalculateMass();
        }

        // ── RAILS ───────────────────────────────────────────────────────
        updateRails(level);

        // ── ATTENTION DECAY per player ──────────────────────────────────
        tickAttentionDecay(level, gameTime);

        // Emit deferred ATTENTION_TIER director rows
        for (Map.Entry<UUID, AttentionData> entry : playerAttention.entrySet()) {
            AttentionData ad = entry.getValue();
            if (ad.tierChanged) {
                DirectorLog.log(level, "ATTENTION_TIER", ad.lastKnownPos,
                        "player=" + entry.getKey() + " from=" + (ad.previousTier != null ? ad.previousTier.name() : "NONE")
                        + " to=" + ad.currentTier.name() + " attn=" + String.format("%.1f", ad.attention));
                ad.tierChanged = false;
                ad.previousTier = null;
            }
        }

        // ── DAILY LOG ───────────────────────────────────────────────────
        long currentDay = gameTime / 24000;
        if (currentDay != lastDailyLogDay) {
            lastDailyLogDay = currentDay;
            emitDailyLog(level);
            applyDailyAttentionGain(level);
        }

        // ── W2: ORDER MANAGER TICK ───────────────────────────────────────
        orderManager.tickOrders(level, this, gameTime);
        orderManager.checkSurgeIssuance(level, this);

        // ── W2: SORE MANAGER TICK (scoring runs internally on its own timer) ──
        soreManager.tick(level);

        // ── W2: VEIN GROWTH TICK ────────────────────────────────────────
        // Seed initial network on first W2 load — retry until at least one breach is ready
        if (!veinNetworkSeeded) {
            InfectionSavedData infData = InfectionSavedData.get(level);
            veinGrowth.seedInitialNetwork(level, infData, this);
            // Only mark seeded if tasks were actually created
            if (veinGrowth.getActiveTaskCount() > 0) {
                veinNetworkSeeded = true;
            }
        }
        veinGrowth.tick(level);

        setDirty();
    }

    // =====================================================================
    //  MASS helpers
    // =====================================================================

    private void processClaimQueue(ServerLevel level) {
        int processed = 0;
        while (!claimCheckQueue.isEmpty() && processed < 20) {
            long chunkPosLong = claimCheckQueue.poll();
            processed++;

            ChunkPos cp = new ChunkPos(chunkPosLong);
            int chunkX = cp.x;
            int chunkZ = cp.z;

            // Only process if the chunk is loaded
            if (!level.hasChunk(chunkX, chunkZ)) {
                // Re-enqueue for later
                claimCheckQueue.add(chunkPosLong);
                continue;
            }

            LevelChunk chunk = level.getChunk(chunkX, chunkZ);
            Heightmap heightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE);

            // 16-point sample: 4×4 grid within the chunk
            int beastBodyCount = 0;
            int sampleCount = 0;
            for (int sx = 2; sx < 16; sx += 4) {
                for (int sz = 2; sz < 16; sz += 4) {
                    int surfaceY = heightmap.getFirstAvailable(sx, sz);
                    boolean found = false;
                    // Scan down up to 6 blocks for first solid
                    for (int dy = 0; dy < 6; dy++) {
                        int y = surfaceY - 1 - dy;
                        BlockPos samplePos = new BlockPos(cp.getMinBlockX() + sx, y, cp.getMinBlockZ() + sz);
                        BlockState state = level.getBlockState(samplePos);
                        if (!state.isAir()) {
                            if (state.is(BEAST_BODY_TAG)) {
                                beastBodyCount++;
                            }
                            found = true;
                            break;
                        }
                    }
                    sampleCount++;
                }
            }

            // >= 60% of sample points are beast_body → claimed
            if (sampleCount > 0 && (float) beastBodyCount / sampleCount >= cfgClaimThreshold()) {
                claimedChunks.add(chunkPosLong);
            } else {
                claimedChunks.remove(chunkPosLong);
            }
        }
    }

    private void recalculateMass() {
        float denominator = Math.max(500.0f, exploredChunkSet.size());
        mass = claimedChunks.size() / denominator * 100.0f;
        mass = Math.min(100.0f, mass);
    }

    // =====================================================================
    //  RAILS helpers
    // =====================================================================

    private void updateRails(ServerLevel level) {
        if (railsOverride >= 0) {
            railThrottled = getMass() > railsOverride;
            return;
        }

        long currentDay = level.getDayTime() / 24000;
        this.currentWeek = (int) ((currentDay - baselineGameDay) / 7);

        float cap = computeRailCap();
        railThrottled = getMass() > cap;
    }

    // =====================================================================
    //  ATTENTION
    // =====================================================================

    /**
     * Add attention to a specific player.
     *
     * @param playerId the player's UUID
     * @param amount   attention points to add (can be negative for manual manipulation)
     * @param gameTime the current server game time (level.getDayTime())
     */
    public void addAttention(UUID playerId, float amount, long gameTime) {
        AttentionData data = playerAttention.computeIfAbsent(playerId, k -> {
            AttentionData d = new AttentionData();
            d.lastTierChangeGameTime = gameTime;
            return d;
        });

        data.attention = Math.max(0, Math.min(100, data.attention + amount));
        data.lastActivityGameTime = gameTime;

        // Tier transition check
        AttentionTier newTier = AttentionTier.getTierForValue(data.attention);
        if (newTier != data.currentTier) {
            data.tierChanged = true;
            data.previousTier = data.currentTier;
            data.currentTier = newTier;
            data.lastTierChangeGameTime = gameTime;
            OthersideMod.LOGGER.debug("Player {} attention tier changed to {} ({})",
                    playerId, newTier.name(), data.attention);
        }

        setDirty();
    }

    private void tickAttentionDecay(ServerLevel level, long gameTime) {
        for (Map.Entry<UUID, AttentionData> entry : playerAttention.entrySet()) {
            AttentionData data = entry.getValue();

            // "Quiet" = 60 seconds (1200 ticks) with no gain
            boolean isQuiet = (gameTime - data.lastActivityGameTime) > 1200;
            if (!isQuiet) continue;

            // Check distance to nearest claimed chunk
            BlockPos playerPos = data.lastKnownPos;
            boolean farFromClaimed = isPositionFarFromClaimed(playerPos, cfgAttentionDecayMinDist());

            if (!farFromClaimed) continue;

            // Decay: configurable rate per 60 seconds → per-second = -rate/60
            float decayPerSecond = -cfgAttentionDecayRate() / 60.0f;
            data.attention = Math.max(0, data.attention + decayPerSecond);

            // Tier floor: cannot drop more than 1 tier per 2 in-game days (48000 ticks)
            AttentionTier newTier = AttentionTier.getTierForValue(data.attention);
            if (newTier.ordinal() < data.currentTier.ordinal()) {
                long ticksSinceLastChange = gameTime - data.lastTierChangeGameTime;
                if (ticksSinceLastChange < 48000) {
                    // Clamp attention to current tier floor
                    data.attention = Math.max(data.attention, data.currentTier.min);
                    newTier = data.currentTier;
                }
            }

            if (newTier != data.currentTier) {
                data.tierChanged = true;
                data.previousTier = data.currentTier;
                data.currentTier = newTier;
                data.lastTierChangeGameTime = gameTime;
            }
        }
    }

    /**
     * Check if a position is more than {@code radius} blocks from any claimed chunk.
     */
    private boolean isPositionFarFromClaimed(BlockPos pos, int radius) {
        int chunkRadius = (radius >> 4) + 1;
        int playerChunkX = pos.getX() >> 4;
        int playerChunkZ = pos.getZ() >> 4;

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                long candidate = ChunkPos.asLong(playerChunkX + dx, playerChunkZ + dz);
                if (claimedChunks.contains(candidate)) {
                    // Check actual block distance (chunk centre to player pos)
                    int cx = ((playerChunkX + dx) << 4) + 8;
                    int cz = ((playerChunkZ + dz) << 4) + 8;
                    double distSq = (pos.getX() - cx) * (pos.getX() - cx)
                            + (pos.getZ() - cz) * (pos.getZ() - cz);
                    if (distSq <= (long) radius * radius) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // =====================================================================
    //  DAILY LOG & ATTENTION GAIN
    // =====================================================================

    private void emitDailyLog(ServerLevel level) {
        String detail = String.format(
                "mass=%.1f hunger=%.1f acuity=%.1f sated=%b claimed=%d explored=%d",
                getMass(), hunger, acuity, sated, claimedChunks.size(), exploredChunkSet.size());
        DirectorLog.log(level, "BEAST_DAILY", BlockPos.ZERO, detail);
    }

    /**
     * +3 attention to each online player standing in claimed land.
     */
    private void applyDailyAttentionGain(ServerLevel level) {
        long gameTime = level.getDayTime();
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            long playerChunk = ChunkPos.asLong(player.blockPosition().getX() >> 4,
                    player.blockPosition().getZ() >> 4);
            if (claimedChunks.contains(playerChunk)) {
                addAttention(player.getUUID(), 3.0f, gameTime);
            }
            // Also update last known position
            AttentionData data = playerAttention.get(player.getUUID());
            if (data != null) {
                data.lastKnownPos = player.blockPosition();
            }
        }
    }

    // =====================================================================
    //  NUTRITION / HUNGER (called by SpreadEngine)
    // =====================================================================

    /**
     * Feed the beast nutrition from a block conversion.
     * Feeding gently slows hunger gain — coefficient tuned so hunger still trends
     * upward overall (reduction ~0.002/s vs gain ~0.003/s at low mass).
     */
    public void feedNutrition(float amount) {
        nutritionAccumulator += amount;
        hunger -= amount * 0.0002f;
        hunger = Math.max(0.0f, hunger);
        setDirty();
    }

    // =====================================================================
    //  ACUITY
    // =====================================================================

    /**
     * Record a biome tasting. If novel, acuity increments by 1.
     */
    public void tasteBiome(String biomeId) {
        if (tastedBiomes.add(biomeId)) {
            acuity = Math.min(100.0f, acuity + 1.0f);
            OthersideMod.LOGGER.debug("Worldbeast tasted new biome: {} (acuity now {})", biomeId, acuity);
            setDirty();
        }
    }

    /**
     * Record a novelty tasting (non-biome discovery). If novel, acuity increments by 1.
     */
    public void tasteNovelty(String noveltyId) {
        if (tastedNovelties.add(noveltyId)) {
            acuity = Math.min(100.0f, acuity + 1.0f);
            OthersideMod.LOGGER.debug("Worldbeast tasted novelty: {} (acuity now {})", noveltyId, acuity);
            setDirty();
        }
    }

    // =====================================================================
    //  PLAYER-PLACED BLOCK TRACKING
    // =====================================================================

    public void recordPlayerPlacement(BlockPos pos) {
        long chunkKey = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
        playerPlacedBlocks.computeIfAbsent(chunkKey, k -> new HashSet<>()).add(pos.asLong());
        setDirty();
    }

    public boolean isPlayerPlaced(BlockPos pos) {
        long chunkKey = ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4);
        Set<Long> blocks = playerPlacedBlocks.get(chunkKey);
        return blocks != null && blocks.contains(pos.asLong());
    }

    // =====================================================================
    //  EXPLORED / CLAIM QUEUE
    // =====================================================================

    public void recordExploredChunk(long chunkPos) {
        if (exploredChunkSet.add(chunkPos)) {
            setDirty();
        }
    }

    public void enqueueClaimCheck(long chunkPos) {
        claimCheckQueue.add(chunkPos);
        setDirty();
    }

    // =====================================================================
    //  MIGRATION from legacy phases
    // =====================================================================

    /**
     * Seed worldbeast stats from the old phase-based system.
     * Should be called once when migrating a world that used InfectionPhase.
     */
    public void migrateFromPhases(int phaseNumber, ServerLevel level) {
        if (migratedFromPhases) return;
        migratedFromPhases = true;

        // Seed hunger: higher phases → more baseline hunger
        switch (phaseNumber) {
            case 1 -> hunger = 5;
            case 2 -> hunger = 15;
            case 3 -> hunger = 30;
            case 4 -> hunger = 50;
            default -> hunger = 0;
        }

        // Seed acuity: rough estimate from phase progression
        acuity = Math.min(100, phaseNumber * 10.0f);

        OthersideMod.LOGGER.info("Migrated WorldbeastState from phase {} → hunger={}, acuity={}",
                phaseNumber, hunger, acuity);
        DirectorLog.log(level, "BEAST_MIGRATED", BlockPos.ZERO,
                "from_phase=" + phaseNumber + " hunger=" + hunger + " acuity=" + acuity);

        // Enqueue all currently claimed chunks for re-validation
        for (long chunk : claimedChunks) {
            claimCheckQueue.add(chunk);
        }

        setDirty();
    }

    // =====================================================================
    //  GETTERS
    // =====================================================================

    public float getMass() {
        return debugMassOverride >= 0 ? debugMassOverride : mass;
    }

    public float getRawMass() { return mass; }

    public boolean isRailThrottled() { return railThrottled; }

    public boolean isSated() { return sated; }

    public float getHunger() { return hunger; }

    public float getAcuity() { return acuity; }

    public float getNutritionAccumulator() { return nutritionAccumulator; }

    public Set<Long> getClaimedChunks() { return Collections.unmodifiableSet(claimedChunks); }

    public Set<Long> getExploredChunkSet() { return Collections.unmodifiableSet(exploredChunkSet); }

    public Map<UUID, AttentionData> getPlayerAttention() { return Collections.unmodifiableMap(playerAttention); }

    public boolean isMigratedFromPhases() { return migratedFromPhases; }

    public float getDebugMassOverride() { return debugMassOverride; }

    public void setDebugMassOverride(float value) {
        this.debugMassOverride = value;
        setDirty();
    }

    public float getRailsOverride() { return railsOverride; }

    public void setRailsOverride(float value) {
        this.railsOverride = value;
        setDirty();
    }

    public int getSatedTicksRemaining() { return satedTicksRemaining; }

    public long getBaselineGameDay() { return baselineGameDay; }

    public int getClaimedChunkCount() { return claimedChunks.size(); }

    public int getExploredChunkCount() { return exploredChunkSet.size(); }

    public void setHunger(float value) {
        this.hunger = Math.max(0, Math.min(100, value));
        // If set below 20 and was above 50, trigger SATED
        if (this.hunger < 20 && hungerWasAbove50 && !sated) {
            sated = true;
            satedTicksRemaining = cfgSatedDurationTicks();
            hungerWasAbove50 = false;
        }
        setDirty();
    }

    public void setAcuity(float value) {
        this.acuity = Math.max(0, Math.min(100, value));
        setDirty();
    }

    public AttentionData getAttentionData(UUID playerId) {
        return playerAttention.get(playerId);
    }

    public float getPlayerAttention(UUID playerId) {
        AttentionData data = playerAttention.get(playerId);
        return data != null ? data.attention : 0.0f;
    }

    /**
     * Returns the current mass rail cap based on in-game weeks and config table.
     */
    public float getRailCap() {
        if (railsOverride >= 0) return railsOverride;
        return computeRailCap();
    }

    private float computeRailCap() {
        int[] rails = cfgMassRails();
        if (rails.length == 0) return 100.0f;
        int week = currentWeek;
        if (week < 0) week = 0;
        if (week < rails.length - 1) {
            return rails[week];
        }
        // After fixed weeks: last value is per-week increment
        int lastFixed = rails[rails.length - 2];
        int increment = rails[rails.length - 1];
        int extraWeeks = week - (rails.length - 2);
        return lastFixed + increment * extraWeeks;
    }

    private int currentWeek = 0; // updated in tickWorldbeast

    /**
     * Returns gravity targets for all players with attention > 0.
     * Each entry is a pair of (BlockPos lastKnownPos, float attention).
     */
    public List<GravityTarget> getPlayerGravityTargets() {
        List<GravityTarget> targets = new ArrayList<>();
        for (Map.Entry<UUID, AttentionData> entry : playerAttention.entrySet()) {
            AttentionData data = entry.getValue();
            if (data.attention > 0) {
                targets.add(new GravityTarget(data.lastKnownPos, data.attention));
            }
        }
        return targets;
    }

    public record GravityTarget(BlockPos pos, float attention) {}

    // =====================================================================
    //  W2 ACCESSORS
    // =====================================================================

    public OrderManager getOrderManager() { return orderManager; }
    public SoreManager getSoreManager() { return soreManager; }
    public VeinGrowth getVeinGrowth() { return veinGrowth; }

    /** Regional boosts from SURGE/RETALIATION orders — consulted by SpreadEngine. */
    public List<OrderManager.RegionalBoost> getRegionalBoosts() {
        return orderManager.getRegionalBoosts();
    }


    // FIRST_* flags (W2)
    public boolean isFirstSoreEver() { return firstSoreEver; }
    public void setFirstSoreEver(boolean v) { firstSoreEver = v; setDirty(); }
    public boolean isFirstSoreNearBase() { return firstSoreNearBase; }
    public void setFirstSoreNearBase(boolean v) { firstSoreNearBase = v; setDirty(); }
    public boolean isFirstSeveredOrder() { return firstSeveredOrder; }
    public void setFirstSeveredOrder(boolean v) { firstSeveredOrder = v; setDirty(); }

    // =====================================================================
    //  NBT SAVE
    // =====================================================================

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        // Mass
        tag.putFloat("mass", mass);
        tag.put("claimedChunks", new LongArrayTag(claimedChunks.stream().mapToLong(Long::longValue).toArray()));
        tag.put("exploredChunks", new LongArrayTag(exploredChunkSet.stream().mapToLong(Long::longValue).toArray()));
        tag.putFloat("debugMassOverride", debugMassOverride);

        // Rails
        tag.putLong("baselineGameDay", baselineGameDay);
        tag.putFloat("railsOverride", railsOverride);
        tag.putBoolean("railThrottled", railThrottled);

        // Hunger
        tag.putFloat("hunger", hunger);
        tag.putBoolean("sated", sated);
        tag.putInt("satedTicksRemaining", satedTicksRemaining);
        tag.putBoolean("hungerWasAbove50", hungerWasAbove50);
        tag.putFloat("nutritionAccumulator", nutritionAccumulator);

        // Acuity
        tag.putFloat("acuity", acuity);
        ListTag biomeList = new ListTag();
        for (String b : tastedBiomes) biomeList.add(StringTag.valueOf(b));
        tag.put("tastedBiomes", biomeList);
        ListTag noveltyList = new ListTag();
        for (String n : tastedNovelties) noveltyList.add(StringTag.valueOf(n));
        tag.put("tastedNovelties", noveltyList);

        // Attention
        ListTag attentionList = new ListTag();
        for (Map.Entry<UUID, AttentionData> entry : playerAttention.entrySet()) {
            CompoundTag playerTag = entry.getValue().save();
            playerTag.putUUID("playerId", entry.getKey());
            attentionList.add(playerTag);
        }
        tag.put("playerAttention", attentionList);

        // Player-placed blocks
        CompoundTag placedTag = new CompoundTag();
        for (Map.Entry<Long, Set<Long>> entry : playerPlacedBlocks.entrySet()) {
            long[] positions = entry.getValue().stream().mapToLong(Long::longValue).toArray();
            placedTag.put(Long.toString(entry.getKey()), new LongArrayTag(positions));
        }
        tag.put("playerPlacedBlocks", placedTag);

        // Migration
        tag.putBoolean("migratedFromPhases", migratedFromPhases);

        // Claim queue
        tag.put("claimCheckQueue",
                new LongArrayTag(claimCheckQueue.stream().mapToLong(Long::longValue).toArray()));
        tag.putLong("lastDailyLogDay", lastDailyLogDay);

        // W2: orders, sores, growth
        tag.put("orderManager", orderManager.save());
        CompoundTag soreTag = new CompoundTag();
        soreManager.saveTo(soreTag);
        tag.put("soreManager", soreTag);
        CompoundTag veinTag = new CompoundTag();
        veinGrowth.saveTo(veinTag);
        tag.put("veinGrowth", veinTag);
        tag.putBoolean("veinNetworkSeeded", veinNetworkSeeded);
        tag.putBoolean("firstSoreEver", firstSoreEver);
        tag.putBoolean("firstSoreNearBase", firstSoreNearBase);
        tag.putBoolean("firstSeveredOrder", firstSeveredOrder);

        return tag;
    }

    // =====================================================================
    //  NBT LOAD
    // =====================================================================

    public static WorldbeastState load(CompoundTag tag, HolderLookup.Provider registries) {
        WorldbeastState state = new WorldbeastState();

        // Mass
        state.mass = tag.getFloat("mass");
        if (tag.contains("claimedChunks")) {
            for (long l : tag.getLongArray("claimedChunks")) state.claimedChunks.add(l);
        }
        if (tag.contains("exploredChunks")) {
            for (long l : tag.getLongArray("exploredChunks")) state.exploredChunkSet.add(l);
        }
        state.debugMassOverride = tag.contains("debugMassOverride") ? tag.getFloat("debugMassOverride") : -1;

        // Rails
        state.baselineGameDay = tag.contains("baselineGameDay") ? tag.getLong("baselineGameDay") : -1;
        state.railsOverride = tag.contains("railsOverride") ? tag.getFloat("railsOverride") : -1;
        state.railThrottled = tag.getBoolean("railThrottled");

        // Hunger
        state.hunger = tag.getFloat("hunger");
        state.sated = tag.getBoolean("sated");
        state.satedTicksRemaining = tag.getInt("satedTicksRemaining");
        state.hungerWasAbove50 = tag.getBoolean("hungerWasAbove50");
        state.nutritionAccumulator = tag.getFloat("nutritionAccumulator");

        // Acuity
        state.acuity = tag.getFloat("acuity");
        if (tag.contains("tastedBiomes")) {
            ListTag biomes = tag.getList("tastedBiomes", Tag.TAG_STRING);
            for (int i = 0; i < biomes.size(); i++) {
                state.tastedBiomes.add(biomes.getString(i));
            }
        }
        if (tag.contains("tastedNovelties")) {
            ListTag novelties = tag.getList("tastedNovelties", Tag.TAG_STRING);
            for (int i = 0; i < novelties.size(); i++) {
                state.tastedNovelties.add(novelties.getString(i));
            }
        }

        // Attention
        if (tag.contains("playerAttention")) {
            ListTag attList = tag.getList("playerAttention", Tag.TAG_COMPOUND);
            for (int i = 0; i < attList.size(); i++) {
                CompoundTag playerTag = attList.getCompound(i);
                UUID id = playerTag.getUUID("playerId");
                AttentionData data = AttentionData.load(playerTag);
                state.playerAttention.put(id, data);
            }
        }

        // Player-placed blocks
        if (tag.contains("playerPlacedBlocks")) {
            CompoundTag placedTag = tag.getCompound("playerPlacedBlocks");
            for (String key : placedTag.getAllKeys()) {
                long chunkKey = Long.parseLong(key);
                Set<Long> positions = new HashSet<>();
                for (long l : placedTag.getLongArray(key)) positions.add(l);
                state.playerPlacedBlocks.put(chunkKey, positions);
            }
        }

        // Migration
        state.migratedFromPhases = tag.getBoolean("migratedFromPhases");

        // Claim queue
        if (tag.contains("claimCheckQueue")) {
            for (long l : tag.getLongArray("claimCheckQueue")) state.claimCheckQueue.add(l);
        }
        state.lastDailyLogDay = tag.contains("lastDailyLogDay") ? tag.getLong("lastDailyLogDay") : -1;

        // W2: orders, sores, growth
        if (tag.contains("orderManager")) {
            state.orderManager.load(tag.getCompound("orderManager"));
        }
        if (tag.contains("soreManager")) {
            state.soreManager.loadFrom(tag.getCompound("soreManager"));
        }
        if (tag.contains("veinGrowth")) {
            state.veinGrowth.loadFrom(tag.getCompound("veinGrowth"));
        }
        state.veinNetworkSeeded = tag.getBoolean("veinNetworkSeeded");
        state.firstSoreEver = tag.getBoolean("firstSoreEver");
        state.firstSoreNearBase = tag.getBoolean("firstSoreNearBase");
        state.firstSeveredOrder = tag.getBoolean("firstSeveredOrder");

        return state;
    }

    // =====================================================================
    //  STATIC ACCESSOR
    // =====================================================================

    /**
     * Get the global WorldbeastState from the overworld data storage.
     * Can be called from any dimension — always resolves to the overworld.
     */
    public static WorldbeastState get(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(
                new Factory<>(WorldbeastState::new, WorldbeastState::load),
                DATA_NAME
        );
    }
}
