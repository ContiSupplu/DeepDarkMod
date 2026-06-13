package com._jackoboy.otherside.registry;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.entity.WugEntity;
import com._jackoboy.otherside.entity.WarbEntity;
import com._jackoboy.otherside.entity.WardEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, OthersideMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<WugEntity>> WUG =
            ENTITY_TYPES.register("wug", () -> EntityType.Builder.of(WugEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 0.8F)
                    .clientTrackingRange(8)
                    .build("otherside:wug"));

    public static final DeferredHolder<EntityType<?>, EntityType<WarbEntity>> WARB =
            ENTITY_TYPES.register("warb", () -> EntityType.Builder.of(WarbEntity::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.8F)
                    .clientTrackingRange(10)
                    .build("otherside:warb"));

    public static final DeferredHolder<EntityType<?>, EntityType<WardEntity>> WARD =
            ENTITY_TYPES.register("ward", () -> EntityType.Builder.of(WardEntity::new, MobCategory.MONSTER)
                    .sized(0.7F, 1.4F)
                    .clientTrackingRange(10)
                    .build("otherside:ward"));
}
