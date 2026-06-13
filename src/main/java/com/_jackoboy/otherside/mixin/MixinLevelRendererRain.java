package com._jackoboy.otherside.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels ALL vanilla rain/snow rendering.
 * The overcast sky, clouds, and sky darkening remain intact because those
 * are driven by weatherLevel/rainLevel in the sky renderer, not here.
 * Only the visible rain drops/snow particles are removed.
 */
@Mixin(LevelRenderer.class)
public class MixinLevelRendererRain {

    @Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true)
    private void otherside$cancelRain(LightTexture lightTexture, float partialTick,
                                       double camX, double camY, double camZ,
                                       CallbackInfo ci) {
        // Cancel rain streak rendering — keep overcast atmosphere only
        ci.cancel();
    }

    @Inject(method = "tickRain", at = @At("HEAD"), cancellable = true)
    private void otherside$cancelRainParticles(net.minecraft.client.Camera camera, CallbackInfo ci) {
        // Cancel rain splash particles on the ground
        ci.cancel();
    }
}
