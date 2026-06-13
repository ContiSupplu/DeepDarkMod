package com._jackoboy.otherside.infection;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com._jackoboy.otherside.registry.ModBlocks;
import java.util.*;

public class ConversionMap {
    private static final Map<Block, BlockState> DIRECT_MAP = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        // ===== DIRT FAMILY → sculk =====
        DIRECT_MAP.put(Blocks.GRASS_BLOCK, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.DIRT, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.DIRT_PATH, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.COARSE_DIRT, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.PODZOL, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.MYCELIUM, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.ROOTED_DIRT, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.MUD, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.FARMLAND, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.CLAY, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.MOSS_BLOCK, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.MUDDY_MANGROVE_ROOTS, Blocks.SCULK.defaultBlockState());

        // ===== STONE FAMILY → sculk / sculk_stone (30%) =====
        DIRECT_MAP.put(Blocks.STONE, null);      // handled in getConversion
        DIRECT_MAP.put(Blocks.DEEPSLATE, null);   // handled in getConversion
        DIRECT_MAP.put(Blocks.GRANITE, null);
        DIRECT_MAP.put(Blocks.DIORITE, null);
        DIRECT_MAP.put(Blocks.ANDESITE, null);
        DIRECT_MAP.put(Blocks.COBBLESTONE, null);
        DIRECT_MAP.put(Blocks.COBBLED_DEEPSLATE, null);
        DIRECT_MAP.put(Blocks.TUFF, null);
        DIRECT_MAP.put(Blocks.CALCITE, null);
        DIRECT_MAP.put(Blocks.DRIPSTONE_BLOCK, null);
        DIRECT_MAP.put(Blocks.SMOOTH_BASALT, null);
        DIRECT_MAP.put(Blocks.SMOOTH_STONE, null);
        DIRECT_MAP.put(Blocks.STONE_BRICKS, null);
        DIRECT_MAP.put(Blocks.MOSSY_STONE_BRICKS, null);
        DIRECT_MAP.put(Blocks.CRACKED_STONE_BRICKS, null);
        DIRECT_MAP.put(Blocks.POLISHED_DEEPSLATE, null);
        DIRECT_MAP.put(Blocks.DEEPSLATE_BRICKS, null);
        DIRECT_MAP.put(Blocks.DEEPSLATE_TILES, null);
        DIRECT_MAP.put(Blocks.POLISHED_GRANITE, null);
        DIRECT_MAP.put(Blocks.POLISHED_DIORITE, null);
        DIRECT_MAP.put(Blocks.POLISHED_ANDESITE, null);

        // ===== SAND / GRAVEL =====
        DIRECT_MAP.put(Blocks.SAND, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.RED_SAND, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.GRAVEL, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.SUSPICIOUS_SAND, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.SUSPICIOUS_GRAVEL, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.SANDSTONE, null);   // stone variant
        DIRECT_MAP.put(Blocks.RED_SANDSTONE, null);

        // ===== SNOW / ICE =====
        DIRECT_MAP.put(Blocks.SNOW, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.SNOW_BLOCK, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.POWDER_SNOW, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.ICE, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.PACKED_ICE, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.BLUE_ICE, Blocks.SCULK.defaultBlockState());

        // ===== WOOD STRUCTURES → sculk_wood =====
        // Planks
        DIRECT_MAP.put(Blocks.OAK_PLANKS, null);     // handled as wood
        DIRECT_MAP.put(Blocks.SPRUCE_PLANKS, null);
        DIRECT_MAP.put(Blocks.BIRCH_PLANKS, null);
        DIRECT_MAP.put(Blocks.JUNGLE_PLANKS, null);
        DIRECT_MAP.put(Blocks.ACACIA_PLANKS, null);
        DIRECT_MAP.put(Blocks.DARK_OAK_PLANKS, null);
        DIRECT_MAP.put(Blocks.MANGROVE_PLANKS, null);
        DIRECT_MAP.put(Blocks.CHERRY_PLANKS, null);
        DIRECT_MAP.put(Blocks.BAMBOO_PLANKS, null);
        DIRECT_MAP.put(Blocks.CRIMSON_PLANKS, null);
        DIRECT_MAP.put(Blocks.WARPED_PLANKS, null);

        // ===== PLANTS / VEGETATION → AIR (silently consumed) =====
        DIRECT_MAP.put(Blocks.SHORT_GRASS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.TALL_GRASS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.FERN, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.LARGE_FERN, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.DEAD_BUSH, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.SWEET_BERRY_BUSH, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.SUGAR_CANE, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.BAMBOO, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.KELP, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.KELP_PLANT, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.SEAGRASS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.TALL_SEAGRASS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.VINE, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.MOSS_CARPET, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.HANGING_ROOTS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.GLOW_LICHEN, Blocks.AIR.defaultBlockState());

        // ===== FLOWERS → AIR =====
        DIRECT_MAP.put(Blocks.DANDELION, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.POPPY, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.BLUE_ORCHID, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.ALLIUM, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.AZURE_BLUET, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.CORNFLOWER, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.LILY_OF_THE_VALLEY, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.SUNFLOWER, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.LILAC, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.ROSE_BUSH, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.PEONY, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.TORCHFLOWER, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.PITCHER_PLANT, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.LILY_PAD, Blocks.AIR.defaultBlockState());

        // ===== CROPS → AIR =====
        DIRECT_MAP.put(Blocks.WHEAT, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.CARROTS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.POTATOES, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.BEETROOTS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.MELON, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.PUMPKIN, Blocks.SCULK.defaultBlockState());

        // ===== TERRACOTTA / CONCRETE / WOOL → sculk =====
        DIRECT_MAP.put(Blocks.TERRACOTTA, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.WHITE_TERRACOTTA, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.ORANGE_TERRACOTTA, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.YELLOW_TERRACOTTA, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.RED_TERRACOTTA, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.BROWN_TERRACOTTA, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.SCULK.defaultBlockState());

        // ===== FENCES → sculk_wood (structure blocks) =====
        DIRECT_MAP.put(Blocks.OAK_FENCE, null);     // handled as wood
        DIRECT_MAP.put(Blocks.SPRUCE_FENCE, null);
        DIRECT_MAP.put(Blocks.BIRCH_FENCE, null);
        DIRECT_MAP.put(Blocks.JUNGLE_FENCE, null);
        DIRECT_MAP.put(Blocks.ACACIA_FENCE, null);
        DIRECT_MAP.put(Blocks.DARK_OAK_FENCE, null);
        DIRECT_MAP.put(Blocks.MANGROVE_FENCE, null);
        DIRECT_MAP.put(Blocks.CHERRY_FENCE, null);
        DIRECT_MAP.put(Blocks.OAK_FENCE_GATE, null);
        DIRECT_MAP.put(Blocks.SPRUCE_FENCE_GATE, null);
        DIRECT_MAP.put(Blocks.BIRCH_FENCE_GATE, null);
        DIRECT_MAP.put(Blocks.JUNGLE_FENCE_GATE, null);
        DIRECT_MAP.put(Blocks.ACACIA_FENCE_GATE, null);
        DIRECT_MAP.put(Blocks.DARK_OAK_FENCE_GATE, null);

        // ===== STAIRS → AIR (destroyed) =====
        DIRECT_MAP.put(Blocks.OAK_STAIRS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.SPRUCE_STAIRS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.BIRCH_STAIRS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.JUNGLE_STAIRS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.ACACIA_STAIRS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.DARK_OAK_STAIRS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.COBBLESTONE_STAIRS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.STONE_BRICK_STAIRS, Blocks.AIR.defaultBlockState());

        // ===== SLABS → AIR =====
        DIRECT_MAP.put(Blocks.OAK_SLAB, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.SPRUCE_SLAB, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.BIRCH_SLAB, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.JUNGLE_SLAB, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.ACACIA_SLAB, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.DARK_OAK_SLAB, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.COBBLESTONE_SLAB, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.STONE_BRICK_SLAB, Blocks.AIR.defaultBlockState());

        // ===== DOORS / TRAPDOORS → AIR =====
        DIRECT_MAP.put(Blocks.OAK_DOOR, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.SPRUCE_DOOR, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.BIRCH_DOOR, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.JUNGLE_DOOR, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.ACACIA_DOOR, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.DARK_OAK_DOOR, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.IRON_DOOR, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.OAK_TRAPDOOR, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.SPRUCE_TRAPDOOR, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.BIRCH_TRAPDOOR, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.IRON_TRAPDOOR, Blocks.AIR.defaultBlockState());

        // ===== GLASS → AIR (shattered) =====
        DIRECT_MAP.put(Blocks.GLASS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.GLASS_PANE, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.WHITE_STAINED_GLASS, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.WHITE_STAINED_GLASS_PANE, Blocks.AIR.defaultBlockState());

        // ===== WOOL → sculk (soft blocks consumed) =====
        DIRECT_MAP.put(Blocks.WHITE_WOOL, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.RED_WOOL, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.ORANGE_WOOL, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.YELLOW_WOOL, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.BROWN_WOOL, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.LIGHT_GRAY_WOOL, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.GRAY_WOOL, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.BLACK_WOOL, Blocks.SCULK.defaultBlockState());

        // ===== BEDS / CARPETS / SIGNS → AIR =====
        DIRECT_MAP.put(Blocks.WHITE_BED, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.RED_BED, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.WHITE_CARPET, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.RED_CARPET, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.OAK_SIGN, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.OAK_WALL_SIGN, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.SPRUCE_SIGN, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.SPRUCE_WALL_SIGN, Blocks.AIR.defaultBlockState());

        // ===== OTHER STRUCTURE BLOCKS → AIR =====
        DIRECT_MAP.put(Blocks.BOOKSHELF, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.LADDER, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.COBBLESTONE_WALL, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.STONE_BRICK_WALL, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.HAY_BLOCK, Blocks.SCULK.defaultBlockState());
        DIRECT_MAP.put(Blocks.BELL, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.FLOWER_POT, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.COMPOSTER, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.LOOM, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.CARTOGRAPHY_TABLE, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.FLETCHING_TABLE, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.SMITHING_TABLE, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.GRINDSTONE, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.STONECUTTER, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.CAULDRON, Blocks.AIR.defaultBlockState());
        DIRECT_MAP.put(Blocks.LECTERN, Blocks.AIR.defaultBlockState());

    }

    // Blocks that are IMMUNE — never convert
    private static final Set<Block> IMMUNE = new HashSet<>();
    static {
        IMMUNE.addAll(Set.of(
                // Technical / unbreakable
                Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.REINFORCED_DEEPSLATE,
                Blocks.AMETHYST_BLOCK, Blocks.BEDROCK, Blocks.BARRIER,
                Blocks.END_PORTAL_FRAME, Blocks.END_PORTAL, Blocks.NETHER_PORTAL,
                Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK,
                Blocks.STRUCTURE_BLOCK, Blocks.JIGSAW, Blocks.LIGHT,
                // Already sculk
                Blocks.SCULK, Blocks.SCULK_VEIN, Blocks.SCULK_SENSOR,
                Blocks.SCULK_CATALYST, Blocks.SCULK_SHRIEKER,
                // Light sources (player defense)
                Blocks.TORCH, Blocks.WALL_TORCH, Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH,
                Blocks.LANTERN, Blocks.SOUL_LANTERN,
                Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE,
                Blocks.GLOWSTONE, Blocks.SEA_LANTERN,
                Blocks.JACK_O_LANTERN, Blocks.SHROOMLIGHT,
                // Storage (don't destroy player stuff)
                Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST,
                Blocks.BARREL, Blocks.SHULKER_BOX,
                Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER,
                Blocks.CRAFTING_TABLE, Blocks.ENCHANTING_TABLE, Blocks.ANVIL,
                Blocks.BREWING_STAND, Blocks.BEACON,
                // Redstone (game mechanic preservation)
                Blocks.IRON_BLOCK, Blocks.GOLD_BLOCK, Blocks.DIAMOND_BLOCK,
                Blocks.EMERALD_BLOCK, Blocks.NETHERITE_BLOCK, Blocks.COPPER_BLOCK
        ));
    }

    // Blocks that are considered "wood" for conversion purposes
    private static final Set<Block> WOOD_BLOCKS = new HashSet<>(Set.of(
            Blocks.OAK_PLANKS, Blocks.SPRUCE_PLANKS, Blocks.BIRCH_PLANKS,
            Blocks.JUNGLE_PLANKS, Blocks.ACACIA_PLANKS, Blocks.DARK_OAK_PLANKS,
            Blocks.MANGROVE_PLANKS, Blocks.CHERRY_PLANKS, Blocks.BAMBOO_PLANKS,
            Blocks.CRIMSON_PLANKS
    ));
    static {
        WOOD_BLOCKS.addAll(Set.of(
                Blocks.WARPED_PLANKS,
                Blocks.OAK_FENCE, Blocks.SPRUCE_FENCE, Blocks.BIRCH_FENCE,
                Blocks.JUNGLE_FENCE, Blocks.ACACIA_FENCE, Blocks.DARK_OAK_FENCE,
                Blocks.MANGROVE_FENCE, Blocks.CHERRY_FENCE,
                Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE
        ));
        WOOD_BLOCKS.addAll(Set.of(
                Blocks.BIRCH_FENCE_GATE, Blocks.JUNGLE_FENCE_GATE,
                Blocks.ACACIA_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE
        ));
    }

    // All stone-type blocks that should get the sculk_stone chance
    private static final Set<Block> STONE_TYPES = Set.of(
            Blocks.STONE, Blocks.DEEPSLATE, Blocks.GRANITE, Blocks.DIORITE,
            Blocks.ANDESITE, Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE,
            Blocks.TUFF, Blocks.CALCITE, Blocks.DRIPSTONE_BLOCK,
            Blocks.SMOOTH_BASALT, Blocks.SMOOTH_STONE,
            Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS,
            Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILES,
            Blocks.POLISHED_GRANITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_ANDESITE,
            Blocks.SANDSTONE, Blocks.RED_SANDSTONE
    );

    public static boolean isImmune(BlockState state) {
        Block block = state.getBlock();
        if (IMMUNE.contains(block)) return true;
        // Our own mod blocks are immune
        if (block == ModBlocks.SCULK_STONE.get() ||
            block == ModBlocks.SCULK_WOOD.get() ||
            block == ModBlocks.SCULK_MEMBRANE.get() ||
            block == ModBlocks.SCORCHED_EARTH.get() ||
            block == ModBlocks.TENDRIL_BLOCK.get() ||
            block == ModBlocks.TENDRIL_HEART.get() ||
            block == ModBlocks.CHARGED_FRAME.get()) {
            return true;
        }
        return false;
    }

    public static boolean isConvertible(BlockState state) {
        if (isImmune(state)) return false;
        if (state.isAir()) return false;

        Block block = state.getBlock();
        if (DIRECT_MAP.containsKey(block)) return true;
        if (state.is(BlockTags.LOGS)) return true;
        if (state.is(BlockTags.LEAVES)) return true;
        if (state.is(BlockTags.FLOWERS)) return true;
        if (state.is(BlockTags.REPLACEABLE)) return true;
        if (state.is(BlockTags.PLANKS)) return true;
        if (state.is(BlockTags.WOODEN_FENCES)) return true;
        if (state.is(BlockTags.WOODEN_DOORS)) return true;
        if (state.is(BlockTags.WOODEN_STAIRS)) return true;
        if (state.is(BlockTags.WOODEN_SLABS)) return true;
        if (state.is(BlockTags.WOOL)) return true;

        // Fallback: any non-fluid solid block is convertible
        if (!state.getFluidState().isEmpty()) return false;
        return true;
    }

    public static BlockState getConversion(BlockState source) {
        Block block = source.getBlock();

        // === LOGS → sculk_wood ===
        if (source.is(BlockTags.LOGS)) {
            return ModBlocks.SCULK_WOOD.get().defaultBlockState();
        }

        // === LEAVES → sculk_membrane ===
        if (source.is(BlockTags.LEAVES)) {
            return ModBlocks.SCULK_MEMBRANE.get().defaultBlockState();
        }

        // === WOODEN STRUCTURES → sculk_wood ===
        if (WOOD_BLOCKS.contains(block) || source.is(BlockTags.PLANKS) ||
            source.is(BlockTags.WOODEN_FENCES) || source.is(BlockTags.WOODEN_DOORS) ||
            source.is(BlockTags.WOODEN_STAIRS) || source.is(BlockTags.WOODEN_SLABS)) {
            return ModBlocks.SCULK_WOOD.get().defaultBlockState();
        }

        // === REPLACEABLE / flowers → AIR ===
        if (source.is(BlockTags.REPLACEABLE) || source.is(BlockTags.FLOWERS)) {
            return Blocks.AIR.defaultBlockState();
        }

        // === WOOL → sculk (soft blocks consumed) ===
        if (source.is(BlockTags.WOOL)) {
            return Blocks.SCULK.defaultBlockState();
        }

        // === STONE-TYPE → 50% sculk_stone, 50% sculk (more variety!) ===
        if (STONE_TYPES.contains(block)) {
            if (RANDOM.nextFloat() < 0.50f) {
                return ModBlocks.SCULK_STONE.get().defaultBlockState();
            }
            return Blocks.SCULK.defaultBlockState();
        }

        // === Direct map ===
        BlockState result = DIRECT_MAP.get(block);
        if (result != null) return result;

        // === Fallback for any unknown block → sculk ===
        return Blocks.SCULK.defaultBlockState();
    }

    // === NUTRITION TABLE (§2.2) ===
    private static final Map<Block, Float> NUTRITION_TABLE = new HashMap<>();

    // Entity nutrition constants (wired in W3 Maws, not consumed in W1)
    public static final float NUTRITION_ANIMAL = 25.0f;
    public static final float NUTRITION_MONSTER = 8.0f;
    public static final float NUTRITION_VILLAGER = 60.0f;

    static {
        // Populate nutrition values
        // Stone family: 0.02
        for (Block b : List.of(Blocks.STONE, Blocks.DEEPSLATE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.TUFF, Blocks.CALCITE, Blocks.COBBLESTONE, Blocks.COBBLED_DEEPSLATE, Blocks.MOSSY_COBBLESTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS, Blocks.DEEPSLATE_BRICKS, Blocks.DEEPSLATE_TILES, Blocks.POLISHED_DEEPSLATE, Blocks.POLISHED_GRANITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_ANDESITE, Blocks.BRICKS, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.PRISMARINE, Blocks.TERRACOTTA, Blocks.BLACKSTONE, Blocks.BASALT, Blocks.SMOOTH_BASALT, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.OBSIDIAN)) {
            NUTRITION_TABLE.put(b, 0.02f);
        }
        // Dirt family: 0.1
        for (Block b : List.of(Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.ROOTED_DIRT, Blocks.MUD, Blocks.CLAY, Blocks.FARMLAND, Blocks.DIRT_PATH, Blocks.PACKED_MUD, Blocks.MUDDY_MANGROVE_ROOTS)) {
            NUTRITION_TABLE.put(b, 0.1f);
        }
        // Grass/Sand: 0.2
        for (Block b : List.of(Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.GRAVEL, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.MOSS_BLOCK)) {
            NUTRITION_TABLE.put(b, 0.2f);
        }
        // Flowers: 0.5 (small)
        for (Block b : List.of(Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY, Blocks.SUNFLOWER, Blocks.LILAC, Blocks.ROSE_BUSH, Blocks.PEONY, Blocks.WITHER_ROSE, Blocks.TORCHFLOWER, Blocks.PITCHER_PLANT)) {
            NUTRITION_TABLE.put(b, 0.5f);
        }
        // Mushroom/Fungus: 1.0
        for (Block b : List.of(Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.MUSHROOM_STEM, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.CRIMSON_FUNGUS, Blocks.WARPED_FUNGUS, Blocks.CRIMSON_STEM, Blocks.WARPED_STEM, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.SHROOMLIGHT)) {
            NUTRITION_TABLE.put(b, 1.0f);
        }
        // Crops: 2.0
        for (Block b : List.of(Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS, Blocks.MELON, Blocks.PUMPKIN, Blocks.SUGAR_CANE, Blocks.BAMBOO, Blocks.SWEET_BERRY_BUSH, Blocks.CAVE_VINES, Blocks.COCOA)) {
            NUTRITION_TABLE.put(b, 2.0f);
        }
    }

    public static float getNutrition(Block block) {
        Float val = NUTRITION_TABLE.get(block);
        if (val != null) return val;
        // Tag fallbacks
        BlockState state = block.defaultBlockState();
        if (state.is(BlockTags.LOGS)) return 1.5f;
        if (state.is(BlockTags.LEAVES)) return 0.4f;
        if (state.is(BlockTags.PLANKS)) return 0.8f;
        if (state.is(BlockTags.CROPS)) return 2.0f;
        if (state.is(BlockTags.FLOWERS)) return 0.5f;
        return 0.01f; // unknown blocks: negligible nutrition
    }
}
