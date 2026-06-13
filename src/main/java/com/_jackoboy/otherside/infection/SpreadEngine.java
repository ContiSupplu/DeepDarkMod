package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class SpreadEngine {
    private static final Direction[] ALL_DIRECTIONS = Direction.values();
    private static final Random RANDOM = new Random();
    private int tickCounter = 0;
    private int debugLogTimer = 0;
    private int chunkLoadTimer = 0;

    // setBlock flag: UPDATE_CLIENTS only — no neighbor updates, no sounds, no drops
    private static final int SILENT_SET = 2;

    public void tick(ServerLevel level) {
        InfectionSavedData data = InfectionSavedData.get(level);
        if (!data.isInitialized()) return;

        // Force-load chunks around breaches every 5 seconds
        chunkLoadTimer++;
        if (chunkLoadTimer >= 100) {
            chunkLoadTimer = 0;
            forceLoadBreachChunks(level, data);
        }

        int cycleTicks = OthersideConfig.SERVER.spreadCycleTicks.get();
        int effectiveCycle = data.isTimelapseActive() ? Math.max(1, cycleTicks / data.getTimelapseMultiplier()) : cycleTicks;

        tickCounter++;

        // Debug log every 5 seconds
        debugLogTimer++;
        if (debugLogTimer >= 100) {
            debugLogTimer = 0;
            int totalFrontier = data.getBreaches().stream().mapToInt(b -> b.getFrontier().size()).sum();
            WorldbeastState beast = WorldbeastState.get(level);
            OthersideMod.LOGGER.info("[SPREAD] Status: mass={}, hunger={}, breaches={}, frontier={}, conversions={}, timelapse={}({}x)",
                    String.format("%.1f", beast.getMass()),
                    String.format("%.1f", beast.getHunger()),
                    data.getBreaches().size(),
                    totalFrontier, data.getTotalConversions(),
                    data.isTimelapseActive(), data.getTimelapseMultiplier());
        }

        if (tickCounter < effectiveCycle) return;
        tickCounter = 0;

        int budgetPerBreach = OthersideConfig.SERVER.spreadBudgetPerCycle.get();
        int globalBudget = OthersideConfig.SERVER.globalSpreadBudget.get();
        WorldbeastState beast = WorldbeastState.get(level);
        double massMultiplier = 1.0 + beast.getMass() / 50.0;
        int effectiveBudgetPerBreach = (int) Math.ceil(budgetPerBreach * massMultiplier);
        // Apply pacing rail throttle
        if (beast.isRailThrottled()) {
            effectiveBudgetPerBreach = (int) Math.ceil(effectiveBudgetPerBreach * OthersideConfig.SERVER.beastRailThrottleMultiplier.get());
        }
        // Apply sated throttle
        if (beast.isSated()) {
            effectiveBudgetPerBreach = (int) Math.ceil(effectiveBudgetPerBreach * OthersideConfig.SERVER.beastSatedSpreadMultiplier.get());
        }

        int globalUsed = 0;

        for (BreachData breachData : data.getBreaches()) {
            if (!breachData.isActive()) continue;
            if (globalUsed >= globalBudget) break;

            int breachBudget = Math.min(effectiveBudgetPerBreach, globalBudget - globalUsed);
            // W2: apply per-breach budget fraction (Sores use 0.25 of main budget)
            breachBudget = (int) Math.ceil(breachBudget * breachData.getBudgetFraction());
            int noiseBonus = breachData.consumeNoiseBonus();
            breachBudget = Math.min(breachBudget + noiseBonus, globalBudget - globalUsed);

            int converted = spreadFromBreach(level, data, breachData, breachBudget, beast);
            globalUsed += converted;
        }

        data.setDirty();
    }

    private void forceLoadBreachChunks(ServerLevel level, InfectionSavedData data) {
        for (BreachData breach : data.getBreaches()) {
            if (!breach.isActive()) continue;
            BlockPos surface = breach.getSurfaceBreakout();
            if (surface == null) continue;

            int chunkRadius = 5;
            ChunkPos center = new ChunkPos(surface);
            for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
                for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                    level.setChunkForced(center.x + dx, center.z + dz, true);
                }
            }
        }
    }

    // ─── Scored candidate for frontier selection ─────────────────────────
    private record ScoredCandidate(long packedFrontier, BlockPos target, BlockState targetState, float score,
                                   Direction dir, BlockPos frontierPos) {}

    private int spreadFromBreach(ServerLevel level, InfectionSavedData data, BreachData breachData, int budget, WorldbeastState beast) {
        Set<Long> frontier = breachData.getFrontier();
        if (frontier.isEmpty()) return 0;

        List<Long> candidates = new ArrayList<>(frontier);
        Collections.shuffle(candidates, RANDOM);

        List<Long> toRemove = new ArrayList<>();
        List<Long> toAdd = new ArrayList<>();
        int converted = 0;

        // ── Phase 1: Sample and score candidates ────────────────────────
        int sampleLimit = Math.min(budget * 4, candidates.size());
        List<ScoredCandidate> scoredCandidates = new ArrayList<>();
        List<WorldbeastState.GravityTarget> gravityTargets = beast.getPlayerGravityTargets();
        // W2: active regional boosts from SURGE/RETALIATION orders
        java.util.List<OrderManager.RegionalBoost> regionalBoosts = beast.getRegionalBoosts();

        for (int i = 0; i < sampleLimit; i++) {
            long packed = candidates.get(i);
            BlockPos frontierPos = BlockPos.of(packed);

            if (!level.isLoaded(frontierPos)) continue;

            boolean foundConvertible = false;
            Direction[] directions = getShuffledDirections();

            // Find the BEST adjacent convertible block for this frontier position
            float bestScore = -1f;
            BlockPos bestTarget = null;
            BlockState bestTargetState = null;
            Direction bestDir = null;

            for (Direction dir : directions) {
                BlockPos target = frontierPos.relative(dir);
                if (!level.isLoaded(target)) continue;

                BlockState targetState = level.getBlockState(target);

                // === WATER: convert riverbed, leave water intact ===
                if (targetState.is(Blocks.WATER)) {
                    BlockPos riverbed = findRiverbed(level, target);
                    if (riverbed != null) {
                        BlockState riverbedState = level.getBlockState(riverbed);
                        if (ConversionMap.isConvertible(riverbedState)) {
                            float score = 1.0f;
                            score *= (1.0f + ConversionMap.getNutrition(riverbedState.getBlock()) / 2.0f);
                            for (WorldbeastState.GravityTarget gt : gravityTargets) {
                                double dist = Math.sqrt(frontierPos.distSqr(gt.pos()));
                                if (dist < 96) {
                                    float proximity = (float)(1.0 - dist / 96.0);
                                    score *= (1.0f + 0.6f * proximity * (gt.attention() / 100f));
                                }
                            }
                            // W2: regional boost
                            for (OrderManager.RegionalBoost rb : regionalBoosts) {
                                double dx = frontierPos.getX() - rb.center().getX();
                            double dz = frontierPos.getZ() - rb.center().getZ();
                            if (dx * dx + dz * dz < (double) rb.radius() * rb.radius()) {
                                    score *= rb.multiplier();
                                }
                            }
                            if (score > bestScore) {
                                bestScore = score;
                                bestTarget = riverbed;
                                bestTargetState = riverbedState;
                                bestDir = dir;
                            }
                            foundConvertible = true;
                        }
                    }
                    foundConvertible = true;
                    continue;
                }

                if (!ConversionMap.isConvertible(targetState)) continue;

                foundConvertible = true;

                // Score this candidate
                float score = 1.0f;
                score *= (1.0f + ConversionMap.getNutrition(targetState.getBlock()) / 2.0f);
                for (WorldbeastState.GravityTarget gt : gravityTargets) {
                    double dist = Math.sqrt(frontierPos.distSqr(gt.pos()));
                    if (dist < 96) {
                        float proximity = (float)(1.0 - dist / 96.0);
                        score *= (1.0f + 0.6f * proximity * (gt.attention() / 100f));
                    }
                }
                // W2: regional boost
                for (OrderManager.RegionalBoost rb : regionalBoosts) {
                    double dx = frontierPos.getX() - rb.center().getX();
                    double dz = frontierPos.getZ() - rb.center().getZ();
                    if (dx * dx + dz * dz < (double) rb.radius() * rb.radius()) {
                        score *= rb.multiplier();
                    }
                }

                if (score > bestScore) {
                    bestScore = score;
                    bestTarget = target;
                    bestTargetState = targetState;
                    bestDir = dir;
                }
            }

            if (bestTarget != null) {
                scoredCandidates.add(new ScoredCandidate(packed, bestTarget, bestTargetState, bestScore, bestDir, frontierPos));
            } else if (!foundConvertible) {
                toRemove.add(packed);
            }
        }

        // ── Phase 2: Sort by score descending, process top budget ────────
        scoredCandidates.sort(Comparator.comparingDouble(ScoredCandidate::score).reversed());

        int processLimit = Math.min(budget, scoredCandidates.size());
        for (int i = 0; i < processLimit && converted < budget; i++) {
            ScoredCandidate sc = scoredCandidates.get(i);
            BlockPos target = sc.target();
            BlockState targetState = sc.targetState();

            if (!level.isLoaded(target)) continue;
            // Re-validate: block may have changed since scoring
            BlockState currentState = level.getBlockState(target);
            if (!currentState.equals(targetState)) continue;

            // === PLAYER-BLOCK RESISTANCE ===
            if (beast.isPlayerPlaced(target)) {
                if (beast.getMass() < OthersideConfig.SERVER.beastPlayerBlockResistanceMassGate.get()) {
                    // Below gate: player-placed blocks are immune
                    continue;
                }
                // Above gate: resistance chance
                if (RANDOM.nextFloat() > OthersideConfig.SERVER.beastPlayerBlockResistance.get().floatValue()) {
                    continue;
                }
            }

            // === WATER/RIVERBED (target is riverbed pos, targetState is riverbed state) ===
            // Detect riverbed conversion: if the original direction neighbor was water
            BlockState dirNeighborState = level.getBlockState(sc.frontierPos().relative(sc.dir()));
            if (dirNeighborState.is(Blocks.WATER)) {
                // This is a riverbed conversion
                BlockState newState = ConversionMap.getConversion(targetState);
                level.setBlock(target, newState, SILENT_SET);
                converted++;
                data.incrementConversions();
                toAdd.add(target.asLong());

                // Feed nutrition
                float nutrition = ConversionMap.getNutrition(targetState.getBlock()) * OthersideConfig.SERVER.beastNutritionScale.get().floatValue();
                if (beast.isPlayerPlaced(target)) nutrition *= 1.5f;
                beast.feedNutrition(nutrition);
                breachData.addNoiseCharge((int)(nutrition * 5));
                // Record biome taste
                String biomeId = level.getBiome(target).unwrapKey().map(k -> k.location().toString()).orElse("");
                if (!biomeId.isEmpty()) beast.tasteBiome(biomeId);

                continue;
            }

            // Light resistance check
            int blockLight = level.getBrightness(LightLayer.BLOCK, target);
            if (blockLight >= 12) {
                double resistChance = OthersideConfig.SERVER.lightResistChance.get();
                if (RANDOM.nextDouble() < resistChance) {
                    continue;
                }
            }

            // === TREE CONSUMPTION ===
            // When we hit a log, consume the ENTIRE tree (logs → sculk_wood stumps, leaves → air)
            if (targetState.is(BlockTags.LOGS)) {
                consumeTree(level, target, data);
                converted++;
                data.incrementConversions();
                toAdd.add(target.asLong());

                // Feed nutrition
                float nutrition = ConversionMap.getNutrition(targetState.getBlock()) * OthersideConfig.SERVER.beastNutritionScale.get().floatValue();
                if (beast.isPlayerPlaced(target)) nutrition *= 1.5f;
                beast.feedNutrition(nutrition);
                breachData.addNoiseCharge((int)(nutrition * 5));
                // Record biome taste
                String biomeId = level.getBiome(target).unwrapKey().map(k -> k.location().toString()).orElse("");
                if (!biomeId.isEmpty()) beast.tasteBiome(biomeId);

                continue;
            }

            // === LEAVES: just delete them silently ===
            if (targetState.is(BlockTags.LEAVES)) {
                level.setBlock(target, Blocks.AIR.defaultBlockState(), SILENT_SET);
                converted++;
                data.incrementConversions();
                toAdd.add(target.asLong());

                // Feed nutrition
                float nutrition = ConversionMap.getNutrition(targetState.getBlock()) * OthersideConfig.SERVER.beastNutritionScale.get().floatValue();
                if (beast.isPlayerPlaced(target)) nutrition *= 1.5f;
                beast.feedNutrition(nutrition);
                breachData.addNoiseCharge((int)(nutrition * 5));
                // Record biome taste
                String biomeId = level.getBiome(target).unwrapKey().map(k -> k.location().toString()).orElse("");
                if (!biomeId.isEmpty()) beast.tasteBiome(biomeId);

                continue;
            }

            // === STANDARD CONVERSION ===
            // Clear plants above first
            clearAbove(level, target);

            BlockState newState = ConversionMap.getConversion(targetState);
            level.setBlock(target, newState, SILENT_SET);
            converted++;
            data.incrementConversions();

            // Sculk feature seeding — every 8th block for balanced variety
            if (converted % 8 == 0) {
                seedSculkFeature(level, target, beast);
            }

            // Proactively scan for trees directly above converted surface blocks
            scanAndConsumeTreeAbove(level, target, data);

            toAdd.add(target.asLong());

            // Feed nutrition
            float nutrition = ConversionMap.getNutrition(targetState.getBlock()) * OthersideConfig.SERVER.beastNutritionScale.get().floatValue();
            if (beast.isPlayerPlaced(target)) nutrition *= 1.5f;
            beast.feedNutrition(nutrition);
            breachData.addNoiseCharge((int)(nutrition * 5));
            // Record biome taste
            String biomeId = level.getBiome(target).unwrapKey().map(k -> k.location().toString()).orElse("");
            if (!biomeId.isEmpty()) beast.tasteBiome(biomeId);
        }

        // ── Phase 3: BONUS BUDGET for active regional boosts ─────────────
        // Boosts grant extra conversions ON TOP of the normal budget so the
        // front visibly accelerates in the boosted region.
        if (!regionalBoosts.isEmpty()) {
            // Collect un-processed in-region candidates (skip ones already converted above)
            Set<Long> processedFrontiers = new HashSet<>();
            for (int i = 0; i < processLimit; i++) {
                processedFrontiers.add(scoredCandidates.get(i).packedFrontier());
            }

            for (OrderManager.RegionalBoost rb : regionalBoosts) {
                int bonusBudget = (int)(budget * (rb.multiplier() - 1.0));
                if (bonusBudget <= 0) continue;

                // Gather in-region candidates not yet processed
                List<ScoredCandidate> boostCandidates = new ArrayList<>();
                for (ScoredCandidate sc : scoredCandidates) {
                    if (processedFrontiers.contains(sc.packedFrontier())) continue;
                    double dx = sc.frontierPos().getX() - rb.center().getX();
                    double dz = sc.frontierPos().getZ() - rb.center().getZ();
                    if (dx * dx + dz * dz < (double) rb.radius() * rb.radius()) {
                        boostCandidates.add(sc);
                    }
                }
                boostCandidates.sort(Comparator.comparingDouble(ScoredCandidate::score).reversed());

                int bonusConverted = 0;
                for (ScoredCandidate sc : boostCandidates) {
                    if (bonusConverted >= bonusBudget) break;

                    BlockPos target = sc.target();
                    BlockState targetState = sc.targetState();
                    if (!level.isLoaded(target)) continue;
                    BlockState currentState = level.getBlockState(target);
                    if (!currentState.equals(targetState)) continue;

                    // Player-block resistance (same rules as normal)
                    if (beast.isPlayerPlaced(target)) {
                        if (beast.getMass() < OthersideConfig.SERVER.beastPlayerBlockResistanceMassGate.get()) continue;
                        if (RANDOM.nextFloat() > OthersideConfig.SERVER.beastPlayerBlockResistance.get().floatValue()) continue;
                    }

                    // Riverbed check
                    BlockState dirNeighborState = level.getBlockState(sc.frontierPos().relative(sc.dir()));
                    if (dirNeighborState.is(Blocks.WATER)) {
                        BlockState newState = ConversionMap.getConversion(targetState);
                        level.setBlock(target, newState, SILENT_SET);
                        bonusConverted++;
                        converted++;
                        data.incrementConversions();
                        toAdd.add(target.asLong());
                        float nutrition = ConversionMap.getNutrition(targetState.getBlock()) * OthersideConfig.SERVER.beastNutritionScale.get().floatValue();
                        beast.feedNutrition(nutrition);
                        breachData.addNoiseCharge((int)(nutrition * 5));
                        continue;
                    }

                    // Light resistance
                    int blockLight = level.getBrightness(LightLayer.BLOCK, target);
                    if (blockLight >= 12) {
                        if (RANDOM.nextDouble() < OthersideConfig.SERVER.lightResistChance.get()) continue;
                    }

                    // Tree consumption
                    if (targetState.is(BlockTags.LOGS)) {
                        consumeTree(level, target, data);
                        bonusConverted++;
                        converted++;
                        data.incrementConversions();
                        toAdd.add(target.asLong());
                        float nutrition = ConversionMap.getNutrition(targetState.getBlock()) * OthersideConfig.SERVER.beastNutritionScale.get().floatValue();
                        beast.feedNutrition(nutrition);
                        breachData.addNoiseCharge((int)(nutrition * 5));
                        continue;
                    }

                    // Leaves
                    if (targetState.is(BlockTags.LEAVES)) {
                        level.setBlock(target, Blocks.AIR.defaultBlockState(), SILENT_SET);
                        bonusConverted++;
                        converted++;
                        data.incrementConversions();
                        toAdd.add(target.asLong());
                        float nutrition = ConversionMap.getNutrition(targetState.getBlock()) * OthersideConfig.SERVER.beastNutritionScale.get().floatValue();
                        beast.feedNutrition(nutrition);
                        breachData.addNoiseCharge((int)(nutrition * 5));
                        continue;
                    }

                    // Standard conversion
                    clearAbove(level, target);
                    BlockState newState = ConversionMap.getConversion(targetState);
                    level.setBlock(target, newState, SILENT_SET);
                    bonusConverted++;
                    converted++;
                    data.incrementConversions();
                    toAdd.add(target.asLong());
                    float nutrition = ConversionMap.getNutrition(targetState.getBlock()) * OthersideConfig.SERVER.beastNutritionScale.get().floatValue();
                    beast.feedNutrition(nutrition);
                    breachData.addNoiseCharge((int)(nutrition * 5));
                }

                if (bonusConverted > 0) {
                    OthersideMod.LOGGER.debug("[SPREAD] Boost at {} granted {} bonus conversions (budget={})",
                            rb.center().toShortString(), bonusConverted, bonusBudget);
                }
            }
        }

        frontier.removeAll(toRemove);
        frontier.addAll(toAdd);

        // Enqueue modified chunks for claim checks
        Set<Long> modifiedChunks = new HashSet<>();
        for (long added : toAdd) {
            BlockPos bp = BlockPos.of(added);
            modifiedChunks.add(new ChunkPos(bp).toLong());
        }
        for (long chunk : modifiedChunks) {
            beast.enqueueClaimCheck(chunk);
        }

        return converted;
    }

    /**
     * Consume an entire tree: find the trunk, convert bottom 5 blocks to sculk_wood (stump),
     * convert rest of logs to air, delete all connected leaves.
     */
    private void consumeTree(ServerLevel level, BlockPos logPos, InfectionSavedData data) {
        // Find the base of the tree — scan down
        BlockPos.MutableBlockPos scan = new BlockPos.MutableBlockPos(logPos.getX(), logPos.getY(), logPos.getZ());
        while (scan.getY() > level.getMinBuildHeight()) {
            scan.move(Direction.DOWN);
            BlockState below = level.getBlockState(scan);
            if (!below.is(BlockTags.LOGS)) {
                scan.move(Direction.UP); // back to the lowest log
                break;
            }
        }
        BlockPos trunkBase = scan.immutable();

        // Scan upward — convert trunk to sculk_wood stumps (first 2 blocks) then delete rest
        int height = 0;
        scan.set(trunkBase);
        while (height < 50) { // safety limit
            BlockState state = level.getBlockState(scan);
            if (!state.is(BlockTags.LOGS)) break;

            if (height < 2) {
                // Stump: sculk_wood (just 2 blocks tall)
                level.setBlock(scan.immutable(), ModBlocks.SCULK_WOOD.get().defaultBlockState(), SILENT_SET);
            } else {
                // Above stump: delete
                level.setBlock(scan.immutable(), Blocks.AIR.defaultBlockState(), SILENT_SET);
            }
            data.incrementConversions();
            height++;
            scan.move(Direction.UP);
        }

        // Clear ALL leaves AND any stray logs in a wide radius around the entire trunk
        BlockPos trunkTop = new BlockPos(trunkBase.getX(), trunkBase.getY() + height, trunkBase.getZ());
        int leafRadius = 8;
        for (int dx = -leafRadius; dx <= leafRadius; dx++) {
            for (int dy = -3; dy <= leafRadius + 3; dy++) {
                for (int dz = -leafRadius; dz <= leafRadius; dz++) {
                    BlockPos leafPos = trunkTop.offset(dx, dy, dz);
                    if (!level.isLoaded(leafPos)) continue;
                    BlockState leafState = level.getBlockState(leafPos);
                    if (leafState.is(BlockTags.LEAVES) || leafState.is(BlockTags.LOGS)) {
                        level.setBlock(leafPos, Blocks.AIR.defaultBlockState(), SILENT_SET);
                    }
                }
            }
        }
    }

    /**
     * After converting a surface block, scan upward for tree trunks and consume them.
     */
    private void scanAndConsumeTreeAbove(ServerLevel level, BlockPos convertedPos, InfectionSavedData data) {
        BlockPos.MutableBlockPos scan = new BlockPos.MutableBlockPos(convertedPos.getX(), convertedPos.getY(), convertedPos.getZ());
        for (int i = 1; i <= 3; i++) {
            scan.move(Direction.UP);
            if (!level.isLoaded(scan)) return;
            BlockState state = level.getBlockState(scan);
            if (state.is(BlockTags.LOGS)) {
                consumeTree(level, scan.immutable(), data);
                return;
            }
            if (!state.isAir() && !state.is(BlockTags.REPLACEABLE) && !state.is(BlockTags.FLOWERS)) {
                return;
            }
        }
    }

    /**
     * Clear any replaceable blocks sitting on top of a block before converting it.
     */
    private void clearAbove(ServerLevel level, BlockPos pos) {
        BlockPos above = pos.above();
        if (!level.isLoaded(above)) return;
        BlockState aboveState = level.getBlockState(above);

        if (aboveState.is(BlockTags.REPLACEABLE) || aboveState.is(BlockTags.FLOWERS) ||
            aboveState.is(Blocks.SNOW)) {
            level.setBlock(above, Blocks.AIR.defaultBlockState(), SILENT_SET);
            // For double-tall plants
            BlockPos above2 = above.above();
            if (level.isLoaded(above2)) {
                BlockState above2State = level.getBlockState(above2);
                if (above2State.is(BlockTags.REPLACEABLE) || above2State.is(BlockTags.FLOWERS)) {
                    level.setBlock(above2, Blocks.AIR.defaultBlockState(), SILENT_SET);
                }
            }
        }
    }

    private BlockPos findRiverbed(ServerLevel level, BlockPos waterPos) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(waterPos.getX(), waterPos.getY(), waterPos.getZ());
        for (int i = 0; i < 15; i++) {
            mutable.move(Direction.DOWN);
            if (!level.isLoaded(mutable)) return null;
            BlockState state = level.getBlockState(mutable);
            if (!state.is(Blocks.WATER) && !state.isAir()) {
                return mutable.immutable();
            }
        }
        return null;
    }

    /**
     * Seed sculk features ON TOP of converted blocks.
     * Places sensors/catalysts ABOVE the surface block so they sit on solid ground.
     */
    private void seedSculkFeature(ServerLevel level, BlockPos pos, WorldbeastState beast) {
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        if (!aboveState.isAir()) return; // Only place if air above

        // Ensure the block below is solid sculk (it should be, we just converted it)
        BlockState below = level.getBlockState(pos);
        if (below.isAir()) return;

        float roll = RANDOM.nextFloat();
        if (roll < 0.03f) {
            // 3% sculk sensor on top (reduced from 8%)
            level.setBlock(above, Blocks.SCULK_SENSOR.defaultBlockState(), SILENT_SET);
        } else if (roll < 0.06f) {
            // 3% sculk catalyst on top
            level.setBlock(above, Blocks.SCULK_CATALYST.defaultBlockState(), SILENT_SET);
        } else if (roll < 0.07f && beast.getMass() >= 5) {
            // 1% sculk shrieker on top (mass >= 5 — shriekers appear early in the body)
            level.setBlock(above, Blocks.SCULK_SHRIEKER.defaultBlockState(), SILENT_SET);
        } else if (roll < 0.12f) {
            // 5% sculk vein on top — visual variety
            level.setBlock(above, Blocks.SCULK_VEIN.defaultBlockState(), SILENT_SET);
        } else if (roll < 0.30f) {
            // 18% sculk stone — REPLACE the block itself for ground variety
            level.setBlock(pos, ModBlocks.SCULK_STONE.get().defaultBlockState(), SILENT_SET);
        }
    }

    private Direction[] getShuffledDirections() {
        List<Direction> dirs = new ArrayList<>(6);
        dirs.add(Direction.NORTH);
        dirs.add(Direction.SOUTH);
        dirs.add(Direction.EAST);
        dirs.add(Direction.WEST);
        dirs.add(Direction.DOWN);
        dirs.add(Direction.UP); // Always include UP — needed for tree trunk consumption
        Collections.shuffle(dirs, RANDOM);
        return dirs.toArray(new Direction[0]);
    }
}
