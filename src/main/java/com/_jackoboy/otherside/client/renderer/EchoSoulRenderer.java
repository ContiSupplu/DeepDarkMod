package com._jackoboy.otherside.client.renderer;

import com._jackoboy.otherside.client.model.EchoSoulModel;
import com._jackoboy.otherside.entity.EchoSoulEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class EchoSoulRenderer extends MobRenderer<EchoSoulEntity, EchoSoulModel<EchoSoulEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("otherside", "textures/entity/echo_soul.png");

    public EchoSoulRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new EchoSoulModel<>(ctx.bakeLayer(EchoSoulModel.LAYER)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(EchoSoulEntity entity) {
        return TEXTURE;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(EchoSoulEntity entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        return RenderType.entityTranslucent(TEXTURE);
    }

    @Override
    public boolean shouldRender(EchoSoulEntity entity, net.minecraft.client.renderer.culling.Frustum frustum,
                                double x, double y, double z) {
        return true;
    }
}
