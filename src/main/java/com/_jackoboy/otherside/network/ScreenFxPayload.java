package com._jackoboy.otherside.network;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Play-to-client payload for screen effects (vignette pulse, blackout).
 * Used during portal ignition cinematic.
 */
public record ScreenFxPayload(int fxType, int durationTicks) implements CustomPacketPayload {

    public static final int VIGNETTE_PULSE = 0;
    public static final int BLACKOUT = 1;
    public static final int BLACKOUT_HOLD = 2;
    public static final int BLACKOUT_RELEASE = 3;

    public static final Type<ScreenFxPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(OthersideMod.MOD_ID, "screen_fx")
    );

    public static final StreamCodec<FriendlyByteBuf, ScreenFxPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeInt(payload.fxType);
                        buf.writeInt(payload.durationTicks);
                    },
                    buf -> new ScreenFxPayload(buf.readInt(), buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
