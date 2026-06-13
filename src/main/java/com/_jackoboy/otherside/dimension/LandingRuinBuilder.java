package com._jackoboy.otherside.dimension;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.registry.ModBlocks;
import com._jackoboy.otherside.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Procedural builder for the landing ruin — the first-arrival spawn point
 * in the Otherside dimension.
 * <p>
 * Generates:
 * <ul>
 *   <li>13×13 sculk_stone platform</li>
 *   <li>3×4 reinforced deepslate portal frame, pre-ignited with otherside_portal</li>
 *   <li>1 chest with 8 echo dust</li>
 *   <li>4 gloom bulb lights around perimeter</li>
 * </ul>
 * <p>
 * This is a first-pass procedural fallback until NBT structure templates arrive.
 */
public class LandingRuinBuilder {

    /** Platform size: 13×13 blocks. */
    private static final int PLATFORM_SIZE = 13;

    /** Half-size for centering calculations. */
    private static final int HALF = PLATFORM_SIZE / 2; // 6

    /**
     * Build the landing ruin at the given X/Z coordinates, finding a safe Y.
     *
     * @param level   the Otherside server level
     * @param centerX the X coordinate for the platform center
     * @param centerZ the Z coordinate for the platform center
     * @return the center BlockPos of the platform (player spawn point, 1 above platform)
     */
    public static BlockPos build(ServerLevel level, int centerX, int centerZ) {
        int platformY = findSafeY(level, centerX, centerZ);
        BlockPos center = new BlockPos(centerX, platformY, centerZ);

        OthersideMod.LOGGER.info("Building landing ruin at {} (Y={})", center, platformY);

        // 1. Build the 13×13 sculk_stone platform
        buildPlatform(level, center, platformY);

        // 2. Clear air space above the platform (5 blocks high for the portal + headroom)
        clearAbove(level, center, platformY);

        // 3. Build the 3×4 portal frame (reinforced deepslate) and ignite it
        buildPortalFrame(level, center, platformY);

        // 4. Place a chest with starter items
        placeChest(level, center, platformY);

        // 5. Place 4 gloom bulb lights around the perimeter
        placeGloomBulbs(level, center, platformY);

        return center;
    }

    /**
     * Find a safe Y level for the platform. Scans down from Y=120 for the first
     * solid block, then places the platform there. Falls back to Y=100 if nothing found.
     */
    private static int findSafeY(ServerLevel level, int x, int z) {
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos(x, 120, z);
        for (int y = 120; y > 40; y--) {
            probe.setY(y);
            BlockState state = level.getBlockState(probe);
            if (!state.isAir() && !state.liquid()) {
                // Place platform on top of this solid block
                return y + 1;
            }
        }
        // Fallback: build at Y=100 in the void
        return 100;
    }

    /**
     * Build the 13×13 sculk_stone platform.
     */
    private static void buildPlatform(ServerLevel level, BlockPos center, int y) {
        BlockState sculkStone = ModBlocks.SCULK_STONE.get().defaultBlockState();
        for (int dx = -HALF; dx <= HALF; dx++) {
            for (int dz = -HALF; dz <= HALF; dz++) {
                level.setBlock(new BlockPos(center.getX() + dx, y, center.getZ() + dz),
                        sculkStone, 3);
            }
        }
    }

    /**
     * Clear 6 blocks of air above the platform to ensure space for portal + player.
     */
    private static void clearAbove(ServerLevel level, BlockPos center, int y) {
        for (int dy = 1; dy <= 6; dy++) {
            for (int dx = -HALF; dx <= HALF; dx++) {
                for (int dz = -HALF; dz <= HALF; dz++) {
                    BlockPos pos = new BlockPos(center.getX() + dx, y + dy, center.getZ() + dz);
                    if (!level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    /**
     * Build a 3-wide × 4-tall portal frame from reinforced deepslate and fill interior
     * with otherside_portal blocks. Frame is aligned on the X axis (portal faces Z).
     *
     * <pre>
     * Frame layout (looking along Z):
     *   F F F
     *   F P F
     *   F P F
     *   F F F
     * F = frame block (reinforced deepslate)
     * P = portal block (otherside_portal, axis=X)
     * </pre>
     *
     * The frame sits 2 blocks north of center, on top of the platform.
     */
    private static void buildPortalFrame(ServerLevel level, BlockPos center, int platformY) {
        int frameBaseY = platformY + 1; // Frame sits on top of platform
        int frameX = center.getX();
        int frameZ = center.getZ() - 2; // Offset north so it's not right on the player

        BlockState frame = Blocks.REINFORCED_DEEPSLATE.defaultBlockState();
        BlockState portal = ModBlocks.OTHERSIDE_PORTAL.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X);

        // Build the 3-wide × 4-tall frame
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 0; dy < 4; dy++) {
                BlockPos pos = new BlockPos(frameX + dx, frameBaseY + dy, frameZ);
                boolean isEdge = (dx == -1 || dx == 1 || dy == 0 || dy == 3);
                if (isEdge) {
                    level.setBlock(pos, frame, 3);
                } else {
                    // Interior — portal block
                    level.setBlock(pos, portal, 3);
                }
            }
        }
    }

    /**
     * Place a chest 2 blocks south of center containing 8 echo dust.
     */
    private static void placeChest(ServerLevel level, BlockPos center, int platformY) {
        BlockPos chestPos = new BlockPos(center.getX(), platformY + 1, center.getZ() + 2);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);

        // Try to insert items into the chest
        if (level.getBlockEntity(chestPos) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(ModItems.ECHO_DUST.get(), 8));
        }
    }

    /**
     * Place 4 gloom bulb lights at the corners of the platform (on top of platform).
     */
    private static void placeGloomBulbs(ServerLevel level, BlockPos center, int platformY) {
        BlockState gloomBulb = ModBlocks.GLOOM_BULB.get().defaultBlockState();
        int[][] offsets = {
                { HALF,  HALF},
                { HALF, -HALF},
                {-HALF,  HALF},
                {-HALF, -HALF}
        };
        for (int[] off : offsets) {
            BlockPos pos = new BlockPos(
                    center.getX() + off[0],
                    platformY + 1,
                    center.getZ() + off[1]);
            level.setBlock(pos, gloomBulb, 3);
        }
    }
}
