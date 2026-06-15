package com._jackoboy.otherside.network;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Server → Client: syncs the player's current corruption level.
 * Sent on every corruption change (set/add) so the client overlay stays in sync.
 */
public record CorruptionSyncPayload(float corruption) implements CustomPacketPayload {

    public static final Type<CorruptionSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(OthersideMod.MOD_ID, "corruption_sync"));

    public static final StreamCodec<FriendlyByteBuf, CorruptionSyncPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> buf.writeFloat(payload.corruption()),
                    buf -> new CorruptionSyncPayload(buf.readFloat())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
