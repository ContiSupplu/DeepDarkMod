package com._jackoboy.otherside.client;

import com._jackoboy.otherside.network.BreachBorderPayload;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client-side cache for breach border columns received from the server.
 * Uses spatial hashing by chunk cell (x>>4, z>>4) for fast proximity lookups.
 */
public class ClientBreachBorderCache {

    private static final int RAIN_BAND_RADIUS = 30;
    private static final int RAIN_BAND_RADIUS_SQ = RAIN_BAND_RADIUS * RAIN_BAND_RADIUS;

    // All frontier columns as (x, z) pairs stored in a flat array for iteration
    private static int[] columnsX = new int[0];
    private static int[] columnsZ = new int[0];
    private static int columnCount = 0;

    // Spatial hash: key = cellKey(x>>4, z>>4), value = list of indices into columns arrays
    private static final Long2ObjectOpenHashMap<int[]> spatialHash = new Long2ObjectOpenHashMap<>();

    public static void handle(BreachBorderPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            long[] packed = payload.packedColumns();
            int count = packed.length;

            int[] newX = new int[count];
            int[] newZ = new int[count];

            Long2ObjectOpenHashMap<LongList> tempHash = new Long2ObjectOpenHashMap<>();

            for (int i = 0; i < count; i++) {
                int x = BlockPos.getX(packed[i]);
                int z = BlockPos.getZ(packed[i]);
                newX[i] = x;
                newZ[i] = z;

                long cell = cellKey(x >> 4, z >> 4);
                tempHash.computeIfAbsent(cell, k -> new LongArrayList()).add(i);
            }

            // Convert temp hash to final form with int[] values
            spatialHash.clear();
            for (Long2ObjectMap.Entry<LongList> entry : tempHash.long2ObjectEntrySet()) {
                LongList indices = entry.getValue();
                int[] arr = new int[indices.size()];
                for (int i = 0; i < indices.size(); i++) {
                    arr[i] = (int) indices.getLong(i);
                }
                spatialHash.put(entry.getLongKey(), arr);
            }

            columnsX = newX;
            columnsZ = newZ;
            columnCount = count;
        });
    }

    /**
     * Returns true if the given world position (x, z) is within RAIN_BAND_RADIUS blocks
     * of any frontier column.
     */
    public static boolean inRainBand(double x, double z) {
        return distSqToBorder(x, z) <= RAIN_BAND_RADIUS_SQ;
    }

    /**
     * Returns the squared 2D distance from (x, z) to the nearest frontier column.
     * Returns Double.MAX_VALUE if no frontier columns exist.
     */
    public static double distSqToBorder(double x, double z) {
        return distSqToBorderRange(x, z, RAIN_BAND_RADIUS);
    }

    /**
     * Returns the squared 2D distance from (x, z) to the nearest frontier column,
     * searching within the specified maxRange blocks.
     * Returns Double.MAX_VALUE if none found.
     */
    public static double distSqToBorderRange(double x, double z, int maxRange) {
        if (columnCount == 0) return Double.MAX_VALUE;

        int cellRadius = (maxRange >> 4) + 1;
        int centerCellX = ((int) Math.floor(x)) >> 4;
        int centerCellZ = ((int) Math.floor(z)) >> 4;

        double bestSq = Double.MAX_VALUE;

        for (int cx = centerCellX - cellRadius; cx <= centerCellX + cellRadius; cx++) {
            for (int cz = centerCellZ - cellRadius; cz <= centerCellZ + cellRadius; cz++) {
                long cell = cellKey(cx, cz);
                int[] indices = spatialHash.get(cell);
                if (indices == null) continue;

                for (int idx : indices) {
                    double dx = x - (columnsX[idx] + 0.5);
                    double dz = z - (columnsZ[idx] + 0.5);
                    double sq = dx * dx + dz * dz;
                    if (sq < bestSq) {
                        bestSq = sq;
                    }
                }
            }
        }
        return bestSq;
    }

    /**
     * Returns the number of frontier columns currently cached.
     */
    public static int getColumnCount() {
        return columnCount;
    }

    /**
     * Returns the X coordinate of the frontier column at the given index.
     */
    public static int getColumnX(int index) {
        return columnsX[index];
    }

    /**
     * Returns the Z coordinate of the frontier column at the given index.
     */
    public static int getColumnZ(int index) {
        return columnsZ[index];
    }

    private static long cellKey(int cellX, int cellZ) {
        return ((long) cellX & 0xFFFFFFFFL) | (((long) cellZ & 0xFFFFFFFFL) << 32);
    }
}
