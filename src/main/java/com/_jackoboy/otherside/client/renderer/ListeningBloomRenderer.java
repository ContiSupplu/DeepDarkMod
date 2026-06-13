package com._jackoboy.otherside.client.renderer;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.client.model.ListeningBloomModel;
import com._jackoboy.otherside.entity.ListeningBloomEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Renderer for the Listening Bloom.
 * Shadow = 0 (immobile). Scales by the entity's bloomScale (1.0 full / 0.4 bud).
 * Emissive focus glow on the dish (brightest in ALERT_LOCK) via RenderType.eyes().
 */
public class ListeningBloomRenderer extends MobRenderer<ListeningBloomEntity, ListeningBloomModel<ListeningBloomEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("otherside", "textures/entity/listening_bloom.png");
    private static final ResourceLocation GLOW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("otherside", "textures/entity/listening_bloom_glow.png");

    public ListeningBloomRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new ListeningBloomModel<>(ctx.bakeLayer(ListeningBloomModel.LAYER)), 0F);
    }

    @Override
    public ResourceLocation getTextureLocation(ListeningBloomEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(ListeningBloomEntity entity, PoseStack poseStack, float partialTick) {
        float s = entity.getBloomScale();
        poseStack.scale(s, s, s);
    }

    @Override
    public void render(ListeningBloomEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        // Emissive glow layer (if enabled and glow texture exists)
        if (OthersideConfig.SERVER.bloomGlowEnabled.get()) {
            // The glow layer renders the focus/membrane cubes at full brightness
            // brightest during ALERT_LOCK
            float glowAlpha = entity.getState() == ListeningBloomEntity.BloomState.ALERT_LOCK ? 1.0F : 0.5F;
            if (entity.getState() != ListeningBloomEntity.BloomState.DORMANT
                    && entity.getState() != ListeningBloomEntity.BloomState.FOLD) {
                // We render the model again with the eyes render type for the glow
                // This will render the glow texture at full brightness, ignoring light level
                poseStack.pushPose();
                float s = entity.getBloomScale();
                poseStack.scale(s, s, s);
                int alpha = (int)(glowAlpha * 255) & 0xFF;
                int color = (alpha << 24) | 0xFFFFFF; // ARGB with variable alpha
                this.getModel().renderToBuffer(poseStack,
                        bufferSource.getBuffer(RenderType.eyes(GLOW_TEXTURE)),
                        packedLight, getOverlayCoords(entity, 0F), color);
                poseStack.popPose();
            }
        }
    }

    @Override
    public boolean shouldRender(ListeningBloomEntity entity, net.minecraft.client.renderer.culling.Frustum frustum,
                                double x, double y, double z) {
        return true; // always render when in range
    }
}
