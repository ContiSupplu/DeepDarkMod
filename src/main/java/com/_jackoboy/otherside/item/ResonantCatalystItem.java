package com._jackoboy.otherside.item;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.portal.IgnitionManager;
import com._jackoboy.otherside.portal.IgnitionSequence;
import com._jackoboy.otherside.portal.PortalFrameShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

/**
 * The Resonant Catalyst — portal ignition item (Epic rarity, max stack 1).
 * Right-click on a portal frame block to validate and start the ignition sequence.
 * Consumed on use.
 */
public class ResonantCatalystItem extends Item {

    private static final TagKey<Block> FRAME_TAG = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("otherside", "portal_frame"));

    public ResonantCatalystItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        var level = context.getLevel();

        // Must click a frame block
        if (!level.getBlockState(clickedPos).is(FRAME_TAG)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        // Validate the frame
        PortalFrameShape.Result result = PortalFrameShape.tryCreate(level, clickedPos, true);

        if (!result.valid()) {
            // Send failure message to player
            if (context.getPlayer() != null) {
                String msg = switch (result.failureReason()) {
                    case NO_FRAME_PLANE -> "No valid portal frame detected.";
                    case TOO_SMALL -> "Frame too small (minimum 3×3 interior).";
                    case TOO_BIG -> "Frame too large (maximum 32×32 interior).";
                    case RING_INCOMPLETE -> "Frame ring incomplete: " + result.failureDetail();
                    case INTERIOR_BLOCKED -> "Interior blocked: " + result.failureDetail();
                };
                context.getPlayer().displayClientMessage(
                        Component.literal(msg).withStyle(net.minecraft.ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }

        // Start ignition sequence
        IgnitionSequence sequence = new IgnitionSequence(serverLevel, result,
                context.getPlayer() != null ? context.getPlayer().getUUID() : new java.util.UUID(0, 0));

        if (IgnitionManager.startIgnition(sequence)) {
            // Consume the catalyst
            if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
                context.getItemInHand().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
