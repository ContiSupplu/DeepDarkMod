package com._jackoboy.otherside.registry;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OthersideMod.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> OTHERSIDE_TAB =
            CREATIVE_TABS.register("otherside_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + OthersideMod.MOD_ID))
                    .icon(() -> new ItemStack(ModItems.ECHO_DUST.get()))
                    .displayItems((params, output) -> {
                        // ── Existing blocks ──
                        output.accept(ModBlocks.SCULK_STONE.get());
                        output.accept(ModBlocks.SCULK_WOOD.get());
                        output.accept(ModBlocks.SCULK_MEMBRANE.get());
                        output.accept(ModBlocks.SCORCHED_EARTH.get());
                        output.accept(ModBlocks.TENDRIL_BLOCK.get());
                        output.accept(ModBlocks.TENDRIL_HEART.get());
                        output.accept(ModBlocks.CHARGED_FRAME.get());
                        output.accept(ModBlocks.GLOOM_BULB.get());
                        output.accept(ModBlocks.ECHO_ANEMONE.get());
                        output.accept(ModBlocks.EXTINGUISHED_TORCH.get());
                        output.accept(ModBlocks.EXTINGUISHED_SOUL_TORCH.get());
                        output.accept(ModBlocks.EXTINGUISHED_LANTERN.get());
                        output.accept(ModBlocks.ECHO_ANCHOR.get());

                        // ── Echo Dimension — Terrain ──
                        output.accept(ModBlocks.SCULK_GRASS.get());
                        output.accept(ModBlocks.SCULK_DIRT.get());
                        output.accept(ModBlocks.SCULK_GRAVEL.get());
                        output.accept(ModBlocks.SCULK_LEAVES.get());

                        // ── Echo Dimension — Wood Set ──
                        output.accept(ModBlocks.ECHO_LOG.get());
                        output.accept(ModBlocks.STRIPPED_ECHO_LOG.get());
                        output.accept(ModBlocks.ECHO_WOOD_BLOCK.get());
                        output.accept(ModBlocks.STRIPPED_ECHO_WOOD.get());
                        output.accept(ModBlocks.ECHO_PLANKS.get());
                        output.accept(ModBlocks.ECHO_STAIRS.get());
                        output.accept(ModBlocks.ECHO_SLAB.get());
                        output.accept(ModBlocks.ECHO_FENCE.get());
                        output.accept(ModBlocks.ECHO_FENCE_GATE.get());
                        output.accept(ModBlocks.ECHO_DOOR.get());
                        output.accept(ModBlocks.ECHO_TRAPDOOR.get());
                        output.accept(ModBlocks.ECHO_BUTTON.get());
                        output.accept(ModBlocks.ECHO_PRESSURE_PLATE.get());

                        // ── Echo Dimension — Portal ──
                        output.accept(ModBlocks.ECHO_PORTAL_FRAME.get());

                        // ── Items ──
                        output.accept(ModItems.ECHO_DUST.get());
                        output.accept(ModItems.ECHO_SHARD_CLUSTER.get());
                        output.accept(ModItems.RESONANT_CORE.get());
                        output.accept(ModItems.STALKER_SINEW.get());
                        output.accept(ModItems.TAINTED_FLESH.get());
                        output.accept(ModItems.RESONANT_CATALYST.get());
                        output.accept(ModItems.IGNITION_SPARK.get());
                        output.accept(ModItems.WARDEN_HEARTCATALYST.get());
                        output.accept(ModItems.PURGE_CHARGE.get());
                    })
                    .build());
}
