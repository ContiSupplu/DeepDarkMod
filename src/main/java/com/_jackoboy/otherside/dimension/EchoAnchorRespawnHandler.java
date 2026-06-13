package com._jackoboy.otherside.dimension;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.block.EchoAnchorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Listens for player respawn events. When a player respawns at an Echo Anchor
 * in the Otherside dimension, the anchor is depleted (CHARGED → false) and a
 * deplete sound is played.
 */
@EventBusSubscriber(modid = OthersideMod.MOD_ID)
public class EchoAnchorRespawnHandler {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // Check if the player's respawn dimension is the Otherside
        ResourceKey<Level> respawnDim = player.getRespawnDimension();
        BlockPos respawnPos = player.getRespawnPosition();

        if (respawnDim == null || respawnPos == null) return;
        if (!respawnDim.equals(DimensionRulesManager.OTHERSIDE_DIM)) return;

        ServerLevel level = player.server.getLevel(respawnDim);
        if (level == null) return;

        BlockState state = level.getBlockState(respawnPos);
        if (state.getBlock() instanceof EchoAnchorBlock && state.getValue(EchoAnchorBlock.CHARGED)) {
            // Deplete the anchor
            level.setBlock(respawnPos, state.setValue(EchoAnchorBlock.CHARGED, false), 3);
            level.playSound(null, respawnPos.getX() + 0.5, respawnPos.getY() + 0.5, respawnPos.getZ() + 0.5,
                    SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}
