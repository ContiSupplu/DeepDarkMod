package com._jackoboy.otherside.network;

import com._jackoboy.otherside.client.AudioDuckHandler;
import com._jackoboy.otherside.client.ClientBreachBorderCache;
import com._jackoboy.otherside.client.ClientBeastData;
import com._jackoboy.otherside.client.ScreenFxOverlay;
import com._jackoboy.otherside.client.WardenBossBarOverlay;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        // W1 Worldbeast Rework: replaced InfectionSyncPayload with BeastSyncPayload
        registrar.playToClient(
                BeastSyncPayload.TYPE,
                BeastSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ClientBeastData.mass = payload.mass();
                    ClientBeastData.setBreachPositions(payload.breachX(), payload.breachZ());
                })
        );

        registrar.playToClient(
                BreachBorderPayload.TYPE,
                BreachBorderPayload.STREAM_CODEC,
                ClientBreachBorderCache::handle
        );

        registrar.playToClient(
                ScreenFxPayload.TYPE,
                ScreenFxPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ScreenFxOverlay.trigger(payload.fxType(), payload.durationTicks());
                })
        );

        registrar.playToClient(
                AudioDuckPayload.TYPE,
                AudioDuckPayload.STREAM_CODEC,
                AudioDuckHandler::handle
        );

        registrar.playToClient(
                StyledBossBarPayload.TYPE,
                StyledBossBarPayload.STREAM_CODEC,
                WardenBossBarOverlay::handleStyled
        );

        registrar.playToClient(
                ResonancePayload.TYPE,
                ResonancePayload.STREAM_CODEC,
                com._jackoboy.otherside.client.ResonanceHudOverlay::handlePayload
        );
    }
}
