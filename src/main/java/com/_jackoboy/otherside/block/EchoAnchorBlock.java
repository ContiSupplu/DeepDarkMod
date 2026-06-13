package com._jackoboy.otherside.block;

import com._jackoboy.otherside.dimension.DimensionRulesManager;
import com._jackoboy.otherside.registry.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Echo Anchor — a respawn anchor for the Otherside dimension.
 *
 * Blockstate:
 *   - CHARGED (boolean): true = glowing/charged (light 7), false = spent (light 0)
 *
 * Behaviour:
 *   - Right-click with echo_shard_cluster on a spent anchor → recharge (set CHARGED=true)
 *   - Right-click (charged, empty hand or any other item) in the Otherside → attune respawn
 *   - On respawn, the anchor is depleted (handled by EchoAnchorRespawnHandler)
 */
public class EchoAnchorBlock extends Block {
    public static final MapCodec<EchoAnchorBlock> CODEC = simpleCodec(EchoAnchorBlock::new);

    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");

    public EchoAnchorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CHARGED, true));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGED);
    }

    // ── Interaction: recharge with echo_shard_cluster ────────────────────────────

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                               BlockPos pos, Player player, InteractionHand hand,
                                               BlockHitResult hitResult) {
        // Recharge a spent anchor with an echo shard cluster
        if (stack.is(ModItems.ECHO_SHARD_CLUSTER.get()) && !state.getValue(CHARGED)) {
            if (!level.isClientSide()) {
                // Set charged
                level.setBlock(pos, state.setValue(CHARGED, true), 3);

                // Consume item
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                // Sound
                level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_CHARGE,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            // Client-side particles
            if (level.isClientSide()) {
                spawnChargeParticles(level, pos);
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }

        // Fall through to useWithoutItem for attunement
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    // ── Interaction: attune respawn point (empty hand or non-cluster item) ───────

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        // Only attune in the Otherside dimension
        if (!level.dimension().equals(DimensionRulesManager.OTHERSIDE_DIM)) {
            return InteractionResult.PASS;
        }

        // Only attune if the anchor is charged
        if (!state.getValue(CHARGED)) {
            // Spent anchor — can't set spawn here
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("The anchor is spent.")
                                .withStyle(ChatFormatting.GRAY)
                                .withStyle(ChatFormatting.ITALIC),
                        true);
            }
            return InteractionResult.CONSUME;
        }

        if (!level.isClientSide()) {
            ServerPlayer serverPlayer = (ServerPlayer) player;

            // Set respawn point using vanilla API
            serverPlayer.setRespawnPosition(
                    level.dimension(),  // dimension
                    pos,                // position
                    player.getYRot(),   // angle
                    true,               // forced (bypass bed obstruction checks)
                    true                // showMessage (vanilla message suppressed, we send our own)
            );

            // Action bar message
            serverPlayer.displayClientMessage(
                    Component.literal("Respawn point set")
                            .withStyle(ChatFormatting.DARK_AQUA),
                    true);

            // Sound
            level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN,
                    SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        // Client-side particles
        if (level.isClientSide()) {
            spawnAttuneParticles(level, pos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    // ── Particle helpers ────────────────────────────────────────────────────────

    private static void spawnChargeParticles(Level level, BlockPos pos) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;
        for (int i = 0; i < 12; i++) {
            level.addParticle(ParticleTypes.SCULK_SOUL,
                    cx + (level.random.nextDouble() - 0.5) * 0.8,
                    cy + level.random.nextDouble() * 0.6,
                    cz + (level.random.nextDouble() - 0.5) * 0.8,
                    0.0, 0.06, 0.0);
        }
    }

    private static void spawnAttuneParticles(Level level, BlockPos pos) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 1.0;
        double cz = pos.getZ() + 0.5;
        for (int i = 0; i < 8; i++) {
            level.addParticle(ParticleTypes.SCULK_SOUL,
                    cx + (level.random.nextDouble() - 0.5) * 0.5,
                    cy + level.random.nextDouble() * 0.3,
                    cz + (level.random.nextDouble() - 0.5) * 0.5,
                    0.0, 0.04, 0.0);
        }
    }
}
