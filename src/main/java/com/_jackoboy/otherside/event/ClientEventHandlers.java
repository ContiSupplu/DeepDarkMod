package com._jackoboy.otherside.event;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.client.AudioDuckHandler;
import com._jackoboy.otherside.client.WardenBossBarOverlay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Client-side event handlers — ticks the AudioDuckHandler and resets it on disconnect.
 */
@EventBusSubscriber(modid = OthersideMod.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandlers {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        AudioDuckHandler.tick();
    }

    @SubscribeEvent
    public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        AudioDuckHandler.resetImmediate();
        WardenBossBarOverlay.clear();
    }
}
