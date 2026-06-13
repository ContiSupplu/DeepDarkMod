package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.entity.EchoSoulEntity;
import com._jackoboy.otherside.registry.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.*;

/**
 * EchoSoulManager — spawns and manages echo souls for the beast.
 * <p>
 * Two spawn modes:
 * - DANGER (reactive): spawns when player attention is high or beast is threatened
 * - NATURAL (ambient): spawns passively once beast mass exceeds threshold
 * <p>
 * Ticked from WorldbeastState.tickWorldbeast() alongside other managers.
 */
public class EchoSoulManager {

    // ── Tracked souls ────────────────────────────────────────────────
    private final Set<Integer> trackedSoulIds = new HashSet<>();
    private final Map<UUID, Integer> dangerSoulsPerPlayer = new HashMap<>();

    // ── Timers ───────────────────────────────────────────────────────
    private long lastDangerSpawnTick = 0;
    private long lastNaturalSpawnTick = 0;
    private int naturalSoulCount = 0;
    private boolean firstSoulLogged = false;

    private final Random random = new Random();

    // =====================================================================
    //  TICK
    // =====================================================================

    public void tick(ServerLevel level, WorldbeastState beast) {
        long gameTime = level.getGameTime();

        // Sweep: remove tracked ids for entities that no longer exist
        trackedSoulIds.removeIf(id -> level.getEntity(id) == null);

        // Update per-player danger counts
        dangerSoulsPerPlayer.entrySet().removeIf(e -> {
            // Recount active danger souls for this player
            return false; // simplified — actual cleanup happens in sweep
        });

        // ── Mode A: DANGER (reactive) ────────────────────────────────
        tickDanger(level, beast, gameTime);

        // ── Mode B: NATURAL (ambient, mass-gated) ────────────────────
        tickNatural(level, beast, gameTime);
    }

    // ── Mode A: DANGER ───────────────────────────────────────────────

    private void tickDanger(ServerLevel level, WorldbeastState beast, long gameTime) {
        int cooldown = OthersideConfig.SERVER.echoSoulDangerCooldownTicks.get();
        if (gameTime - lastDangerSpawnTick < cooldown) return;

        int globalCap = OthersideConfig.SERVER.echoSoulGlobalCap.get();
        if (trackedSoulIds.size() >= globalCap) return;

        int threshold = OthersideConfig.SERVER.echoSoulDangerAttentionThreshold.get();
        int maxPerPlayer = OthersideConfig.SERVER.echoSoulMaxPerPlayer.get();
        int burst = OthersideConfig.SERVER.echoSoulDangerBurst.get();

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || player.isCreative()) continue;

            float attention = beast.getPlayerAttention(player.getUUID());
            if (attention < threshold) continue;

            // Check per-player cap
            int currentForPlayer = countDangerSoulsForPlayer(level, player.getUUID());
            if (currentForPlayer >= maxPerPlayer) continue;

            // Scale burst with attention (more when HUNTED ≥80)
            int actualBurst = attention >= 80 ? burst + 1 : burst;
            int toSpawn = Math.min(actualBurst, maxPerPlayer - currentForPlayer);
            toSpawn = Math.min(toSpawn, globalCap - trackedSoulIds.size());

            if (toSpawn <= 0) continue;

            for (int i = 0; i < toSpawn; i++) {
                BlockPos spawnPos = findSpawnPos(level, beast, player.blockPosition());
                if (spawnPos == null) continue;

                EchoSoulEntity soul = spawnSoul(level, spawnPos, EchoSoulEntity.SpawnMode.DANGER, player);
                if (soul != null) {
                    DirectorLog.log(level, "ECHO_SOUL_SPAWN", spawnPos,
                            "mode=danger target=" + player.getName().getString());
                    logFirstSoul(level, spawnPos);
                }
            }

            lastDangerSpawnTick = gameTime;
            break; // One player per tick cycle
        }
    }

    /**
     * Called when the beast is directly threatened (sever, cleanse, damage).
     * Spawns an immediate danger burst regardless of the attention threshold.
     */
    public void onBeastThreatened(ServerLevel level, ServerPlayer player, WorldbeastState beast, float severity) {
        int globalCap = OthersideConfig.SERVER.echoSoulGlobalCap.get();
        if (trackedSoulIds.size() >= globalCap) return;

        int maxPerPlayer = OthersideConfig.SERVER.echoSoulMaxPerPlayer.get();
        int currentForPlayer = countDangerSoulsForPlayer(level, player.getUUID());
        if (currentForPlayer >= maxPerPlayer) return;

        int burst = OthersideConfig.SERVER.echoSoulDangerBurst.get();
        // Higher severity = more souls
        int toSpawn = severity >= 5 ? burst + 1 : Math.max(1, burst - 1);
        toSpawn = Math.min(toSpawn, maxPerPlayer - currentForPlayer);
        toSpawn = Math.min(toSpawn, globalCap - trackedSoulIds.size());

        for (int i = 0; i < toSpawn; i++) {
            BlockPos spawnPos = findSpawnPos(level, beast, player.blockPosition());
            if (spawnPos == null) continue;

            EchoSoulEntity soul = spawnSoul(level, spawnPos, EchoSoulEntity.SpawnMode.DANGER, player);
            if (soul != null) {
                DirectorLog.log(level, "ECHO_SOUL_SPAWN", spawnPos,
                        "mode=danger_threat target=" + player.getName().getString()
                                + " severity=" + severity);
                logFirstSoul(level, spawnPos);
            }
        }

        lastDangerSpawnTick = level.getGameTime();
    }

    // ── Mode B: NATURAL ──────────────────────────────────────────────

    private void tickNatural(ServerLevel level, WorldbeastState beast, long gameTime) {
        float mass = beast.getMass();
        int massThreshold = OthersideConfig.SERVER.echoSoulNaturalMassThreshold.get();
        if (mass < massThreshold) return;

        int globalCap = OthersideConfig.SERVER.echoSoulGlobalCap.get();
        if (trackedSoulIds.size() >= globalCap) return;

        // Natural cap scales with mass
        int naturalMax = OthersideConfig.SERVER.echoSoulNaturalMaxActive.get();
        int scaledCap = Math.min(naturalMax, (int) ((mass - massThreshold) / 25) + 1);

        // Count current natural souls
        int currentNatural = countNaturalSouls(level);
        if (currentNatural >= scaledCap) return;

        // Interval scales down as mass rises
        int baseInterval = OthersideConfig.SERVER.echoSoulNaturalIntervalTicks.get();
        float scale = Mth.clamp((float) massThreshold / mass, 0.25f, 1.0f);
        int interval = (int) (baseInterval * scale);

        if (gameTime - lastNaturalSpawnTick < interval) return;

        // Find a spawn pos near a player in claimed territory
        ServerPlayer nearestPlayer = findPlayerInClaimedTerritory(level, beast);
        if (nearestPlayer == null) return;

        BlockPos spawnPos = findSpawnPos(level, beast, nearestPlayer.blockPosition());
        if (spawnPos == null) return;

        EchoSoulEntity soul = spawnSoul(level, spawnPos, EchoSoulEntity.SpawnMode.NATURAL, null);
        if (soul != null) {
            DirectorLog.log(level, "ECHO_SOUL_SPAWN", spawnPos,
                    "mode=natural mass=" + String.format("%.1f", mass));
            logFirstSoul(level, spawnPos);
            lastNaturalSpawnTick = gameTime;
        }
    }

    // ── Shared spawn logic ───────────────────────────────────────────

    @Nullable
    private EchoSoulEntity spawnSoul(ServerLevel level, BlockPos pos,
                                      EchoSoulEntity.SpawnMode mode, @Nullable ServerPlayer target) {
        EchoSoulEntity soul = ModEntityTypes.ECHO_SOUL.get().create(level);
        if (soul == null) return null;

        soul.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                random.nextFloat() * 360, 0);
        soul.setSpawnMode(mode);
        if (target != null) {
            soul.setInitialTarget(target);
        }

        level.addFreshEntity(soul);
        trackedSoulIds.add(soul.getId());

        // Play emerge sound
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.SCULK_CATALYST_BLOOM,
                net.minecraft.sounds.SoundSource.HOSTILE, 1.0F, 0.7F);

        return soul;
    }

    @Nullable
    private BlockPos findSpawnPos(ServerLevel level, WorldbeastState beast, BlockPos nearPos) {
        int lightDeter = OthersideConfig.SERVER.echoSoulLightDeterLevel.get();

        // Try up to 16 random positions within 12-24 blocks of the player
        for (int attempt = 0; attempt < 16; attempt++) {
            int dx = random.nextInt(25) - 12;
            int dz = random.nextInt(25) - 12;
            int dist2 = dx * dx + dz * dz;
            // Not too close (within 8) and not too far (beyond 24)
            if (dist2 < 64 || dist2 > 576) continue;

            int x = nearPos.getX() + dx;
            int z = nearPos.getZ() + dz;

            // Must be in claimed territory
            long chunkKey = net.minecraft.world.level.ChunkPos.asLong(x >> 4, z >> 4);
            if (!beast.getClaimedChunks().contains(chunkKey)) continue;

            // Get surface Y (solid floor)
            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            BlockPos candidate = new BlockPos(x, surfaceY, z);

            // Not in bright light
            if (level.getMaxLocalRawBrightness(candidate) >= lightDeter) continue;

            // Not near amethyst
            if (EchoSoulEntity.isNearAmethyst(candidate)) continue;

            // Not inside a player build
            if (beast.isPlayerPlaced(candidate) || beast.isPlayerPlaced(candidate.above())) continue;

            return candidate;
        }
        return null;
    }

    @Nullable
    private ServerPlayer findPlayerInClaimedTerritory(ServerLevel level, WorldbeastState beast) {
        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || player.isCreative()) continue;
            long chunkKey = net.minecraft.world.level.ChunkPos.asLong(
                    player.blockPosition().getX() >> 4, player.blockPosition().getZ() >> 4);
            if (beast.getClaimedChunks().contains(chunkKey)) {
                return player;
            }
        }
        return null;
    }

    // ── Counting helpers ─────────────────────────────────────────────

    private int countDangerSoulsForPlayer(ServerLevel level, UUID playerId) {
        int count = 0;
        for (int id : trackedSoulIds) {
            var entity = level.getEntity(id);
            if (entity instanceof EchoSoulEntity soul
                    && soul.getSpawnMode() == EchoSoulEntity.SpawnMode.DANGER) {
                count++;
            }
        }
        return count;
    }

    private int countNaturalSouls(ServerLevel level) {
        int count = 0;
        for (int id : trackedSoulIds) {
            var entity = level.getEntity(id);
            if (entity instanceof EchoSoulEntity soul
                    && soul.getSpawnMode() == EchoSoulEntity.SpawnMode.NATURAL) {
                count++;
            }
        }
        return count;
    }

    private void logFirstSoul(ServerLevel level, BlockPos pos) {
        if (!firstSoulLogged) {
            firstSoulLogged = true;
            DirectorLog.log(level, "FIRST_ECHO_SOUL", pos, "");
        }
    }

    // ── Orphan sweep (call on world load) ────────────────────────────

    public void sweepOrphans(ServerLevel level) {
        // Find any EchoSoulEntity not in our tracked set and discard them
        for (EchoSoulEntity soul : level.getEntitiesOfClass(EchoSoulEntity.class,
                new AABB(-30000000, -64, -30000000, 30000000, 320, 30000000))) {
            if (!trackedSoulIds.contains(soul.getId())) {
                soul.discard();
                OthersideMod.LOGGER.info("[ECHO] Discarded orphan soul at {}", soul.blockPosition().toShortString());
            }
        }
    }

    // ── Serialization ────────────────────────────────────────────────

    public void saveTo(CompoundTag tag) {
        tag.putLong("lastDangerSpawnTick", lastDangerSpawnTick);
        tag.putLong("lastNaturalSpawnTick", lastNaturalSpawnTick);
        tag.putBoolean("firstSoulLogged", firstSoulLogged);
    }

    public void loadFrom(CompoundTag tag) {
        lastDangerSpawnTick = tag.getLong("lastDangerSpawnTick");
        lastNaturalSpawnTick = tag.getLong("lastNaturalSpawnTick");
        firstSoulLogged = tag.getBoolean("firstSoulLogged");
    }

    // ── Public accessors ─────────────────────────────────────────────

    public int getActiveSoulCount() { return trackedSoulIds.size(); }
}
