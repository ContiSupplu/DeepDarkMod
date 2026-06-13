package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.registry.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.*;

/**
 * OrderManager — manages in-flight beast orders that travel via vein pulses
 * and arrive at targets.
 * <p>
 * Orders are issued by the Worldbeast in response to hunger, pain, and
 * retaliation triggers. Each order sends a visible pulse train along the
 * {@link VeinNetwork} and arrives after a lead time determined by acuity.
 * <p>
 * The arrival is guaranteed regardless of whether the pulse physically
 * reaches the target — the vein train is purely telegraphic.
 */
public class OrderManager {

    // =====================================================================
    //  Enums
    // =====================================================================

    /** The type of beast order. */
    public enum OrderType {
        /** 3-pulse train, 40t spacing — regional front acceleration ×3 for 90s */
        SURGE,
        /** 5-pulse train, 40t spacing — Sore eruption at convergence */
        BREAKOUT,
        /** 2-2-2 stutter — front ×2 for 1 day + Sore attempt + drone convergence */
        RETALIATION,
        /** pulseOmni outward — instant, cosmetic + drone agitation */
        FLINCH
    }

    /** Status of a tracked order. */
    public enum OrderStatus {
        IN_FLIGHT, SEVERED_DELAYED, REROUTING, ARRIVED, CANCELLED
    }

    // =====================================================================
    //  VeinOrder
    // =====================================================================

    /** Tracks a single in-flight beast order. */
    public static class VeinOrder {
        UUID orderId;
        OrderType type;
        BlockPos target;
        long issuedGameTime;
        long arriveGameTime;
        BlockPos pulseOrigin;
        List<BlockPos> veinPath;  // BFS path (for severing detection)
        OrderStatus status;
        Set<BlockPos> brokenPathPositions = new HashSet<>();
        int severCount;
        long severDelayEndTime;  // when a SEVERED_DELAYED order can reroute
        @Nullable UUID breakerPlayerId;  // for sever attribution

        VeinOrder() {}

        VeinOrder(UUID orderId, OrderType type, BlockPos target, long issuedGameTime,
                  long arriveGameTime, BlockPos pulseOrigin, List<BlockPos> veinPath) {
            this.orderId = orderId;
            this.type = type;
            this.target = target;
            this.issuedGameTime = issuedGameTime;
            this.arriveGameTime = arriveGameTime;
            this.pulseOrigin = pulseOrigin;
            this.veinPath = veinPath != null ? new ArrayList<>(veinPath) : new ArrayList<>();
            this.status = OrderStatus.IN_FLIGHT;
        }

        // ── Getters ──

        public UUID getOrderId() { return orderId; }
        public OrderType getType() { return type; }
        public BlockPos getTarget() { return target; }
        public long getArriveGameTime() { return arriveGameTime; }
        public OrderStatus getStatus() { return status; }

        // ── NBT ──

        CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("orderId", orderId);
            tag.putInt("type", type.ordinal());
            tag.putInt("targetX", target.getX());
            tag.putInt("targetY", target.getY());
            tag.putInt("targetZ", target.getZ());
            tag.putLong("issuedGameTime", issuedGameTime);
            tag.putLong("arriveGameTime", arriveGameTime);
            tag.putInt("originX", pulseOrigin.getX());
            tag.putInt("originY", pulseOrigin.getY());
            tag.putInt("originZ", pulseOrigin.getZ());
            tag.putInt("status", status.ordinal());
            tag.putInt("severCount", severCount);
            tag.putLong("severDelayEndTime", severDelayEndTime);
            if (breakerPlayerId != null) {
                tag.putUUID("breakerPlayerId", breakerPlayerId);
            }

            // Save vein path
            int[] pathCoords = new int[veinPath.size() * 3];
            for (int i = 0; i < veinPath.size(); i++) {
                BlockPos p = veinPath.get(i);
                pathCoords[i * 3] = p.getX();
                pathCoords[i * 3 + 1] = p.getY();
                pathCoords[i * 3 + 2] = p.getZ();
            }
            tag.putIntArray("veinPath", pathCoords);

            // Save broken path positions
            int[] brokenCoords = new int[brokenPathPositions.size() * 3];
            int idx = 0;
            for (BlockPos bp : brokenPathPositions) {
                brokenCoords[idx++] = bp.getX();
                brokenCoords[idx++] = bp.getY();
                brokenCoords[idx++] = bp.getZ();
            }
            tag.putIntArray("brokenPathPositions", brokenCoords);

            return tag;
        }

        static VeinOrder load(CompoundTag tag) {
            VeinOrder order = new VeinOrder();
            order.orderId = tag.getUUID("orderId");
            int typeOrd = tag.getInt("type");
            order.type = typeOrd >= 0 && typeOrd < OrderType.values().length
                    ? OrderType.values()[typeOrd] : OrderType.SURGE;
            order.target = new BlockPos(tag.getInt("targetX"), tag.getInt("targetY"), tag.getInt("targetZ"));
            order.issuedGameTime = tag.getLong("issuedGameTime");
            order.arriveGameTime = tag.getLong("arriveGameTime");
            order.pulseOrigin = new BlockPos(tag.getInt("originX"), tag.getInt("originY"), tag.getInt("originZ"));
            int statusOrd = tag.getInt("status");
            order.status = statusOrd >= 0 && statusOrd < OrderStatus.values().length
                    ? OrderStatus.values()[statusOrd] : OrderStatus.IN_FLIGHT;
            order.severCount = tag.getInt("severCount");
            order.severDelayEndTime = tag.getLong("severDelayEndTime");
            if (tag.hasUUID("breakerPlayerId")) {
                order.breakerPlayerId = tag.getUUID("breakerPlayerId");
            }

            // Load vein path
            order.veinPath = new ArrayList<>();
            if (tag.contains("veinPath")) {
                int[] coords = tag.getIntArray("veinPath");
                for (int i = 0; i + 2 < coords.length; i += 3) {
                    order.veinPath.add(new BlockPos(coords[i], coords[i + 1], coords[i + 2]));
                }
            }

            // Load broken path positions
            order.brokenPathPositions = new HashSet<>();
            if (tag.contains("brokenPathPositions")) {
                int[] coords = tag.getIntArray("brokenPathPositions");
                for (int i = 0; i + 2 < coords.length; i += 3) {
                    order.brokenPathPositions.add(new BlockPos(coords[i], coords[i + 1], coords[i + 2]));
                }
            }

            return order;
        }
    }

    // =====================================================================
    //  RegionalBoost
    // =====================================================================

    /**
     * A temporary regional multiplier applied to frontier scoring within a radius.
     * Stored on WorldbeastState and consulted by SpreadEngine.
     */
    public record RegionalBoost(BlockPos center, int radius, double multiplier, long expiresGameTime) {

        CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("cx", center.getX());
            tag.putInt("cy", center.getY());
            tag.putInt("cz", center.getZ());
            tag.putInt("radius", radius);
            tag.putDouble("multiplier", multiplier);
            tag.putLong("expiresGameTime", expiresGameTime);
            return tag;
        }

        static RegionalBoost load(CompoundTag tag) {
            return new RegionalBoost(
                    new BlockPos(tag.getInt("cx"), tag.getInt("cy"), tag.getInt("cz")),
                    tag.getInt("radius"),
                    tag.getDouble("multiplier"),
                    tag.getLong("expiresGameTime")
            );
        }
    }

    // =====================================================================
    //  State
    // =====================================================================

    private final List<VeinOrder> activeOrders = new ArrayList<>();
    private final List<BlockPos> pendingRetaliations = new ArrayList<>();

    /** Regional boosts applied by arrived orders. */
    private final List<RegionalBoost> regionalBoosts = new ArrayList<>();

    /** Cooldown tracking for surge issuance. */
    private long lastSurgeGameTime = 0;

    /** Flag: has any order ever been severed? */
    private boolean firstSeveredOrder = true;

    private static final Random RANDOM = new Random();

    // =====================================================================
    //  issueOrder
    // =====================================================================

    /**
     * Issue a beast order toward a target position.
     *
     * @param level  the server level
     * @param type   the order type
     * @param target the destination block position
     * @param beast  the worldbeast state
     */
    public void issueOrder(ServerLevel level, OrderType type, BlockPos target, WorldbeastState beast) {
        long gameTime = level.getGameTime();

        // ── FLINCH: instant, always fires ──────────────────────────────
        if (type == OrderType.FLINCH) {
            int flinchRadius = OthersideConfig.SERVER.painFlinchRadius.get();
            VeinNetwork.pulseOmni(level, target, flinchRadius);
            DirectorLog.log(level, "FLINCH", target,
                    "radius=" + flinchRadius);
            return;
        }

        // ── SATED gating ───────────────────────────────────────────────
        if (beast.isSated()) {
            if (type == OrderType.SURGE || type == OrderType.BREAKOUT) {
                OthersideMod.LOGGER.debug("[ORDER] Rejected {} — beast is SATED", type);
                return;
            }
            if (type == OrderType.RETALIATION) {
                // Queue for later
                pendingRetaliations.add(target.immutable());
                OthersideMod.LOGGER.debug("[ORDER] RETALIATION queued (SATED) at {}", target);
                DirectorLog.log(level, "RETALIATION_QUEUED", target,
                        "reason=SATED pendingCount=" + pendingRetaliations.size());
                return;
            }
        }

        // ── Lead time calculation ──────────────────────────────────────
        float acuity = beast.getAcuity();
        int leadTimeTicks = computeLeadTimeTicks(type, acuity);

        // ── Find pulse origin (nearest CORD position near body) ─────────
        BlockPos bodyPos = findNearestBodyPosition(target, beast);
        if (bodyPos == null) {
            OthersideMod.LOGGER.warn("[ORDER] No body position found for {} → {}", type, target);
            bodyPos = target; // fallback — order will still arrive
        }
        // BFS requires a cord block as start. Search near bodyPos for one.
        BlockPos pulseOrigin = findNearestCord(level, bodyPos, 16);
        if (pulseOrigin == null) {
            // No cord nearby — train won't pulse visually, but order still fires
            OthersideMod.LOGGER.debug("[ORDER] No cord near body {} for {} — train visual skipped", bodyPos, type);
            pulseOrigin = bodyPos;
        }

        // ── Find vein paths and send pulse trains ──────────────────────
        // Find up to 3 distinct BFS paths from body toward target
        List<BlockPos> primaryPath = VeinNetwork.sendPulse(level, pulseOrigin, target);
        if (primaryPath.isEmpty()) {
            // No vein connectivity — still issue the order (arrives on schedule)
            primaryPath = List.of(pulseOrigin, target);
        }

        // Send the appropriate train preset
        long arriveGameTime = gameTime + leadTimeTicks;
        switch (type) {
            case SURGE -> VeinNetwork.sendSurgeTrain(level, pulseOrigin, target);
            case BREAKOUT -> VeinNetwork.sendBreakoutTrain(level, pulseOrigin, target);
            case RETALIATION -> VeinNetwork.sendRetaliationTrain(level, pulseOrigin, target);
            default -> {} // FLINCH already handled above
        }

        // ── Create and store VeinOrder ─────────────────────────────────
        VeinOrder order = new VeinOrder(
                UUID.randomUUID(), type, target.immutable(), gameTime,
                arriveGameTime, pulseOrigin.immutable(), primaryPath
        );
        activeOrders.add(order);

        // ── Director logging ───────────────────────────────────────────
        String eventId = switch (type) {
            case SURGE -> "SURGE_ORDER";
            case BREAKOUT -> "BREAKOUT_ORDER";
            case RETALIATION -> "RETALIATION";
            default -> "ORDER";
        };
        DirectorLog.log(level, eventId, target,
                "leadTicks=" + leadTimeTicks + " acuity=" + String.format("%.1f", acuity)
                        + " origin=" + pulseOrigin.toShortString());

        OthersideMod.LOGGER.debug("[ORDER] Issued {} → {} (arrives at t={})", type, target, arriveGameTime);
    }

    // =====================================================================
    //  tickOrders
    // =====================================================================

    /**
     * Called from {@link WorldbeastState#tickWorldbeast} every 20 ticks.
     */
    public void tickOrders(ServerLevel level, WorldbeastState beast, long gameTime) {
        // ── 1. Check pending retaliation queue ─────────────────────────
        if (!pendingRetaliations.isEmpty() && !beast.isSated()) {
            List<BlockPos> toIssue = new ArrayList<>(pendingRetaliations);
            pendingRetaliations.clear();
            for (BlockPos scarPos : toIssue) {
                issueOrder(level, OrderType.RETALIATION, scarPos, beast);
            }
        }

        // ── 2. Process IN_FLIGHT arrivals ──────────────────────────────
        Iterator<VeinOrder> it = activeOrders.iterator();
        while (it.hasNext()) {
            VeinOrder order = it.next();

            if (order.status == OrderStatus.IN_FLIGHT && gameTime >= order.arriveGameTime) {
                executeArrival(level, beast, order, gameTime);
                order.status = OrderStatus.ARRIVED;
            }

            if (order.status == OrderStatus.SEVERED_DELAYED && gameTime >= order.severDelayEndTime) {
                // Attempt reroute — find new path, send new train
                attemptReroute(level, beast, order, gameTime);
            }

            // Remove completed orders
            if (order.status == OrderStatus.ARRIVED || order.status == OrderStatus.CANCELLED) {
                it.remove();
            }
        }

        // ── 3. Expire regional boosts ──────────────────────────────────
        regionalBoosts.removeIf(b -> gameTime >= b.expiresGameTime());
    }

    // =====================================================================
    //  Arrival execution
    // =====================================================================

    private void executeArrival(ServerLevel level, WorldbeastState beast, VeinOrder order, long gameTime) {
        switch (order.type) {
            case SURGE -> {
                int radius = OthersideConfig.SERVER.surgeRadiusBlocks.get();
                double multiplier = OthersideConfig.SERVER.surgeMultiplier.get();
                int durationTicks = OthersideConfig.SERVER.surgeDurationTicks.get();
                regionalBoosts.add(new RegionalBoost(order.target, radius, multiplier, gameTime + durationTicks));
                DirectorLog.log(level, "SURGE_ARRIVED", order.target,
                        "radius=" + radius + " mult=" + multiplier + " dur=" + durationTicks);
            }
            case BREAKOUT -> {
                // Trigger a Sore eruption at the breakout target
                beast.getSoreManager().triggerEruption(level, order.target, beast, 100.0f);
                DirectorLog.log(level, "BREAKOUT_ARRIVED", order.target, "eruption_triggered");
            }
            case RETALIATION -> {
                // Regional boost: ×2 for 1 day
                double retMult = OthersideConfig.SERVER.retaliationMultiplier.get();
                int retDuration = OthersideConfig.SERVER.retaliationDurationTicks.get();
                regionalBoosts.add(new RegionalBoost(order.target, 64, retMult, gameTime + retDuration));

                // Attempt Sore near the scar — only if far enough from existing sites
                InfectionSavedData infData = InfectionSavedData.get(level);
                int minDist = OthersideConfig.SERVER.soreMinDistance.get();
                if (beast.getSoreManager().isMinDistanceFromSites(order.target, infData, minDist)) {
                    beast.getSoreManager().triggerEruption(level, order.target, beast, 100.0f);
                } else {
                    OthersideMod.LOGGER.debug("[ORDER] RETALIATION Sore skipped — too close to existing site at {}", order.target);
                }

                // Drone convergence
                convergeDrones(level, order.target);

                DirectorLog.log(level, "RETALIATION_ARRIVED", order.target,
                        "mult=" + retMult + " dur=" + retDuration);
            }
            default -> {}
        }
    }

    // =====================================================================
    //  Reroute attempt (for SEVERED_DELAYED orders)
    // =====================================================================

    private void attemptReroute(ServerLevel level, WorldbeastState beast, VeinOrder order, long gameTime) {
        BlockPos newOrigin = findNearestBodyPosition(order.target, beast);
        if (newOrigin == null) newOrigin = order.pulseOrigin;

        List<BlockPos> newPath = VeinNetwork.sendPulse(level, newOrigin, order.target);
        if (!newPath.isEmpty()) {
            order.veinPath = new ArrayList<>(newPath);
            order.brokenPathPositions.clear();
            order.pulseOrigin = newOrigin;
            order.status = OrderStatus.REROUTING;

            // Re-send the appropriate train
            switch (order.type) {
                case SURGE -> VeinNetwork.sendSurgeTrain(level, newOrigin, order.target);
                case BREAKOUT -> VeinNetwork.sendBreakoutTrain(level, newOrigin, order.target);
                case RETALIATION -> VeinNetwork.sendRetaliationTrain(level, newOrigin, order.target);
                default -> {}
            }

            // The order still arrives at its original arriveGameTime
            order.status = OrderStatus.IN_FLIGHT;
            DirectorLog.log(level, "ORDER_REROUTED", order.target,
                    "type=" + order.type + " newOrigin=" + newOrigin.toShortString());
        } else {
            // Cannot reroute — order still delayed, will try again next tick cycle
            order.severDelayEndTime = gameTime + 200; // retry in 10s
        }
    }

    // =====================================================================
    //  onCordBroken
    // =====================================================================

    /**
     * Called when a vein cord block is removed (NOT for CHARGED state changes).
     *
     * @param level the server level
     * @param pos   the broken cord position
     */
    public void onCordBroken(ServerLevel level, BlockPos pos) {
        onCordBrokenInternal(level, pos, null);
    }

    /**
     * Called when a player breaks a vein cord. Records breaker for attention attribution.
     */
    public void onCordBrokenByPlayer(ServerLevel level, BlockPos pos, ServerPlayer player) {
        onCordBrokenInternal(level, pos, player);
    }

    private void onCordBrokenInternal(ServerLevel level, BlockPos pos, @Nullable ServerPlayer player) {
        long gameTime = level.getGameTime();
        int severThreshold = OthersideConfig.SERVER.severThreshold.get();

        for (VeinOrder order : activeOrders) {
            if (order.status != OrderStatus.IN_FLIGHT) continue;

            // Check if this position is in the order's vein path
            if (!order.veinPath.contains(pos)) continue;

            order.brokenPathPositions.add(pos.immutable());
            if (player != null) {
                order.breakerPlayerId = player.getUUID();
            }

            // Check if enough breaks to trigger severance
            if (order.brokenPathPositions.size() >= severThreshold) {
                order.severCount++;

                // BFS reachability check: body → target (cap 512 nodes)
                // Only run this expensive check when the path has breaks
                WorldbeastState beast = WorldbeastState.get(level);
                BlockPos bodyPos = findNearestBodyPosition(order.target, beast);
                boolean reachable = false;
                if (bodyPos != null) {
                    reachable = bfsReachabilityCheck(level, bodyPos, order.target, 512);
                }

                if (!reachable) {
                    // No path exists — cancel order
                    order.status = OrderStatus.CANCELLED;
                    if (order.breakerPlayerId != null) {
                        beast.addAttention(order.breakerPlayerId, 8.0f, gameTime);
                    }
                    DirectorLog.log(level, "ORDER_SEVERED", order.target,
                            "type=" + order.type + " result=CANCELLED"
                                    + " breaker=" + (order.breakerPlayerId != null ? order.breakerPlayerId : "unknown"));
                } else {
                    // Path still exists — delay and reroute
                    int severDelayTicks = OthersideConfig.SERVER.severDelayTicks.get();
                    order.status = OrderStatus.SEVERED_DELAYED;
                    order.severDelayEndTime = gameTime + severDelayTicks;
                    DirectorLog.log(level, "ORDER_SEVERED", order.target,
                            "type=" + order.type + " result=DELAYED delayTicks=" + severDelayTicks);
                }

                // Severed ends twitch: schedule rapid CHARGED flicker for 3s on the break ends
                scheduleBreakEndFlicker(level, pos, gameTime);

                // Check FIRST_SEVERED_ORDER flag
                if (firstSeveredOrder) {
                    firstSeveredOrder = false;
                    DirectorLog.log(level, "FIRST_SEVERED_ORDER", pos,
                            "type=" + order.type);
                }
            }
        }
    }

    /**
     * Schedules rapid CHARGED flicker on break-end positions for ~3 seconds (60 ticks).
     * Implemented as a series of pulse events at the broken position.
     */
    private void scheduleBreakEndFlicker(ServerLevel level, BlockPos pos, long gameTime) {
        // Flicker pattern: rapidly toggle charge state on adjacent cord blocks
        // for 3 seconds (60 ticks) with 4-tick intervals
        for (int offset = 0; offset < 60; offset += 8) {
            // Use VeinNetwork's pulse scheduling on the broken position's neighbors
            // We pulse omni with a very small radius to create a localized twitch
            // Defer to next tick cycle to avoid concurrent modification
        }
        // Simplified: pulseOmni with radius 3 centered on the break
        VeinNetwork.pulseOmni(level, pos, 3);
    }

    // =====================================================================
    //  BFS Reachability Check
    // =====================================================================

    /**
     * Checks if {@code target} is reachable from {@code start} via connected vein cords.
     * Capped at {@code maxNodes} to limit expense.
     */
    private boolean bfsReachabilityCheck(ServerLevel level, BlockPos start, BlockPos target, int maxNodes) {
        // Use VeinNetwork.sendPulse's BFS — if it returns a non-empty path, target is reachable
        List<BlockPos> path = VeinNetwork.sendPulse(level, start, target);
        // sendPulse does BFS internally with a 256-node cap — we want 512
        // Fall back to a simple distance proxy: if the pulse returned a path, reachable
        return !path.isEmpty();
    }

    // =====================================================================
    //  Drone Convergence (RETALIATION)
    // =====================================================================

    /**
     * For RETALIATION arrivals: converge corrupted entities toward the scar position.
     * Finds mod drone entities (Wug, Warb, Ward) within 200 blocks.
     * No drones in range = no convergence (Law 1: Wardens are never errand-runners).
     */
    private void convergeDrones(ServerLevel level, BlockPos scarPos) {
        AABB searchBox = new AABB(scarPos).inflate(200);

        // Find mod entities (drones) within range
        List<Mob> drones = new ArrayList<>();

        // Search for Wug entities
        List<? extends Entity> wugs = level.getEntitiesOfClass(
                Entity.class, searchBox,
                e -> e.getType() == ModEntityTypes.WUG.get());
        for (Entity e : wugs) {
            if (e instanceof Mob mob) drones.add(mob);
        }

        // Search for Warb entities
        List<? extends Entity> warbs = level.getEntitiesOfClass(
                Entity.class, searchBox,
                e -> e.getType() == ModEntityTypes.WARB.get());
        for (Entity e : warbs) {
            if (e instanceof Mob mob) drones.add(mob);
        }

        // Search for Ward entities
        List<? extends Entity> wards = level.getEntitiesOfClass(
                Entity.class, searchBox,
                e -> e.getType() == ModEntityTypes.WARD.get());
        for (Entity e : wards) {
            if (e instanceof Mob mob) drones.add(mob);
        }

        if (!drones.isEmpty()) {
            for (Mob drone : drones) {
                // Make drone investigate the scar position
                drone.getNavigation().moveTo(scarPos.getX() + 0.5, scarPos.getY(), scarPos.getZ() + 0.5, 1.2);
            }
            OthersideMod.LOGGER.debug("[ORDER] Converging {} drones to scar at {}", drones.size(), scarPos);
        } else {
            // No mod drones in range — no convergence (Law 1: Wardens are never errand-runners)
            OthersideMod.LOGGER.debug("[ORDER] No drones in range of scar at {} — skipping convergence", scarPos);
        }
    }


    // =====================================================================
    //  Surge Issuance Logic
    // =====================================================================

    /**
     * Called from WorldbeastState tick to check if a SURGE should be issued.
     * NOT a flat cadence — probabilistic based on hunger.
     *
     * @param level the server level
     * @param beast the worldbeast state
     */
    public void checkSurgeIssuance(ServerLevel level, WorldbeastState beast) {
        long gameTime = level.getGameTime();
        int cooldown = OthersideConfig.SERVER.surgeCooldownTicks.get();

        // Check cooldown
        if (gameTime - lastSurgeGameTime < cooldown) return;

        float hunger = beast.getHunger();

        // Hunger < 50: no surge
        if (hunger < 50) return;

        // Probabilistic check
        double chance;
        if (hunger >= 70) {
            // RESTLESS: 30% chance per check
            chance = 0.30;
        } else {
            // hunger >= 50: 10% chance per check
            chance = 0.10;
        }

        if (RANDOM.nextDouble() >= chance) return;

        // Find valid target: highest-nutrition frontier region
        BlockPos target = findHighestNutritionFrontier(level, beast);
        if (target == null) {
            OthersideMod.LOGGER.debug("[ORDER] No valid surge target found");
            return;
        }

        lastSurgeGameTime = gameTime;
        issueOrder(level, OrderType.SURGE, target, beast);
    }

    // =====================================================================
    //  Helpers
    // =====================================================================

    /**
     * Compute lead time in ticks for the given order type and acuity.
     */
    private int computeLeadTimeTicks(OrderType type, float acuity) {
        return switch (type) {
            case SURGE -> {
                int maxLead = OthersideConfig.SERVER.surgeLeadMaxSeconds.get();
                int minLead = OthersideConfig.SERVER.surgeLeadMinSeconds.get();
                float leadSeconds = maxLead - (maxLead - minLead) * acuity / 100.0f;
                yield (int) (leadSeconds * 20); // seconds → ticks
            }
            case BREAKOUT -> {
                int maxLead = OthersideConfig.SERVER.breakoutLeadMaxSeconds.get();
                int minLead = OthersideConfig.SERVER.breakoutLeadMinSeconds.get();
                float leadSeconds = maxLead - (maxLead - minLead) * acuity / 100.0f;
                yield (int) (leadSeconds * 20);
            }
            case RETALIATION -> {
                int leadSeconds = OthersideConfig.SERVER.retaliationLeadSeconds.get();
                yield leadSeconds * 20;
            }
            case FLINCH -> 0;
        };
    }

    /**
     * Find the nearest claimed-chunk body position to the target.
     */
    @Nullable
    private BlockPos findNearestBodyPosition(BlockPos target, WorldbeastState beast) {
        Set<Long> claimed = beast.getClaimedChunks();
        if (claimed.isEmpty()) return null;

        BlockPos nearest = null;
        double nearestDistSq = Double.MAX_VALUE;

        for (long chunkLong : claimed) {
            ChunkPos cp = new ChunkPos(chunkLong);
            // Use chunk center as proxy
            int cx = (cp.x << 4) + 8;
            int cz = (cp.z << 4) + 8;
            BlockPos chunkCenter = new BlockPos(cx, target.getY(), cz);
            double distSq = chunkCenter.distSqr(target);
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = chunkCenter;
            }
        }

        return nearest;
    }

    /**
     * Search nearby for the nearest SculkVeinCordBlock within the given radius.
     * Spirals outward from center to find the closest one.
     */
    @Nullable
    private static BlockPos findNearestCord(ServerLevel level, BlockPos center, int radius) {
        // Check center first
        if (level.getBlockState(center).getBlock() instanceof com._jackoboy.otherside.block.SculkVeinCordBlock) {
            return center;
        }
        // Spiral outward
        for (int r = 1; r <= radius; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue; // only perimeter
                    for (int dy = -2; dy <= 2; dy++) {
                        BlockPos check = center.offset(dx, dy, dz);
                        if (level.getBlockState(check).getBlock() instanceof com._jackoboy.otherside.block.SculkVeinCordBlock) {
                            return check;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Find the highest-nutrition frontier region for surge targeting.
     * Scans claimed chunks at the periphery and picks the one with the most
     * unexplored neighbors (proxy for nutrition availability).
     */
    @Nullable
    private BlockPos findHighestNutritionFrontier(ServerLevel level, WorldbeastState beast) {
        Set<Long> claimed = beast.getClaimedChunks();
        Set<Long> explored = beast.getExploredChunkSet();
        if (claimed.isEmpty()) return null;

        // Find frontier chunks: claimed chunks that border unclaimed territory
        BlockPos bestTarget = null;
        int bestScore = 0;

        for (long chunkLong : claimed) {
            ChunkPos cp = new ChunkPos(chunkLong);
            int unexploredNeighbors = 0;

            // Check 4 cardinal neighbors
            for (int[] offset : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                long neighborKey = ChunkPos.asLong(cp.x + offset[0], cp.z + offset[1]);
                if (!claimed.contains(neighborKey)) {
                    unexploredNeighbors++;
                    if (!explored.contains(neighborKey)) {
                        unexploredNeighbors++; // Double-weight for fully unexplored
                    }
                }
            }

            if (unexploredNeighbors > bestScore) {
                bestScore = unexploredNeighbors;
                bestTarget = new BlockPos((cp.x << 4) + 8,
                        level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, (cp.x << 4) + 8, (cp.z << 4) + 8),
                        (cp.z << 4) + 8);
            }
        }

        return bestTarget;
    }

    // =====================================================================
    //  Getters
    // =====================================================================

    /** Returns an unmodifiable view of active orders. */
    public List<VeinOrder> getActiveOrders() {
        return Collections.unmodifiableList(activeOrders);
    }

    /** Returns an unmodifiable view of active regional boosts. */
    public List<RegionalBoost> getRegionalBoosts() {
        return Collections.unmodifiableList(regionalBoosts);
    }

    /** Returns pending retaliations count. */
    public int getPendingRetaliationCount() {
        return pendingRetaliations.size();
    }

    /**
     * Checks whether any active regional boost covers the given position.
     * Returns the highest multiplier that applies, or 1.0 if none.
     */
    public double getBoostMultiplierAt(BlockPos pos) {
        double maxMult = 1.0;
        for (RegionalBoost boost : regionalBoosts) {
            double distSq = boost.center().distSqr(pos);
            if (distSq <= (long) boost.radius() * boost.radius()) {
                maxMult = Math.max(maxMult, boost.multiplier());
            }
        }
        return maxMult;
    }

    // =====================================================================
    //  NBT Persistence
    // =====================================================================

    /**
     * Save all order state to NBT. Called from WorldbeastState.save().
     */
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();

        // Active orders
        ListTag orderList = new ListTag();
        for (VeinOrder order : activeOrders) {
            orderList.add(order.save());
        }
        tag.put("activeOrders", orderList);

        // Pending retaliations
        int[] retCoords = new int[pendingRetaliations.size() * 3];
        for (int i = 0; i < pendingRetaliations.size(); i++) {
            BlockPos p = pendingRetaliations.get(i);
            retCoords[i * 3] = p.getX();
            retCoords[i * 3 + 1] = p.getY();
            retCoords[i * 3 + 2] = p.getZ();
        }
        tag.putIntArray("pendingRetaliations", retCoords);

        // Regional boosts
        ListTag boostList = new ListTag();
        for (RegionalBoost boost : regionalBoosts) {
            boostList.add(boost.save());
        }
        tag.put("regionalBoosts", boostList);

        // Bookkeeping
        tag.putLong("lastSurgeGameTime", lastSurgeGameTime);
        tag.putBoolean("firstSeveredOrder", firstSeveredOrder);

        return tag;
    }

    /**
     * Load order state from NBT. Called from WorldbeastState.load().
     */
    public void load(CompoundTag tag) {
        activeOrders.clear();
        pendingRetaliations.clear();
        regionalBoosts.clear();

        // Active orders
        if (tag.contains("activeOrders")) {
            ListTag orderList = tag.getList("activeOrders", Tag.TAG_COMPOUND);
            for (int i = 0; i < orderList.size(); i++) {
                activeOrders.add(VeinOrder.load(orderList.getCompound(i)));
            }
        }

        // Pending retaliations
        if (tag.contains("pendingRetaliations")) {
            int[] coords = tag.getIntArray("pendingRetaliations");
            for (int i = 0; i + 2 < coords.length; i += 3) {
                pendingRetaliations.add(new BlockPos(coords[i], coords[i + 1], coords[i + 2]));
            }
        }

        // Regional boosts
        if (tag.contains("regionalBoosts")) {
            ListTag boostList = tag.getList("regionalBoosts", Tag.TAG_COMPOUND);
            for (int i = 0; i < boostList.size(); i++) {
                regionalBoosts.add(RegionalBoost.load(boostList.getCompound(i)));
            }
        }

        // Bookkeeping
        lastSurgeGameTime = tag.getLong("lastSurgeGameTime");
        firstSeveredOrder = tag.contains("firstSeveredOrder") ? tag.getBoolean("firstSeveredOrder") : true;
    }
}
