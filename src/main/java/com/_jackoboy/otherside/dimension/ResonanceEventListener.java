package com._jackoboy.otherside.dimension;

import com._jackoboy.otherside.OthersideMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.VanillaGameEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Bridges Minecraft game events to the Resonance system.
 * 
 * Listens for VanillaGameEvent (which fires for ALL game events server-wide)
 * and maps them to resonance weights. Early-exits on dimension mismatch.
 */
@EventBusSubscriber(modid = OthersideMod.MOD_ID)
public class ResonanceEventListener {

    @SubscribeEvent
    public static void onGameEvent(VanillaGameEvent event) {
        // Early exit if not in the Otherside
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) return;

        // Only player-caused events
        Entity cause = event.getCause();
        if (!(cause instanceof ServerPlayer player)) return;

        GameEvent gameEvent = event.getVanillaEvent().value();
        BlockPos pos = BlockPos.containing(event.getEventPosition());

        float weight = getWeight(gameEvent, player);
        if (weight > 0) {
            ResonanceManager.addResonance(player, weight, pos);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) return;

        if (event.getPlayer() instanceof ServerPlayer player) {
            ResonanceManager.addResonance(player, 8.0F, event.getPos());
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) return;

        if (event.getEntity() instanceof ServerPlayer player) {
            ResonanceManager.addResonance(player, 4.0F, event.getPos());
        }
    }

    private static float getWeight(GameEvent gameEvent, ServerPlayer player) {
        // Match known game events to resonance weights
        if (gameEvent == GameEvent.STEP.value()) {
            return player.isCrouching() ? 0 : 1.0F;
        }
        if (gameEvent == GameEvent.SWIM.value()) {
            return 3.0F;
        }
        if (gameEvent == GameEvent.CONTAINER_OPEN.value() || gameEvent == GameEvent.CONTAINER_CLOSE.value()) {
            return 4.0F;
        }
        if (gameEvent == GameEvent.BLOCK_OPEN.value() || gameEvent == GameEvent.BLOCK_CLOSE.value()) {
            // Doors, trapdoors
            return 6.0F;
        }
        if (gameEvent == GameEvent.EXPLODE.value()) {
            return 60.0F;
        }
        if (gameEvent == GameEvent.EAT.value() || gameEvent == GameEvent.DRINK.value()) {
            return 2.0F;
        }
        if (gameEvent == GameEvent.ELYTRA_GLIDE.value()) {
            return 2.0F;
        }
        if (gameEvent == GameEvent.HIT_GROUND.value()) {
            return 4.0F;
        }
        if (gameEvent == GameEvent.PROJECTILE_SHOOT.value()) {
            return 3.0F;
        }
        if (gameEvent == GameEvent.ENTITY_DAMAGE.value()) {
            return 2.0F;
        }
        if (gameEvent == GameEvent.ENTITY_DIE.value()) {
            return 12.0F;
        }

        // Default: small noise for unrecognized events
        return 0;
    }
}
