package com._jackoboy.otherside.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record StyledBossBarPayload(UUID bossId, int style) implements CustomPacketPayload {
    public static final Type<StyledBossBarPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("otherside", "styled_bossbar"));

    public static final StreamCodec<FriendlyByteBuf, StyledBossBarPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> { buf.writeUUID(payload.bossId); buf.writeVarInt(payload.style); },
                    buf -> new StyledBossBarPayload(buf.readUUID(), buf.readVarInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
