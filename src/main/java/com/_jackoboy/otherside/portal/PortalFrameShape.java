package com._jackoboy.otherside.portal;

import com._jackoboy.otherside.OthersideMod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Validates whether a clicked frame block is part of a valid portal frame
 * and returns the shape information needed for ignition.
 *
 * Modeled on vanilla's PortalShape but adapted for Otherside's custom
 * frame blocks and tag-based validation system.
 */
public class PortalFrameShape {

    // ── Tags ────────────────────────────────────────────────────────────
    /** Blocks that can serve as portal frame pieces. */
    public static final TagKey<Block> FRAME_TAG =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("otherside", "portal_frame"));

    /** Blocks inside the portal area that can be auto-cleared during ignition. */
    public static final TagKey<Block> CLEARABLE_TAG =
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("otherside", "ignition_clearable"));

    // ── Size constraints ────────────────────────────────────────────────
    /** Minimum interior dimension (width or height) for a valid portal. */
    private static final int MIN_SIZE = 3;
    /** Maximum interior dimension (width or height) for a valid portal. */
    private static final int MAX_SIZE = 32;

    // ── Failure reasons ─────────────────────────────────────────────────
    /**
     * Enumerates the possible reasons a portal frame validation can fail.
     */
    public enum FailureReason {
        /** The clicked block is not a frame block, or no valid plane could be determined. */
        NO_FRAME_PLANE,
        /** The portal interior is smaller than the minimum allowed size. */
        TOO_SMALL,
        /** The portal interior is larger than the maximum allowed size. */
        TOO_BIG,
        /** The ring of frame blocks is not complete — a gap or wrong block was found. */
        RING_INCOMPLETE,
        /** A non-replaceable, non-clearable block is inside the portal interior. */
        INTERIOR_BLOCKED
    }

    // ── Result ──────────────────────────────────────────────────────────
    /**
     * Holds the outcome of a portal frame validation attempt.
     * If {@code valid()} is true the shape info can be used for ignition;
     * otherwise {@code failureReason()} and optionally {@code failureDetail()}
     * describe what went wrong.
     */
    public static class Result {
        private final boolean valid;
        @Nullable private final Direction.Axis axis;
        @Nullable private final BlockPos bottomLeft;
        private final int width;
        private final int height;
        private final List<BlockPos> ringPositions;
        private final List<BlockPos> interiorPositions;
        @Nullable private final FailureReason failureReason;
        @Nullable private final String failureDetail;

        private Result(Direction.Axis axis, BlockPos bottomLeft, int width, int height,
                       List<BlockPos> ringPositions, List<BlockPos> interiorPositions) {
            this.valid = true;
            this.axis = axis;
            this.bottomLeft = bottomLeft;
            this.width = width;
            this.height = height;
            this.ringPositions = Collections.unmodifiableList(ringPositions);
            this.interiorPositions = Collections.unmodifiableList(interiorPositions);
            this.failureReason = null;
            this.failureDetail = null;
        }

        private Result(FailureReason reason, @Nullable String detail) {
            this.valid = false;
            this.axis = null;
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
            this.ringPositions = Collections.emptyList();
            this.interiorPositions = Collections.emptyList();
            this.failureReason = reason;
            this.failureDetail = detail;
        }

        // Accessor methods
        public boolean valid() { return valid; }
        @Nullable public Direction.Axis axis() { return axis; }
        @Nullable public BlockPos bottomLeft() { return bottomLeft; }
        public int width() { return width; }
        public int height() { return height; }
        public List<BlockPos> ringPositions() { return ringPositions; }
        public List<BlockPos> interiorPositions() { return interiorPositions; }
        @Nullable public FailureReason failureReason() { return failureReason; }
        @Nullable public String failureDetail() { return failureDetail; }

        /** Create a successful result. */
        static Result success(Direction.Axis axis, BlockPos bottomLeft, int width, int height,
                              List<BlockPos> ring, List<BlockPos> interior) {
            return new Result(axis, bottomLeft, width, height, ring, interior);
        }

        /** Create a failed result with no extra detail. */
        static Result fail(FailureReason reason) {
            return new Result(reason, null);
        }

        /** Create a failed result with a detail string. */
        static Result fail(FailureReason reason, String detail) {
            return new Result(reason, detail);
        }
    }

    // ── Public API ──────────────────────────────────────────────────────

    /**
     * Attempts to validate a portal frame starting from the block the player clicked.
     *
     * @param level              The world/level accessor.
     * @param clickedFramePos    Position of the frame block the player interacted with.
     * @param autoClearInterior  If true, blocks tagged {@link #CLEARABLE_TAG} inside the
     *                           interior are treated as valid (they will be cleared on ignition).
     * @return A {@link Result} describing whether the frame is valid and, if so, its shape.
     */
    public static Result tryCreate(LevelAccessor level, BlockPos clickedFramePos, boolean autoClearInterior) {
        // Step 1: The clicked block itself must be a frame block.
        if (!isFrame(level, clickedFramePos)) {
            return Result.fail(FailureReason.NO_FRAME_PLANE);
        }

        // Step 2: Try both possible axes. A portal in the XY plane uses axis X;
        //         a portal in the ZY plane uses axis Z.
        for (Direction.Axis axis : new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}) {
            Result result = tryAxis(level, clickedFramePos, axis, autoClearInterior);
            if (result.valid()) {
                return result;
            }
            // If the failure is something other than NO_FRAME_PLANE it means we found
            // a plausible plane but it failed for a concrete reason — report that.
            if (result.failureReason() != FailureReason.NO_FRAME_PLANE) {
                return result;
            }
        }

        // Neither axis produced a valid frame.
        return Result.fail(FailureReason.NO_FRAME_PLANE);
    }

    // ── Per-axis validation ─────────────────────────────────────────────

    /**
     * Attempts to validate the frame on a single axis.
     *
     * For axis X the portal face lies in the XY plane (horizontal travel is along X).
     * For axis Z the portal face lies in the ZY plane (horizontal travel is along Z).
     */
    private static Result tryAxis(LevelAccessor level, BlockPos clicked, Direction.Axis axis,
                                  boolean autoClear) {
        Direction leftDir  = axisToNegative(axis);
        Direction rightDir = axisToPositive(axis);

        OthersideMod.LOGGER.info("[PORTAL] tryAxis={} clicked=[{},{},{}] leftDir={} rightDir={}",
                axis, clicked.getX(), clicked.getY(), clicked.getZ(), leftDir, rightDir);

        // Step 2a: Find interior seed — probe in-plane directions only.
        // Each candidate is validated: the opposite direction must also terminate at a frame block,
        // proving the seed is between two frame boundaries (interior), not outside (exterior).
        BlockPos interiorSeed = findInteriorSeed(level, clicked, leftDir, rightDir, axis);
        if (interiorSeed == null) {
            OthersideMod.LOGGER.info("[PORTAL] tryAxis={}: no valid interior seed found", axis);
            return Result.fail(FailureReason.NO_FRAME_PLANE);
        }
        OthersideMod.LOGGER.info("[PORTAL] tryAxis={}: seed=[{},{},{}]",
                axis, interiorSeed.getX(), interiorSeed.getY(), interiorSeed.getZ());

        // Step 2b: From the interior seed, descend to find the bottom row and go left
        // to find the left edge.
        BlockPos bottomLeft = findBottomLeft(level, interiorSeed, leftDir);
        if (bottomLeft == null) {
            OthersideMod.LOGGER.info("[PORTAL] tryAxis={}: findBottomLeft returned null (exceeded bounds)", axis);
            return Result.fail(FailureReason.NO_FRAME_PLANE);
        }
        OthersideMod.LOGGER.info("[PORTAL] tryAxis={}: bottomLeft=[{},{},{}]",
                axis, bottomLeft.getX(), bottomLeft.getY(), bottomLeft.getZ());

        // Step 2c/d: Measure width (scan right until frame) and height (scan up until frame).
        int width  = measureSpan(level, bottomLeft, rightDir);
        int height = measureSpan(level, bottomLeft, Direction.UP);
        OthersideMod.LOGGER.info("[PORTAL] tryAxis={}: measured {}×{}", axis, width, height);

        // Step 2e: Enforce size constraints.
        if (width < MIN_SIZE || height < MIN_SIZE) {
            OthersideMod.LOGGER.info("[PORTAL] tryAxis={}: TOO_SMALL {}×{} (min {})", axis, width, height, MIN_SIZE);
            return Result.fail(FailureReason.TOO_SMALL);
        }
        if (width > MAX_SIZE || height > MAX_SIZE) {
            return Result.fail(FailureReason.TOO_BIG);
        }

        // Step 2f: Verify the complete ring of frame blocks.
        Result ringCheck = verifyRing(level, bottomLeft, width, height, leftDir, rightDir);
        if (!ringCheck.valid()) {
            OthersideMod.LOGGER.info("[PORTAL] tryAxis={}: ring check failed: {} — {}",
                    axis, ringCheck.failureReason(), ringCheck.failureDetail());
            return ringCheck;
        }

        // Step 2g: Verify the interior — every position must be replaceable or clearable.
        Result interiorCheck = verifyInterior(level, bottomLeft, width, height, rightDir, autoClear);
        if (!interiorCheck.valid()) {
            OthersideMod.LOGGER.info("[PORTAL] tryAxis={}: interior check failed: {} — {}",
                    axis, interiorCheck.failureReason(), interiorCheck.failureDetail());
            return interiorCheck;
        }

        // Step 2h: Build ordered position lists.
        List<BlockPos> ring     = buildRing(clicked, bottomLeft, width, height, leftDir, rightDir);
        List<BlockPos> interior = buildInterior(bottomLeft, width, height, rightDir);

        OthersideMod.LOGGER.info("[PORTAL] tryAxis={}: VALID! {}×{}, ring={}, interior={}",
                axis, width, height, ring.size(), interior.size());
        return Result.success(axis, bottomLeft, width, height, ring, interior);
    }

    // ── Helper: find interior seed ──────────────────────────────────────

    /**
     * From the clicked frame block, probe outward in all 4 in-plane directions
     * to find a valid interior seed. Directions probed: left, right, up, down
     * (all lie within the candidate portal plane).
     *
     * Each candidate seed is validated: we check the opposite direction from the
     * seed to confirm it reaches another frame boundary. This rejects seeds that
     * exit through the frame to the exterior (where the opposite side has no
     * frame block within range).
     *
     * Probe order: UP first (handles bottom-row clicks), then left, right, down.
     */
    @Nullable
    private static BlockPos findInteriorSeed(LevelAccessor level, BlockPos clicked,
                                             Direction leftDir, Direction rightDir,
                                             Direction.Axis axis) {
        // Probe UP first — most likely to find interior when clicking the bottom row.
        // Then left/right (for side column clicks), then down (for top row clicks).
        Direction[] probeDirs = { Direction.UP, leftDir, rightDir, Direction.DOWN };

        for (Direction dir : probeDirs) {
            BlockPos seed = probeForInterior(level, clicked, dir);
            if (seed != null) {
                // Validate: the opposite direction from the seed must also reach a frame block
                // within range. This confirms the seed is interior (between two frame boundaries),
                // not exterior (outside the frame on the far side).
                Direction opposite = dir.getOpposite();
                if (hasFrameBoundary(level, seed, opposite)) {
                    OthersideMod.LOGGER.info("[PORTAL] axis={}: seed accepted via {} at [{},{},{}]",
                            axis, dir, seed.getX(), seed.getY(), seed.getZ());
                    return seed;
                } else {
                    OthersideMod.LOGGER.info("[PORTAL] axis={}: seed rejected via {} at [{},{},{}] — no frame boundary in {} direction",
                            axis, dir, seed.getX(), seed.getY(), seed.getZ(), opposite);
                }
            }
        }
        return null;
    }

    /**
     * Probe outward from a frame block in the given direction, skipping through
     * frame blocks until finding a non-frame passable block (replaceable OR clearable).
     * Returns the position of that block, or null if not found within MAX_SIZE steps.
     */
    @Nullable
    private static BlockPos probeForInterior(LevelAccessor level, BlockPos start, Direction dir) {
        BlockPos pos = start;
        for (int i = 0; i < MAX_SIZE; i++) {
            pos = pos.relative(dir);
            BlockState state = level.getBlockState(pos);
            boolean frame = state.is(FRAME_TAG);
            boolean passable = isPassable(level, pos);
            if (!frame) {
                if (passable) {
                    return pos;
                }
                // Non-frame but not passable = solid obstruction
                return null;
            }
        }
        return null; // All frame blocks within range
    }

    /**
     * Checks whether there is a frame block within MAX_SIZE steps in the given
     * direction from the starting position. Used to validate that a seed is
     * truly interior (bounded by frame on the opposite side).
     */
    private static boolean hasFrameBoundary(LevelAccessor level, BlockPos start, Direction dir) {
        BlockPos pos = start;
        for (int i = 0; i < MAX_SIZE; i++) {
            pos = pos.relative(dir);
            if (isFrame(level, pos)) {
                return true;
            }
            // If we hit a non-passable non-frame block, there's no frame boundary this way.
            if (!isPassable(level, pos)) {
                return false;
            }
        }
        return false;
    }

    // ── Helper: find bottom-left interior corner ────────────────────────

    /**
     * Starting from an interior seed, move down until hitting a frame block
     * (that becomes the bottom edge), then move left until hitting a frame block
     * (that becomes the left edge). The resulting position is the bottom-left
     * interior corner.
     *
     * Returns null if no bounded frame is found (safety limit: MAX_SIZE + 2 steps).
     */
    @Nullable
    private static BlockPos findBottomLeft(LevelAccessor level, BlockPos seed, Direction leftDir) {
        // Descend until the block below is a frame block.
        BlockPos pos = seed;
        int steps = 0;
        while (!isFrame(level, pos.below())) {
            pos = pos.below();
            steps++;
            // Safety: prevent infinite loops
            if (steps > MAX_SIZE + 2 || pos.getY() < level.getMinBuildHeight()) return null;
        }

        // Move left until the block to the left is a frame block.
        steps = 0;
        while (!isFrame(level, pos.relative(leftDir))) {
            pos = pos.relative(leftDir);
            steps++;
            // Safety: prevent infinite loops
            if (steps > MAX_SIZE + 2) return null;
        }

        return pos;
    }

    // ── Helper: measure span ────────────────────────────────────────────

    /**
     * Starting from {@code origin}, counts how many consecutive positions in
     * {@code direction} are NOT frame blocks. Stops at the first frame block
     * or after exceeding MAX_SIZE + 1 (to allow the caller to detect oversized portals).
     */
    private static int measureSpan(LevelAccessor level, BlockPos origin, Direction direction) {
        int count = 0;
        BlockPos pos = origin;
        while (!isFrame(level, pos) && count <= MAX_SIZE + 1) {
            count++;
            pos = pos.relative(direction);
        }
        return count;
    }

    // ── Helper: verify the frame ring ───────────────────────────────────

    /**
     * Checks that every position along the four edges of the frame rectangle
     * is a valid frame block. The frame rectangle surrounds the interior
     * defined by (bottomLeft, width, height).
     *
     * Layout (looking at the portal face):
     * <pre>
     *   F F F F F F   ← top row of frame (y = bottomLeft.y + height)
     *   F . . . . F   ← interior rows
     *   F . . . . F
     *   F . . . . F
     *   F F F F F F   ← bottom row of frame (y = bottomLeft.y - 1)
     * </pre>
     * Where F = frame, . = interior. Left and right columns are one step
     * outside the interior in the horizontal direction.
     */
    private static Result verifyRing(LevelAccessor level, BlockPos bottomLeft,
                                     int width, int height,
                                     Direction leftDir, Direction rightDir) {
        // Positions just outside the interior edges:
        // Bottom-left frame corner = bottomLeft moved one step down and one step left.
        // We check four edges: bottom, top, left column, right column.

        // ── Bottom edge: from (bottomLeft - 1 down, -1 left) to (bottomLeft - 1 down, +width right) ──
        // That's width + 2 blocks (including the two corners).
        BlockPos bottomFrameLeft = bottomLeft.below().relative(leftDir);
        for (int i = 0; i < width + 2; i++) {
            BlockPos p = bottomFrameLeft.relative(rightDir, i);
            if (!isFrame(level, p)) {
                return Result.fail(FailureReason.RING_INCOMPLETE, describeBlock(level, p));
            }
        }

        // ── Top edge: from (bottomLeft + height up, -1 left) to (+width right) ──
        BlockPos topFrameLeft = bottomLeft.above(height).relative(leftDir);
        for (int i = 0; i < width + 2; i++) {
            BlockPos p = topFrameLeft.relative(rightDir, i);
            if (!isFrame(level, p)) {
                return Result.fail(FailureReason.RING_INCOMPLETE, describeBlock(level, p));
            }
        }

        // ── Left column: from (bottomLeft -1 left) upward for 'height' blocks ──
        for (int j = 0; j < height; j++) {
            BlockPos p = bottomLeft.relative(leftDir).above(j);
            if (!isFrame(level, p)) {
                return Result.fail(FailureReason.RING_INCOMPLETE, describeBlock(level, p));
            }
        }

        // ── Right column: from (bottomLeft + width right) upward for 'height' blocks ──
        for (int j = 0; j < height; j++) {
            BlockPos p = bottomLeft.relative(rightDir, width).above(j);
            if (!isFrame(level, p)) {
                return Result.fail(FailureReason.RING_INCOMPLETE, describeBlock(level, p));
            }
        }

        // All ring positions verified.
        return Result.success(null, null, 0, 0, Collections.emptyList(), Collections.emptyList());
    }

    // ── Helper: verify interior ─────────────────────────────────────────

    /**
     * Checks that every interior position is either replaceable (air, water, etc.)
     * or, if autoClear is enabled, tagged as clearable.
     */
    private static Result verifyInterior(LevelAccessor level, BlockPos bottomLeft,
                                         int width, int height, Direction rightDir,
                                         boolean autoClear) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                BlockPos p = bottomLeft.above(y).relative(rightDir, x);
                BlockState state = level.getBlockState(p);

                boolean replaceable = state.canBeReplaced();
                boolean clearable   = autoClear && state.is(CLEARABLE_TAG);

                if (!replaceable && !clearable) {
                    return Result.fail(FailureReason.INTERIOR_BLOCKED, describeBlock(level, p));
                }
            }
        }
        // Dummy success — caller only checks .valid.
        return Result.success(null, null, 0, 0, Collections.emptyList(), Collections.emptyList());
    }

    // ── Helper: build ring positions (clockwise from clicked block) ──────

    /**
     * Builds the full list of frame block positions in clockwise order,
     * starting from the position in the ring nearest to the clicked block.
     *
     * The ring is walked as:
     *   bottom row → right column → top row (reversed) → left column (reversed)
     *
     * After building the full ordered ring we rotate it so the clicked block's
     * position is first.
     */
    private static List<BlockPos> buildRing(BlockPos clicked, BlockPos bottomLeft,
                                            int width, int height,
                                            Direction leftDir, Direction rightDir) {
        List<BlockPos> ring = new ArrayList<>();

        // Bottom row of frame: left corner to right corner (width + 2 positions).
        BlockPos bottomFrameLeft = bottomLeft.below().relative(leftDir);
        for (int i = 0; i < width + 2; i++) {
            ring.add(bottomFrameLeft.relative(rightDir, i));
        }

        // Right column (excluding corners already added): bottom+1 to top-1.
        BlockPos rightCol = bottomLeft.relative(rightDir, width);
        for (int j = 0; j < height; j++) {
            ring.add(rightCol.above(j));
        }

        // Top row of frame: right corner to left corner (reversed, width + 2 positions).
        BlockPos topFrameRight = bottomLeft.above(height).relative(rightDir, width);
        for (int i = 0; i < width + 2; i++) {
            ring.add(topFrameRight.relative(leftDir, i));
        }

        // Left column (excluding corners): top-1 down to bottom+1 (reversed).
        BlockPos leftCol = bottomLeft.relative(leftDir);
        for (int j = height - 1; j >= 0; j--) {
            ring.add(leftCol.above(j));
        }

        // Rotate the list so the clicked block is first.
        int clickedIndex = -1;
        for (int i = 0; i < ring.size(); i++) {
            if (ring.get(i).equals(clicked)) {
                clickedIndex = i;
                break;
            }
        }
        if (clickedIndex > 0) {
            List<BlockPos> rotated = new ArrayList<>(ring.size());
            rotated.addAll(ring.subList(clickedIndex, ring.size()));
            rotated.addAll(ring.subList(0, clickedIndex));
            return rotated;
        }

        return ring;
    }

    // ── Helper: build interior positions ────────────────────────────────

    /**
     * Collects interior positions row by row, bottom to top.
     * Each row is collected left to right. This ordering is the fill order
     * used when placing portal blocks during ignition.
     */
    private static List<BlockPos> buildInterior(BlockPos bottomLeft, int width, int height,
                                                Direction rightDir) {
        List<BlockPos> interior = new ArrayList<>(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                interior.add(bottomLeft.above(y).relative(rightDir, x));
            }
        }
        return interior;
    }

    // ── Block-state utilities ───────────────────────────────────────────

    /** Returns true if the block at {@code pos} is tagged as a valid frame block. */
    private static boolean isFrame(LevelAccessor level, BlockPos pos) {
        return level.getBlockState(pos).is(FRAME_TAG);
    }

    /**
     * Returns true if the block at {@code pos} is "passable" — meaning it is
     * air, replaceable, or tagged as clearable. Used to identify potential
     * interior directions.
     */
    private static boolean isPassable(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.canBeReplaced() || state.is(CLEARABLE_TAG);
    }

    /**
     * Returns a human-readable description of the block at a position,
     * for use in failure detail messages.
     * Format: "minecraft:stone at [10, 64, -20]"
     */
    private static String describeBlock(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        String blockName = state.getBlock().builtInRegistryHolder().key().location().toString();
        return blockName + " at [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
    }

    // ── Axis / direction helpers ────────────────────────────────────────

    /**
     * Returns the Direction pointing in the negative direction along the given axis.
     * For X → WEST, for Z → NORTH.
     */
    private static Direction axisToNegative(Direction.Axis axis) {
        return axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH;
    }

    /**
     * Returns the Direction pointing in the positive direction along the given axis.
     * For X → EAST, for Z → SOUTH.
     */
    private static Direction axisToPositive(Direction.Axis axis) {
        return axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
    }

    // No instantiation — purely static utility class.
    private PortalFrameShape() {}
}
