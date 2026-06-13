package com._jackoboy.otherside.client.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public final class MawTentacleAnimations {
    private MawTentacleAnimations() {}

    public static final AnimationDefinition EMERGE = AnimationDefinition.Builder.withLength(4.6F)
            .addAnimation("seg0", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F, -126F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7F, KeyframeAnimations.posVec(0F, -126F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.45F, KeyframeAnimations.posVec(0F, -112F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.3F, KeyframeAnimations.posVec(0F, -112F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.55F, KeyframeAnimations.posVec(0F, -10F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.62F, KeyframeAnimations.posVec(0F, -10F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.1F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg0", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, -30F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.35F, KeyframeAnimations.degreeVec(0F, 0F, -30F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.55F, KeyframeAnimations.degreeVec(0F, 0F, 15F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, -4.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.95F, KeyframeAnimations.degreeVec(0F, 0F, -7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.35F, KeyframeAnimations.degreeVec(0F, 0F, -7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.8F, KeyframeAnimations.degreeVec(0F, 0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.25F, KeyframeAnimations.degreeVec(0F, 0F, -0.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("collar_170", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.55F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.62F, KeyframeAnimations.posVec(0F, -18F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.72F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.posVec(0F, -17F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9F, KeyframeAnimations.posVec(0F, -19F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.05F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.3F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.5F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.58F, KeyframeAnimations.posVec(0F, 2.5F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("collar_20", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.55F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.62F, KeyframeAnimations.posVec(0F, -18F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.72F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.posVec(0F, -17F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9F, KeyframeAnimations.posVec(0F, -19F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.05F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.3F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.5F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.58F, KeyframeAnimations.posVec(0F, 2.5F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("collar_250", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.55F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.62F, KeyframeAnimations.posVec(0F, -18F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.72F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.posVec(0F, -17F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9F, KeyframeAnimations.posVec(0F, -19F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.05F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.3F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.5F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.58F, KeyframeAnimations.posVec(0F, 2.5F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("collar_320", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.55F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.62F, KeyframeAnimations.posVec(0F, -18F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.72F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.posVec(0F, -17F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9F, KeyframeAnimations.posVec(0F, -19F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.05F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.3F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.5F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.58F, KeyframeAnimations.posVec(0F, 2.5F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("collar_95", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.55F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.62F, KeyframeAnimations.posVec(0F, -18F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.72F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.posVec(0F, -17F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9F, KeyframeAnimations.posVec(0F, -19F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.05F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.3F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.5F, KeyframeAnimations.posVec(0F, -13F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.58F, KeyframeAnimations.posVec(0F, 2.5F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("hook_0", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0F, 0F, -26F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3F, KeyframeAnimations.degreeVec(0F, 0F, -26F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.55F, KeyframeAnimations.degreeVec(0F, 0F, -26F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(0F, 0F, 8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("hook_120", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.56F, KeyframeAnimations.degreeVec(0F, 0F, -26F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.05F, KeyframeAnimations.degreeVec(0F, 0F, -20F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.15F, KeyframeAnimations.degreeVec(0F, 0F, -26F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.3F, KeyframeAnimations.degreeVec(0F, 0F, -26F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.55F, KeyframeAnimations.degreeVec(0F, 0F, -26F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(0F, 0F, 8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("hook_240", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.62F, KeyframeAnimations.degreeVec(0F, 0F, -26F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3F, KeyframeAnimations.degreeVec(0F, 0F, -26F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.55F, KeyframeAnimations.degreeVec(0F, 0F, -26F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(0F, 0F, 8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 33F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.35F, KeyframeAnimations.degreeVec(0F, 0F, 33F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.57F, KeyframeAnimations.degreeVec(0F, 0F, -16.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, 4.95F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.95F, KeyframeAnimations.degreeVec(0F, 0F, -7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.35F, KeyframeAnimations.degreeVec(0F, 0F, -7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.8F, KeyframeAnimations.degreeVec(0F, 0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.25F, KeyframeAnimations.degreeVec(0F, 0F, -0.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, -36F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.35F, KeyframeAnimations.degreeVec(0F, 0F, -36F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.59F, KeyframeAnimations.degreeVec(0F, 0F, 18F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, -5.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.95F, KeyframeAnimations.degreeVec(0F, 0F, -7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.35F, KeyframeAnimations.degreeVec(0F, 0F, -7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.8F, KeyframeAnimations.degreeVec(0F, 0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.25F, KeyframeAnimations.degreeVec(0F, 0F, -0.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 39F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.35F, KeyframeAnimations.degreeVec(0F, 0F, 39F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.61F, KeyframeAnimations.degreeVec(0F, 0F, -19.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, 5.85F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.95F, KeyframeAnimations.degreeVec(0F, 0F, -7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.35F, KeyframeAnimations.degreeVec(0F, 0F, -7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.8F, KeyframeAnimations.degreeVec(0F, 0F, 2.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.25F, KeyframeAnimations.degreeVec(0F, 0F, -0.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, -42F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3F, KeyframeAnimations.degreeVec(0F, 0F, -42F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.42F, KeyframeAnimations.degreeVec(0F, 0F, -42F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.45F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.58F, KeyframeAnimations.degreeVec(0F, 0F, 37.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.6F, KeyframeAnimations.degreeVec(0F, -15F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.74F, KeyframeAnimations.degreeVec(0F, 0F, -14.7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.85F, KeyframeAnimations.degreeVec(0F, 8F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.95F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.2F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.35F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.75F, KeyframeAnimations.degreeVec(0F, 0F, 3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.15F, KeyframeAnimations.degreeVec(0F, 0F, -1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg5", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 45F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3F, KeyframeAnimations.degreeVec(0F, 0F, 45F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.44F, KeyframeAnimations.degreeVec(0F, 0F, 45F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.45F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.6F, KeyframeAnimations.degreeVec(0F, 0F, -40.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.6F, KeyframeAnimations.degreeVec(0F, 15F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.76F, KeyframeAnimations.degreeVec(0F, 0F, 15.75F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.85F, KeyframeAnimations.degreeVec(0F, -8F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.95F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.2F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.35F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.75F, KeyframeAnimations.degreeVec(0F, 0F, 3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.15F, KeyframeAnimations.degreeVec(0F, 0F, -1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.32F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.35F, KeyframeAnimations.degreeVec(0F, 0F, 3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.45F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg6", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, -48F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3F, KeyframeAnimations.degreeVec(0F, 0F, -48F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.45F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.46F, KeyframeAnimations.degreeVec(0F, 0F, -48F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.6F, KeyframeAnimations.degreeVec(0F, -15F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.62F, KeyframeAnimations.degreeVec(0F, 0F, 43.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.78F, KeyframeAnimations.degreeVec(0F, 0F, -16.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.85F, KeyframeAnimations.degreeVec(0F, 8F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.95F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.2F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.35F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.75F, KeyframeAnimations.degreeVec(0F, 0F, 3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.15F, KeyframeAnimations.degreeVec(0F, 0F, -1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg7", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 51F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(0F, 0F, 51F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(0F, 6F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(0F, 0F, 12.75F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.45F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.3F, KeyframeAnimations.degreeVec(0F, 0F, 12.75F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.42F, KeyframeAnimations.degreeVec(0F, 0F, 12.75F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.56F, KeyframeAnimations.degreeVec(0F, 0F, -40.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.62F, KeyframeAnimations.degreeVec(0F, 14F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.7F, KeyframeAnimations.degreeVec(0F, 0F, 15.3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.95F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.35F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.7F, KeyframeAnimations.degreeVec(0F, 0F, 3.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.1F, KeyframeAnimations.degreeVec(0F, 0F, -1.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg8", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, -54F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(0F, 0F, -54F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(0F, -6F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(0F, 0F, -13.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.45F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.3F, KeyframeAnimations.degreeVec(0F, 0F, -13.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.42F, KeyframeAnimations.degreeVec(0F, 0F, -13.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.56F, KeyframeAnimations.degreeVec(0F, 0F, 43.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.62F, KeyframeAnimations.degreeVec(0F, -14F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.7F, KeyframeAnimations.degreeVec(0F, 0F, -16.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.95F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.35F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.7F, KeyframeAnimations.degreeVec(0F, 0F, 3.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.1F, KeyframeAnimations.degreeVec(0F, 0F, -1.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition IDLE_SWAY = AnimationDefinition.Builder.withLength(10.5F)
            .looping()
            .addAnimation("seg0", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(0F, 0F, 1.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.9F, KeyframeAnimations.degreeVec(0F, 0F, -1.26F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.3F, KeyframeAnimations.degreeVec(0F, 0F, 0.45F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(6.1F, KeyframeAnimations.degreeVec(0F, 0F, 0.45F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(7.6F, KeyframeAnimations.degreeVec(0F, 0F, -1.44F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(9.2F, KeyframeAnimations.degreeVec(0F, 0F, 1.08F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.53F, KeyframeAnimations.degreeVec(0F, 0F, 2.25F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.23F, KeyframeAnimations.degreeVec(0F, 0F, -1.57F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.3F, KeyframeAnimations.degreeVec(0F, 0F, 0.56F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(6.1F, KeyframeAnimations.degreeVec(0F, 0F, 0.56F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(7.798F, KeyframeAnimations.degreeVec(0F, 0F, -1.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(9.398F, KeyframeAnimations.degreeVec(0F, 0F, 1.35F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.86F, KeyframeAnimations.degreeVec(0F, 0F, 2.7F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.56F, KeyframeAnimations.degreeVec(0F, 0F, -1.89F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.3F, KeyframeAnimations.degreeVec(0F, 0F, 0.68F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(6.1F, KeyframeAnimations.degreeVec(0F, 0F, 0.68F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(7.996F, KeyframeAnimations.degreeVec(0F, 0F, -2.16F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(9.596F, KeyframeAnimations.degreeVec(0F, 0F, 1.62F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.19F, KeyframeAnimations.degreeVec(0F, 0F, 3.15F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.89F, KeyframeAnimations.degreeVec(0F, 0F, -2.21F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.3F, KeyframeAnimations.degreeVec(0F, 0F, 0.79F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(6.1F, KeyframeAnimations.degreeVec(0F, 0F, 0.79F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.194F, KeyframeAnimations.degreeVec(0F, 0F, -2.52F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(9.794F, KeyframeAnimations.degreeVec(0F, 0F, 1.89F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.52F, KeyframeAnimations.degreeVec(0F, 0F, 3.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.22F, KeyframeAnimations.degreeVec(0F, 0F, -2.52F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.3F, KeyframeAnimations.degreeVec(0F, 0F, 0.9F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(6.1F, KeyframeAnimations.degreeVec(0F, 0F, 0.9F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.392F, KeyframeAnimations.degreeVec(0F, 0F, -2.88F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(9.992F, KeyframeAnimations.degreeVec(0F, 0F, 2.16F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg5", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.85F, KeyframeAnimations.degreeVec(0F, 0F, 4.05F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.3F, KeyframeAnimations.degreeVec(0F, 0F, 1.01F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.55F, KeyframeAnimations.degreeVec(0F, 0F, -2.83F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.1F, KeyframeAnimations.degreeVec(0F, 0F, 1.01F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.59F, KeyframeAnimations.degreeVec(0F, 0F, -3.24F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.19F, KeyframeAnimations.degreeVec(0F, 0F, 2.43F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg6", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.18F, KeyframeAnimations.degreeVec(0F, 0F, 4.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.3F, KeyframeAnimations.degreeVec(0F, 0F, 1.12F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.88F, KeyframeAnimations.degreeVec(0F, 0F, -3.15F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.1F, KeyframeAnimations.degreeVec(0F, 0F, 1.12F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(6.13F, KeyframeAnimations.degreeVec(0F, 0F, 4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.788F, KeyframeAnimations.degreeVec(0F, 0F, -3.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.388F, KeyframeAnimations.degreeVec(0F, 0F, 2.7F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg7", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.51F, KeyframeAnimations.degreeVec(0F, 0F, 4.95F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.3F, KeyframeAnimations.degreeVec(0F, 0F, 1.24F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(5.21F, KeyframeAnimations.degreeVec(0F, 0F, -3.46F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.1F, KeyframeAnimations.degreeVec(0F, 0F, 1.24F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.986F, KeyframeAnimations.degreeVec(0F, 0F, -3.96F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.586F, KeyframeAnimations.degreeVec(0F, 0F, 2.97F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg8", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F, 4F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.84F, KeyframeAnimations.degreeVec(0F, 0F, 5.4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0F, -4F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.3F, KeyframeAnimations.degreeVec(0F, 0F, 1.35F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(5.54F, KeyframeAnimations.degreeVec(0F, 0F, -3.78F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.1F, KeyframeAnimations.degreeVec(0F, 0F, 1.35F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.0F, KeyframeAnimations.degreeVec(0F, 5F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(9.184F, KeyframeAnimations.degreeVec(0F, 0F, -4.32F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.784F, KeyframeAnimations.degreeVec(0F, 0F, 3.24F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition STRIKE = AnimationDefinition.Builder.withLength(1.05F)
            .addAnimation("seg0", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(0F, 0F, 6.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(0F, 0F, 6.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.38F, KeyframeAnimations.degreeVec(0F, 0F, 6.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0F, 0F, -24.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.62F, KeyframeAnimations.degreeVec(0F, 0F, 8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.76F, KeyframeAnimations.degreeVec(0F, 0F, -2.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(0F, 0F, 7.84F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(0F, 0F, 7.84F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(0F, 0F, 7.84F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.54F, KeyframeAnimations.degreeVec(0F, 0F, -30.38F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.66F, KeyframeAnimations.degreeVec(0F, 0F, 9.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F, 0F, -3.43F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(0F, 0F, 9.28F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(0F, 0F, 9.28F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.46F, KeyframeAnimations.degreeVec(0F, 0F, 9.28F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.58F, KeyframeAnimations.degreeVec(0F, 0F, -35.96F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(0F, 0F, 11.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.84F, KeyframeAnimations.degreeVec(0F, 0F, -4.06F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(0F, 0F, 10.72F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(0F, 0F, 10.72F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0F, 0F, 10.72F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.62F, KeyframeAnimations.degreeVec(0F, 0F, -41.54F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.74F, KeyframeAnimations.degreeVec(0F, 0F, 13.4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.88F, KeyframeAnimations.degreeVec(0F, 0F, -4.69F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(0F, 0F, 12.16F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(0F, 0F, 12.16F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.54F, KeyframeAnimations.degreeVec(0F, 0F, 12.16F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.66F, KeyframeAnimations.degreeVec(0F, 0F, -47.12F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.78F, KeyframeAnimations.degreeVec(0F, 0F, 15.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.92F, KeyframeAnimations.degreeVec(0F, 0F, -5.32F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg5", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(0F, 0F, 13.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(0F, 0F, 13.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.58F, KeyframeAnimations.degreeVec(0F, 0F, 13.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(0F, 0F, -52.7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.82F, KeyframeAnimations.degreeVec(0F, 0F, 17F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.96F, KeyframeAnimations.degreeVec(0F, 0F, -5.95F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg6", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(0F, 0F, 15.04F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(0F, 0F, 15.04F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.62F, KeyframeAnimations.degreeVec(0F, 0F, 15.04F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.74F, KeyframeAnimations.degreeVec(0F, 0F, -58.28F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.86F, KeyframeAnimations.degreeVec(0F, 0F, 18.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, -6.58F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg7", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(0F, 0F, 16.48F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(0F, 0F, 16.48F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.66F, KeyframeAnimations.degreeVec(0F, 0F, 16.48F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.78F, KeyframeAnimations.degreeVec(0F, 0F, -63.86F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(0F, 0F, 20.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.04F, KeyframeAnimations.degreeVec(0F, 0F, -7.21F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("seg8", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(0F, 0F, 17.92F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(0F, 0F, 17.92F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(0F, 0F, 17.92F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.82F, KeyframeAnimations.degreeVec(0F, 0F, -69.44F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.94F, KeyframeAnimations.degreeVec(0F, 0F, 22.4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.08F, KeyframeAnimations.degreeVec(0F, 0F, -7.84F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition GRAB = AnimationDefinition.Builder.withLength(2.8F)
            .addAnimation("seg0", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F, 0F, 5.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F, 0F, 5.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(0F, 0F, -18.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F, 0F, -4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F, 0F, -4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, 18F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F, 0F, 6.37F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F, 0F, 6.37F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.33F, KeyframeAnimations.degreeVec(0F, 0F, -22.54F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F, 0F, -4.9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F, 0F, -4.9F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, 18F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F, 0F, 7.54F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F, 0F, 7.54F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.36F, KeyframeAnimations.degreeVec(0F, 0F, -26.68F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F, 0F, -5.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F, 0F, -5.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, 18F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F, 0F, 8.71F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F, 0F, 8.71F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.39F, KeyframeAnimations.degreeVec(0F, 0F, -30.82F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F, 0F, -6.7F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F, 0F, -6.7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, 4F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F, 0F, 9.88F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F, 0F, 9.88F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(0F, 0F, -34.96F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F, 0F, -7.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F, 0F, -7.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, 4F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg5", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F, 0F, 11.05F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F, 0F, 11.05F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(0F, 0F, -39.1F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.55F, KeyframeAnimations.degreeVec(0F, 0F, -39.1F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.77F, KeyframeAnimations.degreeVec(0F, 0F, 25F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.15F, KeyframeAnimations.degreeVec(0F, 0F, 23.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.29F, KeyframeAnimations.degreeVec(0F, 0F, 26.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.43F, KeyframeAnimations.degreeVec(0F, 0F, 23.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.57F, KeyframeAnimations.degreeVec(0F, 0F, 26.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.71F, KeyframeAnimations.degreeVec(0F, 0F, 23.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F, 0F, 25F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, 33F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg6", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F, 0F, 12.22F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F, 0F, 12.22F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.48F, KeyframeAnimations.degreeVec(0F, 0F, -43.24F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.61F, KeyframeAnimations.degreeVec(0F, 0F, -43.24F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.83F, KeyframeAnimations.degreeVec(0F, 0F, 38F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.15F, KeyframeAnimations.degreeVec(0F, 0F, 36.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.29F, KeyframeAnimations.degreeVec(0F, 0F, 39.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.43F, KeyframeAnimations.degreeVec(0F, 0F, 36.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.57F, KeyframeAnimations.degreeVec(0F, 0F, 39.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.71F, KeyframeAnimations.degreeVec(0F, 0F, 36.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F, 0F, 38F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, 46F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg7", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F, 0F, 13.39F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F, 0F, 13.39F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.51F, KeyframeAnimations.degreeVec(0F, 0F, -47.38F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.67F, KeyframeAnimations.degreeVec(0F, 0F, -47.38F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.89F, KeyframeAnimations.degreeVec(0F, 0F, 52F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.15F, KeyframeAnimations.degreeVec(0F, 0F, 50.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.29F, KeyframeAnimations.degreeVec(0F, 0F, 53.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.43F, KeyframeAnimations.degreeVec(0F, 0F, 50.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.57F, KeyframeAnimations.degreeVec(0F, 0F, 53.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.71F, KeyframeAnimations.degreeVec(0F, 0F, 50.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F, 0F, 52F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, 60F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg8", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F, 0F, 14.56F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F, 0F, 14.56F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.54F, KeyframeAnimations.degreeVec(0F, 0F, -51.52F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.73F, KeyframeAnimations.degreeVec(0F, 0F, -51.52F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0F, 0F, 66F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.15F, KeyframeAnimations.degreeVec(0F, 0F, 64.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.29F, KeyframeAnimations.degreeVec(0F, 0F, 67.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.43F, KeyframeAnimations.degreeVec(0F, 0F, 64.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.57F, KeyframeAnimations.degreeVec(0F, 0F, 67.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.71F, KeyframeAnimations.degreeVec(0F, 0F, 64.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F, 0F, 66F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F, 0F, 74F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    public static final AnimationDefinition RETRACT = AnimationDefinition.Builder.withLength(1.45F)
            .addAnimation("seg0", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.05F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4F, KeyframeAnimations.posVec(0F, -12F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.62F, KeyframeAnimations.posVec(0F, -34F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.74F, KeyframeAnimations.posVec(0F, -34F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.1F, KeyframeAnimations.posVec(0F, -126F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg0", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0F, 0F, -24F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0F, 0F, -30F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0F, 0F, 26.4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0F, 0F, 33F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0F, 0F, -28.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0F, 0F, -36F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0F, 0F, 31.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0F, 0F, 39F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(0F, 0F, -25.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0F, 0F, -42F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg5", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(0F, 0F, 27F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0F, 0F, 45F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg6", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(0F, 0F, -28.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0F, 0F, -48F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg7", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(0F, 0F, -35F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.48F, KeyframeAnimations.degreeVec(0F, 0F, 30F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.64F, KeyframeAnimations.degreeVec(0F, 0F, -22F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(0F, 0F, 35.7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0F, 0F, 51F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg8", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(0F, 0F, 35F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.48F, KeyframeAnimations.degreeVec(0F, 0F, -30F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.64F, KeyframeAnimations.degreeVec(0F, 0F, 22F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(0F, 0F, -37.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0F, 0F, -54F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("collar_170", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.95F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.2F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("collar_20", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.95F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.2F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("collar_250", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.95F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.2F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("collar_320", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.95F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.2F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("collar_95", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.95F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.2F, KeyframeAnimations.posVec(0F, -20F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    public static final AnimationDefinition WOUNDED_RECOIL = AnimationDefinition.Builder.withLength(1.2F)
            .addAnimation("seg0", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.09F, KeyframeAnimations.degreeVec(0F, 0F, -16F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.22F, KeyframeAnimations.degreeVec(0F, 0F, 7.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(0F, 0F, -3.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.46F, KeyframeAnimations.degreeVec(0F, 0F, 1.28F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.09F, KeyframeAnimations.degreeVec(0F, 0F, 19F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.22F, KeyframeAnimations.degreeVec(0F, 0F, -8.55F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(0F, 0F, 3.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.46F, KeyframeAnimations.degreeVec(0F, 0F, -1.52F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.09F, KeyframeAnimations.degreeVec(0F, 0F, -22F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.22F, KeyframeAnimations.degreeVec(0F, 0F, 9.9F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(0F, 0F, -4.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.46F, KeyframeAnimations.degreeVec(0F, 0F, 1.76F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(0F, 0F, -6F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg3", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.09F, KeyframeAnimations.degreeVec(0F, 0F, 25F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.22F, KeyframeAnimations.degreeVec(0F, 0F, -11.25F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(0F, 0F, 5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.46F, KeyframeAnimations.degreeVec(0F, 0F, -2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F, 0F, 8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(0F, 0F, 8F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg4", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.09F, KeyframeAnimations.degreeVec(0F, 0F, -28F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.22F, KeyframeAnimations.degreeVec(0F, 0F, 12.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(0F, 0F, -5.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.46F, KeyframeAnimations.degreeVec(0F, 0F, 2.24F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F, 0F, 9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(0F, 0F, 9F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg5", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.09F, KeyframeAnimations.degreeVec(0F, 0F, 31F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.22F, KeyframeAnimations.degreeVec(0F, 0F, -13.95F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(0F, 0F, 6.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.46F, KeyframeAnimations.degreeVec(0F, 0F, -2.48F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F, 0F, 10F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(0F, 0F, 10F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg6", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.09F, KeyframeAnimations.degreeVec(0F, 0F, -34F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.22F, KeyframeAnimations.degreeVec(0F, 0F, 15.3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(0F, 0F, -6.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.46F, KeyframeAnimations.degreeVec(0F, 0F, 2.72F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F, 0F, 11F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(0F, 0F, 11F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg7", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.09F, KeyframeAnimations.degreeVec(0F, 0F, 37F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.22F, KeyframeAnimations.degreeVec(0F, 0F, -16.65F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(0F, 0F, 7.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.46F, KeyframeAnimations.degreeVec(0F, 0F, -2.96F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F, 0F, 12F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(0F, 0F, 12F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("seg8", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.09F, KeyframeAnimations.degreeVec(0F, 0F, -40F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.22F, KeyframeAnimations.degreeVec(0F, 0F, 18F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(0F, 0F, -8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.46F, KeyframeAnimations.degreeVec(0F, 0F, 3.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F, 0F, 13F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(0F, 0F, 13F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

}