package com._jackoboy.otherside.registry;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.entity.WugEntity;
import com._jackoboy.otherside.entity.WarbEntity;
import com._jackoboy.otherside.entity.WardEntity;
import com._jackoboy.otherside.entity.MawTentacleEntity;
import com._jackoboy.otherside.entity.EchoSoulEntity;
import com._jackoboy.otherside.entity.ListeningBloomEntity;
import com._jackoboy.otherside.entity.WhisperingEchoEntity;
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

    public static final DeferredHolder<EntityType<?>, EntityType<MawTentacleEntity>> MAW_TENTACLE =
            ENTITY_TYPES.register("maw_tentacle", () -> EntityType.Builder.of(MawTentacleEntity::new, MobCategory.MISC)
                    .sized(1.4F, 7.5F)
                    .clientTrackingRange(16)
                    .fireImmune()
                    .build("otherside:maw_tentacle"));

    public static final DeferredHolder<EntityType<?>, EntityType<EchoSoulEntity>> ECHO_SOUL =
            ENTITY_TYPES.register("echo_soul", () -> EntityType.Builder.of(EchoSoulEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(16)
                    .build("otherside:echo_soul"));

    public static final DeferredHolder<EntityType<?>, EntityType<ListeningBloomEntity>> LISTENING_BLOOM =
            ENTITY_TYPES.register("listening_bloom", () -> EntityType.Builder.of(ListeningBloomEntity::new, MobCategory.MISC)
                    .sized(1.2F, 1.2F)
                    .clientTrackingRange(16)
                    .build("otherside:listening_bloom"));

    public static final DeferredHolder<EntityType<?>, EntityType<WhisperingEchoEntity>> WHISPERING_ECHO =
            ENTITY_TYPES.register("whispering_echo", () -> EntityType.Builder.of(WhisperingEchoEntity::new, MobCategory.CREATURE)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .build("otherside:whispering_echo"));
}
