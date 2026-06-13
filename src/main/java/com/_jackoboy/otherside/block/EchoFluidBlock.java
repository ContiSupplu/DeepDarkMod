package com._jackoboy.otherside.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Echo Fluid — v1 as a non-flowing fluid-looking block.
 * 
 * Translucent, no collision (entities fall through slowly), custom movement speed.
 * Full FluidType upgrade deferred — recorded in DEVIATIONS.md.
 */
public class EchoFluidBlock extends Block {

    public EchoFluidBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        // No solid collision — entities sink through slowly
        return Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // Slow movement like water but thicker
        entity.makeStuckInBlock(state, new Vec3(0.25, 0.05, 0.25));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return false; // Opaque to light transmission
    }
}
