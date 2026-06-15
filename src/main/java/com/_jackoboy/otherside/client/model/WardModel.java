package com._jackoboy.otherside.client.model;

import com._jackoboy.otherside.entity.WardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class WardModel<T extends WardEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("otherside", "ward"), "main");

    private final ModelPart body;
    private final ModelPart bodyRear;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart upperJaw;
    private final ModelPart lowerJaw;
    private final ModelPart dorsalFin;
    private final ModelPart leftLeg1, leftLeg2, leftLeg3;
    private final ModelPart rightLeg1, rightLeg2, rightLeg3;

    public WardModel(ModelPart root) {
        this.body = root.getChild("body");
        this.bodyRear = body.getChild("body_rear");
        this.neck = body.getChild("neck");
        this.head = neck.getChild("head");
        this.upperJaw = head.getChild("upper_jaw");
        this.lowerJaw = head.getChild("lower_jaw");
        this.dorsalFin = body.getChild("dorsal_fin");
        this.leftLeg1 = body.getChild("left_leg1");
        this.leftLeg2 = body.getChild("left_leg2");
        this.leftLeg3 = body.getChild("left_leg3");
        this.rightLeg1 = body.getChild("right_leg1");
        this.rightLeg2 = body.getChild("right_leg2");
        this.rightLeg3 = body.getChild("right_leg3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // Main body: 14x7x8
        PartDefinition bodyDef = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-7.0F, -7.0F, -4.0F, 14.0F, 7.0F, 8.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));

        // Body rear: 14x9x13
        bodyDef.addOrReplaceChild("body_rear",
                CubeListBuilder.create().texOffs(0, 15)
                        .addBox(-7.0F, -9.0F, 0.0F, 14.0F, 9.0F, 13.0F),
                PartPose.offset(0.0F, 0.0F, 4.0F));

        // Neck: 10x9x3
        PartDefinition neckDef = bodyDef.addOrReplaceChild("neck",
                CubeListBuilder.create().texOffs(44, 0)
                        .addBox(-5.0F, -5.0F, -3.0F, 10.0F, 9.0F, 3.0F),
                PartPose.offset(0.0F, -2.0F, -4.0F));

        // Head container
        PartDefinition headDef = neckDef.addOrReplaceChild("head",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -1.0F, -3.0F));

        // Upper jaw: 10x4x10
        headDef.addOrReplaceChild("upper_jaw",
                CubeListBuilder.create().texOffs(44, 12)
                        .addBox(-5.0F, -4.0F, -10.0F, 10.0F, 4.0F, 10.0F),
                PartPose.ZERO);

        // Lower jaw: 10x3x10
        headDef.addOrReplaceChild("lower_jaw",
                CubeListBuilder.create().texOffs(44, 26)
                        .addBox(-5.0F, 0.0F, -10.0F, 10.0F, 3.0F, 10.0F),
                PartPose.ZERO);

        // Whiskers
        headDef.addOrReplaceChild("whiskers",
                CubeListBuilder.create().texOffs(84, 0)
                        .addBox(-8.0F, -2.0F, 0.0F, 16.0F, 4.0F, 0.0F),
                PartPose.offset(0.0F, -1.0F, -10.0F));

        // Dorsal fin
        bodyDef.addOrReplaceChild("dorsal_fin",
                CubeListBuilder.create().texOffs(0, 37)
                        .addBox(0.0F, -9.0F, -4.0F, 0.0F, 9.0F, 25.0F),
                PartPose.offset(0.0F, -7.0F, 0.0F));

        // Left legs (3 pairs with outer segments and feelers)
        for (int i = 0; i < 3; i++) {
            float zOff = -3.0F + i * 4.0F;
            PartDefinition legInner = bodyDef.addOrReplaceChild("left_leg" + (i + 1),
                    CubeListBuilder.create().texOffs(0, 62 + i * 7)
                            .addBox(-8.0F, -1.5F, -1.5F, 8.0F, 3.0F, 3.0F),
                    PartPose.offset(-7.0F, -1.0F, zOff));

            legInner.addOrReplaceChild("outer",
                    CubeListBuilder.create().texOffs(22, 62 + i * 7)
                            .addBox(-8.0F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
                    PartPose.offset(-8.0F, 0.0F, 0.0F));

            legInner.addOrReplaceChild("feeler",
                    CubeListBuilder.create().texOffs(84, 4 + i * 4)
                            .addBox(-4.0F, 0.0F, 0.0F, 4.0F, 3.0F, 0.0F),
                    PartPose.offset(-16.0F, 1.0F, 0.0F));
        }

        // Right legs (3 pairs, mirrored)
        for (int i = 0; i < 3; i++) {
            float zOff = -3.0F + i * 4.0F;
            PartDefinition legInner = bodyDef.addOrReplaceChild("right_leg" + (i + 1),
                    CubeListBuilder.create().texOffs(0, 83 + i * 7).mirror()
                            .addBox(0.0F, -1.5F, -1.5F, 8.0F, 3.0F, 3.0F),
                    PartPose.offset(7.0F, -1.0F, zOff));

            legInner.addOrReplaceChild("outer",
                    CubeListBuilder.create().texOffs(22, 83 + i * 7).mirror()
                            .addBox(0.0F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
                    PartPose.offset(8.0F, 0.0F, 0.0F));

            legInner.addOrReplaceChild("feeler",
                    CubeListBuilder.create().texOffs(84, 16 + i * 4).mirror()
                            .addBox(0.0F, 0.0F, 0.0F, 4.0F, 3.0F, 0.0F),
                    PartPose.offset(16.0F, 1.0F, 0.0F));
        }

        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // Head look (through neck)
        this.neck.yRot = netHeadYaw * ((float) Math.PI / 180F) * 0.5F;
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F) * 0.5F;
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        // Jaw animation - idle breathing
        this.lowerJaw.xRot = 0.1F + Mth.sin(ageInTicks * 0.12F) * 0.08F;
        this.upperJaw.xRot = -0.02F + Mth.sin(ageInTicks * 0.12F) * -0.03F;

        // Leg animation (centipede-like wave)
        float legSwing = limbSwing * 0.8F;
        float legAmount = limbSwingAmount * 0.6F;
        for (int i = 0; i < 3; i++) {
            float phase = i * ((float) Math.PI / 3.0F);
            ModelPart leftLeg = switch (i) {
                case 0 -> this.leftLeg1;
                case 1 -> this.leftLeg2;
                default -> this.leftLeg3;
            };
            ModelPart rightLeg = switch (i) {
                case 0 -> this.rightLeg1;
                case 1 -> this.rightLeg2;
                default -> this.rightLeg3;
            };

            leftLeg.zRot = -0.2F + Mth.cos(legSwing + phase) * legAmount;
            rightLeg.zRot = 0.2F - Mth.cos(legSwing + phase + (float) Math.PI) * legAmount;

            leftLeg.yRot = Mth.sin(legSwing + phase) * legAmount * 0.3F;
            rightLeg.yRot = -Mth.sin(legSwing + phase + (float) Math.PI) * legAmount * 0.3F;
        }

        // Dorsal fin sway
        this.dorsalFin.yRot = Mth.sin(ageInTicks * 0.04F) * 0.08F;

        // Body rear slight undulation
        this.bodyRear.yRot = Mth.sin(ageInTicks * 0.06F) * 0.05F;

        // === ATTACK ANIMATIONS ===
        int attackState = entity.getAttackState();
        float attackProgress = entity.getAttackAnimProgress();

        if (attackState == 1 && attackProgress > 0) {
            // BITE: jaws snap open wide, neck lunges forward
            float snap = Mth.sin(attackProgress * (float) Math.PI);

            // Jaws open wide (upper rotates up, lower rotates down)
            this.upperJaw.xRot = -snap * 0.6F;
            this.lowerJaw.xRot = snap * 0.9F;

            // Neck lunges forward and down
            this.neck.xRot = snap * 0.4F;
            this.head.xRot += snap * 0.2F;

            // Body crouches slightly
            this.body.xRot = snap * 0.1F;

            // Legs grip (splay and tense)
            float grip = snap * 0.25F;
            this.leftLeg1.zRot -= grip;
            this.leftLeg2.zRot -= grip;
            this.leftLeg3.zRot -= grip;
            this.rightLeg1.zRot += grip;
            this.rightLeg2.zRot += grip;
            this.rightLeg3.zRot += grip;
        } else if (attackState == 2 && attackProgress > 0) {
            // LEAP: body coils then springs
            float coil = Mth.sin(attackProgress * (float) Math.PI);

            // Body tilts forward (diving pose)
            this.body.xRot = -coil * 0.4F;

            // Legs fold back
            for (int i = 0; i < 3; i++) {
                ModelPart leftLeg = switch (i) {
                    case 0 -> this.leftLeg1;
                    case 1 -> this.leftLeg2;
                    default -> this.leftLeg3;
                };
                ModelPart rightLeg = switch (i) {
                    case 0 -> this.rightLeg1;
                    case 1 -> this.rightLeg2;
                    default -> this.rightLeg3;
                };
                leftLeg.zRot = -0.6F * coil;
                rightLeg.zRot = 0.6F * coil;
                leftLeg.yRot = 0.3F * coil;
                rightLeg.yRot = -0.3F * coil;
            }

            // Jaws open mid-leap for the incoming bite
            this.upperJaw.xRot = -coil * 0.3F;
            this.lowerJaw.xRot = coil * 0.5F;

            // Neck extends forward
            this.neck.xRot = coil * 0.3F;

            // Dorsal fin presses flat
            this.dorsalFin.yRot = 0;
        } else {
            this.body.xRot = 0;
            this.neck.xRot = 0;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        body.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
