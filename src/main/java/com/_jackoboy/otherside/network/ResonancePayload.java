package com._jackoboy.otherside.network;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Syncs the player's current resonance value from server to client.
 * Used by the ResonanceHudOverlay for the noise meter display.
 */
public record ResonancePayload(float value) implements CustomPacketPayload {
    public static final Type<ResonancePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(OthersideMod.MOD_ID, "resonance_sync"));

    public static final StreamCodec<FriendlyByteBuf, ResonancePayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeFloat(payload.value),
                    buf -> new ResonancePayload(buf.readFloat())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
