package com._jackoboy.otherside.client.model;

import com._jackoboy.otherside.client.animation.ListeningBloomAnimations;
import com._jackoboy.otherside.entity.ListeningBloomEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Listening Bloom model — GENERATED from listening_bloom.bbmodel (modded_entity, 75 cubes, 96 bones).
 * The dish (all petals/feelers) are children of `head`, so aiming `head` tilts the whole dish to face
 * the player — the signature "it's listening to you" behavior. Head is driven PROCEDURALLY (yaw free
 * 360°, pitch clamped ±65°) when the entity is tracking; otherwise the SWEEP/idle animations drive it.
 */
public class ListeningBloomModel<T extends ListeningBloomEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("otherside", "listening_bloom"), "main");

    private final ModelPart root;
    private final ModelPart head;

    public ListeningBloomModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("root").getChild("stalk").getChild("head");
    }

    @Override
    public ModelPart root() {
        return root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_root = root.addOrReplaceChild("root",
                CubeListBuilder.create(),
                PartPose.offset(0F,24F,0F)); // +24 = renderer feet-line offset; drops model from 1.5-block float onto the ground. NO rotation.
        PartDefinition p_root_7_yaw = p_root.addOrReplaceChild("root_7_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-2.5F,0F, 0F,0.1256F,0F));
        PartDefinition p_root_7_pitch = p_root_7_yaw.addOrReplaceChild("root_7_pitch",
                CubeListBuilder.create()
                    .texOffs(0,24).addBox(2F,-1.5F,-1F, 5F,2F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,-0.75793F));
        PartDefinition p_root_7_curl = p_root_7_pitch.addOrReplaceChild("root_7_curl",
                CubeListBuilder.create()
                    .texOffs(16,24).addBox(0F,0F,-1F, 4F,1F,2F),
                PartPose.offsetAndRotation(7F,-0.5F,0F, 0F,0F,0.68142F));
        PartDefinition p_root_77_yaw = p_root.addOrReplaceChild("root_77_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-2.5F,0F, 0F,1.36034F,0F));
        PartDefinition p_root_77_pitch = p_root_77_yaw.addOrReplaceChild("root_77_pitch",
                CubeListBuilder.create()
                    .texOffs(0,24).addBox(2F,-1.5F,-1F, 5F,2F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,-0.75151F));
        PartDefinition p_root_77_curl = p_root_77_pitch.addOrReplaceChild("root_77_curl",
                CubeListBuilder.create()
                    .texOffs(16,24).addBox(0F,0F,-1F, 4F,1F,2F),
                PartPose.offsetAndRotation(7F,-0.5F,0F, 0F,0F,0.66076F));
        PartDefinition p_root_125_yaw = p_root.addOrReplaceChild("root_125_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-2.5F,0F, 0F,2.19403F,0F));
        PartDefinition p_root_125_pitch = p_root_125_yaw.addOrReplaceChild("root_125_pitch",
                CubeListBuilder.create()
                    .texOffs(0,24).addBox(2F,-1.5F,-1F, 5F,2F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,-0.60028F));
        PartDefinition p_root_125_curl = p_root_125_pitch.addOrReplaceChild("root_125_curl",
                CubeListBuilder.create()
                    .texOffs(16,24).addBox(0F,0F,-1F, 4F,1F,2F),
                PartPose.offsetAndRotation(7F,-0.5F,0F, 0F,0F,0.50805F));
        PartDefinition p_root_203_yaw = p_root.addOrReplaceChild("root_203_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-2.5F,0F, 0F,3.55786F,0F));
        PartDefinition p_root_203_pitch = p_root_203_yaw.addOrReplaceChild("root_203_pitch",
                CubeListBuilder.create()
                    .texOffs(0,24).addBox(2F,-1.5F,-1F, 5F,2F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,-0.6236F));
        PartDefinition p_root_203_curl = p_root_203_pitch.addOrReplaceChild("root_203_curl",
                CubeListBuilder.create()
                    .texOffs(16,24).addBox(0F,0F,-1F, 4F,1F,2F),
                PartPose.offsetAndRotation(7F,-0.5F,0F, 0F,0F,0.53622F));
        PartDefinition p_root_265_yaw = p_root.addOrReplaceChild("root_265_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-2.5F,0F, 0F,4.63508F,0F));
        PartDefinition p_root_265_pitch = p_root_265_yaw.addOrReplaceChild("root_265_pitch",
                CubeListBuilder.create()
                    .texOffs(0,24).addBox(2F,-1.5F,-1F, 5F,2F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,-0.67599F));
        PartDefinition p_root_265_curl = p_root_265_pitch.addOrReplaceChild("root_265_curl",
                CubeListBuilder.create()
                    .texOffs(16,24).addBox(0F,0F,-1F, 4F,1F,2F),
                PartPose.offsetAndRotation(7F,-0.5F,0F, 0F,0F,0.62432F));
        PartDefinition p_root_327_yaw = p_root.addOrReplaceChild("root_327_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-2.5F,0F, 0F,5.72366F,0F));
        PartDefinition p_root_327_pitch = p_root_327_yaw.addOrReplaceChild("root_327_pitch",
                CubeListBuilder.create()
                    .texOffs(0,24).addBox(2F,-1.5F,-1F, 5F,2F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,-0.76265F));
        PartDefinition p_root_327_curl = p_root_327_pitch.addOrReplaceChild("root_327_curl",
                CubeListBuilder.create()
                    .texOffs(16,24).addBox(0F,0F,-1F, 4F,1F,2F),
                PartPose.offsetAndRotation(7F,-0.5F,0F, 0F,0F,0.67546F));
        PartDefinition p_nub_50 = p_root.addOrReplaceChild("nub_50",
                CubeListBuilder.create()
                    .texOffs(30,24).addBox(2F,-3F,-1F, 2F,2F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0.87266F,0F));
        PartDefinition p_nub_170 = p_root.addOrReplaceChild("nub_170",
                CubeListBuilder.create()
                    .texOffs(30,24).addBox(2F,-3F,-1F, 2F,2F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,2.96706F,0F));
        PartDefinition p_nub_300 = p_root.addOrReplaceChild("nub_300",
                CubeListBuilder.create()
                    .texOffs(30,24).addBox(2F,-3F,-1F, 2F,2F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,5.23599F,0F));
        PartDefinition p_stalk = p_root.addOrReplaceChild("stalk",
                CubeListBuilder.create()
                    .texOffs(40,0).addBox(-3F,-3F,-3F, 6F,3F,6F)
                    .texOffs(78,24).addBox(-2F,-6F,-2.5F, 5F,3F,5F),
                PartPose.offset(0F,0F,0F));
        PartDefinition p_head = p_stalk.addOrReplaceChild("head",
                CubeListBuilder.create()
                    .texOffs(40,0).addBox(-3F,-3F,-3F, 6F,3F,6F)
                    .texOffs(64,0).addBox(-4F,-4F,-4F, 8F,1F,8F)
                    .texOffs(96,0).addBox(-1F,-8F,-1F, 2F,4F,2F),
                PartPose.offset(0F,-6F,0F));
        PartDefinition p_petalO_3_yaw = p_head.addOrReplaceChild("petalO_3_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,0.06141F,0F));
        PartDefinition p_petalO_3_pitch = p_petalO_3_yaw.addOrReplaceChild("petalO_3_pitch",
                CubeListBuilder.create()
                    .texOffs(0,14).addBox(2F,-1F,-3F, 8F,2F,6F)
                    .texOffs(40,24).addBox(3F,-2F,-1F, 7F,1F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.37042F));
        PartDefinition p_petalO_3_curl1 = p_petalO_3_pitch.addOrReplaceChild("petalO_3_curl1",
                CubeListBuilder.create()
                    .texOffs(28,14).addBox(0F,0F,-2.5F, 7F,1F,5F),
                PartPose.offsetAndRotation(10F,0F,0F, 0F,0F,-0.25154F));
        PartDefinition p_petalO_3_curl2 = p_petalO_3_curl1.addOrReplaceChild("petalO_3_curl2",
                CubeListBuilder.create()
                    .texOffs(52,14).addBox(0F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,0F,0F, 0F,0F,-0.34378F));
        PartDefinition p_petalO_43_yaw = p_head.addOrReplaceChild("petalO_43_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,0.76653F,0F));
        PartDefinition p_petalO_43_pitch = p_petalO_43_yaw.addOrReplaceChild("petalO_43_pitch",
                CubeListBuilder.create()
                    .texOffs(0,14).addBox(2F,-1F,-3F, 8F,2F,6F)
                    .texOffs(40,24).addBox(3F,-2F,-1F, 7F,1F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.44752F));
        PartDefinition p_petalO_43_curl1 = p_petalO_43_pitch.addOrReplaceChild("petalO_43_curl1",
                CubeListBuilder.create()
                    .texOffs(28,14).addBox(0F,0F,-2.5F, 7F,1F,5F),
                PartPose.offsetAndRotation(10F,0F,0F, 0F,0F,-0.24988F));
        PartDefinition p_petalO_43_curl2 = p_petalO_43_curl1.addOrReplaceChild("petalO_43_curl2",
                CubeListBuilder.create()
                    .texOffs(52,14).addBox(0F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,0F,0F, 0F,0F,-0.36356F));
        PartDefinition p_petalO_90_yaw = p_head.addOrReplaceChild("petalO_90_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,1.58265F,0F));
        PartDefinition p_petalO_90_pitch = p_petalO_90_yaw.addOrReplaceChild("petalO_90_pitch",
                CubeListBuilder.create()
                    .texOffs(0,14).addBox(2F,-1F,-3F, 8F,2F,6F)
                    .texOffs(40,24).addBox(3F,-2F,-1F, 7F,1F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.39059F));
        PartDefinition p_petalO_90_curl1 = p_petalO_90_pitch.addOrReplaceChild("petalO_90_curl1",
                CubeListBuilder.create()
                    .texOffs(28,14).addBox(0F,0F,-2.5F, 7F,1F,5F),
                PartPose.offsetAndRotation(10F,0F,0F, 0F,0F,-0.14609F));
        PartDefinition p_petalO_90_curl2 = p_petalO_90_curl1.addOrReplaceChild("petalO_90_curl2",
                CubeListBuilder.create()
                    .texOffs(52,14).addBox(0F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,0F,0F, 0F,0F,-0.25505F));
        PartDefinition p_petalO_133_yaw = p_head.addOrReplaceChild("petalO_133_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,2.33602F,0F));
        PartDefinition p_petalO_133_pitch = p_petalO_133_yaw.addOrReplaceChild("petalO_133_pitch",
                CubeListBuilder.create()
                    .texOffs(0,14).addBox(2F,-1F,-3F, 8F,2F,6F)
                    .texOffs(40,24).addBox(3F,-2F,-1F, 7F,1F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.34262F));
        PartDefinition p_petalO_133_curl1 = p_petalO_133_pitch.addOrReplaceChild("petalO_133_curl1",
                CubeListBuilder.create()
                    .texOffs(28,14).addBox(0F,0F,-2.5F, 7F,1F,5F),
                PartPose.offsetAndRotation(10F,0F,0F, 0F,0F,-0.16247F));
        PartDefinition p_petalO_133_curl2 = p_petalO_133_curl1.addOrReplaceChild("petalO_133_curl2",
                CubeListBuilder.create()
                    .texOffs(52,14).addBox(0F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,0F,0F, 0F,0F,-0.22882F));
        PartDefinition p_petalO_176_yaw = p_head.addOrReplaceChild("petalO_176_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,3.07602F,0F));
        PartDefinition p_petalO_176_pitch = p_petalO_176_yaw.addOrReplaceChild("petalO_176_pitch",
                CubeListBuilder.create()
                    .texOffs(0,14).addBox(2F,-1F,-3F, 8F,2F,6F)
                    .texOffs(40,24).addBox(3F,-2F,-1F, 7F,1F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.41679F));
        PartDefinition p_petalO_176_curl1 = p_petalO_176_pitch.addOrReplaceChild("petalO_176_curl1",
                CubeListBuilder.create()
                    .texOffs(28,14).addBox(0F,0F,-2.5F, 7F,1F,5F),
                PartPose.offsetAndRotation(10F,0F,0F, 0F,0F,-0.15801F));
        PartDefinition p_petalO_176_curl2 = p_petalO_176_curl1.addOrReplaceChild("petalO_176_curl2",
                CubeListBuilder.create()
                    .texOffs(52,14).addBox(0F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,0F,0F, 0F,0F,-0.28778F));
        PartDefinition p_petalO_222_yaw = p_head.addOrReplaceChild("petalO_222_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,3.88767F,0F));
        PartDefinition p_petalO_222_pitch = p_petalO_222_yaw.addOrReplaceChild("petalO_222_pitch",
                CubeListBuilder.create()
                    .texOffs(0,14).addBox(2F,-1F,-3F, 8F,2F,6F)
                    .texOffs(40,24).addBox(3F,-2F,-1F, 7F,1F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.42538F));
        PartDefinition p_petalO_222_curl1 = p_petalO_222_pitch.addOrReplaceChild("petalO_222_curl1",
                CubeListBuilder.create()
                    .texOffs(28,14).addBox(0F,0F,-2.5F, 7F,1F,5F),
                PartPose.offsetAndRotation(10F,0F,0F, 0F,0F,-0.17735F));
        PartDefinition p_petalO_222_curl2 = p_petalO_222_curl1.addOrReplaceChild("petalO_222_curl2",
                CubeListBuilder.create()
                    .texOffs(52,14).addBox(0F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,0F,0F, 0F,0F,-0.3676F));
        PartDefinition p_petalO_271_yaw = p_head.addOrReplaceChild("petalO_271_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,4.73206F,0F));
        PartDefinition p_petalO_271_pitch = p_petalO_271_yaw.addOrReplaceChild("petalO_271_pitch",
                CubeListBuilder.create()
                    .texOffs(0,14).addBox(2F,-1F,-3F, 8F,2F,6F)
                    .texOffs(40,24).addBox(3F,-2F,-1F, 7F,1F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.34476F));
        PartDefinition p_petalO_271_curl1 = p_petalO_271_pitch.addOrReplaceChild("petalO_271_curl1",
                CubeListBuilder.create()
                    .texOffs(28,14).addBox(0F,0F,-2.5F, 7F,1F,5F),
                PartPose.offsetAndRotation(10F,0F,0F, 0F,0F,-0.27347F));
        PartDefinition p_petalO_271_curl2 = p_petalO_271_curl1.addOrReplaceChild("petalO_271_curl2",
                CubeListBuilder.create()
                    .texOffs(52,14).addBox(0F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,0F,0F, 0F,0F,-0.34278F));
        PartDefinition p_petalO_313_yaw = p_head.addOrReplaceChild("petalO_313_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,5.47466F,0F));
        PartDefinition p_petalO_313_pitch = p_petalO_313_yaw.addOrReplaceChild("petalO_313_pitch",
                CubeListBuilder.create()
                    .texOffs(0,14).addBox(2F,-1F,-3F, 8F,2F,6F)
                    .texOffs(40,24).addBox(3F,-2F,-1F, 7F,1F,2F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.36947F));
        PartDefinition p_petalO_313_curl1 = p_petalO_313_pitch.addOrReplaceChild("petalO_313_curl1",
                CubeListBuilder.create()
                    .texOffs(28,14).addBox(0F,0F,-2.5F, 7F,1F,5F),
                PartPose.offsetAndRotation(10F,0F,0F, 0F,0F,-0.18895F));
        PartDefinition p_petalO_313_curl2 = p_petalO_313_curl1.addOrReplaceChild("petalO_313_curl2",
                CubeListBuilder.create()
                    .texOffs(52,14).addBox(0F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,0F,0F, 0F,0F,-0.25549F));
        PartDefinition p_petalF_26_yaw = p_head.addOrReplaceChild("petalF_26_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,0.45711F,0F));
        PartDefinition p_petalF_26_pitch = p_petalF_26_yaw.addOrReplaceChild("petalF_26_pitch",
                CubeListBuilder.create()
                    .texOffs(60,24).addBox(3F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.64166F));
        PartDefinition p_petalF_71_yaw = p_head.addOrReplaceChild("petalF_71_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,1.247F,0F));
        PartDefinition p_petalF_71_pitch = p_petalF_71_yaw.addOrReplaceChild("petalF_71_pitch",
                CubeListBuilder.create()
                    .texOffs(60,24).addBox(3F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.60909F));
        PartDefinition p_petalF_110_yaw = p_head.addOrReplaceChild("petalF_110_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,1.93645F,0F));
        PartDefinition p_petalF_110_pitch = p_petalF_110_yaw.addOrReplaceChild("petalF_110_pitch",
                CubeListBuilder.create()
                    .texOffs(60,24).addBox(3F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.50201F));
        PartDefinition p_petalF_158_yaw = p_head.addOrReplaceChild("petalF_158_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,2.76617F,0F));
        PartDefinition p_petalF_158_pitch = p_petalF_158_yaw.addOrReplaceChild("petalF_158_pitch",
                CubeListBuilder.create()
                    .texOffs(60,24).addBox(3F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.5665F));
        PartDefinition p_petalF_203_yaw = p_head.addOrReplaceChild("petalF_203_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,3.55866F,0F));
        PartDefinition p_petalF_203_pitch = p_petalF_203_yaw.addOrReplaceChild("petalF_203_pitch",
                CubeListBuilder.create()
                    .texOffs(60,24).addBox(3F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.48756F));
        PartDefinition p_petalF_245_yaw = p_head.addOrReplaceChild("petalF_245_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,4.2768F,0F));
        PartDefinition p_petalF_245_pitch = p_petalF_245_yaw.addOrReplaceChild("petalF_245_pitch",
                CubeListBuilder.create()
                    .texOffs(60,24).addBox(3F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.63199F));
        PartDefinition p_petalF_293_yaw = p_head.addOrReplaceChild("petalF_293_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,5.11924F,0F));
        PartDefinition p_petalF_293_pitch = p_petalF_293_yaw.addOrReplaceChild("petalF_293_pitch",
                CubeListBuilder.create()
                    .texOffs(60,24).addBox(3F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.5926F));
        PartDefinition p_petalF_334_yaw = p_head.addOrReplaceChild("petalF_334_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,5.83403F,0F));
        PartDefinition p_petalF_334_pitch = p_petalF_334_yaw.addOrReplaceChild("petalF_334_pitch",
                CubeListBuilder.create()
                    .texOffs(60,24).addBox(3F,0F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.54091F));
        PartDefinition p_petalI_34_yaw = p_head.addOrReplaceChild("petalI_34_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,0.59517F,0F));
        PartDefinition p_petalI_34_pitch = p_petalI_34_yaw.addOrReplaceChild("petalI_34_pitch",
                CubeListBuilder.create()
                    .texOffs(68,14).addBox(1F,-2F,-2F, 6F,2F,4F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.72419F));
        PartDefinition p_petalI_34_curl = p_petalI_34_pitch.addOrReplaceChild("petalI_34_curl",
                CubeListBuilder.create()
                    .texOffs(88,14).addBox(0F,-0.5F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,-0.5F,0F, 0F,0F,-0.17599F));
        PartDefinition p_petalI_86_yaw = p_head.addOrReplaceChild("petalI_86_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,1.51789F,0F));
        PartDefinition p_petalI_86_pitch = p_petalI_86_yaw.addOrReplaceChild("petalI_86_pitch",
                CubeListBuilder.create()
                    .texOffs(68,14).addBox(1F,-2F,-2F, 6F,2F,4F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.69024F));
        PartDefinition p_petalI_86_curl = p_petalI_86_pitch.addOrReplaceChild("petalI_86_curl",
                CubeListBuilder.create()
                    .texOffs(88,14).addBox(0F,-0.5F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,-0.5F,0F, 0F,0F,-0.20934F));
        PartDefinition p_petalI_149_yaw = p_head.addOrReplaceChild("petalI_149_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,2.60834F,0F));
        PartDefinition p_petalI_149_pitch = p_petalI_149_yaw.addOrReplaceChild("petalI_149_pitch",
                CubeListBuilder.create()
                    .texOffs(68,14).addBox(1F,-2F,-2F, 6F,2F,4F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.74856F));
        PartDefinition p_petalI_149_curl = p_petalI_149_pitch.addOrReplaceChild("petalI_149_curl",
                CubeListBuilder.create()
                    .texOffs(88,14).addBox(0F,-0.5F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,-0.5F,0F, 0F,0F,-0.25103F));
        PartDefinition p_petalI_206_yaw = p_head.addOrReplaceChild("petalI_206_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,3.59853F,0F));
        PartDefinition p_petalI_206_pitch = p_petalI_206_yaw.addOrReplaceChild("petalI_206_pitch",
                CubeListBuilder.create()
                    .texOffs(68,14).addBox(1F,-2F,-2F, 6F,2F,4F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.62812F));
        PartDefinition p_petalI_206_curl = p_petalI_206_pitch.addOrReplaceChild("petalI_206_curl",
                CubeListBuilder.create()
                    .texOffs(88,14).addBox(0F,-0.5F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,-0.5F,0F, 0F,0F,-0.1775F));
        PartDefinition p_petalI_266_yaw = p_head.addOrReplaceChild("petalI_266_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,4.64595F,0F));
        PartDefinition p_petalI_266_pitch = p_petalI_266_yaw.addOrReplaceChild("petalI_266_pitch",
                CubeListBuilder.create()
                    .texOffs(68,14).addBox(1F,-2F,-2F, 6F,2F,4F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.64474F));
        PartDefinition p_petalI_266_curl = p_petalI_266_pitch.addOrReplaceChild("petalI_266_curl",
                CubeListBuilder.create()
                    .texOffs(88,14).addBox(0F,-0.5F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,-0.5F,0F, 0F,0F,-0.29056F));
        PartDefinition p_petalI_332_yaw = p_head.addOrReplaceChild("petalI_332_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-3F,0F, 0F,5.79684F,0F));
        PartDefinition p_petalI_332_pitch = p_petalI_332_yaw.addOrReplaceChild("petalI_332_pitch",
                CubeListBuilder.create()
                    .texOffs(68,14).addBox(1F,-2F,-2F, 6F,2F,4F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.75993F));
        PartDefinition p_petalI_332_curl = p_petalI_332_pitch.addOrReplaceChild("petalI_332_curl",
                CubeListBuilder.create()
                    .texOffs(88,14).addBox(0F,-0.5F,-1.5F, 5F,1F,3F),
                PartPose.offsetAndRotation(7F,-0.5F,0F, 0F,0F,-0.26604F));
        PartDefinition p_feeler_14_yaw = p_head.addOrReplaceChild("feeler_14_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-7.5F,0F, 0F,0.26141F,0F));
        PartDefinition p_feeler_14_pitch = p_feeler_14_yaw.addOrReplaceChild("feeler_14_pitch",
                CubeListBuilder.create()
                    .texOffs(104,14).addBox(1F,-0.5F,-0.5F, 3F,1F,1F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.60415F));
        PartDefinition p_feeler_137_yaw = p_head.addOrReplaceChild("feeler_137_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-7.5F,0F, 0F,2.39697F,0F));
        PartDefinition p_feeler_137_pitch = p_feeler_137_yaw.addOrReplaceChild("feeler_137_pitch",
                CubeListBuilder.create()
                    .texOffs(104,14).addBox(1F,-0.5F,-0.5F, 3F,1F,1F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.48797F));
        PartDefinition p_feeler_266_yaw = p_head.addOrReplaceChild("feeler_266_yaw",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0F,-7.5F,0F, 0F,4.64873F,0F));
        PartDefinition p_feeler_266_pitch = p_feeler_266_yaw.addOrReplaceChild("feeler_266_pitch",
                CubeListBuilder.create()
                    .texOffs(104,14).addBox(1F,-0.5F,-0.5F, 3F,1F,1F),
                PartPose.offsetAndRotation(0F,0F,0F, 0F,0F,0.56164F));
        return LayerDefinition.create(mesh, 128, 128);
    }
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // ── State animations (entity starts/stops these per its state machine) ──
        // unfurl/fold are keyframed; sweep/idle_listening loop; twitch/alert_lock are reactions.
        this.animate(entity.unfurlState,        ListeningBloomAnimations.UNFURL,         ageInTicks);
        this.animate(entity.foldState,          ListeningBloomAnimations.FOLD,           ageInTicks);
        this.animate(entity.sweepState,         ListeningBloomAnimations.SWEEP,          ageInTicks);
        this.animate(entity.idleListeningState, ListeningBloomAnimations.IDLE_LISTENING, ageInTicks);
        this.animate(entity.twitchState,        ListeningBloomAnimations.TWITCH_DEMO,    ageInTicks);
        this.animate(entity.alertLockState,     ListeningBloomAnimations.ALERT_LOCK,     ageInTicks);

        // ── PROCEDURAL DISH-TRACKING (the signature) ───────────────────────────────────────
        // When the bloom is tracking a player, the head tilts to face them — netHeadYaw/headPitch
        // come from the entity aiming its yHeadRot at the player (lerp ~0.12/tick). This SET
        // (not add) overrides the sweep animation's head channel so the dish points at the real
        // player. Yaw is free (360°); pitch is clamped ±65° (beyond that the underside/roots break
        // the silhouette — per the rig guide).
        if (entity.isTracking()) {
            this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
            float pitch = Mth.clamp(headPitch, -65.0F, 65.0F);
            this.head.xRot = pitch * ((float) Math.PI / 180F);
        }
        // Not tracking → the sweep/idle animations own the head (predatory scan), untouched.
    }
}
