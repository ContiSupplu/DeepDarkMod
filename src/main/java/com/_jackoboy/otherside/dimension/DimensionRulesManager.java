package com._jackoboy.otherside.dimension;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

/**
 * Core dimension rules for the Otherside dimension.
 * Called every server tick from ModEventHandlers.
 *
 * Rules:
 * - Darkness pulse: every 30 seconds, players NOT near a block-light source (>=7)
 *   receive a brief Darkness effect.
 * - Night Vision refusal: every second, any player with Night Vision has it stripped
 *   with a thematic message and fizzle sound.
 */
public class DimensionRulesManager {

    /** Resource key for the Otherside dimension. Reused by other handlers. */
    public static final ResourceKey<Level> OTHERSIDE_DIM = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(OthersideMod.MOD_ID, "the_otherside"));

    private static int tickCounter = 0;

    /** Ticks between darkness pulses (600 = 30 seconds). */
    private static final int DARKNESS_PULSE_INTERVAL = 600;
    /** Duration of the darkness effect in ticks (100 = 5 seconds). */
    private static final int DARKNESS_DURATION = 100;

    /**
     * Called every server tick. Only processes when the given level IS the Otherside.
     *
     * @param level The server level being ticked
     */
    public static void tick(ServerLevel level) {
        if (!level.dimension().equals(OTHERSIDE_DIM)) return;

        tickCounter++;

        // ── Darkness pulse — every 30 seconds ──
        if (tickCounter % DARKNESS_PULSE_INTERVAL == 0) {
            for (ServerPlayer player : level.players()) {
                if (!isNearLight(level, player.blockPosition(), 7)) {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.DARKNESS, DARKNESS_DURATION, 0, false, false, true));
                }
            }
        }

        // ── Night Vision refusal — every 20 ticks (1 second) ──
        if (tickCounter % 20 == 0) {
            for (ServerPlayer player : level.players()) {
                if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                    player.removeEffect(MobEffects.NIGHT_VISION);

                    // Action bar message
                    player.displayClientMessage(
                            Component.literal("the dark refuses your sight.")
                                    .withStyle(ChatFormatting.DARK_AQUA)
                                    .withStyle(ChatFormatting.ITALIC),
                            true);

                    // Fizzle sound
                    level.playSound(null, player.blockPosition(),
                            SoundEvents.FIRE_EXTINGUISH, SoundSource.AMBIENT,
                            0.5F, 1.5F);
                }
            }
        }
    }

    // ── Light sampling ──────────────────────────────────────────────────────────

    /**
     * Fast light check: samples ~25 positions around the player (self, cardinal
     * directions at 2 & 4 blocks, plus ±1 Y each) to see if any block-light
     * meets the threshold. This is intentionally approximate — we want a
     * gameplay feel, not a precise sphere scan.
     *
     * @param level     The server level
     * @param center    Player's block position
     * @param threshold Minimum block-light level to count as "near light"
     * @return true if any sampled position meets or exceeds the threshold
     */
    private static boolean isNearLight(ServerLevel level, BlockPos center, int threshold) {
        // Check the player's own position + above/below first (cheap early-out)
        for (int dy = -1; dy <= 1; dy++) {
            if (level.getBrightness(LightLayer.BLOCK, center.above(dy)) >= threshold) {
                return true;
            }
        }

        // Cardinal directions at radius 2 and 4, checking Y-1 through Y+1
        int[][] offsets = {
                { 2,  0}, {-2,  0}, { 0,  2}, { 0, -2},
                { 4,  0}, {-4,  0}, { 0,  4}, { 0, -4},
                { 3,  3}, {-3,  3}, { 3, -3}, {-3, -3}
        };
        for (int[] off : offsets) {
            BlockPos sample = center.offset(off[0], 0, off[1]);
            for (int dy = -1; dy <= 1; dy++) {
                if (level.getBrightness(LightLayer.BLOCK, sample.above(dy)) >= threshold) {
                    return true;
                }
            }
        }

        return false;
    }
}
