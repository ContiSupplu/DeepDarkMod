package com._jackoboy.otherside.registry;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.block.EchoAnchorBlock;
import com._jackoboy.otherside.block.EchoLanternBlock;
import com._jackoboy.otherside.block.OthersidePortalBlock;
import com._jackoboy.otherside.block.ExtinguishedTorchBlock;
import com._jackoboy.otherside.block.ExtinguishedWallTorchBlock;
import com._jackoboy.otherside.block.ExtinguishedLanternBlock;
import com._jackoboy.otherside.block.EchoFluidBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(OthersideMod.MOD_ID);
    public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(OthersideMod.MOD_ID);

    // Infection blocks
    public static final DeferredBlock<Block> SCULK_STONE = registerBlock("sculk_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> SCULK_WOOD = registerBlock("sculk_wood",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(2.0F)
                    .sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> SCULK_MEMBRANE = registerBlock("sculk_membrane",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(0.5F)
                    .sound(SoundType.SCULK)
                    .noOcclusion()));

    public static final DeferredBlock<Block> SCORCHED_EARTH = registerBlock("scorched_earth",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT)
                    .strength(0.5F)
                    .sound(SoundType.GRAVEL)));

    public static final DeferredBlock<Block> TENDRIL_BLOCK = registerBlock("tendril_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(1.5F)
                    .sound(SoundType.SCULK)
                    .noOcclusion()));

    public static final DeferredBlock<Block> TENDRIL_HEART = registerBlock("tendril_heart",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.0F)
                    .sound(SoundType.SCULK)
                    .lightLevel(state -> 5)));

    // Vein cord — nervous system (W2). BlockItem exists (silk touch) but NOT in creative tab.
    public static final DeferredBlock<Block> SCULK_VEIN_CORD = BLOCKS.register("sculk_vein_cord",
            () -> new com._jackoboy.otherside.block.SculkVeinCordBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.SCULK)
                    .lightLevel(state -> state.getValue(
                            com._jackoboy.otherside.block.SculkVeinCordBlock.CHARGED) ? 5 : 1)
                    .pushReaction(PushReaction.DESTROY)));
    // BlockItem registered separately — director obtains via /give, not creative tab
    public static final DeferredItem<BlockItem> SCULK_VEIN_CORD_ITEM = BLOCK_ITEMS.register("sculk_vein_cord",
            () -> new BlockItem(SCULK_VEIN_CORD.get(), new Item.Properties()));

    // Portal blocks
    public static final DeferredBlock<Block> CHARGED_FRAME = registerBlock("charged_frame",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(55.0F, 1200.0F)
                    .lightLevel(state -> 7)
                    .noLootTable()
                    .sound(SoundType.DEEPSLATE)));

    // Portal block (no block item — portals are not holdable)
    public static final DeferredBlock<Block> OTHERSIDE_PORTAL = BLOCKS.register("otherside_portal",
            () -> new OthersidePortalBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .strength(-1.0F)
                    .sound(SoundType.SCULK)
                    .lightLevel(state -> 11)
                    .noLootTable()
                    .pushReaction(PushReaction.BLOCK)));

    // Otherside flora
    public static final DeferredBlock<Block> GLOOM_BULB = registerBlock("gloom_bulb",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .noCollission()
                    .instabreak()
                    .lightLevel(state -> 7)
                    .sound(SoundType.SPORE_BLOSSOM)));

    public static final DeferredBlock<Block> ECHO_ANEMONE = registerBlock("echo_anemone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .noCollission()
                    .instabreak()
                    .lightLevel(state -> 3)
                    .sound(SoundType.SPORE_BLOSSOM)));

    // ── Extinguished light blocks (dimension suppression) ──

    public static final DeferredBlock<Block> EXTINGUISHED_TORCH = registerBlock("extinguished_torch",
            () -> new ExtinguishedTorchBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.WOOD), false));

    public static final DeferredBlock<Block> EXTINGUISHED_WALL_TORCH = BLOCKS.register("extinguished_wall_torch",
            () -> new ExtinguishedWallTorchBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.WOOD)
                    .lootFrom(EXTINGUISHED_TORCH), false));

    public static final DeferredBlock<Block> EXTINGUISHED_SOUL_TORCH = registerBlock("extinguished_soul_torch",
            () -> new ExtinguishedTorchBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.WOOD), true));

    public static final DeferredBlock<Block> EXTINGUISHED_SOUL_WALL_TORCH = BLOCKS.register("extinguished_soul_wall_torch",
            () -> new ExtinguishedWallTorchBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.WOOD)
                    .lootFrom(EXTINGUISHED_SOUL_TORCH), true));

    public static final DeferredBlock<Block> EXTINGUISHED_LANTERN = registerBlock("extinguished_lantern",
            () -> new ExtinguishedLanternBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .noOcclusion()));

    // ── Echo Fluid (v1: non-flowing block) ──

    public static final DeferredBlock<Block> ECHO_FLUID = BLOCKS.register("echo_fluid",
            () -> new EchoFluidBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .noCollission()
                    .noOcclusion()
                    .strength(100.0F)
                    .noLootTable()
                    .sound(SoundType.EMPTY)
                    .pushReaction(PushReaction.BLOCK)));

    // ── Echo Anchor (respawn in the Otherside) ──

    public static final DeferredBlock<Block> ECHO_ANCHOR = registerBlock("echo_anchor",
            () -> new EchoAnchorBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(30.0F, 1200.0F)
                    .lightLevel(state -> state.getValue(EchoAnchorBlock.CHARGED) ? 7 : 0)
                    .sound(SoundType.SCULK_CATALYST)));

    // ── Echo Lantern (fuel-based, Otherside-only light source) ──

    public static final DeferredBlock<Block> ECHO_LANTERN_BLOCK = registerBlock("echo_lantern",
            () -> new EchoLanternBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.5F)
                    .sound(SoundType.LANTERN)
                    .noOcclusion()
                    .lightLevel(EchoLanternBlock::getLightEmission)));

    // ════════════════════════════════════════════════════════════════════
    // ── ECHO DIMENSION BLOCKS ──────────────────────────────────────────
    // ════════════════════════════════════════════════════════════════════

    // ── Terrain ──

    /** Sculk grass — echo dimension surface block (3-texture top/side/bottom, self-lit). */
    public static final DeferredBlock<Block> SCULK_GRASS = registerBlock("sculk_grass",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(0.6F)
                    .sound(SoundType.SCULK)
                    .lightLevel(state -> 5)));  // soft self-glow for the sculk ground

    /** Sculk dirt — echo dimension sub-surface. */
    public static final DeferredBlock<Block> SCULK_DIRT = registerBlock("sculk_dirt",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(0.5F)
                    .sound(SoundType.SCULK)));

    /** Sculk gravel — echo dimension shoreline/underwater block. */
    public static final DeferredBlock<Block> SCULK_GRAVEL = registerBlock("sculk_gravel",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(0.6F)
                    .sound(SoundType.SCULK)));

    /** Sculk leaves — echo dimension foliage (no decay, non-occluding, faint glow). */
    public static final DeferredBlock<Block> SCULK_LEAVES = registerBlock("sculk_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(0.2F)
                    .sound(SoundType.SCULK_CATALYST)
                    .noOcclusion()
                    .isSuffocating((s, g, p) -> false)
                    .isViewBlocking((s, g, p) -> false)
                    .ignitedByLava()
                    .lightLevel(state -> 2)));  // very subtle foliage glow

    // ── Echo Wood Set (non-flammable) ──

    /** Echo log — rotatable pillar (the echo dimension's wood). */
    public static final DeferredBlock<Block> ECHO_LOG = registerBlock("echo_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(2.0F)
                    .sound(SoundType.SCULK_CATALYST)));

    /** Stripped echo log. */
    public static final DeferredBlock<Block> STRIPPED_ECHO_LOG = registerBlock("stripped_echo_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(2.0F)
                    .sound(SoundType.SCULK_CATALYST)));

    /** Echo wood — bark on all sides (new echo wood type). */
    public static final DeferredBlock<Block> ECHO_WOOD_BLOCK = registerBlock("echo_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(2.0F)
                    .sound(SoundType.SCULK_CATALYST)));

    /** Stripped echo wood. */
    public static final DeferredBlock<Block> STRIPPED_ECHO_WOOD = registerBlock("stripped_echo_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(2.0F)
                    .sound(SoundType.SCULK_CATALYST)));

    /** Echo planks. */
    public static final DeferredBlock<Block> ECHO_PLANKS = registerBlock("echo_planks",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.SCULK_CATALYST)));

    /** Echo stairs. */
    public static final DeferredBlock<Block> ECHO_STAIRS = registerBlock("echo_stairs",
            () -> new StairBlock(ECHO_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(ECHO_PLANKS.get())));

    /** Echo slab. */
    public static final DeferredBlock<Block> ECHO_SLAB = registerBlock("echo_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(ECHO_PLANKS.get())));

    /** Echo fence. */
    public static final DeferredBlock<Block> ECHO_FENCE = registerBlock("echo_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(ECHO_PLANKS.get())));

    /** Echo fence gate. */
    public static final DeferredBlock<Block> ECHO_FENCE_GATE = registerBlock("echo_fence_gate",
            () -> new FenceGateBlock(WoodType.OAK, BlockBehaviour.Properties.ofFullCopy(ECHO_PLANKS.get())));

    /** Echo door. */
    public static final DeferredBlock<Block> ECHO_DOOR = registerBlock("echo_door",
            () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(ECHO_PLANKS.get())
                    .noOcclusion()));

    /** Echo trapdoor. */
    public static final DeferredBlock<Block> ECHO_TRAPDOOR = registerBlock("echo_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(ECHO_PLANKS.get())
                    .noOcclusion()
                    .isValidSpawn((s, g, p, e) -> false)));

    /** Echo button. */
    public static final DeferredBlock<Block> ECHO_BUTTON = registerBlock("echo_button",
            () -> new ButtonBlock(BlockSetType.OAK, 30,
                    BlockBehaviour.Properties.of()
                            .noCollission()
                            .strength(0.5F)
                            .sound(SoundType.SCULK_CATALYST)
                            .pushReaction(PushReaction.DESTROY)));

    /** Echo pressure plate. */
    public static final DeferredBlock<Block> ECHO_PRESSURE_PLATE = registerBlock("echo_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.OAK,
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_CYAN)
                            .noCollission()
                            .strength(0.5F)
                            .sound(SoundType.SCULK_CATALYST)
                            .pushReaction(PushReaction.DESTROY)));

    // ── Portal Frame ──

    /** Echo portal frame — chiseled echo-brick for the dimension portal. */
    public static final DeferredBlock<Block> ECHO_PORTAL_FRAME = registerBlock("echo_portal_frame",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .requiresCorrectToolForDrops()
                    .strength(55.0F, 1200.0F)
                    .lightLevel(state -> 3)
                    .sound(SoundType.DEEPSLATE)));

    // Helper methods
    private static DeferredBlock<Block> registerBlock(String name, Supplier<Block> block) {
        DeferredBlock<Block> registeredBlock = BLOCKS.register(name, block);
        BLOCK_ITEMS.register(name, () -> new BlockItem(registeredBlock.get(), new Item.Properties()));
        return registeredBlock;
    }
}
