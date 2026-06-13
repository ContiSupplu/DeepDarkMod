package com._jackoboy.otherside.dimension;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.network.ScreenFxPayload;
import com._jackoboy.otherside.registry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

/**
 * Per-player tick-driven cinematic script triggered when a player first
 * arrives in the Otherside dimension.
 *
 * Uses PlayerChangedDimensionEvent to detect arrival AFTER teleport completes,
 * then runs a tick-based timeline for the cinematic beats.
 *
 * All sounds are played at or near the player's position to ensure audibility.
 * Directional effects use small offsets (10-20 blocks) within hearing range.
 */
@EventBusSubscriber(modid = OthersideMod.MOD_ID)
public class ArrivalScript {
    private static final Map<UUID, Integer> activeScripts = new HashMap<>();

    /**
     * Detect when a player enters the Otherside for the first time.
     * This fires AFTER the teleport is complete, so the player is already in the dimension.
     */
    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // Check if they arrived in the Otherside
        if (!event.getTo().equals(DimensionRulesManager.OTHERSIDE_DIM)) return;

        ServerLevel level = player.serverLevel();

        OthersideSavedData data = OthersideSavedData.get(level);
        if (data.hasArrived(player.getUUID())) return;

        // Mark arrived immediately to prevent re-triggering
        data.markArrived(player.getUUID());

        OthersideMod.LOGGER.info("[ARRIVAL] Starting first-arrival cinematic for {}",
                player.getName().getString());

        // Start script at tick 0 — the tick handler will send the blackout
        // after a short delay to ensure the client is fully loaded
        activeScripts.put(player.getUUID(), 0);
    }

    /**
     * Tick all active arrival scripts. Called from the server tick handler.
     */
    public static void tick(ServerLevel level) {
        if (!level.dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) return;

        Iterator<Map.Entry<UUID, Integer>> it = activeScripts.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Integer> entry = it.next();
            UUID uuid = entry.getKey();
            int tick = entry.getValue();

            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
            if (player == null || !player.level().dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) {
                it.remove();
                continue;
            }

            runBeat(player, level, tick);
            entry.setValue(tick + 1);

            // Script ends after tick 160
            if (tick >= 160) {
                it.remove();
            }
        }
    }

    private static void runBeat(ServerPlayer player, ServerLevel level, int tick) {
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        // t+5: Fade-in from black (sent from tick handler, not event, to ensure client is ready)
        if (tick == 5) {
            PacketDistributor.sendToPlayer(player,
                    new ScreenFxPayload(ScreenFxPayload.BLACKOUT_RELEASE, 60));
        }

        // t+10: Start background entry music at player position
        // Uses AMBIENT category (not MUSIC which conflicts with MC's music manager)
        if (tick == 10) {
            level.playSound(null, px, py, pz,
                    ModSoundEvents.ENTRY_MUSIC.get(), SoundSource.AMBIENT, 0.5F, 1.0F);
        }

        // t+40: First distant Warden bellow (north-ish, ~20 blocks out — within hearing range)
        if (tick == 40) {
            level.playSound(null, px + 15, py, pz - 15,
                    SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.0F, 0.8F);
        }

        // t+65: Second bellow (east-ish)
        if (tick == 65) {
            level.playSound(null, px - 12, py, pz + 18,
                    SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.0F, 0.9F);
        }

        // t+90: Third bellow (west-ish)
        if (tick == 90) {
            level.playSound(null, px + 14, py, pz + 12,
                    SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.0F, 0.7F);
        }

        // t+110: Play the whisper audio ("you came through. it noticed.")
        if (tick == 110) {
            level.playSound(null, px, py, pz,
                    ModSoundEvents.ARRIVAL_WHISPER.get(), SoundSource.AMBIENT, 1.0F, 1.0F);

            // Director log
            DirectorLog.log(level, "FIRST_ARRIVAL", player.blockPosition(),
                    "Player " + player.getName().getString() + " entered the Otherside");
        }

        // t+130: Grant advancement
        if (tick == 130) {
            var advancement = level.getServer().getAdvancements()
                    .get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("otherside", "you_came_through"));
            if (advancement != null) {
                player.getAdvancements().award(advancement, "impossible");
            }
        }
    }
}
