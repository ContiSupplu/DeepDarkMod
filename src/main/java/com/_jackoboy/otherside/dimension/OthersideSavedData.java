package com._jackoboy.otherside.dimension;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Dimension-specific saved data stored in the Otherside level's own data storage.
 * Tracks the landing ruin state and which players have arrived in the dimension.
 */
public class OthersideSavedData extends SavedData {
    private static final String DATA_NAME = "otherside_dimension";

    /** Whether the landing ruin structure has been placed. */
    private boolean landingRuinPlaced = false;

    /** Center position of the landing ruin, if placed. */
    @Nullable
    private BlockPos landingRuinCenter = null;

    /** UUIDs of players who have arrived in the Otherside at least once. */
    private final Set<UUID> arrivedPlayers = new HashSet<>();

    // ── Getters / Setters ──

    public boolean isLandingRuinPlaced() { return landingRuinPlaced; }

    public void setLandingRuinPlaced(boolean placed) {
        this.landingRuinPlaced = placed;
        setDirty();
    }

    @Nullable
    public BlockPos getLandingRuinCenter() { return landingRuinCenter; }

    public void setLandingRuinCenter(@Nullable BlockPos center) {
        this.landingRuinCenter = center;
        setDirty();
    }

    public Set<UUID> getArrivedPlayers() { return arrivedPlayers; }

    /**
     * Check if a player has arrived in the Otherside before.
     */
    public boolean hasArrived(UUID playerUuid) {
        return arrivedPlayers.contains(playerUuid);
    }

    /**
     * Mark a player as having arrived in the Otherside.
     */
    public void markArrived(UUID playerUuid) {
        if (arrivedPlayers.add(playerUuid)) {
            setDirty();
        }
    }

    // ── NBT Persistence ──

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putBoolean("landingRuinPlaced", landingRuinPlaced);

        if (landingRuinCenter != null) {
            tag.putLong("landingRuinCenter", landingRuinCenter.asLong());
        }

        ListTag playerList = new ListTag();
        for (UUID uuid : arrivedPlayers) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("uuid", uuid);
            playerList.add(entry);
        }
        tag.put("arrivedPlayers", playerList);

        return tag;
    }

    public static OthersideSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        OthersideSavedData data = new OthersideSavedData();
        data.landingRuinPlaced = tag.getBoolean("landingRuinPlaced");

        if (tag.contains("landingRuinCenter")) {
            data.landingRuinCenter = BlockPos.of(tag.getLong("landingRuinCenter"));
        }

        ListTag playerList = tag.getList("arrivedPlayers", Tag.TAG_COMPOUND);
        for (int i = 0; i < playerList.size(); i++) {
            CompoundTag entry = playerList.getCompound(i);
            data.arrivedPlayers.add(entry.getUUID("uuid"));
        }

        return data;
    }

    /**
     * Retrieve (or create) the OthersideSavedData from the given level's own data storage.
     * This should always be called with the Otherside ServerLevel — NOT the overworld.
     */
    public static OthersideSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new Factory<>(OthersideSavedData::new, OthersideSavedData::load),
                DATA_NAME
        );
    }
}
