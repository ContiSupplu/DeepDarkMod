package com._jackoboy.otherside.client.model;

import com._jackoboy.otherside.client.animation.MawTentacleAnimations;
import com._jackoboy.otherside.entity.MawTentacleEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

/**
 * Hierarchical model for the Maw Tentacle entity.
 * GENERATED from assets_source/maw_tentacle.bbmodel (41 cubes, box-UV, baked S-curve coil in
 * bone rest rotations). Bone names match MawTentacleAnimations exactly. -Y = up (renderer Y-flip).
 */
public class MawTentacleModel<T extends MawTentacleEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("otherside", "maw_tentacle"), "main");

    private final ModelPart root;

    public MawTentacleModel(ModelPart root) {
        this.root = root;
    }

    @Override
    public ModelPart root() {
        return root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition p_collar_20 = root.addOrReplaceChild("collar_20",
                CubeListBuilder.create()
                .texOffs(176, 34).addBox(5F, -3F, -2.5F, 8F, 3F, 5F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.34907F, -0.05914F));

        PartDefinition p_collar_95 = root.addOrReplaceChild("collar_95",
                CubeListBuilder.create()
                .texOffs(176, 34).addBox(5F, -3F, -2.5F, 8F, 3F, 5F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 1.65806F, 0.01268F));

        PartDefinition p_collar_170 = root.addOrReplaceChild("collar_170",
                CubeListBuilder.create()
                .texOffs(176, 34).addBox(5F, -3F, -2.5F, 8F, 3F, 5F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 2.96706F, 0.1149F));

        PartDefinition p_collar_250 = root.addOrReplaceChild("collar_250",
                CubeListBuilder.create()
                .texOffs(176, 34).addBox(5F, -3F, -2.5F, 8F, 3F, 5F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 4.36332F, -0.11424F));

        PartDefinition p_collar_320 = root.addOrReplaceChild("collar_320",
                CubeListBuilder.create()
                .texOffs(176, 34).addBox(5F, -3F, -2.5F, 8F, 3F, 5F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 5.58505F, -0.0695F));

        PartDefinition p_seg0 = root.addOrReplaceChild("seg0",
                CubeListBuilder.create()
                .texOffs(0, 0).addBox(-7F, -20F, -7F, 14F, 20F, 14F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -0.17453F));

        PartDefinition p_seg0_oct = p_seg0.addOrReplaceChild("seg0_oct",
                CubeListBuilder.create()
                .texOffs(0, 0).addBox(-6.44F, -20F, -6.44F, 12.88F, 20F, 12.88F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.7854F, 0F));

        PartDefinition p_seg1 = p_seg0.addOrReplaceChild("seg1",
                CubeListBuilder.create()
                .texOffs(56, 0).addBox(-6.5F, -18F, -6.5F, 13F, 18F, 13F)
                .texOffs(148, 34).addBox(6.5F, -6.9F, -1.5F, 3F, 3F, 3F)
                .texOffs(148, 34).addBox(6.5F, -14.1F, -1.5F, 3F, 3F, 3F),
                PartPose.offsetAndRotation(0F, -20F, 0F, 0F, 0.08727F, -0.13963F));

        PartDefinition p_seg1_oct = p_seg1.addOrReplaceChild("seg1_oct",
                CubeListBuilder.create()
                .texOffs(56, 0).addBox(-5.98F, -18F, -5.98F, 11.96F, 18F, 11.96F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.7854F, 0F));

        PartDefinition p_fin1_rot = p_seg1.addOrReplaceChild("fin1_rot",
                CubeListBuilder.create()
                .texOffs(92, 34).addBox(6F, -16F, 0F, 7F, 14F, 0F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.69813F, 0F));

        PartDefinition p_seg2 = p_seg1.addOrReplaceChild("seg2",
                CubeListBuilder.create()
                .texOffs(108, 0).addBox(-6F, -16F, -6F, 12F, 16F, 12F)
                .texOffs(148, 34).addBox(-9F, -6.3F, -1.5F, 3F, 3F, 3F)
                .texOffs(148, 34).addBox(-9F, -12.7F, -1.5F, 3F, 3F, 3F),
                PartPose.offsetAndRotation(0F, -18F, 0F, 0F, 0.13963F, -0.06981F));

        PartDefinition p_seg2_oct = p_seg2.addOrReplaceChild("seg2_oct",
                CubeListBuilder.create()
                .texOffs(108, 0).addBox(-5.52F, -16F, -5.52F, 11.04F, 16F, 11.04F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.7854F, 0F));

        PartDefinition p_fin2_rot = p_seg2.addOrReplaceChild("fin2_rot",
                CubeListBuilder.create()
                .texOffs(106, 34).addBox(5.5F, -14F, 0F, 7F, 12F, 0F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 3.49066F, 0F));

        PartDefinition p_seg3 = p_seg2.addOrReplaceChild("seg3",
                CubeListBuilder.create()
                .texOffs(156, 0).addBox(-5F, -15F, -5F, 10F, 15F, 10F)
                .texOffs(148, 34).addBox(5F, -6F, -1.5F, 3F, 3F, 3F)
                .texOffs(148, 34).addBox(5F, -12F, -1.5F, 3F, 3F, 3F),
                PartPose.offsetAndRotation(0F, -16F, 0F, 0F, 0.17453F, 0.06981F));

        PartDefinition p_seg3_oct = p_seg3.addOrReplaceChild("seg3_oct",
                CubeListBuilder.create()
                .texOffs(156, 0).addBox(-4.6F, -15F, -4.6F, 9.2F, 15F, 9.2F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.7854F, 0F));

        PartDefinition p_fin3_rot = p_seg3.addOrReplaceChild("fin3_rot",
                CubeListBuilder.create()
                .texOffs(120, 34).addBox(4.5F, -13F, 0F, 7F, 11F, 0F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 1.91986F, 0F));

        PartDefinition p_seg4 = p_seg3.addOrReplaceChild("seg4",
                CubeListBuilder.create()
                .texOffs(196, 0).addBox(-4.5F, -13F, -4.5F, 9F, 13F, 9F)
                .texOffs(148, 34).addBox(-7.5F, -5.4F, -1.5F, 3F, 3F, 3F)
                .texOffs(148, 34).addBox(-7.5F, -10.6F, -1.5F, 3F, 3F, 3F),
                PartPose.offsetAndRotation(0F, -15F, 0F, 0F, 0.17453F, 0.13963F));

        PartDefinition p_seg4_oct = p_seg4.addOrReplaceChild("seg4_oct",
                CubeListBuilder.create()
                .texOffs(196, 0).addBox(-4.14F, -13F, -4.14F, 8.28F, 13F, 8.28F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.7854F, 0F));

        PartDefinition p_seg5 = p_seg4.addOrReplaceChild("seg5",
                CubeListBuilder.create()
                .texOffs(0, 34).addBox(-4F, -12F, -4F, 8F, 12F, 8F)
                .texOffs(148, 34).addBox(4F, -5.1F, -1.5F, 3F, 3F, 3F)
                .texOffs(148, 34).addBox(4F, -9.9F, -1.5F, 3F, 3F, 3F),
                PartPose.offsetAndRotation(0F, -13F, 0F, 0F, 0.15708F, 0.17453F));

        PartDefinition p_seg5_oct = p_seg5.addOrReplaceChild("seg5_oct",
                CubeListBuilder.create()
                .texOffs(0, 34).addBox(-3.68F, -12F, -3.68F, 7.36F, 12F, 7.36F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.7854F, 0F));

        PartDefinition p_fin5_rot = p_seg5.addOrReplaceChild("fin5_rot",
                CubeListBuilder.create()
                .texOffs(134, 34).addBox(3.5F, -10F, 0F, 7F, 8F, 0F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 5.23599F, 0F));

        PartDefinition p_seg6 = p_seg5.addOrReplaceChild("seg6",
                CubeListBuilder.create()
                .texOffs(32, 34).addBox(-3F, -10F, -3F, 6F, 10F, 6F),
                PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0.13963F, 0.13963F));

        PartDefinition p_seg6_oct = p_seg6.addOrReplaceChild("seg6_oct",
                CubeListBuilder.create()
                .texOffs(32, 34).addBox(-2.76F, -10F, -2.76F, 5.52F, 10F, 5.52F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.7854F, 0F));

        PartDefinition p_seg7 = p_seg6.addOrReplaceChild("seg7",
                CubeListBuilder.create()
                .texOffs(56, 34).addBox(-2.5F, -8F, -2.5F, 5F, 8F, 5F),
                PartPose.offsetAndRotation(0F, -10F, 0F, 0F, 0.10472F, 0.10472F));

        PartDefinition p_seg7_oct = p_seg7.addOrReplaceChild("seg7_oct",
                CubeListBuilder.create()
                .texOffs(56, 34).addBox(-2.3F, -8F, -2.3F, 4.6F, 8F, 4.6F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.7854F, 0F));

        PartDefinition p_seg8 = p_seg7.addOrReplaceChild("seg8",
                CubeListBuilder.create()
                .texOffs(76, 34).addBox(-2F, -7F, -2F, 4F, 7F, 4F),
                PartPose.offsetAndRotation(0F, -8F, 0F, 0F, 0.08727F, 0.06981F));

        PartDefinition p_seg8_oct = p_seg8.addOrReplaceChild("seg8_oct",
                CubeListBuilder.create()
                .texOffs(76, 34).addBox(-1.84F, -7F, -1.84F, 3.68F, 7F, 3.68F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0.7854F, 0F));

        PartDefinition p_tip = p_seg8.addOrReplaceChild("tip",
                CubeListBuilder.create()
                .texOffs(168, 34).addBox(-1F, -3F, -1F, 2F, 2F, 2F),
                PartPose.offset(0F, -7F, 0F));

        PartDefinition p_hook_0 = p_tip.addOrReplaceChild("hook_0",
                CubeListBuilder.create()
                .texOffs(160, 34).addBox(-1F, -5F, 2F, 2F, 6F, 2F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.31416F));

        PartDefinition p_hook_120 = p_tip.addOrReplaceChild("hook_120",
                CubeListBuilder.create()
                .texOffs(160, 34).addBox(-1F, -5F, 2F, 2F, 6F, 2F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 2.0944F, 0.31416F));

        PartDefinition p_hook_240 = p_tip.addOrReplaceChild("hook_240",
                CubeListBuilder.create()
                .texOffs(160, 34).addBox(-1F, -5F, 2F, 2F, 6F, 2F),
                PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 4.18879F, 0.31416F));

        return LayerDefinition.create(mesh, 256, 256);
    }
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // Per-entity idle-sway SPEED variance (deterministic from network id, no sync needed)
        // so a ring of tentacles drifts out of phase and sways at different rates — not in lockstep.
        float idleSpeed = 0.85f + (((entity.getId() * 2654435761L) >>> 16) & 0xFF) / 255.0f * 0.30f;

        this.animate(entity.emergeAnimState, MawTentacleAnimations.EMERGE, ageInTicks);
        this.animate(entity.idleSwayAnimState, MawTentacleAnimations.IDLE_SWAY, ageInTicks, idleSpeed);
        this.animate(entity.strikeAnimState, MawTentacleAnimations.STRIKE, ageInTicks);
        this.animate(entity.grabAnimState, MawTentacleAnimations.GRAB, ageInTicks);
        this.animate(entity.retractAnimState, MawTentacleAnimations.RETRACT, ageInTicks);
        this.animate(entity.recoilAnimState, MawTentacleAnimations.WOUNDED_RECOIL, ageInTicks);
    }
}
