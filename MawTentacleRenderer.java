package com._jackoboy.otherside.client.renderer;

import com._jackoboy.otherside.client.model.MawTentacleModel;
import com._jackoboy.otherside.entity.MawTentacleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
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

    // Per-entity SIZE variance (deterministic from network id) so the ring reads as
    // individual grown limbs, not copies. Width/depth 0.82–1.28, height a touch more varied.
    @Override
    protected void scale(MawTentacleEntity entity, PoseStack pose, float partialTick) {
        float r = (((entity.getId() * 0x9E3779B97F4A7C15L) >>> 40) & 0xFFFF) / 65535.0f;
        float s = 0.82f + r * 0.46f;
        float sy = s * (0.92f + r * 0.20f);
        pose.scale(s, sy, s);
    }

    @Override
    public boolean shouldRender(MawTentacleEntity entity, net.minecraft.client.renderer.culling.Frustum frustum,
                                double x, double y, double z) {
        return true;
    }
}
