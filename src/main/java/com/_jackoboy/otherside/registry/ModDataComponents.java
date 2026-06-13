package com._jackoboy.otherside.registry;

import com._jackoboy.otherside.OthersideMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, OthersideMod.MOD_ID);

    /** Fuel ticks remaining on an Echo Lantern item. Default 18000 = 15 minutes at 20 tps. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ECHO_FUEL =
            DATA_COMPONENT_TYPES.register("echo_fuel", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT)
                            .build());
}
