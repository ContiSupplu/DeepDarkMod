package com._jackoboy.otherside.client.renderer;

import com._jackoboy.otherside.client.model.WugModel;
import com._jackoboy.otherside.entity.WugEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WugRenderer extends MobRenderer<WugEntity, WugModel<WugEntity>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("otherside", "textures/entity/hpc_wug.png");

    public WugRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WugModel<>(ctx.bakeLayer(WugModel.LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(WugEntity entity) {
        return TEXTURE;
    }
}
