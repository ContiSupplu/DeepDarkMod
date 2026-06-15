package com._jackoboy.otherside.client.renderer;

import com._jackoboy.otherside.block.OthersidePortalBlockEntity;
import com._jackoboy.otherside.block.OthersidePortalBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;

/**
 * Custom renderer for the Otherside portal field.
 * Draws a vibrant vertical gradient (sculk palette) with scrolling cloud layers
 * for depth/parallax, fully emissive. Replaces the flat tiled texture.
 */
public class OthersidePortalRenderer implements BlockEntityRenderer<OthersidePortalBlockEntity> {

    /** Subdivisions per face for smooth gradient + cloud effect */
    private static final int GRID = 16;

    /** Sculk-themed palette (bottom → top) */
    private static final float[][] PALETTE = {
            {  9f/255f,  12f/255f,  18f/255f},  // val 0.00 — dark sculk
            { 24f/255f,  43f/255f,  47f/255f},  // val 0.35 — mid sculk-teal
            { 55f/255f, 115f/255f, 108f/255f},  // val 0.68 — sculk cyan
            {116f/255f, 214f/255f, 196f/255f},  // val 1.00 — bright sculk-cyan
    };
    private static final float[] PALETTE_STOPS = {0.0f, 0.35f, 0.68f, 1.0f};

    /** Custom RenderType: POSITION_COLOR quads, no texture, no cull, translucent */
    private static final RenderType PORTAL_TYPE = RenderType.create(
            "otherside_portal_field",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS, 1024, false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(
                            () -> GameRenderer.getPositionColorShader()))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false)
    );

    public OthersidePortalRenderer(BlockEntityRendererProvider.Context ctx) {
        // No-op
    }

    @Override
    public void render(OthersidePortalBlockEntity be, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {

        Level level = be.getLevel();
        if (level == null) return;

        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof OthersidePortalBlock)) return;

        Direction.Axis axis = state.getValue(OthersidePortalBlock.AXIS);
        int bottomY = be.getPortalBottomY();
        int height = be.getPortalHeight();
        int blockY = be.getBlockPos().getY();

        float time = (level.getGameTime() + partialTick) * 0.001f;

        VertexConsumer vc = bufferSource.getBuffer(PORTAL_TYPE);
        poseStack.pushPose();

        // Portal slab is from z=6/16 to z=10/16 (X axis) or x=6/16 to x=10/16 (Z axis)
        if (axis == Direction.Axis.X) {
            // Portal spans X — visible faces are NORTH (z=6/16) and SOUTH (z=10/16)
            drawFace(vc, poseStack, time, bottomY, height, blockY,
                    0f, 0f, 6f/16f, 1f, 1f, 6f/16f, 0f, 0f, -1f);   // North face
            drawFace(vc, poseStack, time, bottomY, height, blockY,
                    1f, 0f, 10f/16f, 0f, 1f, 10f/16f, 0f, 0f, 1f);  // South face (reversed winding)
        } else {
            // Portal spans Z — visible faces are WEST (x=6/16) and EAST (x=10/16)
            drawFace(vc, poseStack, time, bottomY, height, blockY,
                    6f/16f, 0f, 1f, 6f/16f, 1f, 0f, -1f, 0f, 0f);   // West face
            drawFace(vc, poseStack, time, bottomY, height, blockY,
                    10f/16f, 0f, 0f, 10f/16f, 1f, 1f, 1f, 0f, 0f);  // East face (reversed winding)
        }

        poseStack.popPose();
    }

    /**
     * Draw one face of the portal as a subdivided grid with gradient + cloud colors.
     * The face is defined by two corners forming a vertical rectangle:
     *   bottom-left  = (x0, y0, z0)
     *   top-right    = (x1, y1, z1)
     * where y0=0, y1=1 (local block coords), and x/z vary for width.
     */
    private void drawFace(VertexConsumer vc, PoseStack poseStack, float time,
                          int portalBottomY, int portalHeight, int blockY,
                          float x0, float y0, float z0,
                          float x1, float y1, float z1,
                          float nx, float ny, float nz) {

        PoseStack.Pose pose = poseStack.last();

        for (int gx = 0; gx < GRID; gx++) {
            for (int gy = 0; gy < GRID; gy++) {
                float u0 = (float) gx / GRID;
                float v0 = (float) gy / GRID;
                float u1 = (float) (gx + 1) / GRID;
                float v1 = (float) (gy + 1) / GRID;

                // Center of this cell for color computation
                float uMid = (u0 + u1) * 0.5f;
                float vMid = (v0 + v1) * 0.5f;

                // World Y position of this cell's center
                float worldY = blockY + vMid;

                // Vertical gradient: t across full portal
                float t = (worldY - portalBottomY) / Math.max(1f, portalHeight);
                t = Math.max(0f, Math.min(1f, t));

                // Cloud layers (scrolling upward)
                float cloudFar  = cloudNoise(uMid * 1.0f, vMid * 1.0f - time * 20f);
                float cloudNear = cloudNoise(uMid * 2.0f + 13.7f, vMid * 2.0f - time * 50f);
                float cloud = 0.6f * cloudFar + 0.4f * cloudNear;

                // Combine: gradient + cloud modulation
                float val = t * 1.15f * (0.5f + 0.7f * cloud);
                val = Math.max(0f, Math.min(1f, val));

                // Palette ramp
                float[] color = paletteRamp(val);
                int r = (int)(color[0] * 255);
                int g = (int)(color[1] * 255);
                int b = (int)(color[2] * 255);
                int a = 240; // slightly translucent

                // Interpolate positions for the sub-quad corners
                float qx00 = lerp(x0, x1, u0);
                float qy00 = lerp(y0, y1, v0);
                float qz00 = lerp(z0, z1, u0);

                float qx10 = lerp(x0, x1, u1);
                float qy10 = lerp(y0, y1, v0);
                float qz10 = lerp(z0, z1, u1);

                float qx11 = lerp(x0, x1, u1);
                float qy11 = lerp(y0, y1, v1);
                float qz11 = lerp(z0, z1, u1);

                float qx01 = lerp(x0, x1, u0);
                float qy01 = lerp(y0, y1, v1);
                float qz01 = lerp(z0, z1, u0);

                // Emit quad (4 vertices, CCW winding for the face normal)
                vc.addVertex(pose, qx00, qy00, qz00).setColor(r, g, b, a);
                vc.addVertex(pose, qx10, qy10, qz10).setColor(r, g, b, a);
                vc.addVertex(pose, qx11, qy11, qz11).setColor(r, g, b, a);
                vc.addVertex(pose, qx01, qy01, qz01).setColor(r, g, b, a);
            }
        }
    }

    /** Linear interpolation */
    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    /** Multi-stop palette interpolation */
    private static float[] paletteRamp(float val) {
        val = Math.max(0f, Math.min(1f, val));
        for (int i = 0; i < PALETTE_STOPS.length - 1; i++) {
            if (val <= PALETTE_STOPS[i + 1]) {
                float t = (val - PALETTE_STOPS[i]) / (PALETTE_STOPS[i + 1] - PALETTE_STOPS[i]);
                t = Math.max(0f, Math.min(1f, t));
                return new float[]{
                        lerp(PALETTE[i][0], PALETTE[i + 1][0], t),
                        lerp(PALETTE[i][1], PALETTE[i + 1][1], t),
                        lerp(PALETTE[i][2], PALETTE[i + 1][2], t)
                };
            }
        }
        return PALETTE[PALETTE.length - 1];
    }

    /**
     * Pseudo-noise based on sine waves — cheap, deterministic, good enough for
     * scrolling cloud layers on a per-cell basis.
     */
    private static float pseudoNoise(float x, float y) {
        double v = Math.sin(x * 12.9898 + y * 78.233) * 43758.5453;
        return (float)(v - Math.floor(v));
    }

    /**
     * Multi-octave cloud noise from pseudoNoise.
     */
    private static float cloudNoise(float x, float y) {
        float n = 0;
        n += 0.50f * pseudoNoise(x, y);
        n += 0.25f * pseudoNoise(x * 2.01f, y * 2.01f);
        n += 0.125f * pseudoNoise(x * 4.03f, y * 4.03f);
        n += 0.0625f * pseudoNoise(x * 8.07f, y * 8.07f);
        return n / 0.9375f; // normalize
    }

    @Override
    public boolean shouldRenderOffScreen(OthersidePortalBlockEntity be) {
        return false; // portal blocks are normal size
    }

    @Override
    public int getViewDistance() {
        return 64; // visible from far away
    }
}
