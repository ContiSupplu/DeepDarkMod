package com._jackoboy.otherside.client.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public final class ListeningBloomAnimations {
    private ListeningBloomAnimations() {}

    public static final AnimationDefinition UNFURL = AnimationDefinition.Builder.withLength(2.8F)
            .addAnimation("petalI_149_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,37.11F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.1F, KeyframeAnimations.degreeVec(0F,0F,37.11F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F,0F,-5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_206_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,44.01F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,44.01F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.88F, KeyframeAnimations.degreeVec(0F,0F,-5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_266_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,43.06F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,43.06F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.96F, KeyframeAnimations.degreeVec(0F,0F,-5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.26F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_332_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,36.46F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(0F,0F,36.46F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.04F, KeyframeAnimations.degreeVec(0F,0F,-5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.34F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_34_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,38.51F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(0F,0F,38.51F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.12F, KeyframeAnimations.degreeVec(0F,0F,-5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.42F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_86_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,40.45F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0F,0F,40.45F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(0F,0F,-5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_149_curl", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,29.38F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.1F, KeyframeAnimations.degreeVec(0F,0F,29.38F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_206_curl", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,25.17F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,25.17F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_266_curl", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,31.65F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,31.65F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.26F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_332_curl", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,30.24F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(0F,0F,30.24F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.34F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_34_curl", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,25.08F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(0F,0F,25.08F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.42F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_86_curl", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,26.99F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0F,0F,26.99F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_133_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,52.37F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.35F, KeyframeAnimations.degreeVec(0F,0F,52.37F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.15F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_176_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,48.12F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(0F,0F,48.12F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.25F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_222_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,47.63F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.55F, KeyframeAnimations.degreeVec(0F,0F,47.63F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.35F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.7F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_271_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,52.25F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.65F, KeyframeAnimations.degreeVec(0F,0F,52.25F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.45F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_313_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,50.83F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F,0F,50.83F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.55F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_3_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,50.78F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.05F, KeyframeAnimations.degreeVec(0F,0F,50.78F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.1F, KeyframeAnimations.degreeVec(0F,0F,47.78F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.22F, KeyframeAnimations.degreeVec(0F,0F,-9F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.4F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_43_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,46.36F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0F,0F,46.36F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.75F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.1F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_90_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,49.62F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.05F, KeyframeAnimations.degreeVec(0F,0F,49.62F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.85F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.2F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_133_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,29.31F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0F,0F,29.31F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_176_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,29.05F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(0F,0F,29.05F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_222_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,30.16F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(0F,0F,30.16F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_271_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,35.67F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F,0F,35.67F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.7F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_313_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,30.83F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(0F,0F,30.83F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_3_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,34.41F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.2F, KeyframeAnimations.degreeVec(0F,0F,34.41F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_43_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,34.32F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(0F,0F,34.32F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_90_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,28.37F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(0F,0F,28.37F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.1F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_133_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,38.11F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.65F, KeyframeAnimations.degreeVec(0F,0F,38.11F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.55F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_176_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,41.49F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F,0F,41.49F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.65F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_222_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,46.06F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(0F,0F,46.06F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.75F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_271_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,44.64F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0F,0F,44.64F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.85F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_313_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,39.64F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.05F, KeyframeAnimations.degreeVec(0F,0F,39.64F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.95F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_3_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,44.7F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.35F, KeyframeAnimations.degreeVec(0F,0F,44.7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_43_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,45.83F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.25F, KeyframeAnimations.degreeVec(0F,0F,45.83F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.15F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_90_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,39.61F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.35F, KeyframeAnimations.degreeVec(0F,0F,39.61F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.25F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_110_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,47.24F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0F,0F,47.24F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_158_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,43.54F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.59F, KeyframeAnimations.degreeVec(0F,0F,43.54F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.49F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_203_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,48.06F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.68F, KeyframeAnimations.degreeVec(0F,0F,48.06F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.58F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_245_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,39.79F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.77F, KeyframeAnimations.degreeVec(0F,0F,39.79F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.67F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_26_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,39.24F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.86F, KeyframeAnimations.degreeVec(0F,0F,39.24F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.76F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_293_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,42.05F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0F,0F,42.05F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.85F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_334_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,45.01F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.04F, KeyframeAnimations.degreeVec(0F,0F,45.01F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.94F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_71_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,41.1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.13F, KeyframeAnimations.degreeVec(0F,0F,41.1F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.03F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("feeler_137_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,57.04F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.7F, KeyframeAnimations.degreeVec(0F,0F,57.04F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.2F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.45F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("feeler_14_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,50.39F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.82F, KeyframeAnimations.degreeVec(0F,0F,50.39F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.32F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.57F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("feeler_266_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,52.82F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.94F, KeyframeAnimations.degreeVec(0F,0F,52.82F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.44F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.69F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition FOLD = AnimationDefinition.Builder.withLength(1.1F)
            .addAnimation("feeler_137_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(0F,0F,57.04F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("feeler_14_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(0F,0F,50.39F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("feeler_266_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(0F,0F,52.82F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_149_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(0F,0F,37.11F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_206_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.87F, KeyframeAnimations.degreeVec(0F,0F,44.01F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_266_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.89F, KeyframeAnimations.degreeVec(0F,0F,43.06F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_332_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.91F, KeyframeAnimations.degreeVec(0F,0F,36.46F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_34_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.93F, KeyframeAnimations.degreeVec(0F,0F,38.51F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_86_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0F,0F,40.45F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_149_curl", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(0F,0F,29.38F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_206_curl", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.87F, KeyframeAnimations.degreeVec(0F,0F,25.17F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_266_curl", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.89F, KeyframeAnimations.degreeVec(0F,0F,31.65F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_332_curl", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.91F, KeyframeAnimations.degreeVec(0F,0F,30.24F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_34_curl", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.93F, KeyframeAnimations.degreeVec(0F,0F,25.08F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalI_86_curl", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0F,0F,26.99F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_110_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(0F,0F,47.24F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_158_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.87F, KeyframeAnimations.degreeVec(0F,0F,43.54F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_203_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.89F, KeyframeAnimations.degreeVec(0F,0F,48.06F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_245_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.91F, KeyframeAnimations.degreeVec(0F,0F,39.79F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_26_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.93F, KeyframeAnimations.degreeVec(0F,0F,39.24F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_293_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0F,0F,42.05F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_334_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.97F, KeyframeAnimations.degreeVec(0F,0F,45.01F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalF_71_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.99F, KeyframeAnimations.degreeVec(0F,0F,41.1F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_133_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(0F,0F,52.37F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_176_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.87F, KeyframeAnimations.degreeVec(0F,0F,48.12F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_222_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.89F, KeyframeAnimations.degreeVec(0F,0F,47.63F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_271_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.91F, KeyframeAnimations.degreeVec(0F,0F,52.25F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_313_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.93F, KeyframeAnimations.degreeVec(0F,0F,50.83F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_3_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0F,0F,50.78F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_43_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.97F, KeyframeAnimations.degreeVec(0F,0F,46.36F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_90_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.99F, KeyframeAnimations.degreeVec(0F,0F,49.62F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_133_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(0F,0F,29.31F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_176_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.87F, KeyframeAnimations.degreeVec(0F,0F,29.05F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_222_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.89F, KeyframeAnimations.degreeVec(0F,0F,30.16F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_271_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.91F, KeyframeAnimations.degreeVec(0F,0F,35.67F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_313_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.93F, KeyframeAnimations.degreeVec(0F,0F,30.83F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_3_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0F,0F,34.41F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_43_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.97F, KeyframeAnimations.degreeVec(0F,0F,34.32F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_90_curl1", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.99F, KeyframeAnimations.degreeVec(0F,0F,28.37F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_133_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(0F,0F,38.11F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_176_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.87F, KeyframeAnimations.degreeVec(0F,0F,41.49F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_222_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.89F, KeyframeAnimations.degreeVec(0F,0F,46.06F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_271_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.91F, KeyframeAnimations.degreeVec(0F,0F,44.64F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_313_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.93F, KeyframeAnimations.degreeVec(0F,0F,39.64F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_3_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(0F,0F,44.7F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_43_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.97F, KeyframeAnimations.degreeVec(0F,0F,45.83F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_90_curl2", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.99F, KeyframeAnimations.degreeVec(0F,0F,39.61F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition SWEEP = AnimationDefinition.Builder.withLength(11.0F)
            .looping()
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.6F, KeyframeAnimations.degreeVec(0F,-32F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.62F, KeyframeAnimations.degreeVec(0F,-32F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.9F, KeyframeAnimations.degreeVec(0F,-32F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.98F, KeyframeAnimations.degreeVec(0F,-27F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(6.4F, KeyframeAnimations.degreeVec(0F,-27F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(7.6F, KeyframeAnimations.degreeVec(0F,-48F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(8.15F, KeyframeAnimations.degreeVec(0F,41F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(9.4F, KeyframeAnimations.degreeVec(0F,41F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(11.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_133_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.62F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.9F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.15F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.5F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(9.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_176_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.62F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.9F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.15F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.5F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(9.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_222_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.62F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.9F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.15F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.5F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(9.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_271_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.62F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.9F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.15F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.5F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(9.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_313_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.62F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.9F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.15F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.5F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(9.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_3_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.62F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.9F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.15F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.5F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(9.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_43_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.62F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.9F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.15F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.5F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(9.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_90_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.62F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(4.9F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.15F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.5F, KeyframeAnimations.degreeVec(0F,0F,-4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(9.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition IDLE_LISTENING = AnimationDefinition.Builder.withLength(13.0F)
            .looping()
            .addAnimation("petalO_133_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.036F, KeyframeAnimations.degreeVec(0F,0F,2.7F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.771F, KeyframeAnimations.degreeVec(0F,0F,-1.62F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.009F, KeyframeAnimations.degreeVec(0F,0F,2.7F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(8.745F, KeyframeAnimations.degreeVec(0F,0F,-1.62F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.983F, KeyframeAnimations.degreeVec(0F,0F,2.7F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(12.9F, KeyframeAnimations.degreeVec(0F,0F,-1.62F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(13.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_176_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.397F, KeyframeAnimations.degreeVec(0F,0F,1.9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.559F, KeyframeAnimations.degreeVec(0F,0F,-1.14F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(7.328F, KeyframeAnimations.degreeVec(0F,0F,1.9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(9.489F, KeyframeAnimations.degreeVec(0F,0F,-1.14F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(11.258F, KeyframeAnimations.degreeVec(0F,0F,1.9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(12.9F, KeyframeAnimations.degreeVec(0F,0F,-1.14F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(13.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_222_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.589F, KeyframeAnimations.degreeVec(0F,0F,1.96F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.387F, KeyframeAnimations.degreeVec(0F,0F,-1.18F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.677F, KeyframeAnimations.degreeVec(0F,0F,1.96F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(8.475F, KeyframeAnimations.degreeVec(0F,0F,-1.18F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.765F, KeyframeAnimations.degreeVec(0F,0F,1.96F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(12.9F, KeyframeAnimations.degreeVec(0F,0F,-1.18F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(13.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_271_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.521F, KeyframeAnimations.degreeVec(0F,0F,2.45F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.661F, KeyframeAnimations.degreeVec(0F,0F,-1.47F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.413F, KeyframeAnimations.degreeVec(0F,0F,2.45F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.2F, KeyframeAnimations.degreeVec(0F,0F,2.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(6.553F, KeyframeAnimations.degreeVec(0F,0F,-1.47F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.6F, KeyframeAnimations.degreeVec(0F,0F,2.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(6.68F, KeyframeAnimations.degreeVec(0F,0F,3.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(6.74F, KeyframeAnimations.degreeVec(0F,0F,1.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(6.82F, KeyframeAnimations.degreeVec(0F,0F,2.6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(7.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(8.305F, KeyframeAnimations.degreeVec(0F,0F,2.45F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.445F, KeyframeAnimations.degreeVec(0F,0F,-1.47F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(12.197F, KeyframeAnimations.degreeVec(0F,0F,2.45F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(12.9F, KeyframeAnimations.degreeVec(0F,0F,-1.47F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(13.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_313_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.179F, KeyframeAnimations.degreeVec(0F,0F,2.29F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.108F, KeyframeAnimations.degreeVec(0F,0F,-1.37F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.505F, KeyframeAnimations.degreeVec(0F,0F,2.29F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(9.435F, KeyframeAnimations.degreeVec(0F,0F,-1.37F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(11.832F, KeyframeAnimations.degreeVec(0F,0F,2.29F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(12.9F, KeyframeAnimations.degreeVec(0F,0F,-1.37F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(13.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_3_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.434F, KeyframeAnimations.degreeVec(0F,0F,1.62F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.653F, KeyframeAnimations.degreeVec(0F,0F,-0.97F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.468F, KeyframeAnimations.degreeVec(0F,0F,1.62F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(8.687F, KeyframeAnimations.degreeVec(0F,0F,-0.97F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(10.502F, KeyframeAnimations.degreeVec(0F,0F,1.62F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(12.721F, KeyframeAnimations.degreeVec(0F,0F,-0.97F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(13.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_43_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.587F, KeyframeAnimations.degreeVec(0F,0F,2.99F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.481F, KeyframeAnimations.degreeVec(0F,0F,-1.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.849F, KeyframeAnimations.degreeVec(0F,0F,2.99F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(8.744F, KeyframeAnimations.degreeVec(0F,0F,-1.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(11.112F, KeyframeAnimations.degreeVec(0F,0F,2.99F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(12.9F, KeyframeAnimations.degreeVec(0F,0F,-1.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(13.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_90_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.224F, KeyframeAnimations.degreeVec(0F,0F,2.92F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.036F, KeyframeAnimations.degreeVec(0F,0F,-1.75F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(8.337F, KeyframeAnimations.degreeVec(0F,0F,2.92F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(11.149F, KeyframeAnimations.degreeVec(0F,0F,-1.75F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(13.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("feeler_14_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(9.1F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(9.16F, KeyframeAnimations.degreeVec(0F,3F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(9.22F, KeyframeAnimations.degreeVec(0F,-2F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(9.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.7F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.74F, KeyframeAnimations.degreeVec(0F,2.5F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.2F, KeyframeAnimations.degreeVec(0F,2.5F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(8.23F, KeyframeAnimations.degreeVec(0F,-1.5F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(11.6F, KeyframeAnimations.degreeVec(0F,-1.5F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(11.63F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(13.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    public static final AnimationDefinition TWITCH_DEMO = AnimationDefinition.Builder.withLength(0.9F)
            .addAnimation("petalO_133_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.07F, KeyframeAnimations.degreeVec(0F,9F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F,-4F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,2F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.38F, KeyframeAnimations.degreeVec(0F,-1F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_176_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.07F, KeyframeAnimations.degreeVec(0F,9F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F,-4F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(0F,2F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.38F, KeyframeAnimations.degreeVec(0F,-1F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_222_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.12F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.19F, KeyframeAnimations.degreeVec(0F,4F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("petalO_90_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.17F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.24F, KeyframeAnimations.degreeVec(0F,4F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.47F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.1F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,6F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(0F,6F,0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    public static final AnimationDefinition ALERT_LOCK = AnimationDefinition.Builder.withLength(3.0F)
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.12F, KeyframeAnimations.degreeVec(0F,38F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,38F,0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalO_133_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalO_176_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalO_222_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalO_271_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalO_313_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalO_3_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalO_43_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalO_90_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-8F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalF_110_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalF_158_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalF_203_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalF_245_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalF_26_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalF_293_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalF_334_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("petalF_71_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-6F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("feeler_137_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F,0F,-12F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.45F, KeyframeAnimations.degreeVec(0F,2F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.34F, KeyframeAnimations.degreeVec(0F,-2F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.4F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-12F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("feeler_14_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F,0F,-12F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-12F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("feeler_266_pitch", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(0F,0F,-12F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,0F,-12F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

}