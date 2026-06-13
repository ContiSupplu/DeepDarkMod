package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.entity.ListeningBloomEntity;
import com._jackoboy.otherside.registry.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.*;

/**
 * ListeningBloomManager — spawns, caps, and cleans up Listening Blooms.
 *
 * Fix 1 from review: NO entity ID tracking. The manager's real jobs are spawn timing,
 * cap enforcement (via live entity scan), and cleanup. Persists only the spawn timer.
 */
public class ListeningBloomManager {

    private long lastSpawnTick = 0;
    private boolean firstBloomLogged = false;

    private final Random random = new Random();

    // ── Tick (called from WorldbeastState.tickWorldbeast) ────────────

    public void tick(ServerLevel level, WorldbeastState beast) {
        long gameTime = level.getGameTime();

        // Natural spawning on claimed sculk near players
        int interval = OthersideConfig.SERVER.bloomSpawnIntervalTicks.get();
        if (gameTime - lastSpawnTick >= interval) {
            lastSpawnTick = gameTime;
            tickNaturalSpawn(level, beast);
        }

        // Periodic cleanup: remove blooms on unclaimed ground (every ~30s)
        if (gameTime % 600 == 0) {
            tickCleanup(level, beast);
        }
    }

    // ── Natural spawning ─────────────────────────────────────────────

    private void tickNaturalSpawn(ServerLevel level, WorldbeastState beast) {
        if (beast.getMass() < 30) return; // don't spawn blooms before the beast has a foothold

        int maxPerArea = OthersideConfig.SERVER.bloomMaxPerPlayerArea.get();

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator() || player.isCreative()) continue;

            // Count existing blooms near this player
            int nearbyCount = level.getEntitiesOfClass(ListeningBloomEntity.class,
                    new AABB(player.blockPosition()).inflate(32)).size();
            if (nearbyCount >= maxPerArea) continue;

            // Find a valid sculk surface position near the player
            BlockPos spawnPos = findBloomSpawnPos(level, beast, player.blockPosition());
            if (spawnPos != null) {
                spawnBloom(level, spawnPos, 1.0F, "natural");
            }
        }
    }

    // ── Sore bud spawning (the W4 hook) ──────────────────────────────

    /**
     * Spawn bloom buds at a sore eruption site.
     * Called from SoreManager.phaseComplete().
     */
    public void spawnBloomBuds(ServerLevel level, BlockPos soreCenter, int count) {
        for (int i = 0; i < count; i++) {
            // Offset from center by a few blocks
            int dx = random.nextInt(5) - 2;
            int dz = random.nextInt(5) - 2;
            int x = soreCenter.getX() + dx;
            int z = soreCenter.getZ() + dz;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            BlockPos pos = new BlockPos(x, y, z);

            spawnBloom(level, pos, 0.4F, "sore_bud");
        }
        DirectorLog.log(level, "BLOOM_SPAWN", soreCenter, "sore_buds=" + count);
    }

    // ── Spawning ─────────────────────────────────────────────────────

    private void spawnBloom(ServerLevel level, BlockPos pos, float scale, String reason) {
        ListeningBloomEntity bloom = ModEntityTypes.LISTENING_BLOOM.get().create(level);
        if (bloom == null) return;

        bloom.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        bloom.setBloomScale(scale);
        level.addFreshEntity(bloom);

        // Director logging
        if (!firstBloomLogged) {
            firstBloomLogged = true;
            DirectorLog.log(level, "FIRST_BLOOM", pos,
                    "The beast's first listening bloom — it can hear you now");
        }
        DirectorLog.log(level, "BLOOM_SPAWN", pos,
                "scale=" + scale + " reason=" + reason);

        OthersideMod.LOGGER.info("[BLOOM] Spawned bloom at {} (scale={}, reason={})",
                pos.toShortString(), scale, reason);
    }

    // ── Position finding ─────────────────────────────────────────────

    private BlockPos findBloomSpawnPos(ServerLevel level, WorldbeastState beast, BlockPos near) {
        for (int attempt = 0; attempt < 16; attempt++) {
            int dx = random.nextInt(32) - 16;
            int dz = random.nextInt(32) - 16;
            int x = near.getX() + dx;
            int z = near.getZ() + dz;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            BlockPos candidate = new BlockPos(x, y, z);

            // Must be in beast domain
            if (!beast.isInBeastDomain(candidate, level)) continue;

            // Must be on solid ground (sculk or similar)
            BlockPos below = candidate.below();
            if (!level.getBlockState(below).isSolid()) continue;

            // No bright light
            if (level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, candidate) >= 10) continue;

            // Not too close to another bloom
            List<ListeningBloomEntity> nearby = level.getEntitiesOfClass(
                    ListeningBloomEntity.class, new AABB(candidate).inflate(6));
            if (!nearby.isEmpty()) continue;

            return candidate;
        }
        return null;
    }

    // ── Cleanup ──────────────────────────────────────────────────────

    private void tickCleanup(ServerLevel level, WorldbeastState beast) {
        // Scan all loaded blooms; remove any on unclaimed ground
        for (ListeningBloomEntity bloom : level.getEntitiesOfClass(
                ListeningBloomEntity.class,
                new AABB(-30000000, -64, -30000000, 30000000, 320, 30000000))) {
            if (!beast.isInBeastDomain(bloom.blockPosition(), level)) {
                DirectorLog.log(level, "BLOOM_CLEANSED", bloom.blockPosition(), "ground_lost");
                bloom.discard();
            }
        }
    }

    // ── Serialization ────────────────────────────────────────────────

    public void saveTo(CompoundTag tag) {
        tag.putLong("lastSpawnTick", lastSpawnTick);
        tag.putBoolean("firstBloomLogged", firstBloomLogged);
    }

    public void loadFrom(CompoundTag tag) {
        lastSpawnTick = tag.getLong("lastSpawnTick");
        firstBloomLogged = tag.getBoolean("firstBloomLogged");
    }
}
