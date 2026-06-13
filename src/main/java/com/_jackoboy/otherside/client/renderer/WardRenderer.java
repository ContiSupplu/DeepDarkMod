package com._jackoboy.otherside.client.renderer;

import com._jackoboy.otherside.client.model.WardModel;
import com._jackoboy.otherside.entity.WardEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WardRenderer extends MobRenderer<WardEntity, WardModel<WardEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("otherside", "textures/entity/hpc_ward.png");

    public WardRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WardModel<>(ctx.bakeLayer(WardModel.LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(WardEntity entity) {
        return TEXTURE;
    }
}
