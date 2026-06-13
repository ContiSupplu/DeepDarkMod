package com._jackoboy.otherside.event;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.client.OthersideDimensionEffects;
import com._jackoboy.otherside.client.model.WarbModel;
import com._jackoboy.otherside.client.model.WardModel;
import com._jackoboy.otherside.client.model.WugModel;
import com._jackoboy.otherside.client.renderer.WarbRenderer;
import com._jackoboy.otherside.client.renderer.WardRenderer;
import com._jackoboy.otherside.client.renderer.WugRenderer;
import com._jackoboy.otherside.entity.WarbEntity;
import com._jackoboy.otherside.entity.WardEntity;
import com._jackoboy.otherside.entity.WugEntity;
import com._jackoboy.otherside.network.ModNetworking;
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
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.WUG.get(), WugRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.WARB.get(), WarbRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.WARD.get(), WardRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(WugModel.LAYER, WugModel::createBodyLayer);
        event.registerLayerDefinition(WarbModel.LAYER, WarbModel::createBodyLayer);
        event.registerLayerDefinition(WardModel.LAYER, WardModel::createBodyLayer);
    }
}
