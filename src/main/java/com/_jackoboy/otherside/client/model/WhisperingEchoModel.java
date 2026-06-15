package com._jackoboy.otherside.client.model;

import com._jackoboy.otherside.entity.WhisperingEchoEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

/**
 * Whispering Echo model — small floating wisp companion.
 * <p>
 * Hierarchy: root → echo (pivot) → body (5×5×5) + face (3×3×3).
 * Adapted from Blockbench export. 32×32 texture sheet.
 */
public class WhisperingEchoModel<T extends WhisperingEchoEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("otherside", "whispering_echo"), "main");

    private final ModelPart root;
    private final ModelPart echo;
    private final ModelPart body;
    private final ModelPart face;

    public WhisperingEchoModel(ModelPart root) {
        this.root = root;
        this.echo = root.getChild("echo");
        this.body = this.echo.getChild("body");
        this.face = this.echo.getChild("face");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition echoDef = root.addOrReplaceChild("echo",
                CubeListBuilder.create(),
                PartPose.offset(-0.5F, 18.0F, 0.5F));

        echoDef.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        echoDef.addOrReplaceChild("face",
                CubeListBuilder.create().texOffs(0, 10)
                        .addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -2.5F, 0.0F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // Wire animation states → AnimationDefinitions
        this.animate(entity.idleAnimState, WhisperingEchoAnimations.IDLE, ageInTicks);
        this.animate(entity.followAnimState, WhisperingEchoAnimations.FOLLOW, ageInTicks);
        this.animate(entity.attachmentAnimState, WhisperingEchoAnimations.ATTACHMENT, ageInTicks);
        this.animate(entity.restAnimState, WhisperingEchoAnimations.REST, ageInTicks);
        this.animate(entity.deathAnimState, WhisperingEchoAnimations.DEATH, ageInTicks);
        this.animate(entity.corruptAnimState, WhisperingEchoAnimations.CORRUPT, ageInTicks);
        this.animate(entity.attackAnimState, WhisperingEchoAnimations.ATTACK, ageInTicks);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}
