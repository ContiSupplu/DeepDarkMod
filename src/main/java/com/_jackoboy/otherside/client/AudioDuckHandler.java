package com._jackoboy.otherside.client;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.network.AudioDuckPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client-side handler for audio ducking during the portal guardian cinematic.
 * Lerps MUSIC, AMBIENT, and WEATHER volumes to 0 on start, restores on release.
 * Re-asserts target volumes each tick to counteract player opening options mid-duck.
 */
public class AudioDuckHandler {

    private static final SoundSource[] DUCKED_SOURCES = {
            SoundSource.MUSIC, SoundSource.AMBIENT, SoundSource.WEATHER
    };

    private static boolean active = false;
    private static boolean releasing = false;

    // Saved player-configured volumes (captured at duck start)
    private static float savedMusic = 1.0f;
    private static float savedAmbient = 1.0f;
    private static float savedWeather = 1.0f;

    private static int fadeTotalTicks = 1;
    private static int fadeTicksRemaining = 0;

    /**
     * Network handler entry point. Called on the network thread via context.enqueueWork.
     */
    public static void handle(AudioDuckPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.start()) {
                startDuck(payload.fadeTicks());
            } else {
                releaseDuck(payload.fadeTicks());
            }
        });
    }

    /**
     * Begin ducking: save current volume levels and start fading to 0.
     */
    private static void startDuck(int fadeTicks) {
        Minecraft mc = Minecraft.getInstance();

        // Capture player's configured volume levels before we override them
        savedMusic = mc.options.getSoundSourceVolume(SoundSource.MUSIC);
        savedAmbient = mc.options.getSoundSourceVolume(SoundSource.AMBIENT);
        savedWeather = mc.options.getSoundSourceVolume(SoundSource.WEATHER);

        fadeTotalTicks = Math.max(fadeTicks, 1);
        fadeTicksRemaining = fadeTotalTicks;
        active = true;
        releasing = false;

        OthersideMod.LOGGER.debug("Audio duck started over {} ticks (music={}, ambient={}, weather={})",
                fadeTicks, savedMusic, savedAmbient, savedWeather);
    }

    /**
     * Begin release: restore volumes to player-configured levels over fadeTicks.
     */
    private static void releaseDuck(int fadeTicks) {
        if (!active) return;

        fadeTotalTicks = Math.max(fadeTicks, 1);
        fadeTicksRemaining = fadeTotalTicks;
        releasing = true;

        OthersideMod.LOGGER.debug("Audio duck releasing over {} ticks", fadeTicks);
    }

    /**
     * Called each client tick to lerp volumes. Should be invoked from a client tick event.
     */
    public static void tick() {
        if (!active) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            // Disconnected — instant reset
            resetImmediate();
            return;
        }

        if (fadeTicksRemaining > 0) {
            float progress = 1.0f - (float) fadeTicksRemaining / fadeTotalTicks;

            if (releasing) {
                // Lerp from 0 back to saved values
                applyVolume(mc, SoundSource.MUSIC, savedMusic * progress);
                applyVolume(mc, SoundSource.AMBIENT, savedAmbient * progress);
                applyVolume(mc, SoundSource.WEATHER, savedWeather * progress);
            } else {
                // Lerp from saved values down to 0
                applyVolume(mc, SoundSource.MUSIC, savedMusic * (1.0f - progress));
                applyVolume(mc, SoundSource.AMBIENT, savedAmbient * (1.0f - progress));
                applyVolume(mc, SoundSource.WEATHER, savedWeather * (1.0f - progress));
            }

            fadeTicksRemaining--;
        } else if (releasing) {
            // Release complete — restore final values and deactivate
            applyVolume(mc, SoundSource.MUSIC, savedMusic);
            applyVolume(mc, SoundSource.AMBIENT, savedAmbient);
            applyVolume(mc, SoundSource.WEATHER, savedWeather);
            active = false;
            releasing = false;
            OthersideMod.LOGGER.debug("Audio duck fully released");
        } else {
            // Holding at 0 — re-assert each tick in case player adjusts options
            applyVolume(mc, SoundSource.MUSIC, 0f);
            applyVolume(mc, SoundSource.AMBIENT, 0f);
            applyVolume(mc, SoundSource.WEATHER, 0f);
        }
    }

    /**
     * Instant reset — called on disconnect to prevent stuck volumes.
     */
    public static void resetImmediate() {
        if (!active) return;

        Minecraft mc = Minecraft.getInstance();
        applyVolume(mc, SoundSource.MUSIC, savedMusic);
        applyVolume(mc, SoundSource.AMBIENT, savedAmbient);
        applyVolume(mc, SoundSource.WEATHER, savedWeather);

        active = false;
        releasing = false;
        fadeTicksRemaining = 0;
        OthersideMod.LOGGER.debug("Audio duck reset (disconnect)");
    }

    /**
     * Whether audio ducking is currently active.
     */
    public static boolean isActive() {
        return active;
    }

    private static void applyVolume(Minecraft mc, SoundSource source, float volume) {
        mc.getSoundManager().updateSourceVolume(source, Math.max(0f, Math.min(1f, volume)));
    }
}
