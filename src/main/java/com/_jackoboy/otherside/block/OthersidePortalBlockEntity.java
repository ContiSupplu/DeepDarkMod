package com._jackoboy.otherside.block;

import com._jackoboy.otherside.registry.ModBlockEntityTypes;
import com._jackoboy.otherside.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity for the Otherside portal — stores the portal's vertical bounds
 * (bottom Y and height) so the renderer can draw a continuous gradient across
 * the entire opening rather than per-block.
 */
public class OthersidePortalBlockEntity extends BlockEntity {
    private int portalBottomY = -1;
    private int portalHeight = 1;
    private boolean boundsComputed = false;

    public OthersidePortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.OTHERSIDE_PORTAL.get(), pos, state);
    }

    public int getPortalBottomY() {
        if (!boundsComputed) computeBounds();
        return portalBottomY;
    }

    public int getPortalHeight() {
        if (!boundsComputed) computeBounds();
        return Math.max(1, portalHeight);
    }

    /**
     * Scan up and down from this block to find the full vertical extent of the portal.
     */
    private void computeBounds() {
        boundsComputed = true;
        Level level = this.getLevel();
        if (level == null) {
            portalBottomY = this.getBlockPos().getY();
            portalHeight = 1;
            return;
        }

        BlockPos pos = this.getBlockPos();
        BlockState portalState = ModBlocks.OTHERSIDE_PORTAL.get().defaultBlockState();

        // Scan down to find bottom
        int bottomY = pos.getY();
        for (int y = pos.getY() - 1; y >= pos.getY() - 32; y--) {
            BlockState below = level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ()));
            if (below.getBlock() == ModBlocks.OTHERSIDE_PORTAL.get()) {
                bottomY = y;
            } else {
                break;
            }
        }

        // Scan up to find top
        int topY = pos.getY();
        for (int y = pos.getY() + 1; y <= pos.getY() + 32; y++) {
            BlockState above = level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ()));
            if (above.getBlock() == ModBlocks.OTHERSIDE_PORTAL.get()) {
                topY = y;
            } else {
                break;
            }
        }

        portalBottomY = bottomY;
        portalHeight = topY - bottomY + 1; // inclusive
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (boundsComputed) {
            tag.putInt("PortalBottomY", portalBottomY);
            tag.putInt("PortalHeight", portalHeight);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("PortalBottomY")) {
            portalBottomY = tag.getInt("PortalBottomY");
            portalHeight = tag.getInt("PortalHeight");
            boundsComputed = true;
        }
    }
}
