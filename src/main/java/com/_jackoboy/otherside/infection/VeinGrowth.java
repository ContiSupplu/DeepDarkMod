package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.block.SculkVeinCordBlock;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.*;

/**
 * VeinGrowth — manages vein cord growth along the surface.
 * <p>
 * Vein cords form the Worldbeast's nervous system, carrying pulse signals
 * between breach sites and order targets. This manager handles the growth
 * of new cord segments toward targets, reconnection after severing, and
 * the initial network seeding.
 * <p>
 * Created as part of W2 of the Worldbeast Rework (§3.2).
 */
public class VeinGrowth {

    // ── Constants ───────────────────────────────────────────────────────────
    private static final int AMBIENT_GROWTH_INTERVAL = 6000;  // 5 minutes in 20-tick calls (300 calls)
    private static final int COMPLETION_DISTANCE = 3;          // task completes within 3 blocks
    private static final int BFS_SEED_DISTANCE = 6;            // immediate cord placement range
    private static final int UPDATE_ALL = 3;

    // ── Singleton-ish instance (held by WorldbeastState or tick caller) ──
    private static VeinGrowth INSTANCE;

    // ── State ───────────────────────────────────────────────────────────────
    private final List<GrowthTask> tasks = new ArrayList<>();
    private boolean veinNetworkSeeded = false;
    private final Random random = new Random();
    private int tickCounter = 0;

    // =====================================================================
    //  Inner class: GrowthTask
    // =====================================================================

    /**
     * A single vein growth task — grows a cord from currentTip toward target.
     */
    public static class GrowthTask {
        public UUID id;
        public BlockPos target;
        public BlockPos currentTip;
        public long lastGrowTick;
        public boolean isReconnection;  // severed end growing toward reconnection
        public boolean complete;

        public GrowthTask() {
            this.id = UUID.randomUUID();
            this.complete = false;
            this.isReconnection = false;
        }

        public GrowthTask(BlockPos origin, BlockPos target) {
            this();
            this.currentTip = origin;
            this.target = target;
            this.lastGrowTick = 0;
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("id", id);
            tag.putLong("target", target.asLong());
            tag.putLong("currentTip", currentTip.asLong());
            tag.putLong("lastGrowTick", lastGrowTick);
            tag.putBoolean("isReconnection", isReconnection);
            tag.putBoolean("complete", complete);
            return tag;
        }

        public static GrowthTask load(CompoundTag tag) {
            GrowthTask task = new GrowthTask();
            task.id = tag.getUUID("id");
            task.target = BlockPos.of(tag.getLong("target"));
            task.currentTip = BlockPos.of(tag.getLong("currentTip"));
            task.lastGrowTick = tag.getLong("lastGrowTick");
            task.isReconnection = tag.getBoolean("isReconnection");
            task.complete = tag.getBoolean("complete");
            return task;
        }
    }

    // =====================================================================
    //  CONSTRUCTOR / SINGLETON ACCESS
    // =====================================================================

    public VeinGrowth() {
        INSTANCE = this;
    }

    public static VeinGrowth getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VeinGrowth();
        }
        return INSTANCE;
    }

    // =====================================================================
    //  STATIC API — called externally by SoreManager etc.
    // =====================================================================

    /**
     * Create a new growth task from origin toward target.
     * Called by SoreManager during eruption and other systems.
     */
    public static void startGrowthTask(ServerLevel level, BlockPos origin, BlockPos target) {
        VeinGrowth instance = getInstance();
        GrowthTask task = new GrowthTask(origin, target);
        task.lastGrowTick = level.getGameTime();
        instance.tasks.add(task);

        OthersideMod.LOGGER.debug("[VEIN] New growth task {} → {} (id={})",
                origin.toShortString(), target.toShortString(), task.id);
    }

    // =====================================================================
    //  TICK — called every 20 game ticks (1 second)
    // =====================================================================

    /**
     * Main tick method. Must be called once per second from the beast tick loop.
     */
    public void tick(ServerLevel level) {
        tickCounter++;
        long gameTime = level.getGameTime();

        // ── Seed initial network if needed ───────────────────────────────
        if (!veinNetworkSeeded) {
            InfectionSavedData data = InfectionSavedData.get(level);
            WorldbeastState beast = WorldbeastState.get(level);
            // Only seed if breaches exist and at least one has a surface breakout
            boolean hasBreakout = data.getBreaches().stream()
                    .anyMatch(b -> b.getSurfaceBreakout() != null && b.isColumnComplete());
            if (hasBreakout) {
                seedInitialNetwork(level, data, beast);
            }
        }

        // ── Process active growth tasks ──────────────────────────────────
        int baseInterval = OthersideConfig.SERVER.veinGrowthIntervalTicks.get();

        for (GrowthTask task : tasks) {
            if (task.complete) continue;

            // Randomized interval: base ± 40%
            int jitter = (int) (baseInterval * 0.4f);
            int interval = baseInterval + random.nextInt(jitter * 2 + 1) - jitter;
            interval = Math.max(20, interval); // minimum 1 second

            if (gameTime - task.lastGrowTick < interval) continue;

            // Grow one block
            boolean grew = growOneBlock(level, task);
            task.lastGrowTick = gameTime;

            if (grew) {
                // Check if close enough to target to complete
                double dist = Math.sqrt(task.currentTip.distSqr(task.target));
                if (dist <= COMPLETION_DISTANCE) {
                    task.complete = true;
                    OthersideMod.LOGGER.debug("[VEIN] Task {} completed at {} (target={})",
                            task.id, task.currentTip.toShortString(), task.target.toShortString());

                    if (task.isReconnection) {
                        DirectorLog.log(level, "VEIN_RECONNECT", task.currentTip,
                                "Severed cord reconnected to " + task.target.toShortString());
                    }
                }
            }
        }

        // ── HEARD-tier reroute ───────────────────────────────────────────
        if (tickCounter % 60 == 0) { // check every minute
            checkHeardTierReroute(level);
        }

        // ── Ambient growth every 5 minutes ──────────────────────────────
        if (tickCounter % (AMBIENT_GROWTH_INTERVAL / 20) == 0) {
            ambientGrowth(level);
        }

        // ── Clean up completed tasks periodically ───────────────────────
        if (tickCounter % 300 == 0) { // every 5 minutes
            tasks.removeIf(t -> t.complete);
        }
    }

    // =====================================================================
    //  GROWTH LOGIC
    // =====================================================================

    /**
     * Grow one block of cord from the task's current tip toward its target.
     *
     * @return true if a block was placed, false if stalled.
     */
    private boolean growOneBlock(ServerLevel level, GrowthTask task) {
        BlockPos tip = task.currentTip;
        BlockPos target = task.target;

        // Determine preferred direction (closest cardinal to target)
        Direction preferred = getPreferredDirection(tip, target);
        Direction[] fallbacks = getAdjacentDirections(preferred);

        // Try preferred direction first, then fallbacks
        Direction[] toTry = {preferred, fallbacks[0], fallbacks[1]};

        for (Direction dir : toTry) {
            BlockPos candidate = tip.relative(dir);

            // Find surface position at that x,z
            BlockPos surfacePos = findSurfacePos(level, candidate.getX(), candidate.getZ());
            if (surfacePos == null) continue;

            // Check the block below has a sturdy UP face
            BlockPos supportBlock = surfacePos.below();
            BlockState supportState = level.getBlockState(supportBlock);
            if (!supportState.isFaceSturdy(level, supportBlock, Direction.UP)) continue;

            // Check the surface position is air or replaceable
            BlockState atSurface = level.getBlockState(surfacePos);
            if (!atSurface.isAir() && !(atSurface.getBlock() instanceof SculkVeinCordBlock)) {
                // If it's a non-air, non-cord block, skip
                continue;
            }

            // If it's already a cord, just move the tip
            if (atSurface.getBlock() instanceof SculkVeinCordBlock) {
                task.currentTip = surfacePos;
                return true;
            }

            // Place the cord
            Direction fromDir = dir.getOpposite(); // direction back to previous cord
            Direction toDir = getPreferredDirection(surfacePos, target); // direction toward target
            placeCord(level, surfacePos, fromDir, toDir);

            // Update connection on the previous tip if it's a cord
            updateCordConnection(level, tip, dir);

            task.currentTip = surfacePos;
            return true;
        }

        // All 3 directions failed — stall, will retry next interval
        OthersideMod.LOGGER.debug("[VEIN] Task {} stalled at {} — no valid surface in any direction",
                task.id, tip.toShortString());
        return false;
    }

    /**
     * Get the cardinal direction from 'from' that is closest to 'to'.
     */
    private Direction getPreferredDirection(BlockPos from, BlockPos to) {
        int dx = to.getX() - from.getX();
        int dz = to.getZ() - from.getZ();

        if (Math.abs(dx) >= Math.abs(dz)) {
            return dx >= 0 ? Direction.EAST : Direction.WEST;
        } else {
            return dz >= 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    /**
     * Get the two adjacent horizontal directions to the given direction.
     */
    private Direction[] getAdjacentDirections(Direction dir) {
        return switch (dir) {
            case NORTH -> new Direction[]{Direction.EAST, Direction.WEST};
            case SOUTH -> new Direction[]{Direction.EAST, Direction.WEST};
            case EAST  -> new Direction[]{Direction.NORTH, Direction.SOUTH};
            case WEST  -> new Direction[]{Direction.NORTH, Direction.SOUTH};
            default    -> new Direction[]{Direction.NORTH, Direction.EAST}; // shouldn't happen for horizontal
        };
    }

    // =====================================================================
    //  CORD PLACEMENT
    // =====================================================================

    /**
     * Place a SculkVeinCordBlock at the given position with connections in
     * fromDir and toDir.
     */
    public static void placeCord(ServerLevel level, BlockPos pos, Direction fromDir, Direction toDir) {
        // Ensure support block has a sturdy UP face
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        if (!belowState.isFaceSturdy(level, below, Direction.UP)) {
            return;
        }

        BlockState cordState = ModBlocks.SCULK_VEIN_CORD.get().defaultBlockState();

        // Set direction properties — only horizontal directions
        if (fromDir.getAxis().isHorizontal()) {
            cordState = setDirectionProperty(cordState, fromDir, true);
        }
        if (toDir.getAxis().isHorizontal()) {
            cordState = setDirectionProperty(cordState, toDir, true);
        }

        level.setBlock(pos, cordState, UPDATE_ALL);
    }

    /**
     * Update an existing cord at 'pos' to also connect in the given direction.
     */
    private void updateCordConnection(ServerLevel level, BlockPos pos, Direction dir) {
        if (!level.isLoaded(pos)) return;
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof SculkVeinCordBlock)) return;

        if (dir.getAxis().isHorizontal()) {
            state = setDirectionProperty(state, dir, true);
            level.setBlock(pos, state, UPDATE_ALL);
        }
    }

    /**
     * Set a direction property on a cord block state.
     */
    private static BlockState setDirectionProperty(BlockState state, Direction dir, boolean value) {
        return switch (dir) {
            case NORTH -> state.setValue(SculkVeinCordBlock.NORTH, value);
            case SOUTH -> state.setValue(SculkVeinCordBlock.SOUTH, value);
            case EAST  -> state.setValue(SculkVeinCordBlock.EAST, value);
            case WEST  -> state.setValue(SculkVeinCordBlock.WEST, value);
            default    -> state; // UP/DOWN not applicable
        };
    }

    // =====================================================================
    //  SURFACE FINDING
    // =====================================================================

    /**
     * Find the surface position at the given x,z coordinates.
     * Uses WORLD_SURFACE heightmap and scans down 1-2 blocks for a block
     * with a sturdy UP face.
     *
     * @return the position ON TOP of the surface block (where a cord goes),
     *         or null if no valid surface found.
     */
    public static BlockPos findSurfacePos(ServerLevel level, int x, int z) {
        if (!level.isLoaded(new BlockPos(x, 0, z))) return null;

        int heightmapY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

        // Scan down from heightmap for a block with sturdy UP face
        for (int dy = 0; dy <= 2; dy++) {
            int y = heightmapY - 1 - dy;
            BlockPos check = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(check);

            if (!state.isAir() && state.getFluidState().isEmpty()
                    && state.isFaceSturdy(level, check, Direction.UP)) {
                // Return the position above (where the cord sits)
                return check.above();
            }
        }

        // Fallback: try the heightmap position itself
        BlockPos fallback = new BlockPos(x, heightmapY - 1, z);
        BlockState fallbackState = level.getBlockState(fallback);
        if (fallbackState.isFaceSturdy(level, fallback, Direction.UP)) {
            return fallback.above();
        }

        return null;
    }

    // =====================================================================
    //  INITIAL NETWORK SEEDING
    // =====================================================================

    /**
     * One-time network seeding on W2 first load.
     * For each breach with a surface breakout, creates 4-6 growth tasks
     * radiating outward and places the first few cords immediately.
     */
    public void seedInitialNetwork(ServerLevel level, InfectionSavedData data, WorldbeastState beast) {
        if (veinNetworkSeeded) return;
        // Don't set flag yet — only set after confirming tasks were created

        OthersideMod.LOGGER.info("[VEIN] Attempting to seed initial vein network...");
        int totalTasks = 0;
        int totalCordsPlaced = 0;

        for (BreachData breach : data.getBreaches()) {
            BlockPos breakout = breach.getSurfaceBreakout();
            if (breakout == null || !breach.isColumnComplete()) continue;

            // Place immediate cords (BFS outward 4-8 blocks)
            int immediateDist = 4 + random.nextInt(5); // 4-8
            totalCordsPlaced += placeImmediateCords(level, breakout, immediateDist);

            // Create 4-6 growth tasks radiating outward
            int taskCount = 4 + random.nextInt(3); // 4-6
            Direction[] cardinals = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

            for (int i = 0; i < taskCount; i++) {
                int distance = 16 + random.nextInt(17); // 16-32 blocks

                BlockPos target;
                if (i < 4) {
                    // Cardinal directions
                    target = breakout.relative(cardinals[i], distance);
                } else {
                    // Diagonal directions
                    int dx = (i == 4) ? distance : -distance;
                    int dz = (i == 4) ? distance : -distance;
                    // Reduce diagonal distance to keep roughly same total
                    dx = (int) (dx * 0.7f);
                    dz = (int) (dz * 0.7f);
                    target = breakout.offset(dx, 0, dz);
                }

                GrowthTask task = new GrowthTask(breakout, target);
                task.lastGrowTick = level.getGameTime();
                tasks.add(task);
                totalTasks++;
            }
        }

        OthersideMod.LOGGER.info("[VEIN] Initial seeding complete: {} tasks created, {} cords placed immediately",
                totalTasks, totalCordsPlaced);

        // Only mark as seeded if at least one task was created
        if (totalTasks > 0) {
            veinNetworkSeeded = true;
            DirectorLog.log(level, "VEIN_SEED", BlockPos.ZERO,
                    "tasks=" + totalTasks + " immediate_cords=" + totalCordsPlaced);
        } else {
            OthersideMod.LOGGER.debug("[VEIN] No breaches ready for seeding yet — will retry next tick");
        }
    }

    /**
     * Place cords immediately via BFS outward from a breakout point.
     *
     * @return the number of cords placed.
     */
    private int placeImmediateCords(ServerLevel level, BlockPos origin, int maxDist) {
        int placed = 0;
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();

        BlockPos startSurface = findSurfacePos(level, origin.getX(), origin.getZ());
        if (startSurface == null) return 0;

        visited.add(startSurface);
        queue.add(startSurface);

        while (!queue.isEmpty() && placed < maxDist * 4) {
            BlockPos current = queue.poll();

            // Check distance from origin
            double dist = Math.sqrt(current.distSqr(origin));
            if (dist > maxDist) continue;

            // Place cord if not already one
            BlockState currentState = level.getBlockState(current);
            if (currentState.isAir()) {
                // Determine connections to neighbors
                BlockState cordState = ModBlocks.SCULK_VEIN_CORD.get().defaultBlockState();

                for (Direction dir : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                    BlockPos neighbor = current.relative(dir);
                    if (visited.contains(neighbor)) {
                        cordState = setDirectionProperty(cordState, dir, true);
                    }
                }

                // Check support
                BlockPos below = current.below();
                BlockState belowState = level.getBlockState(below);
                if (belowState.isFaceSturdy(level, below, Direction.UP)) {
                    level.setBlock(current, cordState, UPDATE_ALL);
                    placed++;
                }
            }

            // Expand to cardinal neighbors
            for (Direction dir : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
                BlockPos neighborXZ = current.relative(dir);
                BlockPos surfaceNeighbor = findSurfacePos(level, neighborXZ.getX(), neighborXZ.getZ());
                if (surfaceNeighbor != null && visited.add(surfaceNeighbor)) {
                    queue.add(surfaceNeighbor);
                }
            }
        }

        return placed;
    }

    // =====================================================================
    //  HEARD-TIER REROUTE
    // =====================================================================

    /**
     * When a player reaches HEARD tier (attention ≥ 25), spawn additional
     * growth tasks from existing breach breakouts toward that player's
     * lastKnownPos.
     */
    private void checkHeardTierReroute(ServerLevel level) {
        WorldbeastState beast = WorldbeastState.get(level);
        InfectionSavedData infData = InfectionSavedData.get(level);

        Map<UUID, WorldbeastState.AttentionData> attention = beast.getPlayerAttention();

        for (Map.Entry<UUID, WorldbeastState.AttentionData> entry : attention.entrySet()) {
            WorldbeastState.AttentionData ad = entry.getValue();
            if (ad.getCurrentTier().ordinal() < WorldbeastState.AttentionTier.HEARD.ordinal()) {
                continue;
            }

            BlockPos playerPos = ad.getLastKnownPos();
            if (playerPos.equals(BlockPos.ZERO)) continue;

            // Check if we already have a task targeting near this player
            boolean alreadyTargeting = tasks.stream()
                    .filter(t -> !t.complete)
                    .anyMatch(t -> t.target.distSqr(playerPos) < 32 * 32);
            if (alreadyTargeting) continue;

            // Find the nearest breach breakout to spawn from
            BlockPos bestBreakout = null;
            double bestDist = Double.MAX_VALUE;
            for (BreachData breach : infData.getBreaches()) {
                BlockPos breakout = breach.getSurfaceBreakout();
                if (breakout == null) continue;
                double d = breakout.distSqr(playerPos);
                if (d < bestDist) {
                    bestDist = d;
                    bestBreakout = breakout;
                }
            }

            if (bestBreakout != null) {
                GrowthTask task = new GrowthTask(bestBreakout, playerPos);
                task.lastGrowTick = level.getGameTime();
                tasks.add(task);

                OthersideMod.LOGGER.info("[VEIN] HEARD-tier reroute: growing from {} toward player at {}",
                        bestBreakout.toShortString(), playerPos.toShortString());
                DirectorLog.log(level, "VEIN_REROUTE", bestBreakout,
                        "player=" + entry.getKey() + " target=" + playerPos.toShortString()
                                + " tier=" + ad.getCurrentTier().name());
            }
        }
    }

    // =====================================================================
    //  AMBIENT GROWTH
    // =====================================================================

    /**
     * Slow passive growth: every 5 minutes, spawn 1 new growth task from
     * a random incomplete task's tip (or a breach breakout) outward in a
     * random direction, max 16 blocks.
     */
    private void ambientGrowth(ServerLevel level) {
        // Find a source position — prefer a task tip, fallback to breach breakout
        BlockPos source = null;

        // Try to find an endpoint from existing tasks
        List<GrowthTask> activeTasks = tasks.stream()
                .filter(t -> !t.complete)
                .toList();

        if (!activeTasks.isEmpty()) {
            GrowthTask randomTask = activeTasks.get(random.nextInt(activeTasks.size()));
            source = randomTask.currentTip;
        }

        // Fallback: use a breach breakout
        if (source == null) {
            InfectionSavedData data = InfectionSavedData.get(level);
            List<BreachData> breaches = data.getBreaches();
            if (!breaches.isEmpty()) {
                BreachData randomBreach = breaches.get(random.nextInt(breaches.size()));
                source = randomBreach.getSurfaceBreakout();
            }
        }

        if (source == null) return;

        // Random direction and distance
        Direction[] horizontals = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        Direction dir = horizontals[random.nextInt(horizontals.length)];
        int distance = 8 + random.nextInt(9); // 8-16 blocks
        BlockPos target = source.relative(dir, distance);

        GrowthTask task = new GrowthTask(source, target);
        task.lastGrowTick = level.getGameTime();
        tasks.add(task);

        OthersideMod.LOGGER.debug("[VEIN] Ambient growth task: {} → {} (dir={}, dist={})",
                source.toShortString(), target.toShortString(), dir.name(), distance);
    }

    // =====================================================================
    //  SEVERED-END RECONNECTION
    // =====================================================================

    /**
     * When OrderManager reports a sever, create a GrowthTask from the
     * body-side severed end toward the original target.
     *
     * @param severedEnd      the body-side end of the severed cord
     * @param originalTarget  the original destination the cord was heading to
     */
    public void onSever(ServerLevel level, BlockPos severedEnd, BlockPos originalTarget) {
        GrowthTask task = new GrowthTask(severedEnd, originalTarget);
        task.isReconnection = true;
        task.lastGrowTick = level.getGameTime();
        tasks.add(task);

        OthersideMod.LOGGER.info("[VEIN] Reconnection task created: {} → {} (severed)",
                severedEnd.toShortString(), originalTarget.toShortString());
        DirectorLog.log(level, "VEIN_RECONNECT_START", severedEnd,
                "target=" + originalTarget.toShortString());
    }

    // =====================================================================
    //  GETTERS
    // =====================================================================

    public List<GrowthTask> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public boolean isVeinNetworkSeeded() {
        return veinNetworkSeeded;
    }

    public int getActiveTaskCount() {
        return (int) tasks.stream().filter(t -> !t.complete).count();
    }

    // =====================================================================
    //  NBT PERSISTENCE
    // =====================================================================

    /**
     * Save growth data into the WorldbeastState compound tag.
     */
    public void saveTo(CompoundTag tag) {
        // Tasks list
        ListTag taskList = new ListTag();
        for (GrowthTask task : tasks) {
            taskList.add(task.save());
        }
        tag.put("veinTasks", taskList);

        // Flags
        tag.putBoolean("veinNetworkSeeded", veinNetworkSeeded);
    }

    /**
     * Load growth data from the WorldbeastState compound tag.
     */
    public void loadFrom(CompoundTag tag) {
        tasks.clear();

        // Tasks list
        if (tag.contains("veinTasks")) {
            ListTag taskList = tag.getList("veinTasks", Tag.TAG_COMPOUND);
            for (int i = 0; i < taskList.size(); i++) {
                tasks.add(GrowthTask.load(taskList.getCompound(i)));
            }
        }

        // Flags
        if (tag.contains("veinNetworkSeeded")) {
            veinNetworkSeeded = tag.getBoolean("veinNetworkSeeded");
        }
    }
}
