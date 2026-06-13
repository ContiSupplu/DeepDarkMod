package com._jackoboy.otherside.dimension;

import com._jackoboy.otherside.registry.ModDataComponents;
import com._jackoboy.otherside.registry.ModSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side per-player dynamic light manager for the Echo Lantern held item.
 *
 * When a player holds an Echo Lantern (mainhand or offhand) in the Otherside,
 * this places an invisible {@code minecraft:light} block at the player's eye-level
 * position and moves it as the player moves.
 *
 * Fuel is consumed each tick and synced to the item's data component every 20 ticks.
 */
public class HeldLightManager {

    private static final int MAX_FUEL = 18000;
    private static final int LOW_THRESHOLD = 1200;
    private static final int SYNC_INTERVAL = 20;
    private static final int FULL_LIGHT = 14;
    private static final int LOW_LIGHT = 8;

    /** Tracked light data per player UUID. */
    private static final Map<UUID, LightData> trackedPlayers = new HashMap<>();

    private static class LightData {
        BlockPos currentLightPos;
        int fuel;
        int syncTimer;
        /** Flicker counter for low-fuel state. */
        int flickerTimer;
        boolean flickerHigh;

        LightData(int fuel) {
            this.fuel = fuel;
            this.syncTimer = 0;
            this.flickerTimer = 0;
            this.flickerHigh = true;
        }
    }

    /**
     * Called every server tick for the Otherside level.
     * Manages light placement, fuel drain, and cleanup.
     */
    public static void tick(ServerLevel level) {
        if (!level.dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) return;

        // Process each player currently in the dimension
        for (ServerPlayer player : level.players()) {
            UUID uuid = player.getUUID();
            ItemStack lanternStack = findHeldLantern(player);

            if (lanternStack.isEmpty()) {
                // Player is not holding a lantern — clean up if tracked
                removePlayer(level, uuid);
                continue;
            }

            // Get or create tracking data
            LightData data = trackedPlayers.get(uuid);
            if (data == null) {
                // Read fuel from item data component
                Integer itemFuel = lanternStack.get(ModDataComponents.ECHO_FUEL.get());
                int startFuel = (itemFuel != null) ? itemFuel : MAX_FUEL;
                data = new LightData(startFuel);
                trackedPlayers.put(uuid, data);
            }

            // Fuel exhausted
            if (data.fuel <= 0) {
                removeLightBlock(level, data);
                trackedPlayers.remove(uuid);

                // Sync zero fuel to item
                lanternStack.set(ModDataComponents.ECHO_FUEL.get(), 0);

                // Feedback
                player.displayClientMessage(
                        Component.literal("Your Echo Lantern gutters out...")
                                .withStyle(ChatFormatting.DARK_AQUA)
                                .withStyle(ChatFormatting.ITALIC),
                        true);
                level.playSound(null, player.blockPosition(),
                        ModSoundEvents.LANTERN_DIE.get(), SoundSource.PLAYERS,
                        1.0F, 0.8F);
                continue;
            }

            // Determine target position (player eye-level block)
            BlockPos targetPos = BlockPos.containing(
                    player.getX(),
                    player.getEyeY(),
                    player.getZ());

            // Determine light level
            int lightLevel = FULL_LIGHT;
            if (data.fuel < LOW_THRESHOLD) {
                // Flicker effect
                data.flickerTimer--;
                if (data.flickerTimer <= 0) {
                    data.flickerHigh = !data.flickerHigh;
                    data.flickerTimer = 10 + level.random.nextInt(11); // 10-20 ticks
                }
                lightLevel = data.flickerHigh ? FULL_LIGHT : LOW_LIGHT;
            }

            // Move or place light block
            if (data.currentLightPos == null || !data.currentLightPos.equals(targetPos)) {
                // Remove old light
                removeLightBlock(level, data);

                // Place new light (only if target block is air)
                BlockState atTarget = level.getBlockState(targetPos);
                if (atTarget.isAir()) {
                    BlockState lightState = Blocks.LIGHT.defaultBlockState()
                            .setValue(BlockStateProperties.LEVEL, lightLevel);
                    level.setBlock(targetPos, lightState, 3);
                    data.currentLightPos = targetPos;
                }
            } else {
                // Same position — just update light level if it changed
                BlockState current = level.getBlockState(data.currentLightPos);
                if (current.is(Blocks.LIGHT)) {
                    int currentLevel = current.getValue(BlockStateProperties.LEVEL);
                    if (currentLevel != lightLevel) {
                        level.setBlock(data.currentLightPos,
                                current.setValue(BlockStateProperties.LEVEL, lightLevel), 3);
                    }
                }
            }

            // Drain fuel
            data.fuel--;

            // Sync fuel to item stack periodically
            data.syncTimer++;
            if (data.syncTimer >= SYNC_INTERVAL) {
                data.syncTimer = 0;
                lanternStack.set(ModDataComponents.ECHO_FUEL.get(), data.fuel);
            }
        }

        // Clean up stale entries (players who left the dimension or disconnected)
        orphanSweep(level);
    }

    /**
     * Finds an echo lantern in the player's mainhand or offhand.
     * Returns the stack, or ItemStack.EMPTY if none found.
     */
    private static ItemStack findHeldLantern(ServerPlayer player) {
        // Check mainhand first
        ItemStack mainhand = player.getMainHandItem();
        if (isEchoLantern(mainhand)) return mainhand;

        // Check offhand
        ItemStack offhand = player.getOffhandItem();
        if (isEchoLantern(offhand)) return offhand;

        return ItemStack.EMPTY;
    }

    private static boolean isEchoLantern(ItemStack stack) {
        if (stack.isEmpty()) return false;
        // Check if the item has the ECHO_FUEL data component (our echo lantern block item will have it)
        return stack.has(ModDataComponents.ECHO_FUEL.get());
    }

    /**
     * Removes the tracked light block and entry for a player.
     */
    public static void removePlayer(ServerLevel level, UUID uuid) {
        LightData data = trackedPlayers.remove(uuid);
        if (data != null) {
            removeLightBlock(level, data);
        }
    }

    /**
     * Removes the light block at the tracked position, but only if it's still a light block.
     */
    private static void removeLightBlock(ServerLevel level, LightData data) {
        if (data.currentLightPos != null) {
            BlockState current = level.getBlockState(data.currentLightPos);
            if (current.is(Blocks.LIGHT)) {
                level.setBlock(data.currentLightPos, Blocks.AIR.defaultBlockState(), 3);
            }
            data.currentLightPos = null;
        }
    }

    /**
     * Checks all tracked positions still belong to valid, online players in this level.
     * Removes orphaned entries whose players are no longer present.
     */
    public static void orphanSweep(ServerLevel level) {
        Iterator<Map.Entry<UUID, LightData>> it = trackedPlayers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, LightData> entry = it.next();
            UUID uuid = entry.getKey();
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);

            boolean shouldRemove = false;
            if (player == null) {
                shouldRemove = true; // Disconnected
            } else if (!player.level().dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) {
                shouldRemove = true; // Left the dimension
            } else if (findHeldLantern(player).isEmpty()) {
                shouldRemove = true; // No longer holding lantern
            }

            if (shouldRemove) {
                removeLightBlock(level, entry.getValue());
                it.remove();
            }
        }
    }

    /**
     * Called when a player logs out or changes dimension. Ensures light blocks are cleaned up.
     */
    public static void onPlayerLeave(ServerLevel level, UUID uuid) {
        removePlayer(level, uuid);
    }

    /**
     * Clears all tracked data. Called on server stop.
     */
    public static void clear() {
        trackedPlayers.clear();
    }
}
