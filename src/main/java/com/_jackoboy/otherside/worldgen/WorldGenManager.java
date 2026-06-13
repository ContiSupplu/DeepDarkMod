package com._jackoboy.otherside.worldgen;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.infection.BreachData;
import com._jackoboy.otherside.infection.InfectionSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.ChunkPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles world generation on first load:
 * - Locates vanilla ancient cities near spawn
 * - Creates breaches originating from those cities
 * - Carves sculk-filled tunnels from cities to surface
 */
public class WorldGenManager {
    // Tunnel bore radius
    private static final int TUNNEL_RADIUS = 2;
    // Silent setBlock flag
    private static final int SILENT = 2;

    /**
     * Force-load chunks in a radius around a position so setBlock works
     * even if no player is nearby.
     */
    private static void forceLoadArea(ServerLevel level, BlockPos center, int blockRadius) {
        int chunkRadius = (blockRadius >> 4) + 1;
        ChunkPos cp = new ChunkPos(center);
        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                level.getChunk(cp.x + dx, cp.z + dz);
            }
        }
        OthersideMod.LOGGER.info("[WORLDGEN] Force-loaded {}x{} chunks around {}",
                chunkRadius * 2 + 1, chunkRadius * 2 + 1, center);
    }

    /**
     * Called on first world load — locates vanilla ancient cities and creates breaches.
     */
    public static void initializeWorld(ServerLevel level, InfectionSavedData data) {
        if (data.isInitialized()) return;

        BlockPos spawnPos = level.getSharedSpawnPos();
        RandomSource random = level.getRandom();

        OthersideMod.LOGGER.info("[WORLDGEN] Initializing Otherside — locating vanilla ancient cities near spawn: {}", spawnPos);

        // Locate vanilla ancient cities
        List<BlockPos> cityPositions = findAncientCities(level, spawnPos, 4);

        if (cityPositions.isEmpty()) {
            // Fallback: create breaches at random deep positions if no cities found
            OthersideMod.LOGGER.warn("[WORLDGEN] No ancient cities found! Creating fallback breaches.");
            createFallbackBreaches(level, data, spawnPos, random);
        } else {
            OthersideMod.LOGGER.info("[WORLDGEN] Found {} ancient cities", cityPositions.size());

            // Create breaches from each city
            for (int i = 0; i < cityPositions.size(); i++) {
                BlockPos cityPos = cityPositions.get(i);
                boolean isPrimary = (i == 0);

                // Breach originates from the city position
                BlockPos breachOrigin = cityPos;

                // Force-load surface area above city to get accurate heightmap
                BlockPos surfaceCheck = new BlockPos(cityPos.getX(), 0, cityPos.getZ());
                forceLoadArea(level, surfaceCheck, 30);
                int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, cityPos.getX(), cityPos.getZ());
                if (surfaceY <= level.getMinBuildHeight()) surfaceY = 63;

                BreachData breach = new BreachData(breachOrigin);
                breach.setColumnMaxHeight(surfaceY - breachOrigin.getY());
                breach.setSurfaceBreakout(new BlockPos(cityPos.getX(), surfaceY, cityPos.getZ()));
                data.addBreach(breach);

                OthersideMod.LOGGER.info("[WORLDGEN] {} breach at city: origin={}, surface={}",
                        isPrimary ? "Primary" : "Secondary",
                        breachOrigin, breach.getSurfaceBreakout());

                // Carve tunnel from surface to city
                carveTunnel(level, breach.getSurfaceBreakout(), breachOrigin, random);
            }
        }

        data.setInitialized(true);
        data.setDirty();

        DirectorLog.log(level, "WORLDGEN_COMPLETE", spawnPos,
                String.format("Located %d ancient cities, created %d breaches",
                        cityPositions.size(), data.getBreaches().size()));

        OthersideMod.LOGGER.info("[WORLDGEN] Complete! {} breaches created from {} ancient cities",
                data.getBreaches().size(), cityPositions.size());
    }

    /**
     * Find up to 'maxCount' vanilla ancient cities near the spawn position.
     * Uses ServerLevel.findNearestMapStructure() with increasing search radii.
     */
    private static List<BlockPos> findAncientCities(ServerLevel level, BlockPos spawn, int maxCount) {
        List<BlockPos> cities = new ArrayList<>();

        // Get the ancient_city structure tag/holder
        var structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ResourceLocation ancientCityId = ResourceLocation.withDefaultNamespace("ancient_city");
        var structureHolder = structureRegistry.getHolder(
                structureRegistry.getResourceKey(structureRegistry.get(ancientCityId)).orElse(null)
        );

        if (structureHolder == null || structureHolder.isEmpty()) {
            OthersideMod.LOGGER.warn("[WORLDGEN] Could not find ancient_city structure in registry!");
            return cities;
        }

        HolderSet<Structure> holderSet = HolderSet.direct(structureHolder.get());

        // Search in expanding radius, collecting unique cities
        int searchRadius = 50; // chunks (800 blocks)
        int maxSearchRadius = 200; // chunks (3200 blocks)

        while (cities.size() < maxCount && searchRadius <= maxSearchRadius) {
            OthersideMod.LOGGER.info("[WORLDGEN] Searching for ancient cities within {} chunks of spawn...", searchRadius);

            var result = level.getChunkSource().getGenerator().findNearestMapStructure(
                    level, holderSet, spawn, searchRadius, false
            );

            if (result != null) {
                BlockPos foundPos = result.getFirst();
                // Check it's not a duplicate (within 100 blocks of an existing find)
                boolean isDuplicate = false;
                for (BlockPos existing : cities) {
                    if (existing.distSqr(foundPos) < 10000) { // 100 blocks
                        isDuplicate = true;
                        break;
                    }
                }
                if (!isDuplicate) {
                    cities.add(foundPos);
                    OthersideMod.LOGGER.info("[WORLDGEN] Found ancient city #{} at {}", cities.size(), foundPos);
                }
            }

            searchRadius += 50;
        }

        return cities;
    }

    /**
     * Fallback: create breaches at random deep positions if no ancient cities are found.
     */
    private static void createFallbackBreaches(ServerLevel level, InfectionSavedData data,
                                                BlockPos spawn, RandomSource random) {
        int count = 3 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            int dist = 200 + random.nextInt(300);
            double angle = random.nextDouble() * Math.PI * 2;
            int x = spawn.getX() + (int)(Math.cos(angle) * dist);
            int z = spawn.getZ() + (int)(Math.sin(angle) * dist);
            int y = -40 + random.nextInt(10);

            BlockPos origin = new BlockPos(x, y, z);
            forceLoadArea(level, new BlockPos(x, 0, z), 30);
            int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            if (surfaceY <= level.getMinBuildHeight()) surfaceY = 63;

            BreachData breach = new BreachData(origin);
            breach.setColumnMaxHeight(surfaceY - y);
            breach.setSurfaceBreakout(new BlockPos(x, surfaceY, z));
            data.addBreach(breach);

            carveTunnel(level, breach.getSurfaceBreakout(), origin, random);
            OthersideMod.LOGGER.info("[WORLDGEN] Fallback breach #{} at origin={}, surface=({}, {}, {})",
                    i + 1, origin, x, surfaceY, z);
        }
    }

    /**
     * Carve a spiraling tunnel from surface to ancient city.
     * Uses a helix wrapped around a bezier spine for organic winding curves.
     * Includes a visible entrance on the surface.
     */
    private static void carveTunnel(ServerLevel level, BlockPos surface, BlockPos cityOrigin, RandomSource random) {
        OthersideMod.LOGGER.info("[WORLDGEN] Carving spiral tunnel from {} to {}", surface, cityOrigin);

        int boreRadius = 4; // Wide like vanilla cave tunnels

        // === STEP 1: Build visible surface entrance ===
        buildTunnelEntrance(level, surface, random);

        // === STEP 2: Spiral tunnel using helix around bezier spine ===
        double spineStartX = surface.getX();
        double spineStartZ = surface.getZ();
        double spineStartY = surface.getY() - 3;

        double midX = (surface.getX() + cityOrigin.getX()) / 2.0 + (random.nextDouble() - 0.5) * 30;
        double midZ = (surface.getZ() + cityOrigin.getZ()) / 2.0 + (random.nextDouble() - 0.5) * 30;
        double midY = (surface.getY() + cityOrigin.getY()) / 2.0;

        int steps = 400;
        float spiralRadius = 4.0f;
        float spiralSpeed = 0.05f;

        BlockPos lastPos = null;
        int blocksCarved = 0;

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;

            double oneMinusT = 1 - t;
            double spineX = oneMinusT * oneMinusT * spineStartX + 2 * oneMinusT * t * midX + t * t * cityOrigin.getX();
            double spineY = oneMinusT * oneMinusT * spineStartY + 2 * oneMinusT * t * midY + t * t * (cityOrigin.getY() + 6);
            double spineZ = oneMinusT * oneMinusT * spineStartZ + 2 * oneMinusT * t * midZ + t * t * cityOrigin.getZ();

            double helixFade = Math.min(t * 5, 1.0) * Math.min((1 - t) * 5, 1.0);
            double helixR = spiralRadius * helixFade;
            double angle = i * spiralSpeed * Math.PI * 2;
            double offsetX = Math.cos(angle) * helixR;
            double offsetZ = Math.sin(angle) * helixR;

            double x = spineX + offsetX;
            double y = spineY;
            double z = spineZ + offsetZ;

            BlockPos current = new BlockPos((int) x, (int) y, (int) z);
            if (current.equals(lastPos)) continue;
            lastPos = current;

            if (!level.isLoaded(current)) {
                level.getChunk(current);
            }

            for (int dx = -boreRadius; dx <= boreRadius; dx++) {
                for (int dy = -boreRadius; dy <= boreRadius; dy++) {
                    for (int dz = -boreRadius; dz <= boreRadius; dz++) {
                        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                        if (dist > boreRadius + 0.5) continue;

                        BlockPos borePos = current.offset(dx, dy, dz);
                        if (!level.isLoaded(borePos)) continue;

                        BlockState existing = level.getBlockState(borePos);

                        if (dist > boreRadius - 0.8) {
                            if (!existing.isAir()) {
                                float r = random.nextFloat();
                                if (r < 0.45f) {
                                    level.setBlock(borePos, Blocks.SCULK.defaultBlockState(), SILENT);
                                } else if (r < 0.55f) {
                                    level.setBlock(borePos, Blocks.SCULK_VEIN.defaultBlockState(), SILENT);
                                }
                            }
                        } else {
                            level.setBlock(borePos, Blocks.AIR.defaultBlockState(), SILENT);
                        }
                    }
                }
            }

            if (i % 20 == 0) {
                BlockPos floorPos = current.offset(0, -boreRadius, 0);
                if (level.isLoaded(floorPos)) {
                    float r = random.nextFloat();
                    if (r < 0.3f) {
                        level.setBlock(floorPos, Blocks.SCULK_SENSOR.defaultBlockState(), SILENT);
                    } else if (r < 0.5f) {
                        level.setBlock(floorPos, Blocks.SCULK_CATALYST.defaultBlockState(), SILENT);
                    } else {
                        level.setBlock(floorPos, Blocks.SCULK.defaultBlockState(), SILENT);
                    }
                }
            }

            if (i % 40 == 0 && i > 0) {
                BlockPos ceilingPos = current.offset(0, boreRadius, 0);
                if (level.isLoaded(ceilingPos)) {
                    level.setBlock(ceilingPos, Blocks.SOUL_LANTERN.defaultBlockState(), SILENT);
                }
            }

            blocksCarved++;
        }

        OthersideMod.LOGGER.info("[WORLDGEN] Spiral tunnel carved: {} unique positions", blocksCarved);
    }

    /**
     * Build a visible sinkhole entrance on the surface.
     */
    private static void buildTunnelEntrance(ServerLevel level, BlockPos surface, RandomSource random) {
        int entranceRadius = 4;

        for (int dx = -entranceRadius; dx <= entranceRadius; dx++) {
            for (int dz = -entranceRadius; dz <= entranceRadius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > entranceRadius) continue;

                int depth = (int)(3 + (1.0 - dist / entranceRadius) * 4);

                for (int dy = 0; dy >= -depth; dy--) {
                    BlockPos pos = surface.offset(dx, dy, dz);
                    if (!level.isLoaded(pos)) continue;

                    if (dy == -depth) {
                        level.setBlock(pos, Blocks.SCULK.defaultBlockState(), SILENT);
                    } else if (dist > entranceRadius - 1.5) {
                        level.setBlock(pos, Blocks.SCULK.defaultBlockState(), SILENT);
                    } else {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), SILENT);
                    }
                }
            }
        }

        int[][] markers = {{-entranceRadius, 0}, {entranceRadius, 0}, {0, -entranceRadius}, {0, entranceRadius}};
        for (int[] mp : markers) {
            BlockPos marker = surface.offset(mp[0], 1, mp[1]);
            if (level.isLoaded(marker)) {
                level.setBlock(marker, Blocks.SOUL_LANTERN.defaultBlockState(), SILENT);
            }
        }

        OthersideMod.LOGGER.info("[WORLDGEN] Built tunnel entrance at {}", surface);
    }
}
