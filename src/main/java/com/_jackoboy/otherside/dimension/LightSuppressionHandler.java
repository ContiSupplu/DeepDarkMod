package com._jackoboy.otherside.dimension;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.block.ExtinguishedWallTorchBlock;
import com._jackoboy.otherside.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Handles light suppression in the Otherside dimension.
 *
 * <p>When a player places a light-emitting block tagged {@code #otherside:suppressed_lights}
 * in the Otherside, it is immediately replaced with an extinguished variant (custom block
 * or vanilla unlit state) plus smoke/fizzle effects.</p>
 *
 * <p>Fire blocks (fire, soul_fire) are allowed to burn for ~2 seconds before being
 * snuffed out, tracked via a position→game-time map.</p>
 */
@EventBusSubscriber(modid = OthersideMod.MOD_ID)
public class LightSuppressionHandler {

    /** Tag for blocks that should be suppressed on placement in the Otherside. */
    private static final TagKey<Block> SUPPRESSED_TAG = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("otherside", "suppressed_lights"));

    /** Maps fire block positions to the game time they were placed. */
    private static final Map<BlockPos, Long> fireTracker = new HashMap<>();

    /** How long fire is allowed to burn before being extinguished (40 ticks = 2 seconds). */
    private static final int FIRE_LIFESPAN = 40;

    // ── Block-place event ───────────────────────────────────────────────────────

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) return;

        BlockState placed = event.getPlacedBlock();
        BlockPos pos = event.getPos();

        // ── Fire rule: let it burn briefly, then die ──
        if (placed.is(Blocks.FIRE) || placed.is(Blocks.SOUL_FIRE)) {
            fireTracker.put(pos.immutable(), level.getGameTime());
            return; // Don't suppress immediately — the fire tracker will handle it
        }

        // ── Light suppression: only tagged blocks ──
        if (!placed.is(SUPPRESSED_TAG)) return;

        BlockState replacement = getExtinguishedVariant(placed);
        if (replacement != null) {
            level.setBlock(pos, replacement, Block.UPDATE_ALL);

            // Fizzle sound
            level.playSound(null, pos,
                    SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,
                    0.5F, 1.2F);

            // Smoke particles
            level.sendParticles(ParticleTypes.SMOKE,
                    pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5,
                    5, 0.2, 0.2, 0.2, 0.01);
        }
    }

    // ── Extinguished variant mapping ────────────────────────────────────────────

    /**
     * Returns the extinguished block state for the given placed state, or null
     * if no mapping exists.
     *
     * <p>Priority order:
     * <ol>
     *   <li>Exact block → custom extinguished block (torches, lanterns)</li>
     *   <li>Jack-o-lantern → carved pumpkin (preserve facing)</li>
     *   <li>Any block with a {@code lit} property → same state with {@code lit=false}
     *       (campfires, candles)</li>
     * </ol>
     */
    private static BlockState getExtinguishedVariant(BlockState state) {
        Block block = state.getBlock();

        // Standing torches
        if (block == Blocks.TORCH) {
            return ModBlocks.EXTINGUISHED_TORCH.get().defaultBlockState();
        }
        if (block == Blocks.SOUL_TORCH) {
            return ModBlocks.EXTINGUISHED_SOUL_TORCH.get().defaultBlockState();
        }

        // Wall torches — preserve FACING
        if (block == Blocks.WALL_TORCH) {
            return ModBlocks.EXTINGUISHED_WALL_TORCH.get().defaultBlockState()
                    .setValue(ExtinguishedWallTorchBlock.FACING,
                            state.getValue(WallTorchBlock.FACING));
        }
        if (block == Blocks.SOUL_WALL_TORCH) {
            return ModBlocks.EXTINGUISHED_SOUL_WALL_TORCH.get().defaultBlockState()
                    .setValue(ExtinguishedWallTorchBlock.FACING,
                            state.getValue(WallTorchBlock.FACING));
        }

        // Lanterns — preserve HANGING
        if (block == Blocks.LANTERN || block == Blocks.SOUL_LANTERN) {
            return ModBlocks.EXTINGUISHED_LANTERN.get().defaultBlockState()
                    .setValue(BlockStateProperties.HANGING,
                            state.getValue(BlockStateProperties.HANGING));
        }

        // Jack-o-lantern → carved pumpkin — preserve HORIZONTAL_FACING
        if (block == Blocks.JACK_O_LANTERN) {
            return Blocks.CARVED_PUMPKIN.defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING,
                            state.getValue(BlockStateProperties.HORIZONTAL_FACING));
        }

        // Generic fallback: any block with a LIT property (campfires, candles, etc.)
        if (state.hasProperty(BlockStateProperties.LIT)) {
            return state.setValue(BlockStateProperties.LIT, false);
        }

        return null;
    }

    // ── Fire tracker tick ───────────────────────────────────────────────────────

    /**
     * Called from the server tick handler. Removes tracked fire blocks that have
     * exceeded their lifespan. Plays extinguish sound and spawns large smoke.
     *
     * @param level The server level being ticked (must be the Otherside)
     */
    public static void tickFireTracker(ServerLevel level) {
        if (!level.dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) return;
        if (fireTracker.isEmpty()) return;

        long now = level.getGameTime();
        Iterator<Map.Entry<BlockPos, Long>> it = fireTracker.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<BlockPos, Long> entry = it.next();

            if (now - entry.getValue() >= FIRE_LIFESPAN) {
                BlockPos pos = entry.getKey();
                BlockState current = level.getBlockState(pos);

                // Only extinguish if the block is still fire
                if (current.is(Blocks.FIRE) || current.is(Blocks.SOUL_FIRE)) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

                    level.playSound(null, pos,
                            SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,
                            0.4F, 1.5F);

                    level.sendParticles(ParticleTypes.LARGE_SMOKE,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            8, 0.3, 0.3, 0.3, 0.02);
                }

                it.remove();
            }
        }
    }

    /**
     * Clears the fire tracker. Should be called on server stop or dimension unload
     * to prevent stale data across world reloads.
     */
    public static void clearFireTracker() {
        fireTracker.clear();
    }
}
