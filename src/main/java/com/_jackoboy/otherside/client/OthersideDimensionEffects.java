package com._jackoboy.otherside.client;

import com._jackoboy.otherside.OthersideMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import javax.annotation.Nullable;

/**
 * Custom dimension effects for the Echo Dimension (formerly "the Otherside").
 *
 * Dusky purple-grey sky with drifting green spore-lights.
 * No sun, no moon, no clouds. Timeless overcast.
 * ~200 green spore motes drifting through the air.
 *
 * Registered via RegisterDimensionSpecialEffectsEvent on the mod bus.
 */
public class OthersideDimensionEffects extends DimensionSpecialEffects {

    public static final ResourceLocation EFFECTS_ID = ResourceLocation.fromNamespaceAndPath(
            OthersideMod.MOD_ID, "the_otherside");

    // Spore mote data (green particles drifting on the far plane)
    private static final int MOTE_COUNT = 200;
    private final float[] moteX = new float[MOTE_COUNT];
    private final float[] moteY = new float[MOTE_COUNT];
    private final float[] moteZ = new float[MOTE_COUNT];
    private final float[] moteAlpha = new float[MOTE_COUNT];
    private boolean motesInitialized = false;

    public OthersideDimensionEffects() {
        // cloudLevel NaN = no clouds, hasGround true (surface dimension),
        // skyType NONE (we render our own), forceBrightLightmap false,
        // constantAmbientLight false
        super(Float.NaN, true, SkyType.NONE, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        // Slightly darken fog with reduced brightness — dusky feel
        return fogColor.scale(brightness * 0.94 + 0.06);
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return true; // Always foggy — oppressive atmosphere
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix,
                              Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        // Initialize mote positions on first call
        if (!motesInitialized) {
            initMotes();
            motesInitialized = true;
        }

        // ── Draw dusky purple-grey sky dome ──
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        // Sky gradient: top = dark purple-grey, horizon = slightly lighter teal-grey
        float topR = 0.12f, topG = 0.10f, topB = 0.16f;   // Dark purple-grey
        float horR = 0.14f, horG = 0.16f, horB = 0.18f;    // Slightly teal-grey

        // Draw sky dome as a radial gradient (16 segments, top-to-horizon)
        BufferBuilder skyBuffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        float skyDist = 100.0f;

        // Center (top of sky)
        skyBuffer.addVertex(modelViewMatrix, 0, skyDist, 0).setColor(topR, topG, topB, 1.0f);

        // Ring at horizon
        int segments = 32;
        for (int i = 0; i <= segments; i++) {
            float angle = (float) i / segments * Mth.TWO_PI;
            float x = Mth.sin(angle) * skyDist;
            float z = Mth.cos(angle) * skyDist;
            skyBuffer.addVertex(modelViewMatrix, x, 0, z).setColor(horR, horG, horB, 1.0f);
        }
        BufferUploader.drawWithShader(skyBuffer.buildOrThrow());

        // ── Draw green spore-lights ──
        float time = (ticks + partialTick) * 0.0004F;

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        RenderSystem.disableCull();  // motes are flat XY quads — show on both hemispheres
        for (int i = 0; i < MOTE_COUNT; i++) {
            // Drift motes slowly
            float x = moteX[i] + Mth.sin(time + i * 0.37F) * 3.0F;
            float y = moteY[i] + Mth.cos(time * 0.7F + i * 0.53F) * 2.0F;
            float z = moteZ[i] + Mth.sin(time * 0.5F + i * 0.71F) * 3.0F;

            // Normalize to unit sphere and push to far plane
            float len = Mth.sqrt(x * x + y * y + z * z);
            if (len < 0.001F) continue;
            x = (x / len) * 90.0F;
            y = (y / len) * 90.0F;
            z = (z / len) * 90.0F;

            float alpha = moteAlpha[i];
            // Slow twinkle / pulse
            alpha *= 0.7F + 0.3F * Mth.sin(time * 2.5F + i * 1.13F);

            // Green spore color: dim bioluminescent green
            float r = 0.35F;
            float g = 0.85F;
            float b = 0.40F;

            // Draw a small quad (billboard)
            float size = 0.6F;
            buffer.addVertex(modelViewMatrix, x - size, y - size, z).setColor(r, g, b, alpha);
            buffer.addVertex(modelViewMatrix, x + size, y - size, z).setColor(r, g, b, alpha);
            buffer.addVertex(modelViewMatrix, x + size, y + size, z).setColor(r, g, b, alpha);
            buffer.addVertex(modelViewMatrix, x - size, y + size, z).setColor(r, g, b, alpha);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.enableCull();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        return true; // We handled sky rendering — no sun, no moon, no stars
    }

    private void initMotes() {
        RandomSource random = RandomSource.create(42L); // Fixed seed for consistency
        for (int i = 0; i < MOTE_COUNT; i++) {
            // Random positions on a unit sphere (upper hemisphere biased for sky)
            float theta = random.nextFloat() * Mth.TWO_PI;
            float phi = (float) Math.acos(2.0F * random.nextFloat() - 1.0F);
            moteX[i] = Mth.sin(phi) * Mth.cos(theta);
            moteY[i] = Math.abs(Mth.sin(phi) * Mth.sin(theta)); // Upper hemisphere
            moteZ[i] = Mth.cos(phi);
            // Alpha range 0.1 to 0.35 (dim spores)
            moteAlpha[i] = 0.10F + random.nextFloat() * 0.25F;
        }
    }
}
