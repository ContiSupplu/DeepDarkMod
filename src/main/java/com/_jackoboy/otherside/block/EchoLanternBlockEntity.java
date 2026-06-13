package com._jackoboy.otherside.block;

import com._jackoboy.otherside.dimension.DimensionRulesManager;
import com._jackoboy.otherside.registry.ModBlockEntityTypes;
import com._jackoboy.otherside.registry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity for the Echo Lantern.
 *
 * Tracks fuel and drives blockstate transitions:
 *   fuel > 1200  → lit = FULL (light 14)
 *   0 < fuel ≤ 1200 → lit = LOW (flickering between 8/14)
 *   fuel ≤ 0       → lit = OUT (no light)
 *
 * Only ticks down fuel when placed in the Otherside dimension.
 */
public class EchoLanternBlockEntity extends BlockEntity {

    private static final String TAG_FUEL = "EchoFuel";
    private static final int MAX_FUEL = EchoLanternBlock.MAX_FUEL;
    /** Threshold below which we enter the "low" flicker state (60 seconds). */
    private static final int LOW_THRESHOLD = 1200;

    private int fuel = MAX_FUEL;
    /** Tick counter for the flicker effect in LOW state. */
    private int nextFlickerTick = 0;

    public EchoLanternBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ECHO_LANTERN.get(), pos, state);
    }

    // ── Fuel access ─────────────────────────────────────────────────────────────

    public int getFuel() {
        return fuel;
    }

    /**
     * Adds fuel, clamped to MAX_FUEL. If the lantern was out or low, updates the lit state.
     */
    public void refuel(int amount) {
        this.fuel = Math.min(MAX_FUEL, this.fuel + amount);
        if (level != null && !level.isClientSide()) {
            EchoLanternBlock.LitState desiredLit = fuel > LOW_THRESHOLD
                    ? EchoLanternBlock.LitState.FULL
                    : EchoLanternBlock.LitState.LOW;
            BlockState current = getBlockState();
            if (current.getValue(EchoLanternBlock.LIT) != desiredLit) {
                level.setBlock(worldPosition, current.setValue(EchoLanternBlock.LIT, desiredLit), 3);
            }
            setChanged();
        }
    }

    // ── Server tick ─────────────────────────────────────────────────────────────

    public static void serverTick(Level level, BlockPos pos, BlockState state, EchoLanternBlockEntity be) {
        // Only consume fuel in the Otherside dimension
        if (!level.dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) {
            return;
        }

        EchoLanternBlock.LitState lit = state.getValue(EchoLanternBlock.LIT);

        // Already out — nothing to do
        if (lit == EchoLanternBlock.LitState.OUT && be.fuel <= 0) return;

        // Drain fuel
        be.fuel--;

        // ── State transitions ───────────────────────────────────────────────────
        if (be.fuel <= 0) {
            be.fuel = 0;
            if (lit != EchoLanternBlock.LitState.OUT) {
                level.setBlock(pos, state.setValue(EchoLanternBlock.LIT, EchoLanternBlock.LitState.OUT), 3);
                level.playSound(null, pos, ModSoundEvents.LANTERN_DIE.get(),
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            be.setChanged();
            return;
        }

        if (be.fuel < LOW_THRESHOLD && lit == EchoLanternBlock.LitState.FULL) {
            // Transition full → low
            level.setBlock(pos, state.setValue(EchoLanternBlock.LIT, EchoLanternBlock.LitState.LOW), 3);
            be.setChanged();
            return;
        }

        // ── Low-state flicker ───────────────────────────────────────────────────
        if (lit == EchoLanternBlock.LitState.LOW) {
            if (be.nextFlickerTick <= 0) {
                // Force a block update to trigger light engine recalculation.
                // Re-set the block to LIT=LOW to trigger Block.UPDATE_ALL.
                level.setBlock(pos, state.setValue(EchoLanternBlock.LIT, EchoLanternBlock.LitState.LOW), 3);

                // Schedule next flicker in 10-20 ticks
                be.nextFlickerTick = 10 + level.random.nextInt(11);
            } else {
                be.nextFlickerTick--;
            }
        }

        // Periodically mark dirty for save
        if (be.fuel % 200 == 0) {
            be.setChanged();
        }
    }

    // ── NBT persistence ─────────────────────────────────────────────────────────

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_FUEL, fuel);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TAG_FUEL)) {
            this.fuel = tag.getInt(TAG_FUEL);
        }
    }
}
