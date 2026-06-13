package com._jackoboy.otherside.client;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

/**
 * Always-on fog with intensity ramp near infection.
 * 
 * Baseline: barely visible haze at full render distance (gloom = BASELINE).
 * Near frontier: ramps up to phase ceiling.
 * 
 * No on/off transition ever — only intensity changes.
 */
public class GloomTracker {

    // W1 Worldbeast Rework: MASS-based ceilings (shim, full re-key to HUNGER/ATTENTION in W5)
    // mass < 5 → 0.35, < 15 → 0.55, < 30 → 0.80, >= 30 → 1.0

    // Baseline: always-on minimal haze (barely perceptible)
    private static final float BASELINE = 0.03f;

    // Distance thresholds from frontier
    private static final float INNER_RADIUS = 10.0f;   // Full intensity within 10 blocks
    private static final float OUTER_RADIUS = 64.0f;   // Ramps down to baseline at 64 blocks

    private static float gloom = BASELINE;
    private static float targetGloom = BASELINE;

    private static final float LERP_RATE = 0.02f;

    private static int debugTimer = 0;

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            gloom = BASELINE;
            targetGloom = BASELINE;
            return;
        }

        if (mc.level.dimension() != Level.OVERWORLD) {
            gloom = BASELINE;
            targetGloom = BASELINE;
            return;
        }

        double px = mc.player.getX();
        double pz = mc.player.getZ();

        int frontierCount = ClientBreachBorderCache.getColumnCount();

        // Compute proximity factor (0.0 = far away, 1.0 = right at frontier)
        float proximity = 0.0f;
        if (frontierCount > 0) {
            double distSq = ClientBreachBorderCache.distSqToBorderRange(px, pz, 64);
            double dist = Math.sqrt(distSq);

            if (dist <= INNER_RADIUS) {
                proximity = 1.0f;
            } else if (dist < OUTER_RADIUS) {
                proximity = 1.0f - (float) ((dist - INNER_RADIUS) / (OUTER_RADIUS - INNER_RADIUS));
            }
        }

        // MASS-based ceiling (W1 shim — replaces old phase ceilings)
        float mass = ClientBeastData.mass;
        float ceiling;
        if (mass < 5) ceiling = 0.35f;
        else if (mass < 15) ceiling = 0.55f;
        else if (mass < 30) ceiling = 0.80f;
        else ceiling = 1.0f;

        // Gloom = baseline + proximity ramp up to ceiling
        // At proximity 0: gloom = BASELINE (barely visible)
        // At proximity 1: gloom = ceiling (full phase fog)
        targetGloom = BASELINE + proximity * (ceiling - BASELINE);

        // Smooth lerp
        gloom = Mth.lerp(LERP_RATE, gloom, targetGloom);

        // Debug logging every 5 seconds
        debugTimer++;
        if (debugTimer >= 100) {
            debugTimer = 0;
            double dist = frontierCount > 0 ? Math.sqrt(ClientBreachBorderCache.distSqToBorderRange(px, pz, 64)) : -1;
            OthersideMod.LOGGER.info("[FOG] dist={}, prox={}, target={}, gloom={}, frontier={}, mass={}",
                    String.format("%.0f", dist),
                    String.format("%.3f", proximity),
                    String.format("%.3f", targetGloom),
                    String.format("%.3f", gloom),
                    frontierCount,
                    String.format("%.1f", mass));
        }
    }

    public static float get(float partialTick) {
        return gloom;
    }

    public static float getRaw() {
        return gloom;
    }
}
