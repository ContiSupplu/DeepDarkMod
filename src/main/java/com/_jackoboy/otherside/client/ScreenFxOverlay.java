package com._jackoboy.otherside.client;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.network.ScreenFxPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * Client-side overlay renderer for screen effects during portal ignition.
 * Handles vignette pulse, blackout, and blackout-hold/release for the guardian cinematic.
 */
@EventBusSubscriber(modid = OthersideMod.MOD_ID, value = Dist.CLIENT)
public class ScreenFxOverlay {

    // --- Standard (non-hold) effect state ---
    private static int activeFxType = -1;
    private static int totalDuration = 0;
    private static int ticksRemaining = 0;

    // --- Blackout-hold state ---
    private static boolean holdingBlackout = false;
    private static long holdStartTick = 0;
    private static float holdOpacity = 0.0f;       // current opacity during fade-in or hold
    private static int holdFadeInTotal = 0;         // ticks for the fade-in phase
    private static int holdFadeInRemaining = 0;     // ticks left in fade-in

    // --- Blackout-release state ---
    private static boolean releasingBlackout = false;
    private static float releaseStartOpacity = 0.0f;
    private static int releaseFadeTotal = 0;
    private static int releaseFadeRemaining = 0;

    /** Watchdog: auto-release if held for this many ticks. */
    private static final int HOLD_WATCHDOG_TICKS = 300;
    /** Watchdog auto-release fade duration. */
    private static final int WATCHDOG_FADE_TICKS = 30;

    /**
     * Called from network handler when a ScreenFxPayload arrives.
     */
    public static void trigger(int fxType, int durationTicks) {
        if (fxType == ScreenFxPayload.BLACKOUT_HOLD) {
            // Begin fading to full black, then hold
            holdingBlackout = true;
            holdFadeInTotal = durationTicks;
            holdFadeInRemaining = durationTicks;
            holdOpacity = 0.0f;
            holdStartTick = getCurrentTick();
            releasingBlackout = false;
        } else if (fxType == ScreenFxPayload.BLACKOUT_RELEASE) {
            // Begin releasing from the current hold opacity
            if (holdingBlackout || holdOpacity > 0.0f) {
                releasingBlackout = true;
                releaseStartOpacity = holdOpacity;
                releaseFadeTotal = durationTicks;
                releaseFadeRemaining = durationTicks;
                holdingBlackout = false;
            }
        } else {
            // Standard effects (VIGNETTE_PULSE, BLACKOUT)
            activeFxType = fxType;
            totalDuration = durationTicks;
            ticksRemaining = durationTicks;
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        GuiGraphics gui = event.getGuiGraphics();

        // --- Render standard effects ---
        if (ticksRemaining > 0 && activeFxType >= 0) {
            float progress = 1.0f - (float) ticksRemaining / totalDuration;

            if (activeFxType == ScreenFxPayload.VIGNETTE_PULSE) {
                renderVignette(gui, width, height, progress);
            } else if (activeFxType == ScreenFxPayload.BLACKOUT) {
                renderBlackout(gui, width, height, progress);
            }

            ticksRemaining--;
            if (ticksRemaining <= 0) {
                activeFxType = -1;
            }
        }

        // --- Render blackout-hold (fade-in phase + sustained hold) ---
        if (holdingBlackout) {
            if (holdFadeInRemaining > 0) {
                holdOpacity = 1.0f - (float) holdFadeInRemaining / holdFadeInTotal;
                holdFadeInRemaining--;
            } else {
                holdOpacity = 1.0f;
            }

            // Watchdog: auto-release if held too long
            long heldFor = getCurrentTick() - holdStartTick;
            if (heldFor >= HOLD_WATCHDOG_TICKS) {
                OthersideMod.LOGGER.warn("Blackout hold exceeded {} ticks — watchdog auto-release", HOLD_WATCHDOG_TICKS);
                releasingBlackout = true;
                releaseStartOpacity = holdOpacity;
                releaseFadeTotal = WATCHDOG_FADE_TICKS;
                releaseFadeRemaining = WATCHDOG_FADE_TICKS;
                holdingBlackout = false;
            } else {
                renderBlackoutOverlay(gui, width, height, holdOpacity);
            }
        }

        // --- Render blackout-release (fade-out from hold) ---
        if (releasingBlackout) {
            if (releaseFadeRemaining > 0) {
                float fadeProgress = 1.0f - (float) releaseFadeRemaining / releaseFadeTotal;
                holdOpacity = releaseStartOpacity * (1.0f - fadeProgress);
                releaseFadeRemaining--;
                renderBlackoutOverlay(gui, width, height, holdOpacity);
            } else {
                holdOpacity = 0.0f;
                releasingBlackout = false;
            }
        }
    }

    /**
     * Vignette: dark edges that pulse in and fade out.
     * Peaks at 30% progress, then fades.
     */
    private static void renderVignette(GuiGraphics gui, int width, int height, float progress) {
        float intensity;
        if (progress < 0.3f) {
            intensity = progress / 0.3f;
        } else {
            intensity = 1.0f - ((progress - 0.3f) / 0.7f);
        }
        intensity = Math.max(0, Math.min(1, intensity));

        int alpha = (int) (intensity * 180);
        if (alpha <= 0) return;

        int color = (alpha << 24); // Black with variable alpha

        // Top and bottom bars
        int edgeSize = (int) (height * 0.15f * intensity);
        gui.fill(0, 0, width, edgeSize, color);
        gui.fill(0, height - edgeSize, width, height, color);

        // Left and right bars
        int sideSize = (int) (width * 0.12f * intensity);
        gui.fill(0, edgeSize, sideSize, height - edgeSize, color);
        gui.fill(width - sideSize, edgeSize, width, height - edgeSize, color);
    }

    /**
     * Blackout: full screen black with 0.3s (6 tick) fade in/out edges.
     */
    private static void renderBlackout(GuiGraphics gui, int width, int height, float progress) {
        float fadeInEnd = 6.0f / totalDuration;
        float fadeOutStart = 1.0f - (6.0f / totalDuration);

        float alpha;
        if (progress < fadeInEnd) {
            alpha = progress / fadeInEnd;
        } else if (progress > fadeOutStart) {
            alpha = 1.0f - ((progress - fadeOutStart) / (1.0f - fadeOutStart));
        } else {
            alpha = 1.0f;
        }

        renderBlackoutOverlay(gui, width, height, Math.max(0, Math.min(1, alpha)));
    }

    /**
     * Renders a full-screen teal-black overlay at the given opacity.
     */
    private static void renderBlackoutOverlay(GuiGraphics gui, int width, int height, float opacity) {
        int a = (int) (opacity * 255);
        if (a <= 0) return;

        int r = (int) (0.02f * 255);
        int g = (int) (0.05f * 255);
        int b = (int) (0.06f * 255);
        int color = (a << 24) | (r << 16) | (g << 8) | b;

        gui.fill(0, 0, width, height, color);
    }

    /**
     * Returns the current game tick on the client side.
     */
    private static long getCurrentTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            return mc.level.getGameTime();
        }
        return System.currentTimeMillis() / 50; // Fallback: ~tick approximation
    }
}
