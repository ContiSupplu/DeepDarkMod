package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.worldgen.WorldGenManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

public class BreachManager {
    private static final int COLUMN_BLOCKS_PER_TICK = 2;
    private static final int SILENT = 2;

    /**
     * Initialize the world on first load — delegates to WorldGenManager.
     */
    public static void initializePrimaryBreach(ServerLevel level, InfectionSavedData data) {
        if (data.isInitialized()) return;
        // Full world gen: ancient cities, tunnels, breaches
        WorldGenManager.initializeWorld(level, data);
    }

    /**
     * Grow breach columns each tick — sculk climbs from city to surface.
     */
    public static void tickColumns(ServerLevel level, InfectionSavedData data) {
        for (BreachData breach : data.getBreaches()) {
            if (!breach.isActive() || breach.isColumnComplete()) continue;

            int progress = breach.getColumnProgress();
            int maxHeight = breach.getColumnMaxHeight();

            if (progress >= maxHeight) {
                breach.setColumnComplete(true);
                seedSurfaceFrontier(level, breach);
                DirectorLog.log(level, "BREACH_COLUMN_COMPLETE", breach.getSurfaceBreakout(), "Breach column reached surface");
                data.setDirty();
                continue;
            }

            BlockPos base = breach.getCityOrigin();
            for (int i = 0; i < COLUMN_BLOCKS_PER_TICK; i++) {
                int y = base.getY() + progress + i;
                if (y >= base.getY() + maxHeight) break;

                BlockPos columnPos = new BlockPos(base.getX(), y, base.getZ());
                if (level.isLoaded(columnPos)) {
                    level.setBlock(columnPos, Blocks.SCULK.defaultBlockState(), SILENT);
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dz == 0) continue;
                            BlockPos veinPos = columnPos.offset(dx, 0, dz);
                            if (level.isLoaded(veinPos) && level.getBlockState(veinPos).isAir()) {
                                if (level.getRandom().nextFloat() < 0.3f) {
                                    level.setBlock(veinPos, Blocks.SCULK_VEIN.defaultBlockState(), SILENT);
                                }
                            }
                        }
                    }
                }
            }

            breach.setColumnProgress(progress + COLUMN_BLOCKS_PER_TICK);
            data.setDirty();
        }
    }

    private static void seedSurfaceFrontier(ServerLevel level, BreachData breach) {
        BlockPos surface = breach.getSurfaceBreakout();
        if (surface == null) {
            OthersideMod.LOGGER.warn("[BREACH] seedSurfaceFrontier: surfaceBreakout is NULL!");
            return;
        }

        OthersideMod.LOGGER.info("[BREACH] Seeding frontier around surface breakout: {}", surface);

        int seeded = 0;
        int notConvertible = 0;
        int notLoaded = 0;

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                BlockPos pos = surface.offset(dx, 0, dz);
                if (!level.isLoaded(pos)) {
                    notLoaded++;
                    continue;
                }

                int surfY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
                BlockPos surfacePos = null;
                net.minecraft.world.level.block.state.BlockState state = null;
                for (int dy = 0; dy < 5; dy++) {
                    BlockPos candidate = new BlockPos(pos.getX(), surfY - 1 - dy, pos.getZ());
                    net.minecraft.world.level.block.state.BlockState candidateState = level.getBlockState(candidate);
                    if (!candidateState.isAir() && ConversionMap.isConvertible(candidateState)) {
                        surfacePos = candidate;
                        state = candidateState;
                        break;
                    }
                }
                if (surfacePos == null) {
                    surfacePos = new BlockPos(pos.getX(), surfY - 1, pos.getZ());
                    state = level.getBlockState(surfacePos);
                }

                if (ConversionMap.isConvertible(state)) {
                    net.minecraft.world.level.block.state.BlockState newState = ConversionMap.getConversion(state);
                    level.setBlock(surfacePos, newState, SILENT);
                    breach.getFrontier().add(surfacePos.asLong());
                    seeded++;
                } else {
                    notConvertible++;
                    if (notConvertible <= 3) {
                        OthersideMod.LOGGER.info("[BREACH] Not convertible at {}: {}",
                                surfacePos, net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(state.getBlock()));
                    }
                }
            }
        }

        OthersideMod.LOGGER.info("[BREACH] Frontier seeded: {} converted, {} not convertible, {} not loaded, frontier={}",
                seeded, notConvertible, notLoaded, breach.getFrontier().size());
    }

    /**
     * Create an artificial breach at a specified location (for /otherside spawnbreach command).
     */
    public static void createArtificialBreach(ServerLevel level, InfectionSavedData data, BlockPos pos) {
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
        BlockPos origin = new BlockPos(pos.getX(), Math.min(pos.getY(), surfaceY - 10), pos.getZ());
        BreachData breach = new BreachData(origin);
        breach.setColumnMaxHeight(surfaceY - origin.getY());
        breach.setSurfaceBreakout(new BlockPos(pos.getX(), surfaceY, pos.getZ()));
        breach.setColumnComplete(true);
        breach.setColumnProgress(breach.getColumnMaxHeight());
        data.addBreach(breach);

        OthersideMod.LOGGER.info("[BREACH] Creating artificial breach: origin={}, surfaceY={}", origin, surfaceY);

        seedSurfaceFrontier(level, breach);
        data.setDirty();
        DirectorLog.log(level, "BREACH_SPAWNED", origin, "Artificial breach via command");
    }
}
