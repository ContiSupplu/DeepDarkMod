package com._jackoboy.otherside.client.renderer;

import com._jackoboy.otherside.client.model.MawTentacleModel;
import com._jackoboy.otherside.entity.MawTentacleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MawTentacleRenderer extends MobRenderer<MawTentacleEntity, MawTentacleModel<MawTentacleEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("otherside", "textures/entity/maw_tentacle.png");

    public MawTentacleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MawTentacleModel<>(ctx.bakeLayer(MawTentacleModel.LAYER)), 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(MawTentacleEntity entity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(MawTentacleEntity entity, net.minecraft.client.renderer.culling.Frustum frustum,
                                double x, double y, double z) {
        // Always render when in tracking range — expanded bounding box handles the rest
        return true;
    }
}
