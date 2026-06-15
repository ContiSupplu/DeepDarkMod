package com._jackoboy.otherside.client;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * Client-side corruption vignette overlay. Fully diegetic — no HUD bar.
 * <p>
 * Reads {@link CorruptionClientData#currentCorruption} (synced from server).
 * - >= 30: faint dark vignette begins, alpha scales 30→100
 * - = 100: near-black overlay (Consumed state, until Pass 2 echolocation)
 */
@EventBusSubscriber(modid = OthersideMod.MOD_ID, value = Dist.CLIENT)
public class CorruptionOverlay {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        float corruption = CorruptionClientData.currentCorruption;
        if (corruption < 30.0F) return;

        GuiGraphics gui = event.getGuiGraphics();
        int width = gui.guiWidth();
        int height = gui.guiHeight();

        if (corruption >= 100.0F) {
            // ── Consumed: near-black overlay ──
            // Dark sculk-tinted black, alpha ~240 (survivable — faint shapes still visible)
            int r = 5, g = 8, b = 10;
            int a = 240;
            int color = (a << 24) | (r << 16) | (g << 8) | b;
            gui.fill(0, 0, width, height, color);
        } else {
            // ── Vignette: dark edges that intensify with corruption ──
            // Alpha scales from 0 at 30 to ~180 at 99
            float t = (corruption - 30.0F) / 70.0F; // 0.0 at 30, 1.0 at 100
            int alpha = (int)(t * 180.0F);
            if (alpha <= 0) return;

            // Dark sculk-tinted vignette color
            int r = 3, g = 6, b = 8;
            int color = (alpha << 24) | (r << 16) | (g << 8) | b;

            // Edge bars (vignette effect — darken edges, leave center clearer)
            int edgeThickness = (int)(Math.min(width, height) * 0.15F * t); // grows with corruption
            if (edgeThickness < 2) edgeThickness = 2;

            // Top
            gui.fill(0, 0, width, edgeThickness, color);
            // Bottom
            gui.fill(0, height - edgeThickness, width, height, color);
            // Left
            gui.fill(0, edgeThickness, edgeThickness, height - edgeThickness, color);
            // Right
            gui.fill(width - edgeThickness, edgeThickness, width, height - edgeThickness, color);

            // At higher corruption (>60), add a faint full-screen wash too
            if (corruption > 60.0F) {
                float washT = (corruption - 60.0F) / 40.0F; // 0 at 60, 1 at 100
                int washAlpha = (int)(washT * 80.0F);
                int washColor = (washAlpha << 24) | (r << 16) | (g << 8) | b;
                gui.fill(0, 0, width, height, washColor);
            }
        }
    }
}
