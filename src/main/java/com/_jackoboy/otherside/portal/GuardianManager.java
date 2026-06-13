package com._jackoboy.otherside.portal;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.monster.warden.WardenAi;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import com._jackoboy.otherside.network.StyledBossBarPayload;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Manages Portal Guardian Wardens — spawn, behavior overrides, death handling, persistence.
 *
 * Guardians are vanilla Wardens tagged with "otherside:portal_guardian" and carrying
 * persistent NBT linking them to their home portal. They never dig away, are leashed
 * to their portal, and their death unseals the portal for passage.
 */
public class GuardianManager {

    /** Tag applied to all guardian Wardens for identification. */
    public static final String GUARDIAN_TAG = "otherside:portal_guardian";

    /** NBT key storing the home portal data on the Warden entity. */
    private static final String NBT_PORTAL_HOME = "PortalHome";
    private static final String NBT_PORTAL_DIM = "Dimension";
    private static final String NBT_PORTAL_X = "X";
    private static final String NBT_PORTAL_Y = "Y";
    private static final String NBT_PORTAL_Z = "Z";

    /** UUID of the last successfully spawned guardian. */
    @Nullable
    private static UUID lastSpawnedUUID = null;

    /** Set of player UUIDs who have already triggered SEAL_REJECT director log. */
    private static final Set<UUID> sealRejectLogged = new HashSet<>();

    /** Active boss bars keyed by guardian UUID. */
    private static final Map<UUID, ServerBossEvent> activeBossBars = new HashMap<>();

    /**
     * Called every server tick. Manages behavior overrides for all tagged guardians.
     */
    public static void tick(ServerLevel level) {
        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof Warden warden)) continue;
            if (!warden.getTags().contains(GUARDIAN_TAG)) continue;
            if (!warden.isAlive()) continue;

            tickGuardian(level, warden);
        }
    }

    /**
     * Per-tick behavior overrides for a single guardian Warden.
     */
    private static void tickGuardian(ServerLevel level, Warden warden) {
        // ── Anti-dig: push back the dig cooldown every 20 ticks ──
        if (warden.tickCount % 20 == 0) {
            WardenAi.setDigCooldown(warden);
        }

        // ── Leash enforcement: return home if too far ──
        BlockPos home = getHomePortal(warden);
        if (home != null) {
            double leashRadius = OthersideConfig.SERVER.leashRadius.get();
            double distSq = warden.blockPosition().distSqr(home);

            if (distSq > leashRadius * leashRadius) {
                // Drop targets and navigate home
                warden.getBrain().eraseMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET);
                warden.getNavigation().moveTo(home.getX() + 0.5, home.getY(), home.getZ() + 0.5, 1.2);

                // Regenerate 2 HP/s = 0.1 HP/tick
                if (warden.getHealth() < warden.getMaxHealth()) {
                    warden.heal(0.1F);
                }
            }
        }

        // ── Boss bar: update HP and player proximity ──
        ServerBossEvent bossBar = activeBossBars.get(warden.getUUID());
        if (bossBar != null) {
            bossBar.setProgress(warden.getHealth() / warden.getMaxHealth());

            // Add/remove players within 32 blocks
            for (ServerPlayer player : level.players()) {
                double dist = player.blockPosition().distSqr(warden.blockPosition());
                if (dist <= 32 * 32) {
                    if (!bossBar.getPlayers().contains(player)) {
                        bossBar.addPlayer(player);
                        // Send styled payload so client renders custom bar
                        PacketDistributor.sendToPlayer(player,
                                new StyledBossBarPayload(bossBar.getId(), 0));
                    }
                } else {
                    bossBar.removePlayer(player);
                }
            }
        }
    }

    /**
     * Spawns a guardian Warden at the portal base. Uses MobSpawnType.TRIGGERED
     * so Warden.finalizeSpawn sets EMERGING pose and brain state automatically.
     *
     * @return true if the guardian spawned successfully
     */
    public static boolean spawnGuardian(ServerLevel level, BlockPos portalCenter,
                                         PortalFrameShape.Result shape, UUID igniterUUID) {
        // Find spawn position: base center, 1 block in front of portal plane on igniter's side
        BlockPos spawnPos = findSpawnPosition(level, portalCenter, shape, igniterUUID);
        if (spawnPos == null) {
            OthersideMod.LOGGER.warn("[GUARDIAN] No valid spawn position for portal at {}", portalCenter);
            return false;
        }

        // Spawn using EntityType.create + finalizeSpawn (vanilla shrieker path)
        Warden warden = EntityType.WARDEN.create(level);
        if (warden == null) {
            OthersideMod.LOGGER.warn("[GUARDIAN] Failed to create Warden entity");
            return false;
        }
        // Calculate yaw to face toward the igniter
        float yaw = 0;
        ServerPlayer igniterForFacing = level.getServer().getPlayerList().getPlayer(igniterUUID);
        if (igniterForFacing != null) {
            double dx = igniterForFacing.getX() - (spawnPos.getX() + 0.5);
            double dz = igniterForFacing.getZ() - (spawnPos.getZ() + 0.5);
            yaw = (float)(Math.atan2(-dx, dz) * (180.0 / Math.PI));
        }

        warden.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, yaw, 0);
        warden.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.TRIGGERED, null);

        // Apply guardian properties
        warden.setPersistenceRequired();
        warden.addTag(GUARDIAN_TAG);

        // Store home portal in persistent NBT
        CompoundTag portalTag = new CompoundTag();
        portalTag.putString(NBT_PORTAL_DIM, level.dimension().location().toString());
        portalTag.putInt(NBT_PORTAL_X, portalCenter.getX());
        portalTag.putInt(NBT_PORTAL_Y, portalCenter.getY());
        portalTag.putInt(NBT_PORTAL_Z, portalCenter.getZ());
        warden.getPersistentData().put(NBT_PORTAL_HOME, portalTag);

        // Spawn into the world FIRST — anger must be applied after entity is live
        if (!level.addFreshEntity(warden)) {
            OthersideMod.LOGGER.warn("[GUARDIAN] Failed to add Warden to world at {}", spawnPos);
            return false;
        }

        // NOW apply anger — must happen after addFreshEntity or it gets lost
        ServerPlayer igniter = level.getServer().getPlayerList().getPlayer(igniterUUID);
        if (igniter != null) {
            // Max anger (150) triggers full pursuit behavior
            warden.increaseAngerAt(igniter, 150, true);
            // Explicitly set the attack target in the brain so it chases immediately after emerge
            warden.getBrain().setMemory(
                    net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET, igniter);
        }

        lastSpawnedUUID = warden.getUUID();

        // Create boss bar
        ServerBossEvent bossBar = new ServerBossEvent(
                Component.literal("Warden"), // Name won't be rendered — baked into art
                BossEvent.BossBarColor.BLUE,
                BossEvent.BossBarOverlay.PROGRESS);
        bossBar.setProgress(1.0F);
        activeBossBars.put(warden.getUUID(), bossBar);

        OthersideMod.LOGGER.info("[GUARDIAN] Spawned guardian {} at {} for portal at {}",
                warden.getUUID(), spawnPos, portalCenter);
        return true;
    }

    /**
     * Handles a guardian Warden's death. Called from LivingDeathEvent handler.
     */
    public static void onGuardianDeath(ServerLevel level, Warden warden, @Nullable Entity killer) {
        // Remove boss bar
        ServerBossEvent bossBar = activeBossBars.remove(warden.getUUID());
        if (bossBar != null) {
            bossBar.removeAllPlayers();
        }

        BlockPos home = getHomePortal(warden);
        if (home == null) {
            OthersideMod.LOGGER.warn("[GUARDIAN] Dying guardian has no home portal NBT");
            return;
        }

        ResourceKey<Level> dim = level.dimension();

        // Unseal: mark portal as DEFEATED
        PortalSavedData data = PortalSavedData.get(level);
        data.setDefeated(dim, home);

        // Portal "exhale" effect
        level.playSound(null, home, SoundEvents.CONDUIT_DEACTIVATE,
                SoundSource.BLOCKS, 1.2F, 1.4F);

        // 30 sculk_soul particles off the portal surface
        for (int i = 0; i < 30; i++) {
            double px = home.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 4;
            double py = home.getY() + level.random.nextDouble() * 3;
            double pz = home.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 4;
            level.sendParticles(ParticleTypes.SCULK_SOUL, px, py, pz,
                    1, 0.1, 0.3, 0.1, 0.02);
        }

        // Director log
        DirectorLog.log(level, "GUARDIAN_SLAIN", home,
                String.format("Guardian %s killed by %s at portal %s",
                        warden.getUUID(),
                        killer != null ? killer.getName().getString() : "unknown",
                        home));

        // Grant advancement to killer
        if (killer instanceof ServerPlayer player) {
            grantGatecrasherAdvancement(player);
        }

        // Drop Warden Heartcatalyst (guaranteed)
        net.minecraft.world.item.ItemStack heartcatalyst =
                new net.minecraft.world.item.ItemStack(
                        com._jackoboy.otherside.registry.ModItems.WARDEN_HEARTCATALYST.get());
        warden.spawnAtLocation(heartcatalyst);

        OthersideMod.LOGGER.info("[GUARDIAN] Guardian slain at portal {}, seal lifted", home);
    }

    /**
     * Checks for missing guardians on world load and respawns them.
     * Called periodically from the tick handler.
     */
    public static void checkPersistence(ServerLevel level) {
        PortalSavedData data = PortalSavedData.get(level);

        for (PortalSavedData.PortalEntry entry : data.getAllEntries()) {
            if (entry.guardianState != PortalSavedData.GuardianState.ACTIVE) continue;
            if (entry.guardianUUID == null) continue;
            if (!entry.dimension.equals(level.dimension())) continue;

            // Check if this guardian's chunk is loaded
            if (!level.isLoaded(entry.center)) continue;

            // Look for the guardian entity
            Entity existing = level.getEntity(entry.guardianUUID);
            if (existing != null && existing.isAlive()) continue;

            // Guardian is missing — respawn without cinematic
            OthersideMod.LOGGER.info("[GUARDIAN] Respawning missing guardian for portal at {}", entry.center);
            boolean spawned = spawnGuardianDirect(level, entry.center, entry.igniterUUID);
            if (spawned) {
                data.setGuardianActive(entry.dimension, entry.center, lastSpawnedUUID);
            } else {
                OthersideMod.LOGGER.warn("[GUARDIAN] Failed to respawn guardian at {}", entry.center);
            }
        }
    }

    /**
     * Direct spawn without needing the full shape — for respawns.
     */
    private static boolean spawnGuardianDirect(ServerLevel level, BlockPos portalCenter, UUID igniterUUID) {
        // Find any solid block near the portal base
        BlockPos spawnPos = findSimpleSpawnPos(level, portalCenter);
        if (spawnPos == null) return false;

        Warden warden = EntityType.WARDEN.create(level);
        if (warden == null) return false;

        warden.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
        warden.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos),
                MobSpawnType.TRIGGERED, null);
        warden.setPersistenceRequired();
        warden.addTag(GUARDIAN_TAG);

        CompoundTag portalTag = new CompoundTag();
        portalTag.putString(NBT_PORTAL_DIM, level.dimension().location().toString());
        portalTag.putInt(NBT_PORTAL_X, portalCenter.getX());
        portalTag.putInt(NBT_PORTAL_Y, portalCenter.getY());
        portalTag.putInt(NBT_PORTAL_Z, portalCenter.getZ());
        warden.getPersistentData().put(NBT_PORTAL_HOME, portalTag);

        return level.addFreshEntity(warden);
    }

    // ─── Spawn position helpers ───

    @Nullable
    private static BlockPos findSpawnPosition(ServerLevel level, BlockPos portalCenter,
                                               PortalFrameShape.Result shape, UUID igniterUUID) {
        // Determine which side of the portal the igniter is on
        Direction facing = getIgniterFacing(level, portalCenter, shape.axis(), igniterUUID);

        // 1 block in front of portal plane on igniter's side
        BlockPos candidate = portalCenter.relative(facing);

        // Snap to floor: scan down up to 3 blocks for solid ground
        for (int dy = 0; dy <= 3; dy++) {
            BlockPos check = candidate.below(dy);
            BlockState below = level.getBlockState(check.below());
            if (below.isSolid() && level.getBlockState(check).isAir()) {
                return check;
            }
        }

        // Fallback: try base center itself
        return findSimpleSpawnPos(level, portalCenter);
    }

    @Nullable
    private static BlockPos findSimpleSpawnPos(ServerLevel level, BlockPos center) {
        // Try each horizontal direction from center, scanning down
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos candidate = center.relative(dir);
            for (int dy = 0; dy <= 3; dy++) {
                BlockPos check = candidate.below(dy);
                BlockState below = level.getBlockState(check.below());
                if (below.isSolid() && level.getBlockState(check).isAir()) {
                    return check;
                }
            }
        }
        // Last resort: center itself
        return center;
    }

    private static Direction getIgniterFacing(ServerLevel level, BlockPos center,
                                               Direction.Axis portalAxis, UUID igniterUUID) {
        ServerPlayer igniter = level.getServer().getPlayerList().getPlayer(igniterUUID);
        if (igniter != null) {
            Vec3 toPlayer = igniter.position().subtract(Vec3.atCenterOf(center));
            if (portalAxis == Direction.Axis.X) {
                // Portal is in XY plane, thin along Z
                return toPlayer.z >= 0 ? Direction.SOUTH : Direction.NORTH;
            } else {
                // Portal is in ZY plane, thin along X
                return toPlayer.x >= 0 ? Direction.EAST : Direction.WEST;
            }
        }
        // Fallback: arbitrary direction perpendicular to portal
        return portalAxis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST;
    }

    // ─── NBT helpers ───

    @Nullable
    public static BlockPos getHomePortal(Warden warden) {
        CompoundTag data = warden.getPersistentData();
        if (!data.contains(NBT_PORTAL_HOME)) return null;
        CompoundTag portal = data.getCompound(NBT_PORTAL_HOME);
        return new BlockPos(portal.getInt(NBT_PORTAL_X), portal.getInt(NBT_PORTAL_Y), portal.getInt(NBT_PORTAL_Z));
    }

    @Nullable
    public static ResourceKey<Level> getHomeDimension(Warden warden) {
        CompoundTag data = warden.getPersistentData();
        if (!data.contains(NBT_PORTAL_HOME)) return null;
        String dimStr = data.getCompound(NBT_PORTAL_HOME).getString(NBT_PORTAL_DIM);
        return ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION,
                ResourceLocation.parse(dimStr));
    }

    @Nullable
    public static UUID getLastSpawnedUUID() { return lastSpawnedUUID; }

    // ─── Advancement ───

    private static void grantGatecrasherAdvancement(ServerPlayer player) {
        var advancement = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(OthersideMod.MOD_ID, "gatecrasher"));
        if (advancement != null) {
            // Grant the "impossible" criterion programmatically
            player.getAdvancements().award(advancement, "impossible");
        }
    }

    // ─── Seal reject logging ───

    public static boolean shouldLogSealReject(UUID playerUUID) {
        return sealRejectLogged.add(playerUUID); // returns true if first time
    }

    public static void clearSealRejectLog() {
        sealRejectLogged.clear();
    }

    /**
     * Returns true if ANY portal in this dimension has an ACTIVE guardian.
     * Used to suppress natural Warden spawns during the fight.
     */
    public static boolean hasActiveGuardian(ServerLevel level) {
        PortalSavedData data = PortalSavedData.get(level);
        for (PortalSavedData.PortalEntry entry : data.getAllEntries()) {
            if (entry.dimension.equals(level.dimension())
                    && entry.guardianState == PortalSavedData.GuardianState.ACTIVE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given Warden entity is a portal guardian.
     */
    public static boolean isGuardian(Entity entity) {
        return entity instanceof Warden && entity.getTags().contains(GUARDIAN_TAG);
    }
}
