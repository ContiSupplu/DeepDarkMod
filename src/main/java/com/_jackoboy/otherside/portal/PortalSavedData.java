package com._jackoboy.otherside.portal;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Persistent data store for all Otherside portals across all dimensions.
 * Keyed by (dimension ResourceKey + BlockPos center).
 * Always stored in the overworld data storage so it is accessible from any dimension.
 */
public class PortalSavedData extends SavedData {
    private static final String DATA_NAME = "otherside_portals";

    /** Tracks whether this portal's guardian has been spawned / defeated. */
    public enum GuardianState {
        NONE,
        ACTIVE,
        DEFEATED;

        public int toInt() {
            return ordinal();
        }

        public static GuardianState fromInt(int value) {
            GuardianState[] values = values();
            if (value < 0 || value >= values.length) return NONE;
            return values[value];
        }
    }

    /**
     * Represents a single registered portal instance with all of its metadata.
     */
    public static class PortalEntry {
        /** The dimension this portal resides in. */
        public ResourceKey<Level> dimension;
        /** The center block position of the portal interior. */
        public BlockPos center;
        /** Current guardian state for this portal. */
        public GuardianState guardianState = GuardianState.NONE;
        /** UUID of the spawned guardian entity, if any. */
        @Nullable
        public UUID guardianUUID;
        /** The axis the portal frame is aligned to (X or Z). */
        public Direction.Axis portalAxis;
        /** The bottom-left corner of the portal interior (lowest coordinates). */
        public BlockPos bottomLeft;
        /** Width of the portal interior in blocks. */
        public int width;
        /** Height of the portal interior in blocks. */
        public int height;
        /** UUID of the player who ignited this portal. */
        public UUID igniterUUID;
        /** Whether this is the first time this portal has been ignited. */
        public boolean firstIgnition = true;

        /**
         * Produces a unique string key for this portal entry based on dimension and center position.
         */
        public String key() {
            return dimension.location() + ":" + center.asLong();
        }

        /**
         * Serialize this entry to NBT.
         */
        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putString("dimension", dimension.location().toString());
            tag.putLong("center", center.asLong());
            tag.putInt("guardianState", guardianState.toInt());
            if (guardianUUID != null) {
                tag.putUUID("guardianUUID", guardianUUID);
            }
            tag.putString("portalAxis", portalAxis.getName());
            tag.putLong("bottomLeft", bottomLeft.asLong());
            tag.putInt("width", width);
            tag.putInt("height", height);
            tag.putUUID("igniterUUID", igniterUUID);
            tag.putBoolean("firstIgnition", firstIgnition);
            return tag;
        }

        /**
         * Deserialize a PortalEntry from NBT.
         */
        public static PortalEntry load(CompoundTag tag) {
            PortalEntry entry = new PortalEntry();
            ResourceLocation dimLocation = ResourceLocation.parse(tag.getString("dimension"));
            entry.dimension = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimLocation);
            entry.center = BlockPos.of(tag.getLong("center"));
            entry.guardianState = GuardianState.fromInt(tag.getInt("guardianState"));
            if (tag.hasUUID("guardianUUID")) {
                entry.guardianUUID = tag.getUUID("guardianUUID");
            }
            entry.portalAxis = Direction.Axis.byName(tag.getString("portalAxis"));
            if (entry.portalAxis == null) {
                entry.portalAxis = Direction.Axis.X; // Fallback
            }
            entry.bottomLeft = BlockPos.of(tag.getLong("bottomLeft"));
            entry.width = tag.getInt("width");
            entry.height = tag.getInt("height");
            entry.igniterUUID = tag.getUUID("igniterUUID");
            entry.firstIgnition = tag.getBoolean("firstIgnition");
            return entry;
        }
    }

    /** All registered portals, keyed by their unique string key. */
    private final Map<String, PortalEntry> portals = new HashMap<>();

    /**
     * Retrieve (or create) the PortalSavedData from the overworld data storage.
     * Always uses overworld so portal data is accessible from any dimension.
     */
    public static PortalSavedData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(
                new Factory<>(PortalSavedData::new, PortalSavedData::load),
                DATA_NAME
        );
    }

    // ---- Portal Registration ----

    /**
     * Register a newly ignited portal. If a portal already exists at the same
     * dimension + center, it is treated as a re-ignition (firstIgnition = false).
     *
     * @param dim         the dimension the portal is in
     * @param center      the center block position of the portal interior
     * @param axis        the axis the portal frame is aligned to
     * @param bottomLeft  the bottom-left corner of the portal interior
     * @param width       width of the portal interior
     * @param height      height of the portal interior
     * @param igniterUUID UUID of the igniting player
     * @return the PortalEntry (existing or newly created)
     */
    public PortalEntry registerPortal(ResourceKey<Level> dim, BlockPos center, Direction.Axis axis,
                                      BlockPos bottomLeft, int width, int height, UUID igniterUUID) {
        String key = dim.location() + ":" + center.asLong();
        PortalEntry existing = portals.get(key);
        if (existing != null) {
            // Re-ignition of a previously registered portal
            existing.firstIgnition = false;
            OthersideMod.LOGGER.debug("Re-ignition of existing portal at {} in {}", center, dim.location());
            setDirty();
            return existing;
        }
        PortalEntry entry = new PortalEntry();
        entry.dimension = dim;
        entry.center = center;
        entry.portalAxis = axis;
        entry.bottomLeft = bottomLeft;
        entry.width = width;
        entry.height = height;
        entry.igniterUUID = igniterUUID;
        entry.firstIgnition = true;
        portals.put(key, entry);
        OthersideMod.LOGGER.debug("Registered new portal at {} in {} ({}x{})", center, dim.location(), width, height);
        setDirty();
        return entry;
    }

    // ---- Guardian State Management ----

    /**
     * Mark a portal's guardian as active (spawned).
     */
    public void setGuardianActive(ResourceKey<Level> dim, BlockPos center, UUID guardianUUID) {
        String key = dim.location() + ":" + center.asLong();
        PortalEntry entry = portals.get(key);
        if (entry != null) {
            entry.guardianState = GuardianState.ACTIVE;
            entry.guardianUUID = guardianUUID;
            OthersideMod.LOGGER.debug("Guardian activated at portal {} in {}", center, dim.location());
            setDirty();
        } else {
            OthersideMod.LOGGER.warn("Attempted to set guardian active for unregistered portal at {} in {}", center, dim.location());
        }
    }

    /**
     * Mark a portal's guardian as defeated.
     */
    public void setDefeated(ResourceKey<Level> dim, BlockPos center) {
        String key = dim.location() + ":" + center.asLong();
        PortalEntry entry = portals.get(key);
        if (entry != null) {
            entry.guardianState = GuardianState.DEFEATED;
            entry.guardianUUID = null;
            OthersideMod.LOGGER.debug("Guardian defeated at portal {} in {}", center, dim.location());
            setDirty();
        } else {
            OthersideMod.LOGGER.warn("Attempted to set defeated for unregistered portal at {} in {}", center, dim.location());
        }
    }

    // ---- Queries ----

    /**
     * Get the guardian state for a portal at the given dimension and center.
     *
     * @return the GuardianState, or NONE if the portal is not registered
     */
    public GuardianState getState(ResourceKey<Level> dim, BlockPos center) {
        String key = dim.location() + ":" + center.asLong();
        PortalEntry entry = portals.get(key);
        return entry != null ? entry.guardianState : GuardianState.NONE;
    }

    /**
     * Get the full PortalEntry for a portal at the given dimension and center.
     *
     * @return the PortalEntry, or null if not registered
     */
    @Nullable
    public PortalEntry getEntry(ResourceKey<Level> dim, BlockPos center) {
        String key = dim.location() + ":" + center.asLong();
        return portals.get(key);
    }

    /**
     * Find a registered portal whose interior rectangle contains the given block position.
     * This is used by the seal system to determine which portal a portal block belongs to.
     *
     * <p>The interior rectangle is defined by (bottomLeft, width, height, axis):
     * <ul>
     *   <li>For X axis portals: the rectangle spans Z (width) and Y (height) from bottomLeft</li>
     *   <li>For Z axis portals: the rectangle spans X (width) and Y (height) from bottomLeft</li>
     * </ul>
     *
     * @param dim      the dimension to search in
     * @param blockPos the position to check
     * @return the containing PortalEntry, or null if no portal contains this position
     */
    @Nullable
    public PortalEntry findPortalContaining(ResourceKey<Level> dim, BlockPos blockPos) {
        for (PortalEntry entry : portals.values()) {
            if (!entry.dimension.equals(dim)) {
                continue;
            }

            int bx = entry.bottomLeft.getX();
            int by = entry.bottomLeft.getY();
            int bz = entry.bottomLeft.getZ();
            int px = blockPos.getX();
            int py = blockPos.getY();
            int pz = blockPos.getZ();

            // Y is always vertical: check [by, by + height - 1]
            if (py < by || py > by + entry.height - 1) {
                continue;
            }

            if (entry.portalAxis == Direction.Axis.X) {
                // AXIS=X: portal stretches along X (flat in XY plane), thin on Z.
                // Z is fixed at bz, width spans along X.
                if (pz == bz && px >= bx && px <= bx + entry.width - 1) {
                    return entry;
                }
            } else if (entry.portalAxis == Direction.Axis.Z) {
                // AXIS=Z: portal stretches along Z (flat in ZY plane), thin on X.
                // X is fixed at bx, width spans along Z.
                if (px == bx && pz >= bz && pz <= bz + entry.width - 1) {
                    return entry;
                }
            }
        }
        return null;
    }

    /**
     * Get all registered portal entries.
     */
    public Collection<PortalEntry> getAllEntries() {
        return portals.values();
    }

    /**
     * Remove a portal entry. Used when a portal is permanently destroyed.
     */
    public void removePortal(ResourceKey<Level> dim, BlockPos center) {
        String key = dim.location() + ":" + center.asLong();
        if (portals.remove(key) != null) {
            OthersideMod.LOGGER.debug("Removed portal at {} in {}", center, dim.location());
            setDirty();
        }
    }

    // ---- NBT Persistence ----

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag portalList = new ListTag();
        for (PortalEntry entry : portals.values()) {
            portalList.add(entry.save());
        }
        tag.put("portals", portalList);
        return tag;
    }

    private static PortalSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        PortalSavedData data = new PortalSavedData();
        ListTag portalList = tag.getList("portals", Tag.TAG_COMPOUND);
        for (int i = 0; i < portalList.size(); i++) {
            PortalEntry entry = PortalEntry.load(portalList.getCompound(i));
            data.portals.put(entry.key(), entry);
        }
        OthersideMod.LOGGER.debug("Loaded {} portal entries from saved data", data.portals.size());
        return data;
    }
}
