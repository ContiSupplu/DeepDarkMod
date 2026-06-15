package com._jackoboy.otherside.corruption;

import com._jackoboy.otherside.network.CorruptionSyncPayload;
import com._jackoboy.otherside.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Static helpers for the corruption stat. This is the single entry point for all
 * corruption reads/writes — future content (Core proximity, corrupted animals,
 * ally corruption, villain artifact) feeds through add() without rework.
 */
public final class Corruption {
    private Corruption() {}

    /** Read corruption (works on both client and server). */
    public static float get(Player player) {
        return player.getData(ModAttachments.CORRUPTION);
    }

    /**
     * Set corruption to an exact value (clamped 0–100). Syncs to client.
     * Used by commands, enchanted golden apple full-cleanse, and join-sync.
     */
    public static void set(Player player, float value) {
        value = Math.max(0.0F, Math.min(100.0F, value));
        player.setData(ModAttachments.CORRUPTION, value);
        sync(player);
    }

    /**
     * Add a delta to corruption (clamped 0–100). Syncs to client.
     * This is the main hook — call it from gain ticks, echo soul hits,
     * golden apple cures, Core proximity, etc.
     */
    public static void add(Player player, float delta) {
        float current = get(player);
        float next = Math.max(0.0F, Math.min(100.0F, current + delta));
        if (next != current) {
            player.setData(ModAttachments.CORRUPTION, next);
            sync(player);
        }
    }

    /** Send the current value to the client. Both set() and add() call this. */
    private static void sync(Player player) {
        if (player instanceof ServerPlayer sp) {
            PacketDistributor.sendToPlayer(sp, new CorruptionSyncPayload(get(sp)));
        }
    }
}
