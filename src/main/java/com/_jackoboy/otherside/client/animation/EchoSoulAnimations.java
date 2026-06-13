package com._jackoboy.otherside.client.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public final class EchoSoulAnimations {
    private EchoSoulAnimations() {}

    public static final AnimationDefinition IDLE_FLOAT = AnimationDefinition.Builder.withLength(4.0F)
            .looping()
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3333F, KeyframeAnimations.posVec(0.2F,0.094F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0.346F,0.35F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.posVec(0.4F,0.7F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3333F, KeyframeAnimations.posVec(0.346F,1.05F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6667F, KeyframeAnimations.posVec(0.2F,1.306F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.posVec(0F,1.4F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3333F, KeyframeAnimations.posVec(-0.2F,1.306F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.6667F, KeyframeAnimations.posVec(-0.346F,1.05F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.posVec(-0.4F,0.7F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3333F, KeyframeAnimations.posVec(-0.346F,0.35F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6667F, KeyframeAnimations.posVec(-0.2F,0.094F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3333F, KeyframeAnimations.degreeVec(0F,1.5F,0.4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0F,2.598F,0.693F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F,3F,0.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3333F, KeyframeAnimations.degreeVec(0F,2.598F,0.693F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6667F, KeyframeAnimations.degreeVec(0F,1.5F,0.4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3333F, KeyframeAnimations.degreeVec(0F,-1.5F,-0.4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.6667F, KeyframeAnimations.degreeVec(0F,-2.598F,-0.693F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0F,-3F,-0.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3333F, KeyframeAnimations.degreeVec(0F,-2.598F,-0.693F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6667F, KeyframeAnimations.degreeVec(0F,-1.5F,-0.4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3333F, KeyframeAnimations.degreeVec(0.107F,0F,0.3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0.4F,0F,0.52F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0.8F,0F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3333F, KeyframeAnimations.degreeVec(1.2F,0F,0.52F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6667F, KeyframeAnimations.degreeVec(1.493F,0F,0.3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(1.6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3333F, KeyframeAnimations.degreeVec(1.493F,0F,-0.3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.6667F, KeyframeAnimations.degreeVec(1.2F,0F,-0.52F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(0.8F,0F,-0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3333F, KeyframeAnimations.degreeVec(0.4F,0F,-0.52F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6667F, KeyframeAnimations.degreeVec(0.107F,0F,-0.3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1F,1F,1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3333F, KeyframeAnimations.scaleVec(1.006F,1.006F,1.006F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6667F, KeyframeAnimations.scaleVec(1.019F,1.019F,1.019F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.scaleVec(1.025F,1.025F,1.025F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3333F, KeyframeAnimations.scaleVec(1.019F,1.019F,1.019F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6667F, KeyframeAnimations.scaleVec(1.006F,1.006F,1.006F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.scaleVec(1F,1F,1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3333F, KeyframeAnimations.scaleVec(1.006F,1.006F,1.006F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.6667F, KeyframeAnimations.scaleVec(1.019F,1.019F,1.019F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.scaleVec(1.025F,1.025F,1.025F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3333F, KeyframeAnimations.scaleVec(1.019F,1.019F,1.019F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6667F, KeyframeAnimations.scaleVec(1.006F,1.006F,1.006F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.scaleVec(1F,1F,1F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(1.151F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(1.869F,2.296F,1.273F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(2.303F,4.243F,1.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(2.386F,5.543F,1.273F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(2.106F,6F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.25F, KeyframeAnimations.degreeVec(1.506F,5.543F,-1.273F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0.676F,4.243F,-1.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.75F, KeyframeAnimations.degreeVec(-0.257F,2.296F,-1.273F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(-1.151F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.25F, KeyframeAnimations.degreeVec(-1.869F,-2.296F,1.273F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.5F, KeyframeAnimations.degreeVec(-2.303F,-4.243F,1.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.75F, KeyframeAnimations.degreeVec(-2.386F,-5.543F,1.273F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(-2.106F,-6F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.25F, KeyframeAnimations.degreeVec(-1.506F,-5.543F,-1.273F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.5F, KeyframeAnimations.degreeVec(-0.676F,-4.243F,-1.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.75F, KeyframeAnimations.degreeVec(0.257F,-2.296F,-1.273F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(1.151F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(1.694F,0F,2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3333F, KeyframeAnimations.degreeVec(2.705F,0F,2.201F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(2.991F,0F,2.75F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(2.476F,0F,3.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3333F, KeyframeAnimations.degreeVec(1.297F,0F,4.25F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6667F, KeyframeAnimations.degreeVec(-0.229F,0F,4.799F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(-1.694F,0F,5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3333F, KeyframeAnimations.degreeVec(-2.705F,0F,4.799F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.6667F, KeyframeAnimations.degreeVec(-2.991F,0F,4.25F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(-2.476F,0F,3.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3333F, KeyframeAnimations.degreeVec(-1.297F,0F,2.75F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6667F, KeyframeAnimations.degreeVec(0.229F,0F,2.201F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(1.694F,0F,2F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(2.35F,0F,-2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3333F, KeyframeAnimations.degreeVec(2.968F,0F,-2.201F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(2.79F,0F,-2.75F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(1.865F,0F,-3.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3333F, KeyframeAnimations.degreeVec(0.44F,0F,-4.25F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6667F, KeyframeAnimations.degreeVec(-1.103F,0F,-4.799F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(-2.35F,0F,-5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3333F, KeyframeAnimations.degreeVec(-2.968F,0F,-4.799F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.6667F, KeyframeAnimations.degreeVec(-2.79F,0F,-4.25F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(-1.865F,0F,-3.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3333F, KeyframeAnimations.degreeVec(-0.44F,0F,-2.75F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6667F, KeyframeAnimations.degreeVec(1.103F,0F,-2.201F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(2.35F,0F,-2F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3333F, KeyframeAnimations.degreeVec(2.5F,0F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(4.33F,0F,1.039F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(5F,0F,1.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3333F, KeyframeAnimations.degreeVec(4.33F,0F,1.039F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6667F, KeyframeAnimations.degreeVec(2.5F,0F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3333F, KeyframeAnimations.degreeVec(-2.5F,0F,-0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.6667F, KeyframeAnimations.degreeVec(-4.33F,0F,-1.039F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(-5F,0F,-1.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3333F, KeyframeAnimations.degreeVec(-4.33F,0F,-1.039F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6667F, KeyframeAnimations.degreeVec(-2.5F,0F,-0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3333F, KeyframeAnimations.degreeVec(-2.5F,0F,-0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(-4.33F,0F,-1.039F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-5F,0F,-1.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3333F, KeyframeAnimations.degreeVec(-4.33F,0F,-1.039F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6667F, KeyframeAnimations.degreeVec(-2.5F,0F,-0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3333F, KeyframeAnimations.degreeVec(2.5F,0F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.6667F, KeyframeAnimations.degreeVec(4.33F,0F,1.039F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(5F,0F,1.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3333F, KeyframeAnimations.degreeVec(4.33F,0F,1.039F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6667F, KeyframeAnimations.degreeVec(2.5F,0F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition YEARN = AnimationDefinition.Builder.withLength(6.0F)
            .looping()
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3F, KeyframeAnimations.posVec(0F,2F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3F, KeyframeAnimations.degreeVec(-4F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(-12F,2F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.6F, KeyframeAnimations.degreeVec(-22F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.6F, KeyframeAnimations.degreeVec(-22F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.7F, KeyframeAnimations.degreeVec(-20.925F,1.791F,1.344F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.8F, KeyframeAnimations.degreeVec(-22.626F,-1.043F,-0.782F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.9F, KeyframeAnimations.degreeVec(-22.589F,-0.982F,-0.736F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(-21.102F,1.496F,1.122F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.1F, KeyframeAnimations.degreeVec(-22F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.2F, KeyframeAnimations.degreeVec(-22.796F,-1.327F,-0.995F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3F, KeyframeAnimations.degreeVec(-21.537F,0.772F,0.579F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.4F, KeyframeAnimations.degreeVec(-21.564F,0.727F,0.546F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.5F, KeyframeAnimations.degreeVec(-22.665F,-1.108F,-0.831F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6F, KeyframeAnimations.degreeVec(-22F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6F, KeyframeAnimations.degreeVec(-22F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3F, KeyframeAnimations.degreeVec(-5F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(-30F,0F,10F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3F, KeyframeAnimations.degreeVec(-74F,0F,5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(-74F,0F,5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.1F, KeyframeAnimations.degreeVec(-74F,0F,7.66F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.2F, KeyframeAnimations.degreeVec(-74F,0F,3.467F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3F, KeyframeAnimations.degreeVec(-74F,0F,3.571F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.4F, KeyframeAnimations.degreeVec(-74F,0F,7.156F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.5F, KeyframeAnimations.degreeVec(-74F,0F,5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6F, KeyframeAnimations.degreeVec(-74F,0F,3.125F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.7F, KeyframeAnimations.degreeVec(-74F,0F,6.08F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.8F, KeyframeAnimations.degreeVec(-74F,0F,6.007F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.8F, KeyframeAnimations.degreeVec(-74F,0F,5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(-30F,0F,-10F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3F, KeyframeAnimations.degreeVec(-74F,0F,-5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(-74F,0F,-5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.1F, KeyframeAnimations.degreeVec(-74F,0F,-2.34F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.2F, KeyframeAnimations.degreeVec(-74F,0F,-6.533F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3F, KeyframeAnimations.degreeVec(-74F,0F,-6.429F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.4F, KeyframeAnimations.degreeVec(-74F,0F,-2.844F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.5F, KeyframeAnimations.degreeVec(-74F,0F,-5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6F, KeyframeAnimations.degreeVec(-74F,0F,-6.875F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.7F, KeyframeAnimations.degreeVec(-74F,0F,-3.92F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.8F, KeyframeAnimations.degreeVec(-74F,0F,-3.993F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.8F, KeyframeAnimations.degreeVec(-74F,0F,-5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3F, KeyframeAnimations.degreeVec(9F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3F, KeyframeAnimations.degreeVec(9F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition STALK_WALK = AnimationDefinition.Builder.withLength(1.6F)
            .looping()
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2F, KeyframeAnimations.posVec(0F,0.3F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4F, KeyframeAnimations.posVec(0F,0.6F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.posVec(0F,0.3F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.posVec(0F,0.3F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.posVec(0F,0.6F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4F, KeyframeAnimations.posVec(0F,0.3F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(6F,1.414F,1.061F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(6F,2F,1.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(6F,1.414F,1.061F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(6F,-1.414F,-1.061F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(6F,-2F,-1.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(6F,-1.414F,-1.061F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(2F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(4F,0F,1.414F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(2F,0F,2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(0F,0F,1.414F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(2F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(4F,0F,-1.414F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(2F,0F,-2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(0F,0F,-1.414F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(2F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.1333F, KeyframeAnimations.degreeVec(6F,5F,1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2667F, KeyframeAnimations.degreeVec(6F,8.66F,1.732F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(6F,10F,2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5333F, KeyframeAnimations.degreeVec(6F,8.66F,1.732F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(6F,5F,1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9333F, KeyframeAnimations.degreeVec(6F,-5F,-1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0667F, KeyframeAnimations.degreeVec(6F,-8.66F,-1.732F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(6F,-10F,-2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3333F, KeyframeAnimations.degreeVec(6F,-8.66F,-1.732F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4667F, KeyframeAnimations.degreeVec(6F,-5F,-1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(-11.314F,0F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(-16F,0F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(-11.314F,0F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(11.314F,0F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(16F,0F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(11.314F,0F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0F,0F,3F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F,0F,-3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(11.314F,0F,-3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(16F,0F,-3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(11.314F,0F,-3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F,0F,-3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-11.314F,0F,-3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(-16F,0F,-3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(-11.314F,0F,-3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0F,0F,-3F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(14.142F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(20F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(14.142F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-14.142F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(-20F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(-14.142F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(-14.142F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(-20F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(-14.142F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(14.142F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(20F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(14.142F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition DETECT_LOCK = AnimationDefinition.Builder.withLength(1.7F)
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.12F, KeyframeAnimations.posVec(0F,0.25F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.24F, KeyframeAnimations.posVec(0F,0.7F,-0.4F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4F, KeyframeAnimations.posVec(0F,0.1F,0.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9F, KeyframeAnimations.posVec(0F,0F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.7F, KeyframeAnimations.posVec(0F,-0.2F,1F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(0F,22F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(-8F,30F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.7F, KeyframeAnimations.degreeVec(-12F,30F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.1F, KeyframeAnimations.degreeVec(-3F,-4F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(-5F,-14F,-3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.22F, KeyframeAnimations.degreeVec(-8F,52F,12F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.27F, KeyframeAnimations.degreeVec(-12F,60F,18F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.33F, KeyframeAnimations.degreeVec(-6F,46F,7F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(-9F,51F,11F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.46F, KeyframeAnimations.degreeVec(-7F,49F,13F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(-9F,50F,10F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.55F, KeyframeAnimations.degreeVec(-9F,36F,9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6063F, KeyframeAnimations.degreeVec(-8.524F,36.833F,9.655F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6625F, KeyframeAnimations.degreeVec(-9.719F,34.742F,8.011F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7188F, KeyframeAnimations.degreeVec(-8.354F,37.13F,9.888F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.775F, KeyframeAnimations.degreeVec(-9.31F,35.457F,8.573F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8313F, KeyframeAnimations.degreeVec(-9.128F,35.776F,8.824F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8875F, KeyframeAnimations.degreeVec(-8.52F,36.841F,9.66F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9437F, KeyframeAnimations.degreeVec(-9.607F,34.938F,8.166F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-8.528F,36.827F,9.649F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0562F, KeyframeAnimations.degreeVec(-9.152F,35.733F,8.791F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1125F, KeyframeAnimations.degreeVec(-9.207F,35.639F,8.716F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1687F, KeyframeAnimations.degreeVec(-8.547F,36.792F,9.622F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.225F, KeyframeAnimations.degreeVec(-9.493F,35.138F,8.323F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2812F, KeyframeAnimations.degreeVec(-8.675F,36.57F,9.448F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3375F, KeyframeAnimations.degreeVec(-9.036F,35.937F,8.95F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3937F, KeyframeAnimations.degreeVec(-9.246F,35.569F,8.661F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.45F, KeyframeAnimations.degreeVec(-8.595F,36.709F,9.557F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.45F, KeyframeAnimations.degreeVec(-9F,30F,8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.7F, KeyframeAnimations.degreeVec(-10F,30F,7F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.55F, KeyframeAnimations.degreeVec(-3F,10F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(-9F,26F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(-12F,30F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.7F, KeyframeAnimations.degreeVec(-14F,30F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(3F,0F,1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(-22F,0F,-7F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-26F,0F,-9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-26F,0F,-9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.05F, KeyframeAnimations.degreeVec(-25.153F,0F,-8.322F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(-27.317F,0F,-10.054F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.15F, KeyframeAnimations.degreeVec(-24.735F,0F,-7.988F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(-26.751F,0F,-9.601F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.25F, KeyframeAnimations.degreeVec(-26F,0F,-9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(-25.306F,0F,-8.445F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.35F, KeyframeAnimations.degreeVec(-27.078F,0F,-9.863F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(-24.964F,0F,-8.171F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.45F, KeyframeAnimations.degreeVec(-26.615F,0F,-9.492F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.45F, KeyframeAnimations.degreeVec(-26F,0F,-9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.7F, KeyframeAnimations.degreeVec(-28F,0F,-10F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(3F,0F,-1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(-22F,0F,7F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-26F,0F,9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-26F,0F,9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.05F, KeyframeAnimations.degreeVec(-25.153F,0F,9.678F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(-27.317F,0F,7.946F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.15F, KeyframeAnimations.degreeVec(-24.735F,0F,10.012F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(-26.751F,0F,8.399F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.25F, KeyframeAnimations.degreeVec(-26F,0F,9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(-25.306F,0F,9.555F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.35F, KeyframeAnimations.degreeVec(-27.078F,0F,8.137F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(-24.964F,0F,9.829F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.45F, KeyframeAnimations.degreeVec(-26.615F,0F,8.508F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.45F, KeyframeAnimations.degreeVec(-26F,0F,9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.7F, KeyframeAnimations.degreeVec(-28F,0F,10F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(-10F,0F,2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.7F, KeyframeAnimations.degreeVec(-12F,0F,3F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(-10F,0F,-2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.7F, KeyframeAnimations.degreeVec(-12F,0F,-3F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition CHASE_RUN = AnimationDefinition.Builder.withLength(0.6F)
            .looping()
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0F,1.8F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.075F, KeyframeAnimations.posVec(0.424F,0.9F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.15F, KeyframeAnimations.posVec(0.6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.225F, KeyframeAnimations.posVec(0.424F,0.9F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.posVec(0F,1.8F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.375F, KeyframeAnimations.posVec(-0.424F,0.9F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.posVec(-0.6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.525F, KeyframeAnimations.posVec(-0.424F,0.9F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.posVec(0F,1.8F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(-22F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.075F, KeyframeAnimations.degreeVec(-19F,0F,2.828F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.15F, KeyframeAnimations.degreeVec(-22F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.225F, KeyframeAnimations.degreeVec(-25F,0F,2.828F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(-22F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(-19F,0F,-2.828F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(-22F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.525F, KeyframeAnimations.degreeVec(-25F,0F,-2.828F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(-22F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.075F, KeyframeAnimations.degreeVec(2F,2.828F,2.121F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.15F, KeyframeAnimations.degreeVec(0F,4F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.225F, KeyframeAnimations.degreeVec(-2F,2.828F,2.121F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(2F,-2.828F,-2.121F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(0F,-4F,-3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.525F, KeyframeAnimations.degreeVec(-2F,-2.828F,-2.121F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(15F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.075F, KeyframeAnimations.degreeVec(18F,1.414F,1.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.15F, KeyframeAnimations.degreeVec(21F,2F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.225F, KeyframeAnimations.degreeVec(18F,1.414F,-1.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(15F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(18F,-1.414F,1.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(21F,-2F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.525F, KeyframeAnimations.degreeVec(18F,-1.414F,-1.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(15F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(-18F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.075F, KeyframeAnimations.degreeVec(6.042F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.15F, KeyframeAnimations.degreeVec(16F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.225F, KeyframeAnimations.degreeVec(6.042F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(-18F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(-42.042F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(-52F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.525F, KeyframeAnimations.degreeVec(-42.042F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(-18F,0F,4F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(-18F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.075F, KeyframeAnimations.degreeVec(-42.042F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.15F, KeyframeAnimations.degreeVec(-52F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.225F, KeyframeAnimations.degreeVec(-42.042F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(-18F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(6.042F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(16F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.525F, KeyframeAnimations.degreeVec(6.042F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(-18F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.075F, KeyframeAnimations.degreeVec(28.284F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.15F, KeyframeAnimations.degreeVec(40F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.225F, KeyframeAnimations.degreeVec(28.284F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(-28.284F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(-40F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.525F, KeyframeAnimations.degreeVec(-28.284F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.075F, KeyframeAnimations.degreeVec(-28.284F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.15F, KeyframeAnimations.degreeVec(-40F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.225F, KeyframeAnimations.degreeVec(-28.284F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.375F, KeyframeAnimations.degreeVec(28.284F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(40F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.525F, KeyframeAnimations.degreeVec(28.284F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition ATTACK_SWIPE = AnimationDefinition.Builder.withLength(0.8F)
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(20F,-10F,30F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(-40F,10F,-28F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(-58F,16F,-48F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(-6F,0F,-12F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(8F,0F,14F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(-4F,20F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(2F,-22F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.46F, KeyframeAnimations.degreeVec(4F,-30F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.62F, KeyframeAnimations.degreeVec(-2F,-6F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(0F,14F,-3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.36F, KeyframeAnimations.degreeVec(0F,-18F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.18F, KeyframeAnimations.posVec(0F,0F,-0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.34F, KeyframeAnimations.posVec(0F,0F,0.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(-6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(4F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition ATTACK_OVERHEAD_SLAM = AnimationDefinition.Builder.withLength(1.0F)
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.25F, KeyframeAnimations.posVec(0F,2.2F,-0.5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.32F, KeyframeAnimations.posVec(0F,2.6F,-0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.44F, KeyframeAnimations.posVec(0F,-2.2F,0.8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.posVec(0F,-2.6F,0.9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.66F, KeyframeAnimations.posVec(0F,0.4F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(16F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(18F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.44F, KeyframeAnimations.degreeVec(-32F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(-38F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.66F, KeyframeAnimations.degreeVec(-6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(3F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(-22F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.44F, KeyframeAnimations.degreeVec(24F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.52F, KeyframeAnimations.degreeVec(30F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(-156F,0F,6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(-160F,0F,6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.44F, KeyframeAnimations.degreeVec(8F,0F,2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.52F, KeyframeAnimations.degreeVec(16F,0F,2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(-6F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(-156F,0F,-6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.32F, KeyframeAnimations.degreeVec(-160F,0F,-6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.44F, KeyframeAnimations.degreeVec(8F,0F,-2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.52F, KeyframeAnimations.degreeVec(16F,0F,-2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(-6F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(-8F,0F,2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(6F,0F,1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(-8F,0F,-2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(6F,0F,-1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition ATTACK_LUNGE = AnimationDefinition.Builder.withLength(0.72F)
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.16F, KeyframeAnimations.posVec(0F,0.3F,-1.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.posVec(0F,0.6F,3.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4F, KeyframeAnimations.posVec(0F,0.4F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.72F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(12F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(-34F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(-30F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.72F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(-14F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(22F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(26F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.72F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(24F,0F,14F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(-64F,0F,-18F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(-58F,0F,-22F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.72F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(24F,0F,-14F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(-64F,0F,18F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(-58F,0F,22F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.72F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(-14F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(28F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.72F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.16F, KeyframeAnimations.degreeVec(-14F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(28F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.72F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition ATTACK_FLURRY = AnimationDefinition.Builder.withLength(1.3F)
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.12F, KeyframeAnimations.degreeVec(10F,0F,18F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2F, KeyframeAnimations.degreeVec(-42F,0F,-34F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(-52F,0F,-44F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3F, KeyframeAnimations.degreeVec(-10F,0F,6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(10F,0F,18F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(-42F,0F,-34F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.56F, KeyframeAnimations.degreeVec(-52F,0F,-44F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(-10F,0F,6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.74F, KeyframeAnimations.degreeVec(-58F,0F,-50F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(-64F,0F,-56F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.18F, KeyframeAnimations.degreeVec(-10F,0F,-6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(10F,0F,-18F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.34F, KeyframeAnimations.degreeVec(-42F,0F,34F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(-52F,0F,44F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.48F, KeyframeAnimations.degreeVec(-10F,0F,-6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.58F, KeyframeAnimations.degreeVec(10F,0F,-18F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.66F, KeyframeAnimations.degreeVec(-42F,0F,34F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.72F, KeyframeAnimations.degreeVec(-52F,0F,44F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.74F, KeyframeAnimations.degreeVec(-58F,0F,50F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(-64F,0F,56F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.12F, KeyframeAnimations.degreeVec(-3F,16F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(-3F,-16F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(-3F,16F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.58F, KeyframeAnimations.degreeVec(-3F,-16F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.74F, KeyframeAnimations.degreeVec(-8F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(-12F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.12F, KeyframeAnimations.degreeVec(4F,-14F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.26F, KeyframeAnimations.degreeVec(4F,14F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(4F,-14F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.58F, KeyframeAnimations.degreeVec(4F,14F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.78F, KeyframeAnimations.degreeVec(-6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2F, KeyframeAnimations.posVec(0F,0F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.posVec(0F,0F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.74F, KeyframeAnimations.posVec(0F,0.4F,-0.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.posVec(0F,0F,1.4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.74F, KeyframeAnimations.degreeVec(-8F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.74F, KeyframeAnimations.degreeVec(-8F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition SCREAM_WAIL = AnimationDefinition.Builder.withLength(1.9F)
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.posVec(0F,1.6F,-0.8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.55F, KeyframeAnimations.posVec(0F,1.8F,-0.9F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.55F, KeyframeAnimations.posVec(0F,0.4F,1.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.59F, KeyframeAnimations.posVec(0F,0.69F,1.49F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.63F, KeyframeAnimations.posVec(0F,0.052F,0.852F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.67F, KeyframeAnimations.posVec(0F,0.561F,1.361F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.71F, KeyframeAnimations.posVec(0F,0.516F,1.316F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.75F, KeyframeAnimations.posVec(0F,0.118F,0.918F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.75F, KeyframeAnimations.posVec(0F,0.2F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.posVec(0F,0.127F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.85F, KeyframeAnimations.posVec(0F,0.33F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9F, KeyframeAnimations.posVec(0F,0.031F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.posVec(0F,0.387F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.posVec(0F,0.015F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.05F, KeyframeAnimations.posVec(0F,0.366F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.posVec(0F,0.067F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.15F, KeyframeAnimations.posVec(0F,0.291F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.posVec(0F,0.155F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.25F, KeyframeAnimations.posVec(0F,0.2F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.posVec(0F,0.24F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.posVec(0F,0.2F,0.4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9F, KeyframeAnimations.posVec(0F,-0.3F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(14F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.55F, KeyframeAnimations.degreeVec(16F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.66F, KeyframeAnimations.degreeVec(-6F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(-4F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9F, KeyframeAnimations.degreeVec(6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(-44F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.55F, KeyframeAnimations.degreeVec(-48F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.64F, KeyframeAnimations.degreeVec(-8F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(-4F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(-6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7429F, KeyframeAnimations.degreeVec(-6.842F,-1.01F,-0.673F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7857F, KeyframeAnimations.degreeVec(-4.49F,1.812F,1.208F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8286F, KeyframeAnimations.degreeVec(-7.936F,-2.323F,-1.549F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8714F, KeyframeAnimations.degreeVec(-3.913F,2.505F,1.67F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9143F, KeyframeAnimations.degreeVec(-7.967F,-2.361F,-1.574F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9571F, KeyframeAnimations.degreeVec(-4.387F,1.936F,1.291F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-7.089F,-1.306F,-0.871F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0429F, KeyframeAnimations.degreeVec(-5.528F,0.566F,0.378F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0857F, KeyframeAnimations.degreeVec(-5.848F,0.183F,0.122F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1286F, KeyframeAnimations.degreeVec(-6.707F,-0.848F,-0.565F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1714F, KeyframeAnimations.degreeVec(-4.872F,1.353F,0.902F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2143F, KeyframeAnimations.degreeVec(-7.375F,-1.649F,-1.1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2571F, KeyframeAnimations.degreeVec(-4.569F,1.717F,1.145F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(-7.305F,-1.566F,-1.044F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(-6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(20F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9F, KeyframeAnimations.degreeVec(10F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(34F,0F,42F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.55F, KeyframeAnimations.degreeVec(36F,0F,46F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.66F, KeyframeAnimations.degreeVec(-30F,0F,64F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(-26F,0F,60F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(-27.118F,0F,58.323F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(-24.279F,0F,62.582F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(-27.637F,0F,57.544F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(-25.038F,0F,61.444F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(-26F,0F,60F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-26.871F,0F,58.694F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.05F, KeyframeAnimations.degreeVec(-24.66F,0F,62.011F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(-27.275F,0F,58.087F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.15F, KeyframeAnimations.degreeVec(-25.25F,0F,61.124F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(-26F,0F,60F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.25F, KeyframeAnimations.degreeVec(-26.678F,0F,58.983F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(-24.956F,0F,61.566F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(-26F,0F,58F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(34F,0F,-42F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.55F, KeyframeAnimations.degreeVec(36F,0F,-46F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.66F, KeyframeAnimations.degreeVec(-30F,0F,-64F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(-26F,0F,-60F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(-27.118F,0F,-61.677F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(-24.279F,0F,-57.418F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(-27.637F,0F,-62.456F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(-25.038F,0F,-58.556F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(-26F,0F,-60F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-26.871F,0F,-61.306F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.05F, KeyframeAnimations.degreeVec(-24.66F,0F,-57.989F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(-27.275F,0F,-61.913F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.15F, KeyframeAnimations.degreeVec(-25.25F,0F,-58.876F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(-26F,0F,-60F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.25F, KeyframeAnimations.degreeVec(-26.678F,0F,-61.017F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(-24.956F,0F,-58.434F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(-26F,0F,-58F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(16F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.66F, KeyframeAnimations.degreeVec(-12F,0F,0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(-10F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(-10.839F,-0.839F,-0.559F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(-8.709F,1.291F,0.861F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.85F, KeyframeAnimations.degreeVec(-11.228F,-1.228F,-0.819F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9F, KeyframeAnimations.degreeVec(-9.278F,0.722F,0.481F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.95F, KeyframeAnimations.degreeVec(-10F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-10.653F,-0.653F,-0.435F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.05F, KeyframeAnimations.degreeVec(-8.995F,1.005F,0.67F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(-10.956F,-0.956F,-0.638F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.15F, KeyframeAnimations.degreeVec(-9.438F,0.562F,0.375F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(-10F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.25F, KeyframeAnimations.degreeVec(-10.509F,-0.509F,-0.339F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(-9.217F,0.783F,0.522F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.3F, KeyframeAnimations.degreeVec(-10F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9F, KeyframeAnimations.degreeVec(4F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0F, KeyframeAnimations.scaleVec(1F,1F,1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.55F, KeyframeAnimations.scaleVec(1.04F,1.06F,1.04F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.66F, KeyframeAnimations.scaleVec(1.12F,1.14F,1.12F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9F, KeyframeAnimations.scaleVec(1.05F,1.06F,1.05F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9F, KeyframeAnimations.scaleVec(1F,1F,1F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(-6F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.66F, KeyframeAnimations.degreeVec(4F,0F,2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(-6F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.66F, KeyframeAnimations.degreeVec(4F,0F,-2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition HURT = AnimationDefinition.Builder.withLength(0.45F)
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.05F, KeyframeAnimations.posVec(0F,0.3F,-0.9F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.12F, KeyframeAnimations.posVec(0F,0.1F,-0.4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.12F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.1671F, KeyframeAnimations.posVec(0F,-0.127F,-0.191F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2143F, KeyframeAnimations.posVec(0F,0.103F,0.154F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2614F, KeyframeAnimations.posVec(0F,-0.011F,-0.016F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3086F, KeyframeAnimations.posVec(0F,-0.05F,-0.075F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3557F, KeyframeAnimations.posVec(0F,0.046F,0.069F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4029F, KeyframeAnimations.posVec(0F,-0.009F,-0.014F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.posVec(0F,-0.019F,-0.028F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.05F, KeyframeAnimations.degreeVec(14F,0F,6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.12F, KeyframeAnimations.degreeVec(8F,0F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.12F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.1671F, KeyframeAnimations.degreeVec(-1.909F,-1.273F,-2.545F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2143F, KeyframeAnimations.degreeVec(1.542F,1.028F,2.056F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2614F, KeyframeAnimations.degreeVec(-0.161F,-0.107F,-0.215F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3086F, KeyframeAnimations.degreeVec(-0.746F,-0.497F,-0.994F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3557F, KeyframeAnimations.degreeVec(0.694F,0.462F,0.925F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4029F, KeyframeAnimations.degreeVec(-0.137F,-0.091F,-0.182F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(-0.284F,-0.189F,-0.378F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.05F, KeyframeAnimations.degreeVec(-18F,8F,6F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.12F, KeyframeAnimations.degreeVec(-8F,4F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.12F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.1671F, KeyframeAnimations.degreeVec(-2.545F,-3.182F,-3.182F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2143F, KeyframeAnimations.degreeVec(2.056F,2.57F,2.57F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2614F, KeyframeAnimations.degreeVec(-0.215F,-0.268F,-0.268F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3086F, KeyframeAnimations.degreeVec(-0.994F,-1.243F,-1.243F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3557F, KeyframeAnimations.degreeVec(0.925F,1.156F,1.156F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4029F, KeyframeAnimations.degreeVec(-0.182F,-0.228F,-0.228F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(-0.378F,-0.473F,-0.473F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.06F, KeyframeAnimations.degreeVec(-14F,0F,20F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.06F, KeyframeAnimations.degreeVec(-14F,0F,-20F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.45F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition SPAWN_EMERGE = AnimationDefinition.Builder.withLength(6.5F)
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F,-36F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4F, KeyframeAnimations.posVec(0F,-35.4F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.posVec(0F,-32F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.posVec(0F,-30.4F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.3F, KeyframeAnimations.posVec(0F,-24F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.posVec(0F,-20.4F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.8F, KeyframeAnimations.posVec(0F,-12F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.6F, KeyframeAnimations.posVec(0F,-5F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.2F, KeyframeAnimations.posVec(0F,-0.6F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.4F, KeyframeAnimations.posVec(0F,0.9F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.8F, KeyframeAnimations.posVec(0F,-0.1F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.5F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(22F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(24F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(18F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(10F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.0F, KeyframeAnimations.degreeVec(4F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.1F, KeyframeAnimations.degreeVec(4F,0F,-1.564F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.2F, KeyframeAnimations.degreeVec(4F,0F,-2.244F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.3F, KeyframeAnimations.degreeVec(4F,0F,-1.991F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.4F, KeyframeAnimations.degreeVec(4F,0F,-1.091F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.5F, KeyframeAnimations.degreeVec(4F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.6F, KeyframeAnimations.degreeVec(4F,0F,0.858F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.7F, KeyframeAnimations.degreeVec(4F,0F,1.232F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.8F, KeyframeAnimations.degreeVec(4F,0F,1.092F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.8F, KeyframeAnimations.degreeVec(3F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.35F, KeyframeAnimations.degreeVec(-150F,0F,10F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6F, KeyframeAnimations.degreeVec(-150F,0F,10F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(-90F,0F,8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4F, KeyframeAnimations.degreeVec(-40F,0F,5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.8F, KeyframeAnimations.degreeVec(-150F,0F,12F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.4F, KeyframeAnimations.degreeVec(-80F,0F,8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.0F, KeyframeAnimations.degreeVec(-130F,0F,10F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.6F, KeyframeAnimations.degreeVec(-50F,0F,6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.4F, KeyframeAnimations.degreeVec(-80F,0F,8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.0F, KeyframeAnimations.degreeVec(-30F,0F,4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.55F, KeyframeAnimations.degreeVec(-150F,0F,-10F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8F, KeyframeAnimations.degreeVec(-150F,0F,-10F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2F, KeyframeAnimations.degreeVec(-90F,0F,-8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.6F, KeyframeAnimations.degreeVec(-40F,0F,-5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(-150F,0F,-12F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.6F, KeyframeAnimations.degreeVec(-80F,0F,-8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.3F, KeyframeAnimations.degreeVec(-130F,0F,-10F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.9F, KeyframeAnimations.degreeVec(-50F,0F,-6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(-80F,0F,-8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.2F, KeyframeAnimations.degreeVec(-30F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(30F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.degreeVec(34F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.5F, KeyframeAnimations.degreeVec(28F,0F,5F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.8F, KeyframeAnimations.degreeVec(15F,0F,-4F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.8F, KeyframeAnimations.degreeVec(20F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.2F, KeyframeAnimations.degreeVec(8F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.275F, KeyframeAnimations.degreeVec(5.217F,-3.711F,-1.855F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.35F, KeyframeAnimations.degreeVec(8F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.425F, KeyframeAnimations.degreeVec(10.396F,3.194F,1.597F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.5F, KeyframeAnimations.degreeVec(8F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.575F, KeyframeAnimations.degreeVec(5.938F,-2.749F,-1.375F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.65F, KeyframeAnimations.degreeVec(8F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.725F, KeyframeAnimations.degreeVec(9.775F,2.366F,1.183F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.8F, KeyframeAnimations.degreeVec(8F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.8F, KeyframeAnimations.degreeVec(6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(15F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(20F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.8F, KeyframeAnimations.degreeVec(10F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.0F, KeyframeAnimations.degreeVec(6F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.2F, KeyframeAnimations.degreeVec(3F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.275F, KeyframeAnimations.degreeVec(1.359F,-1.641F,-2.461F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.35F, KeyframeAnimations.degreeVec(1.628F,-1.372F,-2.058F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.425F, KeyframeAnimations.degreeVec(3.244F,0.244F,0.366F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.5F, KeyframeAnimations.degreeVec(4.367F,1.367F,2.051F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.575F, KeyframeAnimations.degreeVec(3.936F,0.936F,1.404F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.65F, KeyframeAnimations.degreeVec(2.623F,-0.377F,-0.565F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.725F, KeyframeAnimations.degreeVec(1.891F,-1.109F,-1.663F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.8F, KeyframeAnimations.degreeVec(2.392F,-0.608F,-0.911F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.8F, KeyframeAnimations.degreeVec(2F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.8F, KeyframeAnimations.degreeVec(26F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(15F,0F,3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.2F, KeyframeAnimations.degreeVec(-5F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(3.8F, KeyframeAnimations.degreeVec(26F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(4.6F, KeyframeAnimations.degreeVec(15F,0F,-3F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.2F, KeyframeAnimations.degreeVec(-5F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(5.8F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(6.5F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

    public static final AnimationDefinition DISSIPATE = AnimationDefinition.Builder.withLength(2.2F)
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0F, KeyframeAnimations.scaleVec(1F,1F,1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4F, KeyframeAnimations.scaleVec(1.02F,1.03F,1.02F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.scaleVec(1.06F,1.12F,1.06F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9F, KeyframeAnimations.scaleVec(1.12F,1.18F,1.12F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.1F, KeyframeAnimations.scaleVec(0.85F,0.95F,0.85F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.5F, KeyframeAnimations.scaleVec(0.35F,0.55F,0.35F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.scaleVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.2F, KeyframeAnimations.scaleVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0F, KeyframeAnimations.posVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.posVec(0F,1F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.posVec(0F,2.2F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.posVec(0F,5F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.2F, KeyframeAnimations.posVec(0F,5F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("soul", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.0778F, KeyframeAnimations.degreeVec(-1.269F,-1.693F,-1.269F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.1556F, KeyframeAnimations.degreeVec(0.698F,0.931F,0.698F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2333F, KeyframeAnimations.degreeVec(0.621F,0.828F,0.621F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3111F, KeyframeAnimations.degreeVec(-0.895F,-1.193F,-0.895F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.3889F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4667F, KeyframeAnimations.degreeVec(0.708F,0.945F,0.708F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5444F, KeyframeAnimations.degreeVec(-0.39F,-0.519F,-0.39F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6222F, KeyframeAnimations.degreeVec(-0.347F,-0.462F,-0.347F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(0.499F,0.666F,0.499F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.4F, KeyframeAnimations.degreeVec(-14F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(-30F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(-40F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(-30F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(10F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(4F,0F,0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0F, KeyframeAnimations.scaleVec(1F,1F,1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9F, KeyframeAnimations.scaleVec(1.1F,1.05F,1.1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4F, KeyframeAnimations.scaleVec(0.6F,0.8F,0.6F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.scaleVec(0.2F,0.2F,0.2F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(-20F,0F,36F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(-40F,0F,60F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(-50F,0F,70F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7F, KeyframeAnimations.degreeVec(-20F,0F,-36F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(-40F,0F,-60F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(-50F,0F,-70F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(-10F,0F,8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(-16F,0F,14F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0F, KeyframeAnimations.degreeVec(0F,0F,0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.1F, KeyframeAnimations.degreeVec(-10F,0F,-8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.degreeVec(-16F,0F,-14F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();

}