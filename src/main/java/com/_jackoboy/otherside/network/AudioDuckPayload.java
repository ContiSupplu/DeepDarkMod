package com._jackoboy.otherside.network;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Play-to-client payload to duck (lower) or restore ambient audio during cinematics.
 * When {@code start} is true, audio fades down over {@code fadeTicks}.
 * When {@code start} is false, audio restores to player-configured levels over {@code fadeTicks}.
 */
public record AudioDuckPayload(boolean start, int fadeTicks) implements CustomPacketPayload {

    public static final Type<AudioDuckPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(OthersideMod.MOD_ID, "audio_duck"));

    public static final StreamCodec<FriendlyByteBuf, AudioDuckPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeBoolean(payload.start);
                        buf.writeInt(payload.fadeTicks);
                    },
                    buf -> new AudioDuckPayload(buf.readBoolean(), buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
