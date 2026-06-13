package com._jackoboy.otherside.client;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.network.ResonancePayload;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Resonance HUD — a ripple bar above the hotbar.
 * 
 * Near-invisible when calm, pulses on noise events,
 * amber ≥30, red ≥60, strobe at ≥90.
 */
@EventBusSubscriber(modid = OthersideMod.MOD_ID, value = Dist.CLIENT)
public class ResonanceHudOverlay {

    private static final ResourceKey<Level> OTHERSIDE_DIM = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(OthersideMod.MOD_ID, "the_otherside"));

    private static float displayValue = 0;
    private static float targetValue = 0;
    private static int pulseTick = 0;

    // Bar dimensions
    private static final int BAR_W = 91;
    private static final int BAR_H = 3;

    public static void handlePayload(ResonancePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            float oldTarget = targetValue;
            targetValue = payload.value();
            if (targetValue > oldTarget + 0.5F) {
                pulseTick = 8; // Pulse animation on increase
            }
        });
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiLayerEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (!mc.level.dimension().equals(OTHERSIDE_DIM)) {
            displayValue = 0;
            targetValue = 0;
            return;
        }
        if (mc.options.hideGui) return;

        // Interpolate display towards target
        displayValue = Mth.lerp(0.1F, displayValue, targetValue);

        // Don't draw when essentially zero
        if (displayValue < 0.5F) return;

        GuiGraphics gui = event.getGuiGraphics();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        // Position: centered above hotbar, just above XP bar
        int x = (screenW - BAR_W) / 2;
        int y = screenH - 38;

        float pct = displayValue / 100.0F;
        int fillW = (int)(BAR_W * pct);

        // Color: teal < 30, amber 30-60, red 60+, strobe 90+
        int color;
        if (displayValue >= 90) {
            // Strobe between red and white
            boolean strobe = (System.currentTimeMillis() / 100) % 2 == 0;
            color = strobe ? 0xFFFF2222 : 0xFFFFAAAA;
        } else if (displayValue >= 60) {
            color = 0xFFFF4444; // Red
        } else if (displayValue >= 30) {
            color = 0xFFFFAA22; // Amber
        } else {
            color = 0xFF44CCBB; // Teal
        }

        // Base alpha: fades in with value
        float alpha = Math.min(displayValue / 15.0F, 1.0F);
        // Pulse boost
        if (pulseTick > 0) {
            pulseTick--;
            alpha = 1.0F;
        }

        int a = (int)(alpha * 255) << 24;
        int bgColor = (0x10101010 & 0x00FFFFFF) | a;
        int barColor = (color & 0x00FFFFFF) | a;

        // Background
        gui.fill(x, y, x + BAR_W, y + BAR_H, bgColor);

        // Fill bar
        if (fillW > 0) {
            gui.fill(x, y, x + fillW, y + BAR_H, barColor);
        }

        // Glow line at fill edge
        if (fillW > 0 && fillW < BAR_W) {
            int glowColor = 0xFFFFFFFF & 0x00FFFFFF | a;
            gui.fill(x + fillW, y, x + fillW + 1, y + BAR_H, glowColor);
        }
    }

    public static void clear() {
        displayValue = 0;
        targetValue = 0;
    }
}
