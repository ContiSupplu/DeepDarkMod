package com._jackoboy.otherside.event;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.client.OthersideDimensionEffects;
import com._jackoboy.otherside.client.model.WarbModel;
import com._jackoboy.otherside.client.model.WardModel;
import com._jackoboy.otherside.client.model.WugModel;
import com._jackoboy.otherside.client.model.MawTentacleModel;
import com._jackoboy.otherside.client.model.EchoSoulModel;
import com._jackoboy.otherside.client.model.ListeningBloomModel;
import com._jackoboy.otherside.client.renderer.WarbRenderer;
import com._jackoboy.otherside.client.renderer.WardRenderer;
import com._jackoboy.otherside.client.renderer.WugRenderer;
import com._jackoboy.otherside.client.renderer.MawTentacleRenderer;
import com._jackoboy.otherside.client.renderer.EchoSoulRenderer;
import com._jackoboy.otherside.client.renderer.ListeningBloomRenderer;
import com._jackoboy.otherside.client.renderer.OthersidePortalRenderer;
import com._jackoboy.otherside.entity.MawTentacleEntity;
import com._jackoboy.otherside.entity.EchoSoulEntity;
import com._jackoboy.otherside.entity.ListeningBloomEntity;
import com._jackoboy.otherside.entity.WarbEntity;
import com._jackoboy.otherside.entity.WardEntity;
import com._jackoboy.otherside.entity.WugEntity;
import com._jackoboy.otherside.network.ModNetworking;
import com._jackoboy.otherside.registry.ModBlockEntityTypes;
import com._jackoboy.otherside.registry.ModEntityTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = OthersideMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusEvents {
    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        ModNetworking.register(event);
    }

    @SubscribeEvent
    public static void onRegisterDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(OthersideDimensionEffects.EFFECTS_ID, new OthersideDimensionEffects());
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.WUG.get(), WugEntity.createAttributes().build());
        event.put(ModEntityTypes.WARB.get(), WarbEntity.createAttributes().build());
        event.put(ModEntityTypes.WARD.get(), WardEntity.createAttributes().build());
        event.put(ModEntityTypes.MAW_TENTACLE.get(), MawTentacleEntity.createAttributes().build());
        event.put(ModEntityTypes.ECHO_SOUL.get(), EchoSoulEntity.createAttributes().build());
        event.put(ModEntityTypes.LISTENING_BLOOM.get(), ListeningBloomEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.WUG.get(), WugRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.WARB.get(), WarbRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.WARD.get(), WardRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.MAW_TENTACLE.get(), MawTentacleRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.ECHO_SOUL.get(), EchoSoulRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.LISTENING_BLOOM.get(), ListeningBloomRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.OTHERSIDE_PORTAL.get(), OthersidePortalRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WugModel.LAYER, WugModel::createBodyLayer);
        event.registerLayerDefinition(WarbModel.LAYER, WarbModel::createBodyLayer);
        event.registerLayerDefinition(WardModel.LAYER, WardModel::createBodyLayer);
        event.registerLayerDefinition(MawTentacleModel.LAYER, MawTentacleModel::createBodyLayer);
        event.registerLayerDefinition(EchoSoulModel.LAYER, EchoSoulModel::createBodyLayer);
        event.registerLayerDefinition(ListeningBloomModel.LAYER, ListeningBloomModel::createBodyLayer);
    }
}
