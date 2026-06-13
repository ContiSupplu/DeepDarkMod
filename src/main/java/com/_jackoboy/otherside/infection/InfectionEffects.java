package com._jackoboy.otherside.infection;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * Handles environmental effects in infected areas:
 * <ul>
 *     <li>Passive mobs (animals) slowly die from Wither I when standing on infected blocks</li>
 *     <li>Players get Poison I when in water over infected blocks</li>
 *     <li>Passive mob spawns are suppressed on infected blocks</li>
 * </ul>
 */
@EventBusSubscriber(modid = OthersideMod.MOD_ID)
public class InfectionEffects {

    /**
     * Checks whether a given block state is an "infected" block.
     * This includes vanilla sculk-family blocks and all custom mod infection blocks.
     *
     * @param state the block state to check
     * @return true if the block is infected
     */
    public static boolean isInfectedBlock(BlockState state) {
        // Vanilla sculk family
        if (state.is(Blocks.SCULK)
                || state.is(Blocks.SCULK_VEIN)
                || state.is(Blocks.SCULK_SENSOR)
                || state.is(Blocks.SCULK_CATALYST)
                || state.is(Blocks.SCULK_SHRIEKER)) {
            return true;
        }

        // Custom mod blocks from the infection set
        if (state.is(ModBlocks.SCULK_STONE.get())
                || state.is(ModBlocks.SCULK_WOOD.get())
                || state.is(ModBlocks.SCULK_MEMBRANE.get())
                || state.is(ModBlocks.SCORCHED_EARTH.get())
                || state.is(ModBlocks.TENDRIL_BLOCK.get())
                || state.is(ModBlocks.TENDRIL_HEART.get())
                || state.is(ModBlocks.CHARGED_FRAME.get())) {
            return true;
        }

        return false;
    }

    // ─── Passive mob spawn suppression ─────────────────────────────────────────

    /**
     * Prevents passive mobs (animals) from spawning if the block at their feet
     * is an infected block.
     */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Animal animal)) return;

        Level level = event.getLevel();
        BlockPos feetPos = animal.blockPosition();
        BlockState below = level.getBlockState(feetPos.below());

        if (isInfectedBlock(below)) {
            event.setCanceled(true);
            OthersideMod.LOGGER.debug("Suppressed passive mob spawn ({}) at {} — infected ground",
                    animal.getType().getDescriptionId(), feetPos);
        }
    }

    // ─── Living tick effects ───────────────────────────────────────────────────

    /**
     * Every living tick:
     * <ul>
     *     <li>Animals standing on infected blocks receive Wither I (5 s), re-applied every 100 ticks.</li>
     *     <li>Players standing in water above infected blocks receive Poison I (3 s), re-applied every 100 ticks.</li>
     * </ul>
     */
    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof net.minecraft.world.entity.LivingEntity)) return;

        // ── Passive mob wither effect (animals, villagers, golems) ──
        if (event.getEntity() instanceof Animal || event.getEntity() instanceof net.minecraft.world.entity.npc.AbstractVillager) {
            net.minecraft.world.entity.LivingEntity living = (net.minecraft.world.entity.LivingEntity) event.getEntity();
            // Check every 40 ticks (2 seconds) — aggressive killing
            if (living.tickCount % 40 != 0) return;

            BlockPos feetPos = living.blockPosition();
            BlockState below = living.level().getBlockState(feetPos.below());
            BlockState atFeet = living.level().getBlockState(feetPos);

            if (isInfectedBlock(below) || isInfectedBlock(atFeet)) {
                // Wither II for 4 seconds (80 ticks) — kills passives in ~10 seconds
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 1, false, true));
            }
            return; // Passives handled — skip player check
        }

        // ── Player toxic water effect ──
        // Check EVERY tick — instant on when entering, wears off ~2s after leaving
        if (event.getEntity() instanceof Player player) {
            if (!player.isInWater()) return; // Cheap skip for non-water players

            BlockPos feetPos = player.blockPosition();
            
            // Scan downward through water to find infected riverbed
            boolean foundInfected = false;
            BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos(feetPos.getX(), feetPos.getY(), feetPos.getZ());
            for (int i = 0; i < 10; i++) {
                scanPos.move(0, -1, 0);
                BlockState scanState = player.level().getBlockState(scanPos);
                if (isInfectedBlock(scanState)) {
                    foundInfected = true;
                    break;
                }
                if (scanState.isSolidRender(player.level(), scanPos) && !scanState.is(Blocks.WATER)) {
                    break;
                }
            }

            if (foundInfected) {
                // Only apply/refresh when no active poison OR it's about to expire.
                // Poison I damages every 25 ticks — if we reapply every tick, the
                // internal counter resets and damage NEVER fires.
                MobEffectInstance existing = player.getEffect(MobEffects.POISON);
                if (existing == null || existing.getDuration() < 10) {
                    player.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0, false, false));
                }
            }
        }
    }
}
