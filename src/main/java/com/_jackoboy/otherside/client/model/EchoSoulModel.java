package com._jackoboy.otherside.client.model;

import com._jackoboy.otherside.client.animation.EchoSoulAnimations;
import com._jackoboy.otherside.entity.EchoSoulEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Echo Soul model — GENERATED from echo_soul.bbmodel (Bedrock, biped rig, 8 cubes, 13 animations).
 * Bone names match EchoSoulAnimations exactly. -Y = up (renderer Y-flip).
 *
 * DYNAMIC HEAD: in aware states the head is aimed at the player at runtime (netHeadYaw/headPitch),
 * overriding any canned head rotation — so the head turns toward the player's real position.
 */
public class EchoSoulModel<T extends EchoSoulEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("otherside", "echo_soul"), "main");

    private final ModelPart root;
    private final ModelPart head;

    public EchoSoulModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("soul").getChild("head");
    }

    @Override
    public ModelPart root() {
        return root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_soul = root.addOrReplaceChild("soul",
                CubeListBuilder.create(),
                PartPose.offset(0F, 10F, 0F));
        PartDefinition p_body = p_soul.addOrReplaceChild("body",
                CubeListBuilder.create()
                    .texOffs(16,16).addBox(-4F,0F,-2F, 8F,12F,4F)
                    .texOffs(16,32).addBox(-4F,0F,-2F, 8F,12F,4F, new CubeDeformation(0.85F)),
                PartPose.offset(0F,-10F,0F));
        PartDefinition p_head = p_soul.addOrReplaceChild("head",
                CubeListBuilder.create()
                    .texOffs(0,0).addBox(-4F,-8F,-4F, 8F,8F,8F)
                    .texOffs(32,0).addBox(-4F,-8F,-4F, 8F,8F,8F, new CubeDeformation(0.6F)),
                PartPose.offsetAndRotation(0F,-10F,0F, 0.20944F,-0.05236F,0F));
        PartDefinition p_rightArm = p_soul.addOrReplaceChild("rightArm",
                CubeListBuilder.create()
                    .texOffs(40,16).addBox(-3F,-2F,-2F, 4F,12F,4F),
                PartPose.offsetAndRotation(-5F,-8F,0F, 0.12217F,0F,0.05236F));
        PartDefinition p_leftArm = p_soul.addOrReplaceChild("leftArm",
                CubeListBuilder.create()
                    .texOffs(32,48).addBox(-1F,-2F,-2F, 4F,12F,4F),
                PartPose.offsetAndRotation(5F,-8F,0F, 0.12217F,0F,-0.05236F));
        PartDefinition p_rightLeg = p_soul.addOrReplaceChild("rightLeg",
                CubeListBuilder.create()
                    .texOffs(0,16).addBox(-2F,0F,-2F, 4F,12F,4F),
                PartPose.offset(-2F,2F,0F));
        PartDefinition p_leftLeg = p_soul.addOrReplaceChild("leftLeg",
                CubeListBuilder.create()
                    .texOffs(16,48).addBox(-2F,0F,-2F, 4F,12F,4F),
                PartPose.offset(2F,2F,0F));
        return LayerDefinition.create(mesh, 64, 64);
    }
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // ── State-driven body animations (entity starts/stops these AnimationStates per state) ──
        this.animate(entity.idleFloatState,  EchoSoulAnimations.IDLE_FLOAT,           ageInTicks);
        this.animate(entity.yearnState,      EchoSoulAnimations.YEARN,                ageInTicks);
        this.animate(entity.stalkState,      EchoSoulAnimations.STALK_WALK,           ageInTicks);
        this.animate(entity.detectLockState, EchoSoulAnimations.DETECT_LOCK,          ageInTicks);
        this.animate(entity.chaseState,      EchoSoulAnimations.CHASE_RUN,            ageInTicks);
        this.animate(entity.swipeState,      EchoSoulAnimations.ATTACK_SWIPE,         ageInTicks);
        this.animate(entity.slamState,       EchoSoulAnimations.ATTACK_OVERHEAD_SLAM, ageInTicks);
        this.animate(entity.lungeState,      EchoSoulAnimations.ATTACK_LUNGE,         ageInTicks);
        this.animate(entity.flurryState,     EchoSoulAnimations.ATTACK_FLURRY,        ageInTicks);
        this.animate(entity.screamState,     EchoSoulAnimations.SCREAM_WAIL,          ageInTicks);
        this.animate(entity.hurtState,       EchoSoulAnimations.HURT,                 ageInTicks);
        this.animate(entity.spawnState,      EchoSoulAnimations.SPAWN_EMERGE,         ageInTicks);
        this.animate(entity.dissipateState,  EchoSoulAnimations.DISSIPATE,            ageInTicks);

        // ── DYNAMIC HEAD (the important part) ───────────────────────────────────────────────
        // When the soul is aware of a target, the head AIMS at the player. netHeadYaw/headPitch
        // come from the entity's look control tracking the player, so the head turns toward the
        // player's REAL position — right if they're on the right, up if above, etc. This SET
        // (not add) overrides the canned detect_lock head keyframes (which point a fixed way).
        if (entity.isAwareOfTarget()) {
            this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
            this.head.xRot = headPitch  * ((float) Math.PI / 180F);

            // Lock adds the creepy character the canned anim had: up-tilt, head cocked, micro-twitch.
            if (entity.isLocked()) {
                this.head.xRot += -0.12F;                                 // eerie up-tilt
                this.head.zRot  =  0.17F;                                 // head cocked to one side
                this.head.yRot += Mth.sin(ageInTicks * 2.3F) * 0.04F;     // twitch jitter
            }
        }
        // In WANDER (not aware), the head keeps its canned idle scan — it has NOT noticed you yet.
    }
}
