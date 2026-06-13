package com._jackoboy.otherside.client;

import com._jackoboy.otherside.OthersideMod;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

/**
 * Dual-mode fog renderer:
 * 
 * OVERWORLD: Teal atmospheric fog driven by infection/gloom proximity.
 * THE OTHERSIDE: Thick, oppressive dark fog — visibility ~40-60 blocks,
 *   deep dark-teal color, always active. Creates the feeling of being
 *   submerged in a vast underground void.
 */
@EventBusSubscriber(modid = OthersideMod.MOD_ID, value = Dist.CLIENT)
public class FogRenderer {

    // Dimension key for the Otherside
    private static final ResourceKey<Level> OTHERSIDE_DIM = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(OthersideMod.MOD_ID, "the_otherside"));

    // ── Overworld infection fog ──
    private static final float OW_FOG_R = 0.35f;
    private static final float OW_FOG_G = 0.52f;
    private static final float OW_FOG_B = 0.53f;

    // ── Otherside dimension fog ──
    // Very dark teal — like looking through deep water in a cave
    private static final float DIM_FOG_R = 0.02f;
    private static final float DIM_FOG_G = 0.05f;
    private static final float DIM_FOG_B = 0.06f;
    private static final float DIM_FOG_NEAR = 4.0f;
    private static final float DIM_FOG_FAR = 56.0f; // ~3.5 chunks visibility

    // ── W3: Maw mood fog ──
    private static final float MAW_FOG_R = 0.05f;
    private static final float MAW_FOG_G = 0.08f;
    private static final float MAW_FOG_B = 0.14f;
    private static float mawFogIntensity = 0f; // 0..1, smoothed

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        GloomTracker.tick();

        // W3: Smooth maw fog intensity via synced flag
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null && !mc.level.dimension().equals(OTHERSIDE_DIM)) {
            float target = ClientBeastData.mawActive ? 1.0f : 0.0f;
            // Smooth over ~3 seconds (60 ticks)
            mawFogIntensity = Mth.lerp(0.017f, mawFogIntensity, target);
            if (mawFogIntensity < 0.01f) mawFogIntensity = 0f;
        } else {
            mawFogIntensity = 0f;
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        if (mc.level.dimension().equals(OTHERSIDE_DIM)) {
            // ── OTHERSIDE: thick oppressive fog ──
            event.setNearPlaneDistance(DIM_FOG_NEAR);
            event.setFarPlaneDistance(DIM_FOG_FAR);
            event.setFogShape(FogShape.SPHERE);
            event.setCanceled(true);
            return;
        }

        // ── OVERWORLD: infection-driven fog ──
        float g = GloomTracker.get(0);
        float far = 200.0f * (1.0f - g * 1.4f);
        far = Math.max(far, 25.0f);

        // W3: Maw mood — reduce far plane by up to 40%
        if (mawFogIntensity > 0) {
            far *= (1.0f - mawFogIntensity * 0.4f);
        }

        float near = 2.0f;

        event.setNearPlaneDistance(near);
        event.setFarPlaneDistance(far);
        event.setFogShape(FogShape.SPHERE);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        if (mc.level.dimension().equals(OTHERSIDE_DIM)) {
            // ── OTHERSIDE: deep dark teal ──
            event.setRed(DIM_FOG_R);
            event.setGreen(DIM_FOG_G);
            event.setBlue(DIM_FOG_B);
            return;
        }

        // ── OVERWORLD: infection tint ──
        float g = GloomTracker.get(0);
        float blend = Math.min(g * 1.2f, 1.0f);

        float r = Mth.lerp(blend, event.getRed(), OW_FOG_R);
        float gr = Mth.lerp(blend, event.getGreen(), OW_FOG_G);
        float b = Mth.lerp(blend, event.getBlue(), OW_FOG_B);

        // W3: Maw mood — darken toward deep sculk blue
        if (mawFogIntensity > 0) {
            r = Mth.lerp(mawFogIntensity, r, MAW_FOG_R);
            gr = Mth.lerp(mawFogIntensity, gr, MAW_FOG_G);
            b = Mth.lerp(mawFogIntensity, b, MAW_FOG_B);
        }

        event.setRed(r);
        event.setGreen(gr);
        event.setBlue(b);
    }
}
