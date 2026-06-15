package com._jackoboy.otherside.client.renderer;

import com._jackoboy.otherside.client.model.WhisperingEchoModel;
import com._jackoboy.otherside.entity.WhisperingEchoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderer for the Whispering Echo — small floating wisp companion.
 * Simple texture, no glow layer in v1.
 */
public class WhisperingEchoRenderer extends MobRenderer<WhisperingEchoEntity, WhisperingEchoModel<WhisperingEchoEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("otherside", "textures/entity/whispering_echo.png");

    public WhisperingEchoRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WhisperingEchoModel<>(ctx.bakeLayer(WhisperingEchoModel.LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(WhisperingEchoEntity entity) {
        return TEXTURE;
    }
}
