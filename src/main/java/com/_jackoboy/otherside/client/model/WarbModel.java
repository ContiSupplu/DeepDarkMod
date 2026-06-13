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

public class WarbModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("otherside", "warb"), "main");

    private final ModelPart body;
    private final ModelPart upperTorso;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart leftArm;
    private final ModelPart leftForearm;
    private final ModelPart rightArm;
    private final ModelPart rightForearm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public WarbModel(ModelPart root) {
        this.body = root.getChild("body");
        this.upperTorso = body.getChild("upper_torso");
        this.head = upperTorso.getChild("head");
        this.jaw = head.getChild("jaw");
        this.leftArm = upperTorso.getChild("left_arm");
        this.leftForearm = leftArm.getChild("left_forearm");
        this.rightArm = upperTorso.getChild("right_arm");
        this.rightForearm = rightArm.getChild("right_forearm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Lower torso: 10x16x5, centered at body
        PartDefinition bodyDef = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-5.0F, -16.0F, -2.5F, 10.0F, 16.0F, 5.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));

        // Upper torso: 11x4x7 on top of lower torso
        PartDefinition upperDef = bodyDef.addOrReplaceChild("upper_torso",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-5.5F, -4.0F, -3.5F, 11.0F, 4.0F, 7.0F),
                PartPose.offset(0.0F, -16.0F, 0.0F));

        // Head: 10x8x10 on top of upper torso
        PartDefinition headDef = upperDef.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(36, 0)
                        .addBox(-5.0F, -8.0F, -5.0F, 10.0F, 8.0F, 10.0F),
                PartPose.offset(0.0F, -4.0F, 0.0F));

        // Jaw plates (lower jaw)
        headDef.addOrReplaceChild("jaw",
                CubeListBuilder.create().texOffs(36, 18)
                        .addBox(-4.0F, 0.0F, -5.0F, 8.0F, 3.0F, 10.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // Head tendrils (flat vertical planes)
        headDef.addOrReplaceChild("tendrils",
                CubeListBuilder.create().texOffs(76, 0)
                        .addBox(-5.0F, -10.0F, 0.0F, 10.0F, 10.0F, 0.0F),
                PartPose.offset(0.0F, -8.0F, 0.0F));

        // Left arm (massive): 6x10x8 upper arm
        PartDefinition leftArmDef = upperDef.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(0, 37)
                        .addBox(-6.0F, 0.0F, -4.0F, 6.0F, 10.0F, 8.0F),
                PartPose.offset(-5.5F, -2.0F, 0.0F));

        // Left forearm: 6x10x8
        PartDefinition leftForearmDef = leftArmDef.addOrReplaceChild("left_forearm",
                CubeListBuilder.create().texOffs(0, 55)
                        .addBox(-6.0F, 0.0F, -4.0F, 6.0F, 10.0F, 8.0F),
                PartPose.offset(0.0F, 10.0F, 0.0F));

        // Shoulder spikes on left arm
        leftArmDef.addOrReplaceChild("shoulder_spikes",
                CubeListBuilder.create().texOffs(28, 37)
                        .addBox(-1.5F, -6.0F, -1.5F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(-3.0F, 0.0F, 0.0F));

        // Left arm tendrils (flat planes hanging down)
        leftForearmDef.addOrReplaceChild("left_tendrils",
                CubeListBuilder.create().texOffs(76, 10)
                        .addBox(-3.0F, 10.0F, 0.0F, 6.0F, 8.0F, 0.0F),
                PartPose.ZERO);

        // Right arm (thin): 3x11x3 upper
        PartDefinition rightArmDef = upperDef.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(40, 37)
                        .addBox(0.0F, 0.0F, -1.5F, 3.0F, 11.0F, 3.0F),
                PartPose.offset(5.5F, -2.0F, 0.0F));

        // Right forearm: 3x11x3
        rightArmDef.addOrReplaceChild("right_forearm",
                CubeListBuilder.create().texOffs(52, 37)
                        .addBox(0.0F, 0.0F, -1.5F, 3.0F, 11.0F, 3.0F),
                PartPose.offset(0.0F, 11.0F, 0.0F));

        // Left leg: 3x10x2
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 73)
                        .addBox(-1.5F, 0.0F, -1.0F, 3.0F, 10.0F, 2.0F),
                PartPose.offset(-2.3F, 14.0F, 0.0F));

        // Right leg: 3x10x2
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(10, 73)
                        .addBox(-1.5F, 0.0F, -1.0F, 3.0F, 10.0F, 2.0F),
                PartPose.offset(2.3F, 14.0F, 0.0F));

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Head look
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        // Walking cycle - legs
        float walkSpeed = limbSwing * 0.6662F;
        float walkAmount = limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(walkSpeed) * 1.0F * walkAmount;
        this.rightLeg.xRot = Mth.cos(walkSpeed + (float) Math.PI) * 1.0F * walkAmount;

        // Arms swing opposite to legs
        this.leftArm.xRot = Mth.cos(walkSpeed + (float) Math.PI) * 0.6F * walkAmount;
        this.rightArm.xRot = Mth.cos(walkSpeed) * 0.8F * walkAmount;

        // Left forearm slight bend
        this.leftForearm.xRot = -0.2F + Mth.sin(ageInTicks * 0.067F) * 0.1F;

        // Right forearm slight swing
        this.rightForearm.xRot = -0.15F + Mth.sin(ageInTicks * 0.08F) * 0.08F;

        // Jaw idle breathing
        this.jaw.xRot = 0.05F + Mth.sin(ageInTicks * 0.1F) * 0.03F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, buffer, packedLight, packedOverlay, color);
        leftLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
        rightLeg.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
