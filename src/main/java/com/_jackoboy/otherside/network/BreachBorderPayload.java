package com._jackoboy.otherside.network;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BreachBorderPayload(
        long[] packedColumns
) implements CustomPacketPayload {

    public static final Type<BreachBorderPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(OthersideMod.MOD_ID, "breach_border")
    );

    public static final StreamCodec<FriendlyByteBuf, BreachBorderPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeVarInt(payload.packedColumns.length);
                        for (long l : payload.packedColumns) {
                            buf.writeLong(l);
                        }
                    },
                    buf -> {
                        int len = buf.readVarInt();
                        long[] cols = new long[len];
                        for (int i = 0; i < len; i++) {
                            cols[i] = buf.readLong();
                        }
                        return new BreachBorderPayload(cols);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
