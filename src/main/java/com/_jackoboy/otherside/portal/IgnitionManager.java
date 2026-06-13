package com._jackoboy.otherside.portal;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages active ignition sequences per level.
 * Ticked from ModEventHandlers.onServerTick.
 * Prevents overlapping ignitions on the same frame.
 */
public class IgnitionManager {

    private static final List<IgnitionSequence> activeSequences = new ArrayList<>();

    /**
     * Tick all active ignition sequences. Called every server tick.
     */
    public static void tick(ServerLevel level) {
        Iterator<IgnitionSequence> it = activeSequences.iterator();
        while (it.hasNext()) {
            IgnitionSequence seq = it.next();
            if (seq.getLevel() != level) continue;

            if (seq.isDone()) {
                it.remove();
                continue;
            }

            seq.tick();
        }
    }

    /**
     * Start a new ignition sequence. Returns false if an ignition is already
     * active on this frame (any overlapping ring position).
     */
    public static boolean startIgnition(IgnitionSequence sequence) {
        // Check for overlap with existing sequences
        for (IgnitionSequence existing : activeSequences) {
            if (existing.getLevel() == sequence.getLevel()) {
                for (BlockPos pos : sequence.getRingPositions()) {
                    if (existing.getRingPositions().contains(pos)) {
                        OthersideMod.LOGGER.info("[PORTAL] Ignition rejected: overlapping with existing sequence");
                        return false;
                    }
                }
            }
        }

        activeSequences.add(sequence);
        OthersideMod.LOGGER.info("[PORTAL] Ignition sequence started: {} ring positions, {} interior positions",
                sequence.getRingPositions().size(), sequence.getInteriorPositions().size());
        return true;
    }

    /**
     * Clear all sequences (e.g., on server shutdown).
     */
    public static void clear() {
        activeSequences.clear();
    }
}
