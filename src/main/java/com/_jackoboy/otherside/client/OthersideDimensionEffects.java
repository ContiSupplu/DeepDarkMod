package com._jackoboy.otherside.client;

import com._jackoboy.otherside.OthersideMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
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
 * Custom dimension effects for the Otherside.
 * 
 * Pure darkness with ~120 faint teal-white star motes drifting slowly on the far plane.
 * Always foggy, no clouds, no skylight.
 * 
 * Registered via RegisterDimensionSpecialEffectsEvent on the mod bus.
 */
public class OthersideDimensionEffects extends DimensionSpecialEffects {

    public static final ResourceLocation EFFECTS_ID = ResourceLocation.fromNamespaceAndPath(
            OthersideMod.MOD_ID, "the_otherside");

    // Star mote data (generated once, drifted by time)
    private static final int MOTE_COUNT = 120;
    private final float[] moteX = new float[MOTE_COUNT];
    private final float[] moteY = new float[MOTE_COUNT];
    private final float[] moteZ = new float[MOTE_COUNT];
    private final float[] moteAlpha = new float[MOTE_COUNT];
    private boolean motesInitialized = false;

    public OthersideDimensionEffects() {
        // cloudLevel NaN = no clouds, hasGround false, skyType NONE,
        // forceBrightLightmap false, constantAmbientLight false
        super(Float.NaN, false, SkyType.NONE, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        // Return fog color unchanged — fog is dark by biome definition
        return fogColor;
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return true; // Always thick fog
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, 
                              Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        // Initialize mote positions on first call
        if (!motesInitialized) {
            initMotes();
            motesInitialized = true;
        }

        // Draw star motes
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        float time = (ticks + partialTick) * 0.0004F;

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i < MOTE_COUNT; i++) {
            // Drift motes slowly
            float x = moteX[i] + Mth.sin(time + i * 0.37F) * 2.0F;
            float y = moteY[i] + Mth.cos(time * 0.7F + i * 0.53F) * 1.5F;
            float z = moteZ[i] + Mth.sin(time * 0.5F + i * 0.71F) * 2.0F;

            // Normalize to unit sphere and push to far plane
            float len = Mth.sqrt(x * x + y * y + z * z);
            if (len < 0.001F) continue;
            x = (x / len) * 100.0F;
            y = (y / len) * 100.0F;
            z = (z / len) * 100.0F;

            float alpha = moteAlpha[i];
            // Slight twinkle
            alpha *= 0.8F + 0.2F * Mth.sin(time * 3.0F + i * 1.13F);

            // Teal-white color: RGB(190, 255, 242) = roughly (0.75, 1.0, 0.95)
            float r = 0.75F;
            float g = 1.0F;
            float b = 0.95F;

            // Draw a small quad facing the camera (billboard)
            float size = 0.5F;
            // Simple axis-aligned quad (acceptable for distant points)
            buffer.addVertex(modelViewMatrix, x - size, y - size, z).setColor(r, g, b, alpha);
            buffer.addVertex(modelViewMatrix, x + size, y - size, z).setColor(r, g, b, alpha);
            buffer.addVertex(modelViewMatrix, x + size, y + size, z).setColor(r, g, b, alpha);
            buffer.addVertex(modelViewMatrix, x - size, y + size, z).setColor(r, g, b, alpha);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        return true; // We handled sky rendering
    }

    private void initMotes() {
        RandomSource random = RandomSource.create(42L); // Fixed seed for consistency
        for (int i = 0; i < MOTE_COUNT; i++) {
            // Random positions on a unit sphere
            float theta = random.nextFloat() * Mth.TWO_PI;
            float phi = (float) Math.acos(2.0F * random.nextFloat() - 1.0F);
            moteX[i] = Mth.sin(phi) * Mth.cos(theta);
            moteY[i] = Mth.sin(phi) * Mth.sin(theta);
            moteZ[i] = Mth.cos(phi);
            // Alpha range 0.15 to 0.4
            moteAlpha[i] = 0.15F + random.nextFloat() * 0.25F;
        }
    }
}
