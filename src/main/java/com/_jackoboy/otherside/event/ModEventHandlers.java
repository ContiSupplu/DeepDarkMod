package com._jackoboy.otherside.event;

import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.command.OthersideCommands;
import com._jackoboy.otherside.dimension.ArrivalScript;
import com._jackoboy.otherside.dimension.DimensionRulesManager;
import com._jackoboy.otherside.dimension.HeldLightManager;
import com._jackoboy.otherside.dimension.LightSuppressionHandler;
import com._jackoboy.otherside.dimension.ResonanceManager;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.infection.*;
import com._jackoboy.otherside.infection.WorldbeastState;
import com._jackoboy.otherside.entity.ListeningBloomEntity;
import com._jackoboy.otherside.network.BeastSyncPayload;
import com._jackoboy.otherside.network.BreachBorderPayload;
import com._jackoboy.otherside.network.ModNetworking;
import com._jackoboy.otherside.portal.GuardianManager;
import com._jackoboy.otherside.portal.IgnitionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = OthersideMod.MOD_ID)
public class ModEventHandlers {
    private static final SpreadEngine SPREAD_ENGINE = new SpreadEngine();
    private static final WeatherDriver WEATHER_DRIVER = new WeatherDriver();
    private static int syncTimer = 0;
    private static int beastTickTimer = 0;
    private static int borderSyncTimer = 0;
    private static int guardianCheckTimer = 0;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        ServerLevel overworld = event.getServer().overworld();

        InfectionSavedData data = InfectionSavedData.get(overworld);

        // Initialize on first tick if needed
        if (!data.isInitialized()) {
            BreachManager.initializePrimaryBreach(overworld, data);
        }

        // Grow breach columns
        BreachManager.tickColumns(overworld, data);

        // Tick vein network pulses (every server tick, NOT the 20-tick beast cadence)
        VeinNetwork.tickPulses(overworld);

        // Run spread engine
        SPREAD_ENGINE.tick(overworld);

        // Run weather driver (forces vanilla rain near breaches)
        WEATHER_DRIVER.tick(overworld);

        // Tick portal ignition sequences
        IgnitionManager.tick(overworld);

        // Tick guardian manager (behavior overrides for tagged Wardens)
        GuardianManager.tick(overworld);

        // ── Otherside dimension rules ──
        ServerLevel othersideLevel = event.getServer().getLevel(DimensionRulesManager.OTHERSIDE_DIM);
        if (othersideLevel != null) {
            DimensionRulesManager.tick(othersideLevel);
            LightSuppressionHandler.tickFireTracker(othersideLevel);
            ResonanceManager.tick(othersideLevel);
            HeldLightManager.tick(othersideLevel);
            ArrivalScript.tick(othersideLevel);
            VeinNetwork.tickPulses(othersideLevel);
        }

        // Check for missing guardians periodically
        guardianCheckTimer++;
        if (guardianCheckTimer >= 200) { // Every 10 seconds
            guardianCheckTimer = 0;
            GuardianManager.checkPersistence(overworld);
        }

        // Tick worldbeast state (every 20 ticks = 1 second)
        beastTickTimer++;
        if (beastTickTimer >= 20) {
            beastTickTimer = 0;
            WorldbeastState beast = WorldbeastState.get(overworld);
            // Migration check (runs once for old worlds)
            InfectionSavedData infData = InfectionSavedData.get(overworld);
            if (!beast.isMigratedFromPhases() && infData.getLegacyPhaseNumber() > 0) {
                beast.migrateFromPhases(infData.getLegacyPhaseNumber(), overworld);
                // Enqueue backfill: all frontier chunks
                for (BreachData bd : infData.getBreaches()) {
                    for (long packed : bd.getFrontier()) {
                        BlockPos bp = BlockPos.of(packed);
                        beast.enqueueClaimCheck(new net.minecraft.world.level.ChunkPos(bp).toLong());
                    }
                    // Also chunks near surface breakout
                    BlockPos surface = bd.getSurfaceBreakout();
                    if (surface != null) {
                        net.minecraft.world.level.ChunkPos sc = new net.minecraft.world.level.ChunkPos(surface);
                        for (int dx = -8; dx <= 8; dx++) {
                            for (int dz = -8; dz <= 8; dz++) {
                                beast.enqueueClaimCheck(net.minecraft.world.level.ChunkPos.asLong(sc.x + dx, sc.z + dz));
                            }
                        }
                    }
                }
            }
            beast.tickWorldbeast(overworld);
            // Update lastKnownPos for all online players every tick
            for (ServerPlayer p : overworld.getServer().getPlayerList().getPlayers()) {
                WorldbeastState.AttentionData attn = beast.getAttentionData(p.getUUID());
                if (attn != null) {
                    attn.lastKnownPos = p.blockPosition();
                }
            }
        }

        // Sync beast state to clients periodically
        syncTimer++;
        if (syncTimer >= 100) { // Every 5 seconds
            syncTimer = 0;
            syncBeastToClients(overworld);
        }

        // Sync breach border columns to clients periodically
        borderSyncTimer++;
        if (borderSyncTimer >= 60) { // Every 3 seconds
            borderSyncTimer = 0;
            syncBreachBorderToClients(overworld, data);
        }
    }

    private static void syncBeastToClients(ServerLevel level) {
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

    private static void syncBreachBorderToClients(ServerLevel level, InfectionSavedData data) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            double px = player.getX();
            double pz = player.getZ();
            double range = 96.0;
            double rangeSq = range * range;

            // Collect unique (x, z) frontier columns within range
            Set<Long> columnSet = new HashSet<>();
            for (BreachData breach : data.getBreaches()) {
                for (long packed : breach.getFrontier()) {
                    int bx = BlockPos.getX(packed);
                    int bz = BlockPos.getZ(packed);
                    double dx = bx + 0.5 - px;
                    double dz = bz + 0.5 - pz;
                    if (dx * dx + dz * dz <= rangeSq) {
                        // Pack as column (y=0) for dedup by x,z
                        columnSet.add(BlockPos.asLong(bx, 0, bz));
                    }
                }
            }

            // Cap at 512 columns
            long[] columns;
            if (columnSet.size() <= 512) {
                columns = new long[columnSet.size()];
                int i = 0;
                for (long l : columnSet) columns[i++] = l;
            } else {
                columns = new long[512];
                int i = 0;
                for (long l : columnSet) {
                    columns[i++] = l;
                    if (i >= 512) break;
                }
            }

            PacketDistributor.sendToPlayer(player, new BreachBorderPayload(columns));
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        OthersideCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerJoin(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerLevel overworld = player.server.overworld();
            InfectionSavedData data = InfectionSavedData.get(overworld);
            java.util.List<BreachData> breaches = data.getBreaches();
            if (!breaches.isEmpty()) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6[Otherside] §eBreach locations:"));
                for (int i = 0; i < breaches.size(); i++) {
                    BlockPos pos = breaches.get(i).getSurfaceBreakout();
                    if (pos != null) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "  §7Breach " + (i + 1) + ": §b" + pos.getX() + " " + pos.getY() + " " + pos.getZ()
                                + " §7(/tp " + pos.getX() + " " + (pos.getY() + 5) + " " + pos.getZ() + ")"));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level() instanceof ServerLevel serverLevel) {
            if (event.getEntity() instanceof net.minecraft.world.entity.monster.warden.Warden warden) {
                if (warden.getTags().contains(GuardianManager.GUARDIAN_TAG)) {
                    GuardianManager.onGuardianDeath(serverLevel, warden, event.getSource().getEntity());
                }
            }

            // Drone kill attention (+6)
            if (event.getSource().getEntity() instanceof ServerPlayer killer) {
                // Check if the killed entity is a drone (any mob on sculk body)
                WorldbeastState beast = WorldbeastState.get(serverLevel);
                beast.addAttention(killer.getUUID(), 6.0f, serverLevel.getGameTime());
                // W4: notify nearby blooms of combat noise
                ListeningBloomEntity.onNoiseNear(serverLevel, event.getEntity().blockPosition());
            }
        }
    }

    /**
     * Block natural Warden spawns (from shriekers) while a portal guardian is alive.
     * Only non-guardian Wardens are blocked — our guardian itself spawns fine.
     */
    @SubscribeEvent
    public static void onEntityJoinLevel(net.neoforged.neoforge.event.entity.EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.monster.warden.Warden warden) {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                // Don't block our own guardian
                if (warden.getTags().contains(GuardianManager.GUARDIAN_TAG)) return;

                // If a guardian fight is active, cancel this natural Warden spawn
                if (GuardianManager.hasActiveGuardian(serverLevel)) {
                    event.setCanceled(true);
                    OthersideMod.LOGGER.info("[GUARDIAN] Blocked natural Warden spawn at {} during guardian fight",
                            warden.blockPosition());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        WorldbeastState beast = WorldbeastState.get(level);
        BlockPos pos = event.getPos();

        // Record player placement
        beast.recordPlayerPlacement(pos);

        // Attention from placing near body
        long gameTime = level.getGameTime();
        // Check if near claimed chunk (within 24 blocks of any claimed chunk)
        // Simplified: just add base attention for any block placement
        beast.addAttention(player.getUUID(), 0.5f, gameTime);

        // Extra attention for light blocks
        BlockState placed = event.getPlacedBlock();
        if (placed.getLightEmission(level, pos) > 0) {
            float lightAttention = 1.5f; // base light
            // Check for loud-light tag
            TagKey<Block> loudLightTag = TagKey.create(Registries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(OthersideMod.MOD_ID, "loud_lights"));
            if (placed.is(loudLightTag)) {
                lightAttention = 4.0f;
            }
            beast.addAttention(player.getUUID(), lightAttention, gameTime);
        }

        // W4: notify nearby blooms of placement noise
        ListeningBloomEntity.onNoiseNear(level, pos);
    }

    // W4: Block break events — the most common noise
    @SubscribeEvent
    public static void onBlockBreak(net.neoforged.neoforge.event.level.BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockPos pos = event.getPos();

        // Small attention bump for breaking blocks
        WorldbeastState beast = WorldbeastState.get(level);
        beast.addAttention(player.getUUID(), 0.3f, level.getGameTime());

        // Notify nearby blooms
        ListeningBloomEntity.onNoiseNear(level, pos);
    }

    @SubscribeEvent
    public static void onExplosion(net.neoforged.neoforge.event.level.ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        WorldbeastState beast = WorldbeastState.get(level);
        long gameTime = level.getGameTime();

        // Try to find the causing player
        net.minecraft.world.entity.LivingEntity source = event.getExplosion().getIndirectSourceEntity();
        if (source instanceof ServerPlayer player) {
            beast.addAttention(player.getUUID(), 20.0f, gameTime);
        } else {
            // Fallback: nearest player
            BlockPos center = BlockPos.containing(event.getExplosion().center());
            net.minecraft.world.entity.player.Player nearestPlayer = level.getNearestPlayer(center.getX(), center.getY(), center.getZ(), 64, false);
            if (nearestPlayer instanceof ServerPlayer nearest) {
                beast.addAttention(nearest.getUUID(), 20.0f, gameTime);
            }
        }

        // W4: notify nearby blooms of explosion noise
        BlockPos blastCenter = BlockPos.containing(event.getExplosion().center());
        ListeningBloomEntity.onNoiseNear(level, blastCenter);
    }

    @SubscribeEvent
    public static void onChunkWatch(net.neoforged.neoforge.event.level.ChunkWatchEvent.Watch event) {
        ServerLevel level = event.getLevel();
        if (!level.dimension().equals(net.minecraft.world.level.Level.OVERWORLD)) return;
        WorldbeastState beast = WorldbeastState.get(level);
        beast.recordExploredChunk(event.getPos().toLong());
    }

    @SubscribeEvent
    public static void onBellRing(net.neoforged.neoforge.event.level.BlockEvent.NeighborNotifyEvent event) {
        // Bell ring detection: bells cause neighbor notify when rung
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        BlockState state = event.getState();
        if (!state.is(net.minecraft.world.level.block.Blocks.BELL)) return;

        WorldbeastState beast = WorldbeastState.get(level);
        long gameTime = level.getGameTime();
        BlockPos pos = event.getPos();

        // Find nearest player within 16 blocks
        net.minecraft.world.entity.player.Player nearestPlayer = level.getNearestPlayer(
                pos.getX(), pos.getY(), pos.getZ(), 16, false);
        if (nearestPlayer instanceof ServerPlayer sp) {
            beast.addAttention(sp.getUUID(), 10.0f, gameTime);
        }

        // W3: Bell force-close on active Maw
        if (beast.getMawManager().isActive()) {
            beast.getMawManager().forceClose(level, pos, beast);
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        VeinNetwork.clear();
    }

    @SubscribeEvent
    public static void onPlayerLogout(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerLevel level = (ServerLevel) player.level();
        WorldbeastState beast = WorldbeastState.get(level);
        WorldbeastState.AttentionData attn = beast.getAttentionData(player.getUUID());
        if (attn != null) {
            attn.lastKnownPos = player.blockPosition();
            beast.setDirty();
        }
    }
}
