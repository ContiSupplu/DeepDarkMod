package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.*;

/**
 * Persists breach data and spread bookkeeping.
 * W1 Worldbeast Rework: phase, infection%, and day-clock fields removed.
 * Beast organism state is now in WorldbeastState.
 */
public class InfectionSavedData extends SavedData {
    private static final String DATA_NAME = "otherside_infection";

    private final List<BreachData> breaches = new ArrayList<>();
    private boolean initialized = false;
    private int totalConversions = 0;
    // Timelapse mode
    private boolean timelapseActive = false;
    private int timelapseMultiplier = 1;

    // Migration: old phase number (read from NBT once for WorldbeastState migration, then ignored)
    private int legacyPhaseNumber = -1;

    // Getters/setters
    public List<BreachData> getBreaches() { return breaches; }
    public boolean isInitialized() { return initialized; }
    public void setInitialized(boolean i) { this.initialized = i; setDirty(); }
    public int getTotalConversions() { return totalConversions; }
    public void incrementConversions() { this.totalConversions++; setDirty(); }
    public boolean isTimelapseActive() { return timelapseActive; }
    public void setTimelapseActive(boolean a) { this.timelapseActive = a; setDirty(); }
    public int getTimelapseMultiplier() { return timelapseMultiplier; }
    public void setTimelapseMultiplier(int m) { this.timelapseMultiplier = m; setDirty(); }

    /** Returns the old phase number from pre-W1 save data, or -1 if none/already migrated. */
    public int getLegacyPhaseNumber() { return legacyPhaseNumber; }

    public void addBreach(BreachData breach) {
        breaches.add(breach);
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        // No longer saves phase/infection/day fields
        tag.putBoolean("initialized", initialized);
        tag.putInt("totalConversions", totalConversions);
        tag.putBoolean("timelapseActive", timelapseActive);
        tag.putInt("timelapseMultiplier", timelapseMultiplier);

        ListTag breachList = new ListTag();
        for (BreachData breach : breaches) {
            breachList.add(breach.save());
        }
        tag.put("breaches", breachList);
        return tag;
    }

    public static InfectionSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        InfectionSavedData data = new InfectionSavedData();
        data.initialized = tag.getBoolean("initialized");
        data.totalConversions = tag.getInt("totalConversions");
        data.timelapseActive = tag.getBoolean("timelapseActive");
        data.timelapseMultiplier = tag.getInt("timelapseMultiplier");

        // Read legacy phase for migration (old worlds will have this field)
        if (tag.contains("phase")) {
            data.legacyPhaseNumber = tag.getInt("phase");
            OthersideMod.LOGGER.info("[BEAST] Detected legacy phase {} in saved data — will migrate to WorldbeastState",
                    data.legacyPhaseNumber);
        }

        ListTag breachList = tag.getList("breaches", Tag.TAG_COMPOUND);
        for (int i = 0; i < breachList.size(); i++) {
            data.breaches.add(BreachData.load(breachList.getCompound(i)));
        }
        return data;
    }

    public static InfectionSavedData get(ServerLevel level) {
        // Always use overworld for global infection data
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(
                new Factory<>(InfectionSavedData::new, InfectionSavedData::load),
                DATA_NAME
        );
    }
}
