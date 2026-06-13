package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.server.level.ServerLevel;

/**
 * Forces vanilla weather (rain) while breaches are active.
 * Uses setWeatherParameters to create real rain with clouds, sky darkening, etc.
 */
public class WeatherDriver {
    private int checkTimer = 0;
    private boolean weatherForced = false;

    public void tick(ServerLevel level) {
        checkTimer++;
        if (checkTimer < 200) return; // Check every 10 seconds
        checkTimer = 0;

        InfectionSavedData data = InfectionSavedData.get(level);
        if (!data.isInitialized()) return;

        boolean hasActiveBreaches = data.getBreaches().stream().anyMatch(b -> b.isActive());

        if (hasActiveBreaches && !level.isRaining()) {
            // Force rain — use long duration so it stays active
            // clearTime=0 (no clear), rainTime=24000 (20 min), thunderTime=24000
            level.setWeatherParameters(0, 24000, true, false);
            weatherForced = true;
            OthersideMod.LOGGER.debug("[WEATHER] Forced rain on — breaches active");
        } else if (!hasActiveBreaches && weatherForced) {
            // Release weather control
            level.setWeatherParameters(6000, 0, false, false);
            weatherForced = false;
            OthersideMod.LOGGER.info("[WEATHER] Released weather control — no active breaches");
        }
    }
}
