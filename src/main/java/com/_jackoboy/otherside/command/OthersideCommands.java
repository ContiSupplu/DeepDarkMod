package com._jackoboy.otherside.command;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.infection.*;
import com._jackoboy.otherside.network.BeastSyncPayload;
import com._jackoboy.otherside.portal.GuardianManager;
import com._jackoboy.otherside.portal.IgnitionManager;
import com._jackoboy.otherside.portal.IgnitionSequence;
import com._jackoboy.otherside.portal.PortalFrameShape;
import com._jackoboy.otherside.portal.PortalSavedData;
import com._jackoboy.otherside.registry.ModBlocks;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class OthersideCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("otherside")
                        .requires(source -> source.hasPermission(2))
                        .then(beastCommand())
                        .then(infectionCommand())
                        .then(spawnBreachCommand())
                        .then(timelapseCommand())
                        .then(statusCommand())
                        .then(whisperCommand())
                        .then(portalCommand())
        );
    }

    // /otherside beast status|mass|hunger|acuity|attention|rails
    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> beastCommand() {
        return Commands.literal("beast")
                .then(Commands.literal("status")
                        .executes(ctx -> beastStatus(ctx.getSource())))
                .then(Commands.literal("mass")
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", DoubleArgumentType.doubleArg(-1, 100))
                                        .executes(ctx -> beastMassSet(ctx.getSource(), DoubleArgumentType.getDouble(ctx, "value"))))))
                .then(Commands.literal("hunger")
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 100))
                                        .executes(ctx -> beastHungerSet(ctx.getSource(), DoubleArgumentType.getDouble(ctx, "value"))))))
                .then(Commands.literal("acuity")
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 100))
                                        .executes(ctx -> beastAcuitySet(ctx.getSource(), DoubleArgumentType.getDouble(ctx, "value"))))))
                .then(Commands.literal("attention")
                        .then(Commands.literal("get")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(ctx -> beastAttentionGet(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))))
                        .then(Commands.literal("set")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0, 100))
                                                .executes(ctx -> beastAttentionSet(ctx.getSource(),
                                                        EntityArgument.getPlayer(ctx, "player"),
                                                        DoubleArgumentType.getDouble(ctx, "value")))))))
                .then(Commands.literal("rails")
                        .then(Commands.literal("override")
                                .then(Commands.argument("percent", DoubleArgumentType.doubleArg(-1, 100))
                                        .executes(ctx -> beastRailsOverride(ctx.getSource(), DoubleArgumentType.getDouble(ctx, "percent"))))))
                // W2: order commands
                .then(Commands.literal("order")
                        .then(Commands.literal("surge")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(ctx -> beastOrderForce(ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "pos"), OrderManager.OrderType.SURGE))))
                        .then(Commands.literal("breakout")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(ctx -> beastOrderForce(ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "pos"), OrderManager.OrderType.BREAKOUT)))))
                // W2: sore commands
                .then(Commands.literal("sore")
                        .then(Commands.literal("spawn")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(ctx -> beastSoreSpawn(ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "pos"))))))
                // W3: maw command
                .then(Commands.literal("maw")
                        .executes(ctx -> beastMawSpawn(ctx.getSource())))
                // W3: echo soul command
                .then(Commands.literal("soul")
                        .executes(ctx -> beastSoulSpawn(ctx.getSource())))
                // W4: listening bloom command
                .then(Commands.literal("bloom")
                        .executes(ctx -> beastBloomSpawn(ctx.getSource())));
    }

    private static int beastStatus(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        WorldbeastState beast = WorldbeastState.get(level);
        
        String hungerState = beast.isSated() ? "SATED" : (beast.getHunger() >= 70 ? "RESTLESS" : "NEUTRAL");
        
        source.sendSuccess(() -> Component.literal("=== Worldbeast Status ===").withStyle(ChatFormatting.DARK_AQUA), false);
        source.sendSuccess(() -> Component.literal("MASS: " + String.format("%.1f%%", beast.getMass())
                + (beast.isRailThrottled() ? " §c[THROTTLED]" : "")).withStyle(ChatFormatting.AQUA), false);
        source.sendSuccess(() -> Component.literal("HUNGER: " + String.format("%.1f", beast.getHunger())
                + " (" + hungerState + ")"
                + (beast.isSated() ? " ticks=" + beast.getSatedTicksRemaining() : "")).withStyle(ChatFormatting.AQUA), false);
        source.sendSuccess(() -> Component.literal("ACUITY: " + String.format("%.1f", beast.getAcuity())).withStyle(ChatFormatting.AQUA), false);
        source.sendSuccess(() -> Component.literal("Rail Cap: " + String.format("%.1f%%", beast.getRailCap())
                + (beast.getRailsOverride() >= 0 ? " [OVERRIDE]" : "")).withStyle(ChatFormatting.AQUA), false);
        
        InfectionSavedData data = InfectionSavedData.get(level);
        source.sendSuccess(() -> Component.literal("Breaches: " + data.getBreaches().size()
                + ", Conversions: " + data.getTotalConversions()
                + ", Claimed: " + beast.getClaimedChunkCount()
                + "/" + beast.getExploredChunkCount() + " chunks").withStyle(ChatFormatting.AQUA), false);
        source.sendSuccess(() -> Component.literal("Timelapse: " + (data.isTimelapseActive() ? "ON (" + data.getTimelapseMultiplier() + "x)" : "OFF")).withStyle(ChatFormatting.GRAY), false);
        return 1;
    }

    private static int beastMassSet(CommandSourceStack source, double value) {
        WorldbeastState beast = WorldbeastState.get(source.getLevel());
        beast.setDebugMassOverride((float) value);
        if (value < 0) {
            source.sendSuccess(() -> Component.literal("Mass override cleared — using computed value").withStyle(ChatFormatting.AQUA), true);
        } else {
            source.sendSuccess(() -> Component.literal("Mass override set to " + String.format("%.1f%%", value)).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    private static int beastHungerSet(CommandSourceStack source, double value) {
        WorldbeastState beast = WorldbeastState.get(source.getLevel());
        beast.setHunger((float) value);
        source.sendSuccess(() -> Component.literal("Hunger set to " + String.format("%.1f", value)).withStyle(ChatFormatting.AQUA), true);
        return 1;
    }

    private static int beastAcuitySet(CommandSourceStack source, double value) {
        WorldbeastState beast = WorldbeastState.get(source.getLevel());
        beast.setAcuity((float) value);
        source.sendSuccess(() -> Component.literal("Acuity set to " + String.format("%.1f", value)).withStyle(ChatFormatting.AQUA), true);
        return 1;
    }

    private static int beastAttentionGet(CommandSourceStack source, ServerPlayer target) {
        WorldbeastState beast = WorldbeastState.get(source.getLevel());
        WorldbeastState.AttentionData attn = beast.getAttentionData(target.getUUID());
        if (attn == null) {
            source.sendSuccess(() -> Component.literal(target.getName().getString() + ": UNNOTICED (no data)").withStyle(ChatFormatting.GRAY), false);
        } else {
            source.sendSuccess(() -> Component.literal(target.getName().getString() + ": "
                    + String.format("%.1f", attn.attention) + " — " + attn.currentTier.name()).withStyle(ChatFormatting.AQUA), false);
        }
        return 1;
    }

    private static int beastAttentionSet(CommandSourceStack source, ServerPlayer target, double value) {
        WorldbeastState beast = WorldbeastState.get(source.getLevel());
        beast.addAttention(target.getUUID(), (float) value - beast.getPlayerAttention(target.getUUID()),
                source.getLevel().getGameTime());
        source.sendSuccess(() -> Component.literal("Attention for " + target.getName().getString()
                + " set to " + String.format("%.1f", value)).withStyle(ChatFormatting.AQUA), true);
        return 1;
    }

    private static int beastRailsOverride(CommandSourceStack source, double value) {
        WorldbeastState beast = WorldbeastState.get(source.getLevel());
        beast.setRailsOverride((float) value);
        if (value < 0) {
            source.sendSuccess(() -> Component.literal("Rails override cleared — using default table").withStyle(ChatFormatting.AQUA), true);
        } else {
            source.sendSuccess(() -> Component.literal("Rails override set to " + String.format("%.1f%%", value)).withStyle(ChatFormatting.AQUA), true);
        }
        return 1;
    }

    // W2: force an order at a given position
    private static int beastOrderForce(CommandSourceStack source, BlockPos pos, OrderManager.OrderType type) {
        ServerLevel level = source.getLevel();
        WorldbeastState beast = WorldbeastState.get(level);
        OrderManager om = beast.getOrderManager();
        if (om != null) {
            om.issueOrder(level, type, pos, beast);
            source.sendSuccess(() -> Component.literal("Issued " + type.name() + " order at " + pos.toShortString())
                    .withStyle(ChatFormatting.DARK_AQUA), true);
        } else {
            source.sendFailure(Component.literal("OrderManager not initialized"));
        }
        return 1;
    }

    // W2: force a sore eruption at a given position (bypass scoring)
    private static int beastSoreSpawn(CommandSourceStack source, BlockPos pos) {
        ServerLevel level = source.getLevel();
        WorldbeastState beast = WorldbeastState.get(level);
        SoreManager sm = beast.getSoreManager();
        if (sm != null) {
            sm.triggerEruption(level, pos, beast, 999.0f); // forced spawn, fake high score
            source.sendSuccess(() -> Component.literal("Triggered Sore eruption at " + pos.toShortString())
                    .withStyle(ChatFormatting.DARK_AQUA), true);
        } else {
            source.sendFailure(Component.literal("SoreManager not initialized"));
        }
        return 1;
    }

    // /otherside beast maw — instant Maw cycle at the player's feet
    private static int beastMawSpawn(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        WorldbeastState beast = WorldbeastState.get(level);
        BlockPos pos = BlockPos.containing(source.getPosition());
        beast.getMawManager().forceOpen(level, pos, beast);
        source.sendSuccess(() -> Component.literal("Maw force-opened at " + pos.toShortString())
                .withStyle(ChatFormatting.DARK_RED), true);
        return 1;
    }

    // /otherside beast soul — spawn an Echo Soul at the player's feet
    private static int beastSoulSpawn(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());
        com._jackoboy.otherside.entity.EchoSoulEntity soul =
                com._jackoboy.otherside.registry.ModEntityTypes.ECHO_SOUL.get().create(level);
        if (soul != null) {
            soul.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            level.addFreshEntity(soul);
            source.sendSuccess(() -> Component.literal("Echo Soul spawned at " + pos.toShortString())
                    .withStyle(ChatFormatting.DARK_PURPLE), true);
        }
        return 1;
    }

    // /otherside beast bloom — spawn a Listening Bloom at the player's feet
    private static int beastBloomSpawn(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        BlockPos pos = BlockPos.containing(source.getPosition());
        com._jackoboy.otherside.entity.ListeningBloomEntity bloom =
                com._jackoboy.otherside.registry.ModEntityTypes.LISTENING_BLOOM.get().create(level);
        if (bloom != null) {
            bloom.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
            level.addFreshEntity(bloom);
            source.sendSuccess(() -> Component.literal("Listening Bloom spawned at " + pos.toShortString())
                    .withStyle(ChatFormatting.DARK_GREEN), true);
        }
        return 1;
    }

    // /otherside infection set <percent>
    // /otherside infection seed <x> <z> <radius>
    // /otherside infection cleanse <radius>
    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> infectionCommand() {
        return Commands.literal("infection")
                .then(Commands.literal("set")
                        .then(Commands.argument("percent", DoubleArgumentType.doubleArg(0, 100))
                                .executes(ctx -> {
                                     ServerLevel level = ctx.getSource().getLevel();
                                    InfectionSavedData data = InfectionSavedData.get(level);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Infection stats: conversions=" + data.getTotalConversions()).withStyle(ChatFormatting.AQUA), true);
                                    return 1;
                                })))
                .then(Commands.literal("seed")
                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                .then(Commands.argument("z", IntegerArgumentType.integer())
                                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, 64))
                                                .executes(ctx -> seedInfection(ctx.getSource(),
                                                        IntegerArgumentType.getInteger(ctx, "x"),
                                                        IntegerArgumentType.getInteger(ctx, "z"),
                                                        IntegerArgumentType.getInteger(ctx, "radius")))))))
                .then(Commands.literal("cleanse")
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, 128))
                                .executes(ctx -> cleanseArea(ctx.getSource(),
                                        IntegerArgumentType.getInteger(ctx, "radius"), false))
                                .then(Commands.literal("silent")
                                        .executes(ctx -> cleanseArea(ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "radius"), true)))));
    }

    private static int seedInfection(CommandSourceStack source, int x, int z, int radius) {
        ServerLevel level = source.getLevel();
        InfectionSavedData data = InfectionSavedData.get(level);

        if (!data.isInitialized()) {
            data.setInitialized(true);
        }

        int converted = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > radius * radius) continue;
                int bx = x + dx;
                int bz = z + dz;
                int surfY = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, bx, bz);
                BlockPos pos = new BlockPos(bx, surfY - 1, bz);
                if (level.isLoaded(pos)) {
                    net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
                    if (ConversionMap.isConvertible(state)) {
                        level.setBlock(pos, ConversionMap.getConversion(state), 3);
                        // Add to nearest breach frontier, or create a new one
                        if (!data.getBreaches().isEmpty()) {
                            data.getBreaches().get(0).getFrontier().add(pos.asLong());
                        }
                        converted++;
                    }
                }
            }
        }

        int finalConverted = converted;
        DirectorLog.log(level, "INFECTION_SEEDED", new BlockPos(x, 0, z), "Radius " + radius + ", converted " + converted);
        source.sendSuccess(() -> Component.literal("Infected " + finalConverted + " blocks in radius " + radius).withStyle(ChatFormatting.AQUA), true);
        return 1;
    }

    private static int cleanseArea(CommandSourceStack source, int radius, boolean silent) {
        ServerLevel level = source.getLevel();
        BlockPos center = source.getPlayer() != null ? source.getPlayer().blockPosition() : BlockPos.ZERO;

        int cleansed = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dy * dy + dz * dz > radius * radius) continue;
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!level.isLoaded(pos)) continue;
                    net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
                    // Revert sculk family blocks
                    if (state.is(Blocks.SCULK) || state.is(Blocks.SCULK_VEIN)) {
                        level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
                        cleansed++;
                    } else if (state.is(Blocks.SCULK_SENSOR) || state.is(Blocks.SCULK_CATALYST) || state.is(Blocks.SCULK_SHRIEKER)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        cleansed++;
                    }
                    // Also revert mod blocks
                    if (state.is(com._jackoboy.otherside.registry.ModBlocks.SCULK_STONE.get()) ||
                            state.is(com._jackoboy.otherside.registry.ModBlocks.SCULK_WOOD.get()) ||
                            state.is(com._jackoboy.otherside.registry.ModBlocks.SCULK_MEMBRANE.get())) {
                        level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
                        cleansed++;
                    }
                    // Also revert vein cords
                    if (state.is(com._jackoboy.otherside.registry.ModBlocks.SCULK_VEIN_CORD.get())) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                        cleansed++;
                    }
                }
            }
        }

        // Remove frontier entries in cleansed area from all breaches
        InfectionSavedData data = InfectionSavedData.get(level);
        for (BreachData breach : data.getBreaches()) {
            breach.getFrontier().removeIf(packed -> {
                BlockPos pos = BlockPos.of(packed);
                return pos.distSqr(center) <= radius * radius;
            });
        }
        data.setDirty();

        // W2: trigger pain response when not silent (cleansing = pain event §6)
        if (!silent && cleansed > 0) {
            WorldbeastState beast = WorldbeastState.get(level);
            OrderManager om = beast.getOrderManager();
            if (om != null) {
                om.issueOrder(level, OrderManager.OrderType.FLINCH, center, beast);
                om.issueOrder(level, OrderManager.OrderType.RETALIATION, center, beast);
            }
        }

        int finalCleansed = cleansed;
        source.sendSuccess(() -> Component.literal("Cleansed " + finalCleansed + " blocks in radius " + radius
                + (silent ? " (silent)" : " (pain triggered)")).withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    // /otherside spawnbreach <x> <z>
    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> spawnBreachCommand() {
        return Commands.literal("spawnbreach")
                .then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("z", IntegerArgumentType.integer())
                                .executes(ctx -> {
                                    ServerLevel level = ctx.getSource().getLevel();
                                    int x = IntegerArgumentType.getInteger(ctx, "x");
                                    int z = IntegerArgumentType.getInteger(ctx, "z");
                                    InfectionSavedData data = InfectionSavedData.get(level);
                                    if (!data.isInitialized()) data.setInitialized(true);
                                    BreachManager.createArtificialBreach(level, data, new BlockPos(x, -30, z));
                                    ctx.getSource().sendSuccess(() -> Component.literal("Spawned breach at " + x + ", " + z).withStyle(ChatFormatting.AQUA), true);
                                    return 1;
                                })));
    }

    // /otherside timelapse <on|off> [multiplier]
    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> timelapseCommand() {
        return Commands.literal("timelapse")
                .then(Commands.literal("on")
                        .then(Commands.argument("multiplier", IntegerArgumentType.integer(1, 20))
                                .executes(ctx -> {
                                    InfectionSavedData data = InfectionSavedData.get(ctx.getSource().getLevel());
                                    int mult = IntegerArgumentType.getInteger(ctx, "multiplier");
                                    data.setTimelapseActive(true);
                                    data.setTimelapseMultiplier(mult);
                                    ctx.getSource().sendSuccess(() -> Component.literal("Timelapse ON (" + mult + "x)").withStyle(ChatFormatting.AQUA), true);
                                    return 1;
                                })))
                .then(Commands.literal("off")
                        .executes(ctx -> {
                            InfectionSavedData data = InfectionSavedData.get(ctx.getSource().getLevel());
                            data.setTimelapseActive(false);
                            data.setTimelapseMultiplier(1);
                            ctx.getSource().sendSuccess(() -> Component.literal("Timelapse OFF").withStyle(ChatFormatting.AQUA), true);
                            return 1;
                        }));
    }

    // /otherside status
    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> statusCommand() {
        return Commands.literal("status")
                .executes(ctx -> {
                    // Delegate to beast status for all organism data
                    return beastStatus(ctx.getSource());
                });
    }

    // /otherside whisper <text>
    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> whisperCommand() {
        return Commands.literal("whisper")
                .then(Commands.argument("text", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String text = StringArgumentType.getString(ctx, "text");
                            for (ServerPlayer player : ctx.getSource().getLevel().getServer().getPlayerList().getPlayers()) {
                                player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket(
                                        Component.literal(text).withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.ITALIC)));
                            }
                            DirectorLog.log(ctx.getSource().getLevel(), "WHISPER", ctx.getSource().getPlayer() != null ? ctx.getSource().getPlayer().blockPosition() : BlockPos.ZERO, text);
                            ctx.getSource().sendSuccess(() -> Component.literal("Whisper sent: " + text).withStyle(ChatFormatting.DARK_AQUA), true);
                            return 1;
                        }));
    }

    private static void syncToClients(ServerLevel level) {
        WorldbeastState beast = WorldbeastState.get(level);
        InfectionSavedData data = InfectionSavedData.get(level);
        java.util.List<BreachData> breaches = data.getBreaches();
        double[] bx = new double[breaches.size()];
        double[] bz = new double[breaches.size()];
        for (int i = 0; i < breaches.size(); i++) {
            BlockPos surface = breaches.get(i).getSurfaceBreakout();
            if (surface != null) {
                bx[i] = surface.getX();
                bz[i] = surface.getZ();
            }
        }
        boolean mawActive = beast.getMawManager().isActive();
        BeastSyncPayload payload = new BeastSyncPayload(beast.getMass(), bx, bz, mawActive);
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            PacketDistributor.sendToPlayer(player, payload);
        }
    }



    // ─── Portal Commands ───

    private static final TagKey<Block> FRAME_TAG = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("otherside", "portal_frame"));

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> portalCommand() {
        return Commands.literal("portal")
                .then(Commands.literal("ignite")
                        .executes(ctx -> portalIgnite(ctx.getSource())))
                .then(Commands.literal("validate")
                        .executes(ctx -> portalValidate(ctx.getSource())))
                .then(Commands.literal("collapse")
                        .executes(ctx -> portalCollapse(ctx.getSource())))
                .then(Commands.literal("guardian")
                        .then(Commands.literal("spawn")
                                .then(Commands.literal("cinematic")
                                        .executes(ctx -> guardianSpawn(ctx.getSource(), true)))
                                .executes(ctx -> guardianSpawn(ctx.getSource(), false)))
                        .then(Commands.literal("kill")
                                .executes(ctx -> guardianKill(ctx.getSource())))
                        .then(Commands.literal("state")
                                .executes(ctx -> guardianState(ctx.getSource()))));
    }

    private static int portalIgnite(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Must be run by a player"));
            return 0;
        }

        BlockHitResult hit = raycastFrameBlock(player, 16);
        if (hit == null || hit.getType() == HitResult.Type.MISS) {
            source.sendFailure(Component.literal("No block in range. Look at a portal frame block."));
            return 0;
        }

        BlockPos pos = hit.getBlockPos();
        BlockState state = player.level().getBlockState(pos);
        if (!state.is(FRAME_TAG)) {
            source.sendFailure(Component.literal("Not a portal frame block: " + state.getBlock().getName().getString()));
            return 0;
        }

        PortalFrameShape.Result result = PortalFrameShape.tryCreate(player.level(), pos, true);
        if (!result.valid()) {
            String msg = switch (result.failureReason()) {
                case NO_FRAME_PLANE -> "No valid portal frame plane detected.";
                case TOO_SMALL -> "Frame too small (min 3×3).";
                case TOO_BIG -> "Frame too large (max 32×32).";
                case RING_INCOMPLETE -> "Ring incomplete: " + result.failureDetail();
                case INTERIOR_BLOCKED -> "Interior blocked: " + result.failureDetail();
            };
            source.sendFailure(Component.literal(msg));
            return 0;
        }

        IgnitionSequence sequence = new IgnitionSequence(
                (ServerLevel) player.level(), result, player.getUUID());

        if (IgnitionManager.startIgnition(sequence)) {
            source.sendSuccess(() -> Component.literal("Portal ignition started! " +
                    result.width() + "×" + result.height() + " frame, " +
                    result.ringPositions().size() + " ring blocks")
                    .withStyle(ChatFormatting.DARK_AQUA), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Ignition rejected (overlapping sequence)"));
            return 0;
        }
    }

    private static int portalValidate(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Must be run by a player"));
            return 0;
        }

        BlockHitResult hit = raycastFrameBlock(player, 16);
        if (hit == null || hit.getType() == HitResult.Type.MISS) {
            source.sendFailure(Component.literal("No block in range."));
            return 0;
        }

        BlockPos pos = hit.getBlockPos();
        BlockState state = player.level().getBlockState(pos);
        if (!state.is(FRAME_TAG)) {
            source.sendFailure(Component.literal("Not a frame block: " + state.getBlock().getName().getString() +
                    " at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
            return 0;
        }

        // Test with autoClear=true
        PortalFrameShape.Result resultClear = PortalFrameShape.tryCreate(player.level(), pos, true);
        // Test with autoClear=false
        PortalFrameShape.Result resultStrict = PortalFrameShape.tryCreate(player.level(), pos, false);

        source.sendSuccess(() -> Component.literal("=== Portal Validation ===").withStyle(ChatFormatting.DARK_AQUA), false);
        source.sendSuccess(() -> Component.literal("Frame block: " + state.getBlock().getName().getString() +
                " at [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]").withStyle(ChatFormatting.AQUA), false);

        // Dump neighbors for debugging
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            BlockState nState = player.level().getBlockState(neighbor);
            String name = nState.getBlock().builtInRegistryHolder().key().location().toString();
            boolean isFrame = nState.is(PortalFrameShape.FRAME_TAG);
            boolean isReplaceable = nState.canBeReplaced();
            boolean isClearable = nState.is(PortalFrameShape.CLEARABLE_TAG);
            final String msg = "  " + dir.getName() + ": " + name + " (frame=" + isFrame + 
                    " replace=" + isReplaceable + " clear=" + isClearable + ")";
            source.sendSuccess(() -> Component.literal(msg).withStyle(ChatFormatting.GRAY), false);
        }

        if (resultClear.valid()) {
            source.sendSuccess(() -> Component.literal("✓ VALID (autoClear=true): " +
                    resultClear.width() + "×" + resultClear.height() + " interior, axis=" + resultClear.axis() +
                    ", ring=" + resultClear.ringPositions().size() + " blocks")
                    .withStyle(ChatFormatting.GREEN), false);
        } else {
            source.sendSuccess(() -> Component.literal("✗ INVALID (autoClear=true): " +
                    resultClear.failureReason() + " — " + resultClear.failureDetail())
                    .withStyle(ChatFormatting.RED), false);
        }

        if (resultStrict.valid()) {
            source.sendSuccess(() -> Component.literal("✓ VALID (autoClear=false): strict pass")
                    .withStyle(ChatFormatting.GREEN), false);
        } else {
            source.sendSuccess(() -> Component.literal("✗ INVALID (autoClear=false): " +
                    resultStrict.failureReason() + " — " + resultStrict.failureDetail())
                    .withStyle(ChatFormatting.YELLOW), false);
        }

        return 1;
    }

    private static int portalCollapse(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Must be run by a player"));
            return 0;
        }

        BlockHitResult hit = raycastFrameBlock(player, 16);
        if (hit == null || hit.getType() == HitResult.Type.MISS) {
            source.sendFailure(Component.literal("No block in range."));
            return 0;
        }

        BlockPos pos = hit.getBlockPos();
        BlockState state = player.level().getBlockState(pos);

        // Must be looking at a portal block
        if (!state.is(ModBlocks.OTHERSIDE_PORTAL.get())) {
            source.sendFailure(Component.literal("Not a portal block. Look at an active portal."));
            return 0;
        }

        // Break the portal block — chain collapse will handle the rest
        player.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        source.sendSuccess(() -> Component.literal("Portal collapsed.").withStyle(ChatFormatting.AQUA), true);
        return 1;
    }

    /**
     * Raycast from player's eye position in look direction up to maxDistance.
     */
    private static BlockHitResult raycastFrameBlock(ServerPlayer player, double maxDistance) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(maxDistance));

        return player.level().clip(new ClipContext(
                eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }

    // ─── Guardian commands ───

    private static int guardianSpawn(CommandSourceStack source, boolean cinematic) {
        ServerPlayer player = source.getPlayer();
        if (player == null) { source.sendFailure(Component.literal("Must be run by a player")); return 0; }

        BlockHitResult hit = raycastFrameBlock(player, 16);
        if (hit == null || hit.getType() == HitResult.Type.MISS) {
            source.sendFailure(Component.literal("No block in range."));
            return 0;
        }

        BlockPos pos = hit.getBlockPos();
        ServerLevel level = (ServerLevel) player.level();

        // Validate the frame to get shape data
        PortalFrameShape.Result result = PortalFrameShape.tryCreate(level, pos, true);
        if (!result.valid()) {
            // Try looking at a portal block directly — find its portal center
            PortalSavedData data = PortalSavedData.get(level);
            PortalSavedData.PortalEntry entry = data.findPortalContaining(level.dimension(), pos);
            if (entry != null) {
                boolean spawned = GuardianManager.spawnGuardian(level, entry.center, null, player.getUUID());
                if (spawned) {
                    data.setGuardianActive(level.dimension(), entry.center, GuardianManager.getLastSpawnedUUID());
                    source.sendSuccess(() -> Component.literal("Guardian spawned at " + entry.center)
                            .withStyle(ChatFormatting.DARK_AQUA), true);
                    return 1;
                }
            }
            source.sendFailure(Component.literal("No valid portal frame found."));
            return 0;
        }

        // Calculate center
        int cx = 0, cy = 0, cz = 0;
        for (BlockPos p : result.interiorPositions()) { cx += p.getX(); cy += p.getY(); cz += p.getZ(); }
        int count = result.interiorPositions().size();
        BlockPos center = new BlockPos(cx / count, cy / count, cz / count);

        boolean spawned = GuardianManager.spawnGuardian(level, center, result, player.getUUID());
        if (spawned) {
            PortalSavedData data = PortalSavedData.get(level);
            data.registerPortal(level.dimension(), center, result.axis(), result.bottomLeft(),
                    result.width(), result.height(), player.getUUID());
            data.setGuardianActive(level.dimension(), center, GuardianManager.getLastSpawnedUUID());
            source.sendSuccess(() -> Component.literal("Guardian spawned at " + center)
                    .withStyle(ChatFormatting.DARK_AQUA), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Failed to spawn guardian (no valid position)."));
            return 0;
        }
    }

    private static int guardianKill(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) { source.sendFailure(Component.literal("Must be run by a player")); return 0; }

        ServerLevel level = (ServerLevel) player.level();

        // Find nearby guardians
        var guardians = level.getEntities(EntityType.WARDEN,
                player.getBoundingBox().inflate(32),
                w -> w.getTags().contains(GuardianManager.GUARDIAN_TAG));

        if (guardians.isEmpty()) {
            source.sendFailure(Component.literal("No guardian found within 32 blocks."));
            return 0;
        }

        for (var warden : guardians) {
            warden.kill();
        }

        source.sendSuccess(() -> Component.literal("Killed " + guardians.size() + " guardian(s). Seal lifted.")
                .withStyle(ChatFormatting.DARK_AQUA), true);
        return 1;
    }

    private static int guardianState(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) { source.sendFailure(Component.literal("Must be run by a player")); return 0; }

        ServerLevel level = (ServerLevel) player.level();
        PortalSavedData data = PortalSavedData.get(level);

        // Try to find portal from raycasted block
        BlockHitResult hit = raycastFrameBlock(player, 16);
        if (hit != null && hit.getType() != HitResult.Type.MISS) {
            BlockPos pos = hit.getBlockPos();
            PortalSavedData.PortalEntry entry = data.findPortalContaining(level.dimension(), pos);
            if (entry != null) {
                source.sendSuccess(() -> Component.literal("Portal at " + entry.center +
                        ": guardianState=" + entry.guardianState +
                        (entry.guardianUUID != null ? ", uuid=" + entry.guardianUUID : "") +
                        ", firstIgnition=" + entry.firstIgnition)
                        .withStyle(ChatFormatting.AQUA), false);
                return 1;
            }
        }

        // Fallback: list all portals
        var entries = data.getAllEntries();
        if (entries.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No registered portals.").withStyle(ChatFormatting.GRAY), false);
        } else {
            source.sendSuccess(() -> Component.literal("=== Registered Portals ===").withStyle(ChatFormatting.DARK_AQUA), false);
            for (PortalSavedData.PortalEntry e : entries) {
                final PortalSavedData.PortalEntry entry = e;
                source.sendSuccess(() -> Component.literal("  " + entry.dimension.location() + " " +
                        entry.center + ": " + entry.guardianState +
                        (entry.firstIgnition ? " (first)" : " (re-lit)"))
                        .withStyle(ChatFormatting.AQUA), false);
            }
        }
        return 1;
    }
}
