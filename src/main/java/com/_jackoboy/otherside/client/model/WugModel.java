package com._jackoboy.otherside.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class WugModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("otherside", "wug"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart leftLeg1, leftLeg2, leftLeg3;
    private final ModelPart rightLeg1, rightLeg2, rightLeg3;
    private final ModelPart dorsalFin;

    public WugModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = body.getChild("head");
        this.jaw = head.getChild("jaw");
        this.leftLeg1 = body.getChild("left_leg1");
        this.leftLeg2 = body.getChild("left_leg2");
        this.leftLeg3 = body.getChild("left_leg3");
        this.rightLeg1 = body.getChild("right_leg1");
        this.rightLeg2 = body.getChild("right_leg2");
        this.rightLeg3 = body.getChild("right_leg3");
        this.dorsalFin = body.getChild("dorsal_fin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Body: 11x4x15, centered
        PartDefinition bodyDef = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(22, 10)
                        .addBox(-5.5F, -2.0F, -7.5F, 11.0F, 4.0F, 15.0F),
                PartPose.offset(0.0F, 21.0F, 0.0F));

        // Head: 8x4x6 at front of body
        PartDefinition headDef = bodyDef.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(8, 28)
                        .addBox(-4.0F, -2.0F, -6.0F, 8.0F, 4.0F, 6.0F),
                PartPose.offset(0.0F, 0.0F, -7.5F));

        // Jaw (lower jaw)
        headDef.addOrReplaceChild("jaw",
                CubeListBuilder.create().texOffs(30, 8)
                        .addBox(-4.0F, 0.0F, -4.0F, 8.0F, 2.0F, 4.0F),
                PartPose.offset(0.0F, 2.0F, 0.0F));

        // Antennae (vertical plane at head front)
        headDef.addOrReplaceChild("antennae",
                CubeListBuilder.create().texOffs(19, 15)
                        .addBox(-9.5F, -8.0F, 0.0F, 19.0F, 8.0F, 0.0F),
                PartPose.offset(0.0F, -2.0F, -6.0F));

        // Dorsal fin (vertical plane along body centerline)
        bodyDef.addOrReplaceChild("dorsal_fin",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(0.0F, -8.0F, -7.0F, 0.0F, 8.0F, 19.0F),
                PartPose.offset(0.0F, -2.0F, 0.0F));

        // Left legs (3 flat plates)
        for (int i = 0; i < 3; i++) {
            float zOff = -5.0F + i * 5.0F;
            bodyDef.addOrReplaceChild("left_leg" + (i + 1),
                    CubeListBuilder.create().texOffs(0, 0)
                            .addBox(-8.0F, 0.0F, -1.0F, 8.0F, 0.0F, 2.0F),
                    PartPose.offset(-5.5F, 2.0F, zOff));
        }

        // Right legs (3 flat plates, mirrored)
        for (int i = 0; i < 3; i++) {
            float zOff = -5.0F + i * 5.0F;
            bodyDef.addOrReplaceChild("right_leg" + (i + 1),
                    CubeListBuilder.create().texOffs(0, 0).mirror()
                            .addBox(0.0F, 0.0F, -1.0F, 8.0F, 0.0F, 2.0F),
                    PartPose.offset(5.5F, 2.0F, zOff));
        }

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Head look
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        // Leg animation (spider-like scuttling)
        float legSwing = limbSwing * 0.6662F;
        float legAmount = limbSwingAmount * 0.8F;
        this.leftLeg1.zRot = -0.3F + Mth.cos(legSwing) * legAmount;
        this.leftLeg2.zRot = -0.3F + Mth.cos(legSwing + (float) Math.PI * 0.66F) * legAmount;
        this.leftLeg3.zRot = -0.3F + Mth.cos(legSwing + (float) Math.PI * 1.33F) * legAmount;
        this.rightLeg1.zRot = 0.3F - Mth.cos(legSwing + (float) Math.PI) * legAmount;
        this.rightLeg2.zRot = 0.3F - Mth.cos(legSwing + (float) Math.PI * 1.66F) * legAmount;
        this.rightLeg3.zRot = 0.3F - Mth.cos(legSwing + (float) Math.PI * 0.33F) * legAmount;

        // Jaw slight open/close
        this.jaw.xRot = 0.1F + Mth.sin(ageInTicks * 0.1F) * 0.05F;

        // Dorsal fin sway
        this.dorsalFin.yRot = Mth.sin(ageInTicks * 0.05F) * 0.1F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
