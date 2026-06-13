package com._jackoboy.otherside.dimension;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.network.ResonancePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

/**
 * Resonance Manager — "sound is detection" system for the Otherside.
 *
 * Every noise event in the dimension adds to a per-player resonance meter (0-100).
 * High resonance attracts hostile attention. Players SEE their noise as sculk particles.
 *
 * Weight table:
 *   step=1 (sneaking=0), sprint=3, block_break=8, block_place=4,
 *   door/trapdoor=6, explosion=60, lantern_place=10, mob_death_16=12,
 *   eat/drink=2, elytra=2/s, loud_light_hum=2
 *
 * Thresholds: 30=echoling convergence(stub), 60=the Answer, 90=Summoning
 */
public class ResonanceManager {

    private static final float DECAY_PER_SECOND = 2.0F;
    private static final float DECAY_PER_TICK = DECAY_PER_SECOND / 20.0F;
    private static final int SPAM_WINDOW = 10; // ticks
    private static final float SPAM_CAP = 20.0F;

    private static final Map<UUID, PlayerResonance> playerData = new HashMap<>();

    public static class PlayerResonance {
        float value = 0;
        float spamAccum = 0;
        int spamWindowStart = 0;
        float lastSynced = 0;
        int syncTimer = 0;

        // Threshold cooldowns to avoid spamming actions
        int answerCooldown = 0;
        int summonCooldown = 0;
    }

    /**
     * Called every server tick when the Otherside is loaded.
     */
    public static void tick(ServerLevel level) {
        if (!level.dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) return;

        Iterator<Map.Entry<UUID, PlayerResonance>> it = playerData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, PlayerResonance> entry = it.next();
            UUID uuid = entry.getKey();
            PlayerResonance data = entry.getValue();

            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            if (player == null || !player.level().dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) {
                it.remove();
                continue;
            }

            // Decay
            data.value = Math.max(0, data.value - DECAY_PER_TICK);

            // Cooldowns
            if (data.answerCooldown > 0) data.answerCooldown--;
            if (data.summonCooldown > 0) data.summonCooldown--;

            // Sync to client
            data.syncTimer++;
            if (data.syncTimer >= 20 || Math.abs(data.value - data.lastSynced) > 1.0F) {
                data.syncTimer = 0;
                data.lastSynced = data.value;
                PacketDistributor.sendToPlayer(player, new ResonancePayload(data.value));
            }

            // Threshold checks
            checkThresholds(level, player, data);
        }
    }

    /**
     * Add resonance from a noise event.
     * Call from game events, block placement listeners, etc.
     */
    public static void addResonance(ServerPlayer player, float weight, BlockPos eventPos) {
        if (!player.level().dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) return;

        PlayerResonance data = playerData.computeIfAbsent(player.getUUID(), k -> new PlayerResonance());

        // Spam cap
        int currentTick = player.getServer().getTickCount();
        if (currentTick - data.spamWindowStart >= SPAM_WINDOW) {
            data.spamWindowStart = currentTick;
            data.spamAccum = 0;
        }
        if (data.spamAccum >= SPAM_CAP) return;
        float effective = Math.min(weight, SPAM_CAP - data.spamAccum);
        data.spamAccum += effective;

        data.value = Math.min(100, data.value + effective);

        // Visible ripples for weight >= 4
        if (weight >= 4 && player.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SCULK_CHARGE_POP,
                    eventPos.getX() + 0.5, eventPos.getY() + 0.5, eventPos.getZ() + 0.5,
                    3 + (int)(weight / 4), 0.5, 0.3, 0.5, 0.02);
            sl.sendParticles(ParticleTypes.SONIC_BOOM,
                    eventPos.getX() + 0.5, eventPos.getY() + 0.5, eventPos.getZ() + 0.5,
                    1, 0, 0, 0, 0);
        }
    }

    /**
     * Get current resonance for a player.
     */
    public static float get(UUID playerUuid) {
        PlayerResonance data = playerData.get(playerUuid);
        return data != null ? data.value : 0;
    }

    /**
     * Set resonance directly (e.g. after summoning, reset to 50).
     */
    public static void set(UUID playerUuid, float value) {
        PlayerResonance data = playerData.get(playerUuid);
        if (data != null) {
            data.value = Math.max(0, Math.min(100, value));
        }
    }

    private static void checkThresholds(ServerLevel level, ServerPlayer player, PlayerResonance data) {
        // >= 30: Echoling convergence (stub)
        if (data.value >= 30 && data.value < 60) {
            // TODO: Echoling convergence particles when echolings exist
        }

        // >= 60: The Answer — nearest Warden bellows + investigates
        if (data.value >= 60 && data.answerCooldown <= 0) {
            data.answerCooldown = 600; // 30s cooldown
            Warden nearestWarden = findNearestWarden(level, player.blockPosition(), 200);
            if (nearestWarden != null) {
                // Make the Warden investigate
                nearestWarden.increaseAngerAt(player, 80, true);
                level.playSound(null, nearestWarden.blockPosition(),
                        SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 3.0F, 1.0F);
                OthersideMod.LOGGER.info("[RESONANCE] The Answer: Warden at {} investigating player {}",
                        nearestWarden.blockPosition(), player.getName().getString());
            }
        }

        // >= 90: Summoning — spawn a Warden if none nearby
        if (data.value >= 90 && data.summonCooldown <= 0) {
            data.summonCooldown = 1200; // 60s cooldown
            Warden nearbyWarden = findNearestWarden(level, player.blockPosition(), 48);
            if (nearbyWarden == null) {
                // Spawn Warden 40-60 blocks away
                BlockPos spawnPos = findSpawnPos(level, player.blockPosition(), 40, 60);
                if (spawnPos != null) {
                    Warden warden = net.minecraft.world.entity.EntityType.WARDEN.create(level);
                    if (warden != null) {
                        warden.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                                player.getYRot() + 180, 0);
                        warden.setPose(net.minecraft.world.entity.Pose.EMERGING);
                        level.addFreshEntity(warden);
                        data.value = 50; // Reset to 50 after summon

                        OthersideMod.LOGGER.info("[RESONANCE] Summoning: Warden spawned at {} (player {})",
                                spawnPos, player.getName().getString());
                    }
                }
            }
        }
    }

    private static Warden findNearestWarden(ServerLevel level, BlockPos center, int radius) {
        AABB box = new AABB(center).inflate(radius);
        List<Warden> wardens = level.getEntitiesOfClass(Warden.class, box);
        Warden nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Warden w : wardens) {
            double dist = w.blockPosition().distSqr(center);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = w;
            }
        }
        return nearest;
    }

    private static BlockPos findSpawnPos(ServerLevel level, BlockPos center, int minDist, int maxDist) {
        Random rng = new Random();
        for (int attempt = 0; attempt < 20; attempt++) {
            double angle = rng.nextDouble() * Math.PI * 2;
            int dist = minDist + rng.nextInt(maxDist - minDist);
            int x = center.getX() + (int)(Math.cos(angle) * dist);
            int z = center.getZ() + (int)(Math.sin(angle) * dist);

            // Scan for solid ground
            for (int y = center.getY() + 10; y > center.getY() - 30; y--) {
                BlockPos check = new BlockPos(x, y, z);
                if (!level.getBlockState(check).isAir() &&
                    level.getBlockState(check.above()).isAir() &&
                    level.getBlockState(check.above(2)).isAir()) {
                    return check.above();
                }
            }
        }
        return null;
    }

    /**
     * Clear data for a player (on logout/dimension change).
     */
    public static void remove(UUID playerUuid) {
        playerData.remove(playerUuid);
    }
}
