package com._jackoboy.otherside.client;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.network.StyledBossBarPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = OthersideMod.MOD_ID, value = Dist.CLIENT)
public class WardenBossBarOverlay {

    private static final ResourceLocation FRAME_TEX = ResourceLocation.fromNamespaceAndPath(
            OthersideMod.MOD_ID, "textures/gui/bossbar/warden_bossbar_frame.png");
    private static final ResourceLocation BG_TEX = ResourceLocation.fromNamespaceAndPath(
            OthersideMod.MOD_ID, "textures/gui/bossbar/warden_bossbar_bg.png");
    private static final ResourceLocation FILL_TEX = ResourceLocation.fromNamespaceAndPath(
            OthersideMod.MOD_ID, "textures/gui/bossbar/warden_bossbar_fill.png");

    private static final int FRAME_W = 320, FRAME_H = 58;
    private static final int BAR_X = 14, BAR_Y = 26;
    private static final int BAR_W = 293, BAR_H = 12;
    private static final int STACK_SPACING = 66;

    /** Set of boss bar UUIDs that should use our custom rendering. */
    private static final Set<UUID> styledBars = new HashSet<>();

    public static void handleStyled(StyledBossBarPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.style() >= 0) {
                styledBars.add(payload.bossId());
            } else {
                styledBars.remove(payload.bossId());
            }
        });
    }

    @SubscribeEvent
    public static void onBossBarRender(CustomizeGuiOverlayEvent.BossEventProgress event) {
        UUID id = event.getBossEvent().getId();
        if (!styledBars.contains(id)) return;

        // Cancel vanilla rendering
        event.setCanceled(true);

        GuiGraphics gui = event.getGuiGraphics();
        int screenW = event.getWindow().getGuiScaledWidth();

        // Center the 320px frame where vanilla centers its 182px bar
        int vanillaX = screenW / 2 - 91; // vanilla's left edge
        int x = vanillaX + (182 - FRAME_W) / 2; // offset to center wider frame
        int y = event.getY();

        float progress = event.getBossEvent().getProgress();
        int fillWidth = Math.round(BAR_W * progress);

        // Draw order: bg → fill → cursor line → frame

        // 1. Background
        gui.blit(BG_TEX, x + BAR_X, y + BAR_Y, 0, 0, BAR_W, BAR_H, BAR_W, BAR_H);

        // 2. Fill (cropped to progress)
        if (fillWidth > 0) {
            gui.blit(FILL_TEX, x + BAR_X, y + BAR_Y, 0, 0, fillWidth, BAR_H, BAR_W, BAR_H);
        }

        // 3. Cursor line at fill edge (only when 0 < progress < 1)
        if (progress > 0 && progress < 1 && fillWidth > 0) {
            int lineX = x + BAR_X + fillWidth;
            // Color: RGB(190, 255, 242) = teal accent
            int color = 0xFF_BE_FF_F2;
            gui.fill(lineX, y + BAR_Y, lineX + 1, y + BAR_Y + BAR_H, color);
        }

        // 4. Frame on top (do NOT render name text — it's baked into the art)
        gui.blit(FRAME_TEX, x, y, 0, 0, FRAME_W, FRAME_H, FRAME_W, FRAME_H);

        // Advance Y for stacking
        event.setIncrement(STACK_SPACING);
    }

    public static void clear() {
        styledBars.clear();
    }
}
