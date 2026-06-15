package com._jackoboy.otherside.client.renderer;

import com._jackoboy.otherside.client.model.WhisperingEchoModel;
import com._jackoboy.otherside.entity.WhisperingEchoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Renderer for the Whispering Echo — small floating wisp companion.
 * Uses TRANSLUCENT render type because the body texture has alpha=64 (semi-transparent).
 * Scaled 2.7× (~0.84 blocks).
 */
public class WhisperingEchoRenderer extends MobRenderer<WhisperingEchoEntity, WhisperingEchoModel<WhisperingEchoEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("otherside", "textures/entity/whispering_echo.png");

    public WhisperingEchoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WhisperingEchoModel<>(ctx.bakeLayer(WhisperingEchoModel.LAYER)), 0.3F);
    }

    @Override
    protected void scale(WhisperingEchoEntity entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(2.7F, 2.7F, 2.7F);
    }

    @Nullable
    @Override
    protected RenderType getRenderType(WhisperingEchoEntity entity, boolean bodyVisible,
                                        boolean translucent, boolean glowing) {
        return RenderType.entityTranslucent(getTextureLocation(entity));
    }

    @Override
    public ResourceLocation getTextureLocation(WhisperingEchoEntity entity) {
        return TEXTURE;
    }
}
