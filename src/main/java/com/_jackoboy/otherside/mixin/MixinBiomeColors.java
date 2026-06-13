package com._jackoboy.otherside.mixin;

import com._jackoboy.otherside.infection.InfectionEffects;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides water color to swampy green when water sits above infected blocks.
 * Uses a smooth gradient based on how deep the sculk is — closer sculk = more swampy.
 */
@Mixin(BiomeColors.class)
public class MixinBiomeColors {

    // Swamp water color: #617B64
    private static final int SWAMP_R = 0x61;
    private static final int SWAMP_G = 0x7B;
    private static final int SWAMP_B = 0x64;

    @Inject(method = "getAverageWaterColor", at = @At("HEAD"), cancellable = true)
    private static void otherside$overrideInfectedWaterColor(BlockAndTintGetter level, BlockPos pos,
                                                              CallbackInfoReturnable<Integer> cir) {
        int scanDepth = 15;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

        for (int depth = 1; depth <= scanDepth; depth++) {
            mutablePos.move(0, -1, 0);

            if (mutablePos.getY() < level.getMinBuildHeight()) break;

            BlockState state = level.getBlockState(mutablePos);

            if (InfectionEffects.isInfectedBlock(state)) {
                // Smooth transition: closer sculk = stronger swamp tint
                // depth 1 (right below) = 100% swamp, depth 10+ = 30% swamp
                float blend = Math.max(0.3f, 1.0f - (depth - 1) * 0.07f);

                // Get the vanilla water color from the position for blending
                // Use a simple default blue as the vanilla base
                int vanillaR = 0x3F;
                int vanillaG = 0x76;
                int vanillaB = 0xE4;

                int r = (int)(vanillaR + (SWAMP_R - vanillaR) * blend);
                int g = (int)(vanillaG + (SWAMP_G - vanillaG) * blend);
                int b = (int)(vanillaB + (SWAMP_B - vanillaB) * blend);

                cir.setReturnValue((r << 16) | (g << 8) | b);
                return;
            }

            // Stop scanning if we hit solid non-water
            if (!state.is(Blocks.WATER) && !state.isAir()) {
                break;
            }
        }
    }
}
