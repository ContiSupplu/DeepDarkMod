package com._jackoboy.otherside.client.renderer;

import com._jackoboy.otherside.client.model.WarbModel;
import com._jackoboy.otherside.entity.WarbEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WarbRenderer extends MobRenderer<WarbEntity, WarbModel<WarbEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("otherside", "textures/entity/hpc_warb.png");

    public WarbRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WarbModel<>(ctx.bakeLayer(WarbModel.LAYER)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(WarbEntity entity) {
        return TEXTURE;
    }
}
