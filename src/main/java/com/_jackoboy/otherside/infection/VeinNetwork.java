package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.block.SculkVeinCordBlock;
import com._jackoboy.otherside.director.DirectorLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.*;

/**
 * Pulse API for sending visible traveling charge signals along {@link SculkVeinCordBlock} networks.
 * <p>
 * Pulses are driven from the per-server-tick handler ({@link #tickPulses}), NOT the
 * 20-tick beast cadence. Pulse timing resolution is 4 game-ticks per path segment.
 * <p>
 * A "train" is a sequence of pulses separated by a fixed spacing, optionally repeating
 * on an interval until cancelled or until a configured deadline.
 */
public class VeinNetwork {

    // ────────────────────────────────────────────────────────────────────────────
    // Scheduled events — driven every server tick
    // ────────────────────────────────────────────────────────────────────────────

    /** A single scheduled charge-on or charge-off event for one block position. */
    public record ScheduledPulseEvent(BlockPos pos, boolean chargeOn, long executionTick, boolean playSound) {}

    /** Pending pulse events keyed by dimension. */
    private static final Map<ResourceKey<Level>, List<ScheduledPulseEvent>> pendingPulses = new HashMap<>();

    // ────────────────────────────────────────────────────────────────────────────
    // Active trains
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Represents an active repeating pulse train.
     *
     * @param trainId              Unique ID for cancellation
     * @param from                 Origin of the path
     * @param to                   Destination of the path
     * @param pulseCount           Number of pulses per train burst
     * @param spacingTicks         Ticks between successive pulses in a burst
     * @param repeatIntervalTicks  Ticks between full train repetitions (0 = no repeat)
     * @param startTick            Game tick when this train was first scheduled
     * @param orderArrivalTick     Game tick at which this train stops repeating (Long.MAX_VALUE = indefinite)
     * @param path                 Cached BFS path (immutable snapshot)
     * @param levelKey             Dimension key for re-scheduling
     * @param lastRepeatTick       Game tick when the last repetition was scheduled
     * @param isRetaliation        True for retaliation-pattern trains (paired pulses with gaps)
     */
    public record ActiveTrain(UUID trainId, BlockPos from, BlockPos to,
                              int pulseCount, int spacingTicks, int repeatIntervalTicks,
                              long startTick, long orderArrivalTick,
                              List<BlockPos> path, ResourceKey<Level> levelKey,
                              long lastRepeatTick, boolean isRetaliation) {}

    private static final List<ActiveTrain> activeTrains = new ArrayList<>();

    /** Default repeat interval: 600 ticks = 30 seconds. */
    private static final int DEFAULT_REPEAT_INTERVAL = 600;

    // ────────────────────────────────────────────────────────────────────────────
    // Direction ↔ BooleanProperty mapping
    // ────────────────────────────────────────────────────────────────────────────

    private static final Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = Map.of(
            Direction.NORTH, SculkVeinCordBlock.NORTH,
            Direction.SOUTH, SculkVeinCordBlock.SOUTH,
            Direction.EAST,  SculkVeinCordBlock.EAST,
            Direction.WEST,  SculkVeinCordBlock.WEST
    );

    private static final Random SOUND_RANDOM = new Random();

    // ════════════════════════════════════════════════════════════════════════════
    // Core API
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * BFS from {@code from} to {@code to} along connected vein cords, then schedule
     * a traveling charge animation.
     *
     * @return the ordered path (including both endpoints), or an empty list if unreachable.
     */
    public static List<BlockPos> sendPulse(ServerLevel level, BlockPos from, BlockPos to) {
        List<BlockPos> path = bfsPath(level, from, to, 256);
        if (path.isEmpty()) return path;

        schedulePulseEvents(level, path, level.getGameTime());
        return path;
    }

    /**
     * BFS outward from {@code origin} along all connected cord branches (up to {@code radius}
     * nodes, hard-capped at 512), scheduling the same charge animation on every discovered node.
     *
     * @return ordered list of all visited positions (BFS order).
     */
    public static List<BlockPos> pulseOmni(ServerLevel level, BlockPos origin, int radius) {
        List<BlockPos> visited = bfsFlood(level, origin, Math.min(radius, 512));
        if (visited.isEmpty()) return visited;

        schedulePulseEvents(level, visited, level.getGameTime());
        return visited;
    }

    /**
     * Sends {@code pulseCount} pulses along the path from → to, spaced by {@code spacingTicks}.
     * The train repeats every {@value #DEFAULT_REPEAT_INTERVAL} ticks until cancelled.
     *
     * @return the train's UUID (for cancellation), or {@code null} if the path is unreachable.
     */
    public static UUID sendTrain(ServerLevel level, BlockPos from, BlockPos to,
                                 int pulseCount, int spacingTicks) {
        return sendTrain(level, from, to, pulseCount, spacingTicks, DEFAULT_REPEAT_INTERVAL, Long.MAX_VALUE);
    }

    /**
     * Full-parameter train scheduling.
     *
     * @param repeatIntervalTicks ticks between full train repetitions (0 = single burst, no repeat)
     * @param orderArrivalTick    game tick at which the train stops repeating
     * @return train UUID, or null if unreachable.
     */
    public static UUID sendTrain(ServerLevel level, BlockPos from, BlockPos to,
                                 int pulseCount, int spacingTicks,
                                 int repeatIntervalTicks, long orderArrivalTick) {
        List<BlockPos> path = bfsPath(level, from, to, 256);
        if (path.isEmpty()) return null;

        long base = level.getGameTime();
        UUID trainId = UUID.randomUUID();

        // Schedule first burst
        scheduleTrainBurst(level, path, base, pulseCount, spacingTicks);

        // Register active train for repeats
        if (repeatIntervalTicks > 0) {
            activeTrains.add(new ActiveTrain(trainId, from, to, pulseCount, spacingTicks,
                    repeatIntervalTicks, base, orderArrivalTick,
                    List.copyOf(path), level.dimension(), base, false));
        }

        // Director log — one row per train, not per pulse
        DirectorLog.log(level, "VEIN_PULSE", from,
                "to=" + to.toShortString() + " count=" + pulseCount + " length=" + path.size());

        return trainId;
    }

    /**
     * Cancel a repeating train by its UUID.
     *
     * @return true if a matching train was found and removed.
     */
    public static boolean cancelTrain(UUID trainId) {
        return activeTrains.removeIf(t -> t.trainId().equals(trainId));
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Train presets
    // ════════════════════════════════════════════════════════════════════════════

    /** Surge: 3 pulses, 40-tick spacing. */
    public static UUID sendSurgeTrain(ServerLevel level, BlockPos from, BlockPos to) {
        return sendTrain(level, from, to, 3, 40);
    }

    /** Breakout: 5 pulses, 40-tick spacing. */
    public static UUID sendBreakoutTrain(ServerLevel level, BlockPos from, BlockPos to) {
        return sendTrain(level, from, to, 5, 40);
    }

    /**
     * Retaliation: 3 paired pulses with large gaps.
     * <pre>
     *   Pulse at tick 0, 16
     *   Gap 60t
     *   Pulse at tick 76, 92
     *   Gap 60t
     *   Pulse at tick 152, 168
     * </pre>
     *
     * @return train UUID, or null if unreachable.
     */
    public static UUID sendRetaliationTrain(ServerLevel level, BlockPos from, BlockPos to) {
        List<BlockPos> path = bfsPath(level, from, to, 256);
        if (path.isEmpty()) return null;

        long base = level.getGameTime();
        UUID trainId = UUID.randomUUID();

        // 3 pairs: (0,16), (76,92), (152,168)
        scheduleRetaliationBurst(level, path, base);

        // Register as active train for repeats
        activeTrains.add(new ActiveTrain(trainId, from, to, 6, 0,
                DEFAULT_REPEAT_INTERVAL, base, Long.MAX_VALUE,
                List.copyOf(path), level.dimension(), base, true));

        DirectorLog.log(level, "VEIN_PULSE", from,
                "to=" + to.toShortString() + " count=6(retaliation) length=" + path.size());

        return trainId;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Per-server-tick driver
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Must be called <b>every server tick</b> from the {@code ServerTickEvent} handler.
     * <p>
     * Processes all pending pulse events whose execution tick has arrived, and
     * re-schedules repeating active trains when their interval elapses.
     */
    public static void tickPulses(ServerLevel level) {
        ResourceKey<Level> key = level.dimension();
        long now = level.getGameTime();

        // ── 1. Fire pending pulse events ──
        List<ScheduledPulseEvent> events = pendingPulses.get(key);
        if (events != null && !events.isEmpty()) {
            Iterator<ScheduledPulseEvent> it = events.iterator();
            while (it.hasNext()) {
                ScheduledPulseEvent ev = it.next();
                if (now >= ev.executionTick()) {
                    BlockState state = level.getBlockState(ev.pos());
                    if (state.getBlock() instanceof SculkVeinCordBlock) {
                        level.setBlock(ev.pos(),
                                state.setValue(SculkVeinCordBlock.CHARGED, ev.chargeOn()), 2);

                        // Soft low thrum on charge-on for flagged sound-segments
                        if (ev.chargeOn() && ev.playSound()) {
                            float pitch = 0.7f + SOUND_RANDOM.nextFloat() * 0.1f;
                            level.playSound(null, ev.pos(), SoundEvents.SCULK_BLOCK_STEP,
                                    SoundSource.BLOCKS, 0.3f, pitch);
                        }
                    }
                    it.remove();
                }
            }
        }

        // ── 2. Repeat active trains ──
        Iterator<ActiveTrain> trainIt = activeTrains.iterator();
        while (trainIt.hasNext()) {
            ActiveTrain t = trainIt.next();
            if (!t.levelKey().equals(key)) continue;

            // Expired?
            if (now >= t.orderArrivalTick()) {
                trainIt.remove();
                continue;
            }

            // Time to repeat?
            if (t.repeatIntervalTicks() > 0 && now >= t.lastRepeatTick() + t.repeatIntervalTicks()) {
                // Re-validate the path — blocks may have been broken
                List<BlockPos> freshPath = bfsPath(level, t.from(), t.to(), 256);
                if (freshPath.isEmpty()) {
                    trainIt.remove();
                    continue;
                }

                // Schedule the burst
                if (t.isRetaliation()) {
                    scheduleRetaliationBurst(level, freshPath, now);
                } else {
                    scheduleTrainBurst(level, freshPath, now, t.pulseCount(), t.spacingTicks());
                }

                // Replace entry with updated lastRepeatTick (records are immutable)
                trainIt.remove();
                activeTrains.add(new ActiveTrain(t.trainId(), t.from(), t.to(),
                        t.pulseCount(), t.spacingTicks(), t.repeatIntervalTicks(),
                        t.startTick(), t.orderArrivalTick(),
                        List.copyOf(freshPath), t.levelKey(), now, t.isRetaliation()));
                break; // Iterator invalidated — remaining trains will be caught next tick
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Lifecycle
    // ════════════════════════════════════════════════════════════════════════════

    /** Clear all pending pulses and active trains. Call from ServerStoppedEvent. */
    public static void clear() {
        pendingPulses.clear();
        activeTrains.clear();
    }

    /** @return an unmodifiable snapshot of all active trains. */
    public static List<ActiveTrain> getActiveTrains() {
        return Collections.unmodifiableList(activeTrains);
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Internal: BFS pathfinding
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * BFS from {@code start} to {@code end} along connected vein cords.
     * Two adjacent cords connect iff they both face each other via their direction properties.
     *
     * @param maxNodes hard cap on explored nodes.
     * @return ordered path from start to end (inclusive), or empty list if unreachable.
     */
    private static List<BlockPos> bfsPath(ServerLevel level, BlockPos start, BlockPos end, int maxNodes) {
        if (!isVeinCord(level, start) || !isVeinCord(level, end)) {
            return Collections.emptyList();
        }
        if (start.equals(end)) return List.of(start);

        Map<BlockPos, BlockPos> cameFrom = new LinkedHashMap<>();
        cameFrom.put(start, null);
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);

        while (!queue.isEmpty() && cameFrom.size() < maxNodes) {
            BlockPos current = queue.poll();
            for (BlockPos neighbor : getConnectedNeighbors(level, current)) {
                if (!cameFrom.containsKey(neighbor)) {
                    cameFrom.put(neighbor, current);
                    if (neighbor.equals(end)) {
                        return reconstructPath(cameFrom, end);
                    }
                    queue.add(neighbor);
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * BFS flood outward from {@code origin} (no specific destination).
     *
     * @param maxNodes cap on total visited positions.
     * @return visited positions in BFS order.
     */
    private static List<BlockPos> bfsFlood(ServerLevel level, BlockPos origin, int maxNodes) {
        if (!isVeinCord(level, origin)) return Collections.emptyList();

        List<BlockPos> visited = new ArrayList<>();
        Set<BlockPos> seen = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();

        seen.add(origin);
        queue.add(origin);
        visited.add(origin);

        while (!queue.isEmpty() && visited.size() < maxNodes) {
            BlockPos current = queue.poll();
            for (BlockPos neighbor : getConnectedNeighbors(level, current)) {
                if (seen.add(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return visited;
    }

    private static List<BlockPos> reconstructPath(Map<BlockPos, BlockPos> cameFrom, BlockPos end) {
        LinkedList<BlockPos> path = new LinkedList<>();
        BlockPos current = end;
        while (current != null) {
            path.addFirst(current);
            current = cameFrom.get(current);
        }
        return path;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Internal: connectivity
    // ════════════════════════════════════════════════════════════════════════════

    private static boolean isVeinCord(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof SculkVeinCordBlock;
    }

    /**
     * Returns positions of adjacent vein cords that are mutually connected to {@code pos}.
     * Connection: both blocks must have the facing-toward-each-other direction property set to true.
     */
    private static List<BlockPos> getConnectedNeighbors(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof SculkVeinCordBlock)) return Collections.emptyList();

        List<BlockPos> result = new ArrayList<>(4);
        for (Map.Entry<Direction, BooleanProperty> entry : DIRECTION_PROPERTIES.entrySet()) {
            Direction dir = entry.getKey();
            BooleanProperty prop = entry.getValue();

            if (!state.getValue(prop)) continue;

            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (!(neighborState.getBlock() instanceof SculkVeinCordBlock)) continue;

            Direction opposite = dir.getOpposite();
            BooleanProperty neighborProp = DIRECTION_PROPERTIES.get(opposite);
            if (neighborProp != null && neighborState.getValue(neighborProp)) {
                result.add(neighborPos);
            }
        }
        return result;
    }

    // ════════════════════════════════════════════════════════════════════════════
    // Internal: pulse event scheduling
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * For a given path, schedule CHARGED=true at tick {@code base + 4*i} and
     * CHARGED=false at tick {@code base + 4*i + 6} for each segment i.
     * Every 3rd segment that charges on plays a soft low thrum sound.
     */
    private static void schedulePulseEvents(ServerLevel level, List<BlockPos> path, long baseTick) {
        ResourceKey<Level> key = level.dimension();
        List<ScheduledPulseEvent> events = pendingPulses.computeIfAbsent(key, k -> new ArrayList<>());

        for (int i = 0; i < path.size(); i++) {
            BlockPos pos = path.get(i);
            long onTick  = baseTick + 4L * i;
            long offTick = baseTick + 4L * i + 6;
            boolean sound = (i % 3 == 0);

            events.add(new ScheduledPulseEvent(pos, true, onTick, sound));
            events.add(new ScheduledPulseEvent(pos, false, offTick, false));
        }
    }

    /**
     * Schedule a burst of {@code pulseCount} pulses along the same path,
     * each offset by {@code spacingTicks}.
     */
    private static void scheduleTrainBurst(ServerLevel level, List<BlockPos> path,
                                           long baseTick, int pulseCount, int spacingTicks) {
        for (int p = 0; p < pulseCount; p++) {
            schedulePulseEvents(level, path, baseTick + (long) p * spacingTicks);
        }
    }

    /**
     * Schedule a retaliation burst: 3 paired pulses at offsets (0, 16, 76, 92, 152, 168).
     */
    private static void scheduleRetaliationBurst(ServerLevel level, List<BlockPos> path, long baseTick) {
        int[] offsets = {0, 16, 76, 92, 152, 168};
        for (int offset : offsets) {
            schedulePulseEvents(level, path, baseTick + offset);
        }
    }
}
