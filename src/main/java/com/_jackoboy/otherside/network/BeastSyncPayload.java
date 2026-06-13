package com._jackoboy.otherside.network;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Syncs beast state data to clients for fog/gloom rendering.
 * W1 rework: stripped phase/infection fields, added mass.
 * W3: added mawActive flag for Maw mood fog.
 * Retains the same payload ID for backward compatibility with registered handlers.
 */
public record BeastSyncPayload(
        float mass,
        double[] breachX,
        double[] breachZ,
        boolean mawActive
) implements CustomPacketPayload {

    public static final Type<BeastSyncPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(OthersideMod.MOD_ID, "beast_sync")
    );

    public static final StreamCodec<FriendlyByteBuf, BeastSyncPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeFloat(payload.mass);
                        buf.writeInt(payload.breachX.length);
                        for (int i = 0; i < payload.breachX.length; i++) {
                            buf.writeDouble(payload.breachX[i]);
                            buf.writeDouble(payload.breachZ[i]);
                        }
                        buf.writeBoolean(payload.mawActive);
                    },
                    buf -> {
                        float mass = buf.readFloat();
                        int count = buf.readInt();
                        double[] bx = new double[count];
                        double[] bz = new double[count];
                        for (int i = 0; i < count; i++) {
                            bx[i] = buf.readDouble();
                            bz[i] = buf.readDouble();
                        }
                        boolean mawActive = buf.readBoolean();
                        return new BeastSyncPayload(mass, bx, bz, mawActive);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
