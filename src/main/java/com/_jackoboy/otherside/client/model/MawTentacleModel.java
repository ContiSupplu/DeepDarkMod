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
 * Uses HierarchicalModel to support keyframe AnimationDefinition playback.
 *
 * Bone chain: root → seg0 → seg1 → … → seg8 → tip
 * Collar ring: root → collar_0..5 (static ground ring)
 *
 * Placeholder geometry — will be refined when Blockbench export is integrated.
 * The bone NAMES must match the AnimationDefinition bone references exactly.
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
        PartDefinition rootPart = mesh.getRoot();

        float baseWidth = 6.0f;
        float segHeight = 12.0f;
        float taper = (baseWidth - 2.4f) / 8.0f;
        float collarR = 10.0f;

        // All texOffs point to the dark sculk region at top of the 256x256 sheet.
        // The real bbmodel UVs are packed in rows 0-24; placeholder cubes sample from there.
        int[] collarAngles = {20, 95, 170, 250, 320};
        for (int angle : collarAngles) {
            double rad = Math.toRadians(angle);
            rootPart.addOrReplaceChild("collar_" + angle,
                    CubeListBuilder.create()
                            .texOffs(0, 0)
                            .addBox(-2.0f, 0.0f, -2.0f, 4.0f, 3.0f, 4.0f),
                    PartPose.offset(
                            (float)(Math.cos(rad) * collarR),
                            0.0f,
                            (float)(Math.sin(rad) * collarR)));
        }

        int[] finSegs = {1, 2, 3, 5};

        PartDefinition current = rootPart;
        for (int i = 0; i <= 8; i++) {
            float w = baseWidth - i * taper;
            float half = w / 2.0f;

            // Segments stack in -Y (upward after renderer's Y-flip)
            PartDefinition seg = current.addOrReplaceChild("seg" + i,
                    CubeListBuilder.create()
                            .texOffs(0, 0)
                            .addBox(-half, -segHeight, -half, w, segHeight, w),
                    PartPose.offset(0.0f, i == 0 ? 0.0f : -segHeight, 0.0f));

            float octHalf = half * 0.7071f;
            float octW = w * 0.7071f;
            seg.addOrReplaceChild("seg" + i + "_oct",
                    CubeListBuilder.create()
                            .texOffs(0, 0)
                            .addBox(-octHalf, -segHeight, -octHalf, octW, segHeight, octW),
                    PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, (float)(Math.PI / 4.0), 0.0f));

            for (int f : finSegs) {
                if (f == i) {
                    seg.addOrReplaceChild("fin" + i + "_rot",
                            CubeListBuilder.create()
                                    .texOffs(0, 0)
                                    .addBox(-half - 3.0f, -segHeight, -0.5f, w + 6.0f, segHeight, 1.0f),
                            PartPose.ZERO);
                    break;
                }
            }

            current = seg;
        }

        PartDefinition tip = current.addOrReplaceChild("tip",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-1.2f, -6.0f, -1.2f, 2.4f, 6.0f, 2.4f),
                PartPose.offset(0.0f, -segHeight, 0.0f));

        int[] hookAngles = {0, 120, 240};
        for (int angle : hookAngles) {
            double rad = Math.toRadians(angle);
            float hookR = 2.0f;
            tip.addOrReplaceChild("hook_" + angle,
                    CubeListBuilder.create()
                            .texOffs(0, 0)
                            .addBox(-0.5f, -4.0f, -0.5f, 1.0f, 4.0f, 1.0f),
                    PartPose.offset(
                            (float)(Math.cos(rad) * hookR),
                            -4.0f,
                            (float)(Math.sin(rad) * hookR)));
        }

        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // Keyframe animations — AnimationState drives which clip plays
        this.animate(entity.emergeAnimState, MawTentacleAnimations.EMERGE, ageInTicks);
        this.animate(entity.idleSwayAnimState, MawTentacleAnimations.IDLE_SWAY, ageInTicks);
        this.animate(entity.strikeAnimState, MawTentacleAnimations.STRIKE, ageInTicks);
        this.animate(entity.grabAnimState, MawTentacleAnimations.GRAB, ageInTicks);
        this.animate(entity.retractAnimState, MawTentacleAnimations.RETRACT, ageInTicks);
        this.animate(entity.recoilAnimState, MawTentacleAnimations.WOUNDED_RECOIL, ageInTicks);
    }
}
