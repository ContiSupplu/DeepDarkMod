package com._jackoboy.otherside;

import com._jackoboy.otherside.registry.ModBlocks;
import com._jackoboy.otherside.registry.ModItems;
import com._jackoboy.otherside.registry.ModSoundEvents;
import com._jackoboy.otherside.registry.ModCreativeTabs;
import com._jackoboy.otherside.registry.ModBlockEntityTypes;
import com._jackoboy.otherside.registry.ModDataComponents;
import com._jackoboy.otherside.registry.ModEntityTypes;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(OthersideMod.MOD_ID)
public class OthersideMod {
    public static final String MOD_ID = "otherside";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OthersideMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register deferred registers
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.BLOCK_ITEMS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModSoundEvents.SOUND_EVENTS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);

        // Register configs
        modContainer.registerConfig(ModConfig.Type.SERVER, OthersideConfig.SERVER_SPEC);
    }
}
