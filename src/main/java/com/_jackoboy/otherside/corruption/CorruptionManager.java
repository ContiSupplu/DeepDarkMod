package com._jackoboy.otherside.corruption;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.dimension.DimensionRulesManager;
import com._jackoboy.otherside.entity.EchoSoulEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.LightLayer;

/**
 * Server-side corruption tick logic. Called from ModEventHandlers.onServerTick().
 * <p>
 * Processes every 20 ticks (1 second). For each player in the Echo dimension:
 * - If NOT warded (block light < threshold): adds corruptionGainPerSec (0.4 default)
 *   ONCE per 20-tick cycle — i.e. exactly corruptionGainPerSec per second.
 * - Applies/refreshes MobEffects based on thresholds.
 * <p>
 * Ward check: player is warded (gain suppressed) if block light >= threshold
 * OR an amethyst block/budding amethyst is within amethystWardRadius.
 */
public final class CorruptionManager {
    private CorruptionManager() {}

    private static int tickCounter = 0;

    /** Call every tick from onServerTick. Internal throttle handles the 20-tick cycle. */
    public static void tick(MinecraftServer server) {
        tickCounter++;
        if (tickCounter < 20) return;
        tickCounter = 0;

        float gainPerCycle = OthersideConfig.SERVER.corruptionGainPerSec.get().floatValue();
        int lightThreshold = OthersideConfig.SERVER.echoSoulLightDeterLevel.get();

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            boolean inEchoDim = player.level().dimension().equals(DimensionRulesManager.OTHERSIDE_DIM);

            // ── Gain (only in Echo dimension, only if unwarded) ──
            if (inEchoDim) {
                int blockLight = player.level().getBrightness(LightLayer.BLOCK, player.blockPosition());
                boolean warded = blockLight >= lightThreshold
                        || EchoSoulEntity.isNearAmethyst(player.level(), player.blockPosition());

                if (!warded) {
                    // Add gainPerCycle ONCE per 20-tick cycle = gainPerCycle per second
                    Corruption.add(player, gainPerCycle);
                }
            }

            // ── Effects (apply regardless of dimension, based on current value) ──
            float corruption = Corruption.get(player);
            applyEffects(player, corruption);
        }
    }

    /**
     * Apply/refresh MobEffects based on corruption thresholds.
     * Duration = 40 ticks (2 sec) — refreshed every 20 ticks so they never lapse.
     * <p>
     * Thresholds (tuned per user feedback — Darkness at 50 is too harsh):
     * - >= 30: vignette only (handled client-side by CorruptionOverlay)
     * - >= 75: Darkness + Slowness I + Weakness I
     * - = 100: Consumed — Slowness II + Weakness II + Darkness
     */
    private static void applyEffects(ServerPlayer player, float corruption) {
        int threshold75 = OthersideConfig.SERVER.corruptionSlowThreshold.get();
        int threshold100 = OthersideConfig.SERVER.corruptionConsumedThreshold.get();

        if (corruption >= threshold100) {
            // Consumed state: Slowness II + Weakness II + Darkness
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0, false, false, true));
        } else if (corruption >= threshold75) {
            // Heavy corruption: Slowness I + Weakness I + Darkness
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0, false, false, true));
        }
        // >= 30: vignette only — handled by CorruptionOverlay on client, no MobEffect needed
    }
}
