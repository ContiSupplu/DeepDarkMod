package com._jackoboy.otherside.registry;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.block.EchoLanternBlockEntity;
import com._jackoboy.otherside.block.OthersidePortalBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, OthersideMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EchoLanternBlockEntity>> ECHO_LANTERN =
            BLOCK_ENTITY_TYPES.register("echo_lantern", () ->
                    BlockEntityType.Builder.of(EchoLanternBlockEntity::new, ModBlocks.ECHO_LANTERN_BLOCK.get())
                            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OthersidePortalBlockEntity>> OTHERSIDE_PORTAL =
            BLOCK_ENTITY_TYPES.register("otherside_portal", () ->
                    BlockEntityType.Builder.of(OthersidePortalBlockEntity::new, ModBlocks.OTHERSIDE_PORTAL.get())
                            .build(null));
}

