package com._jackoboy.otherside.portal;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.network.AudioDuckPayload;
import com._jackoboy.otherside.network.ScreenFxPayload;
import com._jackoboy.otherside.registry.ModBlocks;
import com._jackoboy.otherside.registry.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.UUID;

/**
 * Server-side state machine for the portal ignition cinematic.
 * Ticked every server tick by IgnitionManager.
 *
 * Timeline (v2 — Portal Guardian):
 *   t=0:            CHARGE    — consume catalyst, play charge sound, vignette pulse, rumble
 *   t=20→150:       CRAWL     — convert ring to charged_frame with rising chimes + building sounds
 *   t=150→fill_end: FILL      — fill interior with portal blocks bottom to top
 *   fill_end:       SHOCKWAVE — knockback, particles, director log
 *   fill_end+20:    CINEMATIC — blindness, shrieker, silence, heartbeat, sniffs, guardian spawn, reveal
 *   cinematic_end:  DONE      — remove sequence
 */
public class IgnitionSequence {

    private static final TagKey<Block> CLEARABLE_TAG = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("otherside", "ignition_clearable"));

    private enum Stage { CHARGE, CRAWL, FILL, SHOCKWAVE, CINEMATIC, DONE }

    private final ServerLevel level;
    private final PortalFrameShape.Result shape;
    private final UUID igniterUUID;
    private final BlockPos center;

    private Stage stage = Stage.CHARGE;
    private int tick = 0;
    private boolean done = false;

    // Crawl tracking
    private int crawlIndex = 0;
    private int crawlInterval;

    // Fill tracking
    private int fillRow = 0;
    private int fillTickCounter = 0;
    private int fillStartTick;

    // Shockwave tracking
    private int shockwaveStartTick = -1;

    // Cinematic tracking (all ticks relative to cinematicStartTick)
    private int cinematicStartTick = -1;
    private boolean guardianSpawned = false;
    private boolean guardianSpawnFailed = false;

    // Heartbeat state
    private int nextHeartbeatTick = 0;
    private int heartbeatInterval = 20;

    public IgnitionSequence(ServerLevel level, PortalFrameShape.Result shape, UUID igniterUUID) {
        this.level = level;
        this.shape = shape;
        this.igniterUUID = igniterUUID;

        // Calculate center of the frame
        int cx = 0, cy = 0, cz = 0;
        for (BlockPos pos : shape.interiorPositions()) {
            cx += pos.getX();
            cy += pos.getY();
            cz += pos.getZ();
        }
        int count = shape.interiorPositions().size();
        this.center = new BlockPos(cx / count, cy / count, cz / count);

        // Calculate crawl interval: 120 ticks / ring size, minimum 1
        this.crawlInterval = Math.max(1, 120 / shape.ringPositions().size());
    }

    public void tick() {
        if (done) return;

        switch (stage) {
            case CHARGE -> tickCharge();
            case CRAWL -> tickCrawl();
            case FILL -> tickFill();
            case SHOCKWAVE -> tickShockwave();
            case CINEMATIC -> tickCinematic();
            case DONE -> {} // shouldn't reach
        }

        tick++;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  CHARGE (t=0–20): Initial catalyst reaction — sets the tone
    // ═══════════════════════════════════════════════════════════════════

    private void tickCharge() {
        if (tick == 0) {
            // The catalyst reacts — deep resonance begins
            level.playSound(null, center, ModSoundEvents.PORTAL_CHARGE.get(),
                    SoundSource.BLOCKS, 2.0F, 1.0F);
            // Low warden rumble — something is waking up
            level.playSound(null, center, SoundEvents.WARDEN_AMBIENT,
                    SoundSource.HOSTILE, 0.4F, 0.5F);
            // Sculk sensor activation — the frame is "listening"
            level.playSound(null, center, SoundEvents.SCULK_BLOCK_SPREAD,
                    SoundSource.BLOCKS, 0.8F, 1.4F);

            sendScreenFx(ScreenFxPayload.VIGNETTE_PULSE, 60, 48);
            level.gameEvent(null, GameEvent.RESONATE_15, center);

            // Deep vibration particles radiating from center
            level.sendParticles(ParticleTypes.SCULK_CHARGE_POP,
                    center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5,
                    20, 1.5, 1.5, 1.5, 0.05);

            OthersideMod.LOGGER.info("[PORTAL] Ignition CHARGE at {}", center);
        }

        // t=10: Second pulse — the frame is "answering"
        if (tick == 10) {
            level.playSound(null, center, SoundEvents.RESPAWN_ANCHOR_CHARGE,
                    SoundSource.BLOCKS, 1.0F, 0.5F);
            level.playSound(null, center, SoundEvents.SCULK_BLOCK_SPREAD,
                    SoundSource.BLOCKS, 0.6F, 1.4F);
            // Sculk soul particles — spirits stir
            level.sendParticles(ParticleTypes.SCULK_SOUL,
                    center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5,
                    8, 1.0, 1.0, 1.0, 0.03);
        }

        if (tick >= 20) {
            stage = Stage.CRAWL;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  CRAWL (t=20→150): Ring charges with rising tension
    // ═══════════════════════════════════════════════════════════════════

    private void tickCrawl() {
        // Start the charge swell loop
        if (tick == 20) {
            level.playSound(null, center, ModSoundEvents.PORTAL_CHARGE_SWELL.get(),
                    SoundSource.BLOCKS, 0.3F, 0.8F);
            // Deep conduit hum — building power
            level.playSound(null, center, SoundEvents.CONDUIT_AMBIENT,
                    SoundSource.BLOCKS, 0.6F, 0.5F);
        }

        // Periodic sculk spreading sound — the frame is growing with energy
        if (tick % 30 == 0 && tick > 20) {
            float buildPitch = 0.6F + (float)(tick - 20) / 130.0F * 0.6F;
            level.playSound(null, center, SoundEvents.SCULK_BLOCK_SPREAD,
                    SoundSource.BLOCKS, 0.5F, buildPitch);
        }

        // Mid-crawl tension escalation
        if (tick == 80) {
            level.playSound(null, center, SoundEvents.SCULK_CATALYST_BLOOM,
                    SoundSource.BLOCKS, 0.7F, 0.7F);
            level.playSound(null, center, SoundEvents.WARDEN_TENDRIL_CLICKS,
                    SoundSource.HOSTILE, 0.3F, 0.8F);
        }

        // Late crawl — energy is peaking
        if (tick == 120) {
            level.playSound(null, center, SoundEvents.SCULK_SHRIEKER_SHRIEK,
                    SoundSource.BLOCKS, 0.3F, 1.5F); // Distant shriek, very high pitch
            level.playSound(null, center, SoundEvents.RESPAWN_ANCHOR_CHARGE,
                    SoundSource.BLOCKS, 0.8F, 1.2F);
        }

        // Convert ring blocks with rising pitch chime
        if (tick % crawlInterval == 0 && crawlIndex < shape.ringPositions().size()) {
            BlockPos ringPos = shape.ringPositions().get(crawlIndex);

            if (!level.isLoaded(ringPos)) {
                abort("unloaded block at " + ringPos);
                return;
            }

            level.setBlock(ringPos, ModBlocks.CHARGED_FRAME.get().defaultBlockState(), 3);

            // More particles as crawl progresses
            int particleCount = 5 + (crawlIndex * 3 / shape.ringPositions().size());
            level.sendParticles(ParticleTypes.SCULK_CHARGE_POP,
                    ringPos.getX() + 0.5, ringPos.getY() + 0.5, ringPos.getZ() + 0.5,
                    particleCount, 0.3, 0.3, 0.3, 0.02);

            // Rising chime
            float pitch = 0.5F + (float) crawlIndex / shape.ringPositions().size() * 1.0F;
            level.playSound(null, ringPos, SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.BLOCKS, 0.6F, pitch);

            // Every 4th block: sculk click for rhythmic tension
            if (crawlIndex % 4 == 0) {
                level.playSound(null, ringPos, SoundEvents.SCULK_BLOCK_SPREAD,
                    SoundSource.BLOCKS, 0.4F, pitch + 0.6F);
            }

            crawlIndex++;
        }

        if (tick >= 150 || crawlIndex >= shape.ringPositions().size()) {
            if (tick >= 150) {
                // Ring complete — dramatic flourish
                level.playSound(null, center, SoundEvents.AMETHYST_BLOCK_RESONATE,
                        SoundSource.BLOCKS, 1.2F, 1.5F);
                level.playSound(null, center, SoundEvents.SCULK_CATALYST_BLOOM,
                        SoundSource.BLOCKS, 1.0F, 1.2F);
                level.sendParticles(ParticleTypes.SCULK_SOUL,
                        center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5,
                        15, 2.0, 2.0, 2.0, 0.04);

                stage = Stage.FILL;
                fillStartTick = tick;
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  FILL (t=150→end): Portal blocks fill from bottom to top
    // ═══════════════════════════════════════════════════════════════════

    private void tickFill() {
        if (fillRow >= shape.height()) {
            // Fill complete — brief dramatic pause before shockwave
            level.playSound(null, center, SoundEvents.CONDUIT_ACTIVATE,
                    SoundSource.BLOCKS, 1.0F, 0.6F);
            stage = Stage.SHOCKWAVE;
            shockwaveStartTick = tick + 5;
            return;
        }

        boolean rowHasClearable = false;
        int rowStart = fillRow * shape.width();
        int rowEnd = Math.min(rowStart + shape.width(), shape.interiorPositions().size());
        for (int i = rowStart; i < rowEnd; i++) {
            BlockPos pos = shape.interiorPositions().get(i);
            BlockState state = level.getBlockState(pos);
            if (!state.canBeReplaced() && state.is(CLEARABLE_TAG)) {
                rowHasClearable = true;
                break;
            }
        }
        int ticksPerRow = rowHasClearable ? 6 : 4;

        fillTickCounter++;
        if (fillTickCounter >= ticksPerRow) {
            fillTickCounter = 0;

            for (int i = rowStart; i < rowEnd; i++) {
                BlockPos pos = shape.interiorPositions().get(i);
                BlockState existing = level.getBlockState(pos);

                if (!existing.canBeReplaced() && !existing.is(CLEARABLE_TAG)) {
                    abort("non-clearable block " + existing.getBlock().getName().getString()
                            + " at " + pos);
                    return;
                }

                if (!existing.isAir() && !existing.canBeReplaced()) {
                    level.levelEvent(2001, pos, Block.getId(existing));
                    level.sendParticles(ParticleTypes.SCULK_SOUL,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            3 + level.random.nextInt(3), 0.2, 0.2, 0.2, 0.01);
                    level.playSound(null, pos, SoundEvents.SCULK_BLOCK_BREAK,
                            SoundSource.BLOCKS, 0.8F, 0.8F + level.random.nextFloat() * 0.4F);
                }

                BlockState portalState = ModBlocks.OTHERSIDE_PORTAL.get().defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_AXIS, shape.axis());
                level.setBlock(pos, portalState, 3);
            }

            // Row fill sounds — increasingly intense
            float fillPitch = 0.8F + (float) fillRow / shape.height() * 0.6F;
            level.playSound(null, center, SoundEvents.SCULK_CATALYST_BLOOM,
                    SoundSource.BLOCKS, 0.8F, fillPitch);
            // Warden tendril clicks — each row sounds more ominous
            level.playSound(null, center, SoundEvents.WARDEN_TENDRIL_CLICKS,
                    SoundSource.HOSTILE, 0.15F + fillRow * 0.05F, fillPitch);

            // Portal resonance particles streaming upward from each new row
            for (int i = rowStart; i < rowEnd; i++) {
                BlockPos pos = shape.interiorPositions().get(i);
                level.sendParticles(ParticleTypes.SCULK_SOUL,
                        pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                        2, 0.2, 0.1, 0.2, 0.02);
            }

            fillRow++;
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  SHOCKWAVE: The portal ignites with explosive force
    // ═══════════════════════════════════════════════════════════════════

    private void tickShockwave() {
        if (shockwaveStartTick < 0) {
            shockwaveStartTick = tick;
        }

        int elapsed = tick - shockwaveStartTick;

        if (elapsed == 0) {
            // ── Layered explosion sounds (NO entity.generic.explode) ──
            level.playSound(null, center, SoundEvents.WARDEN_SONIC_BOOM,
                    SoundSource.BLOCKS, 1.2F, 0.7F);
            level.playSound(null, center, SoundEvents.WARDEN_SONIC_CHARGE,
                    SoundSource.BLOCKS, 1.2F, 0.4F);
            level.playSound(null, center, SoundEvents.CONDUIT_DEACTIVATE,
                    SoundSource.BLOCKS, 1.6F, 0.3F);
            // Deep rumble underneath
            level.playSound(null, center, SoundEvents.WARDEN_DEATH,
                    SoundSource.BLOCKS, 0.5F, 0.3F);
            // Respawn anchor deplete — dramatic bass thud
            level.playSound(null, center, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(),
                    SoundSource.BLOCKS, 1.0F, 0.4F);

            // ── Knockback ──
            double kbRadius = OthersideConfig.SERVER.knockbackRadius.get();
            double kbClose = OthersideConfig.SERVER.knockbackStrengthClose.get();
            double kbFar = OthersideConfig.SERVER.knockbackStrengthFar.get();
            double kbVert = OthersideConfig.SERVER.knockbackVertical.get();
            Vec3 centerVec = Vec3.atCenterOf(center);

            for (Entity entity : level.getEntities(null, new net.minecraft.world.phys.AABB(
                    center.getX() - kbRadius, center.getY() - kbRadius, center.getZ() - kbRadius,
                    center.getX() + kbRadius + 1, center.getY() + kbRadius + 1, center.getZ() + kbRadius + 1))) {
                double dist = entity.position().distanceTo(centerVec);
                if (dist > kbRadius || dist < 0.1) continue;

                // lerp: at 3 blocks = kbClose, at kbRadius = kbFar
                double t = Math.max(0, Math.min(1, (dist - 3.0) / (kbRadius - 3.0)));
                double strength = kbClose + t * (kbFar - kbClose);

                Vec3 direction = entity.position().subtract(centerVec).normalize();
                Vec3 knockback = new Vec3(direction.x * strength, kbVert, direction.z * strength);
                entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));

                if (entity instanceof ServerPlayer player) {
                    player.hurtMarked = true;
                }

                // Slow Falling: 3 seconds safety net for city ledge drops
                if (entity instanceof net.minecraft.world.entity.LivingEntity living) {
                    living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0, false, false));
                }
            }

            // ── Particle burst: sculk souls erupting from the portal face ──
            for (int i = 0; i < 40; i++) {
                double px = center.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 6;
                double py = center.getY() + level.random.nextDouble() * 4;
                double pz = center.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 6;
                level.sendParticles(ParticleTypes.SCULK_SOUL, px, py, pz,
                        1, 0.1, 0.3, 0.1, 0.05);
            }

            // ── Game event + director ──
            level.gameEvent(null, GameEvent.EXPLODE, center);
            DirectorLog.log(level, "PORTAL_IGNITED", center,
                    String.format("Portal %dx%d, axis=%s, igniter=%s",
                            shape.width(), shape.height(), shape.axis(), igniterUUID));

            // ── Register portal in SavedData ──
            ResourceKey<Level> dim = level.dimension();
            PortalSavedData portalData = PortalSavedData.get(level);
            portalData.registerPortal(dim, center, shape.axis(), shape.bottomLeft(),
                    shape.width(), shape.height(), igniterUUID);

            OthersideMod.LOGGER.info("[PORTAL] Ignition SHOCKWAVE at {} — {}x{} portal",
                    center, shape.width(), shape.height());
        }

        // Expanding sonic boom particle ring over 20 ticks
        if (elapsed >= 0 && elapsed < 20) {
            float radius = 1.0F + (float) elapsed / 20.0F * 11.0F;
            int particles = (int) (radius * 4);
            for (int i = 0; i < particles; i++) {
                double angle = (double) i / particles * Math.PI * 2;
                double px = center.getX() + 0.5 + Math.cos(angle) * radius;
                double pz = center.getZ() + 0.5 + Math.sin(angle) * radius;
                level.sendParticles(ParticleTypes.SONIC_BOOM,
                        px, center.getY() + 0.5, pz,
                        1, 0, 0, 0, 0);
            }
        }

        // t=10: Secondary rumble — aftershock
        if (elapsed == 10) {
            level.playSound(null, center, SoundEvents.WARDEN_SONIC_CHARGE,
                    SoundSource.BLOCKS, 0.6F, 0.8F);
        }

        if (elapsed >= 20) {
            // Decide whether to enter CINEMATIC or skip
            if (shouldSpawnGuardian()) {
                if (OthersideConfig.SERVER.cinematicBlackout.get()) {
                    stage = Stage.CINEMATIC;
                    cinematicStartTick = tick;
                    nextHeartbeatTick = 110; // relative to cinematic start
                } else {
                    // No cinematic — spawn guardian immediately with emerge
                    boolean spawned = GuardianManager.spawnGuardian(level, center, shape, igniterUUID);
                    if (spawned) {
                        PortalSavedData.get(level).setGuardianActive(level.dimension(), center,
                                GuardianManager.getLastSpawnedUUID());
                        DirectorLog.log(level, "GUARDIAN_EMERGED", center, "Spawned without cinematic");
                    }
                    done = true;
                    stage = Stage.DONE;
                }
            } else {
                done = true;
                stage = Stage.DONE;
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  CINEMATIC: Darkness, dread, and the Guardian emerges
    // ═══════════════════════════════════════════════════════════════════

    private void tickCinematic() {
        int ct = tick - cinematicStartTick; // cinematic-relative tick

        // ── t=10: Darkness falls — warden ambient builds unease ──
        if (ct == 10) {
            level.playSound(null, center, SoundEvents.WARDEN_AMBIENT,
                    SoundSource.HOSTILE, 0.6F, 0.6F);
        }

        // ── t=20: BLINDNESS + DARKNESS + AUDIO DUCK ──
        if (ct == 20) {
            // Apply Blindness AND Darkness to nearby players for maximum dread
            for (ServerPlayer player : level.players()) {
                if (player.blockPosition().distSqr(center) <= 32 * 32) {
                    // Blindness: complete vision loss
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 220, 0, false, false, false));
                    // Darkness: the pulsing dark effect the Warden uses naturally
                    player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 220, 0, false, false, false));
                }
            }
            sendAudioDuck(true, 10, 32);
            OthersideMod.LOGGER.info("[PORTAL] Cinematic BLINDNESS applied at ct={}", ct);
        }

        // ── t=35, 45: SHRIEKER SCREAMS — played directly to player ears ──
        if (ct == 35) {
            playSoundToNearbyPlayers(SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 1.4F, 0.9F);
        }
        if (ct == 45) {
            playSoundToNearbyPlayers(SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 1.2F, 1.1F);
            playSoundToNearbyPlayers(SoundEvents.WARDEN_TENDRIL_CLICKS, SoundSource.HOSTILE, 0.5F, 0.7F);
        }

        // ── t=55: Deep sculk groan — transition to silence ──
        if (ct == 55) {
            level.playSound(null, center, SoundEvents.SCULK_CATALYST_BLOOM,
                    SoundSource.BLOCKS, 0.4F, 0.3F);
        }

        // ── t=70–110: SILENCE (intentional dread gap — do NOT add sounds) ──

        // ── t=110–220: ACCELERATING HEARTBEAT — played directly to ears ──
        if (ct >= 110 && ct <= 220) {
            if (ct >= nextHeartbeatTick) {
                playSoundToNearbyPlayers(SoundEvents.WARDEN_HEARTBEAT, SoundSource.HOSTILE, 1.2F, 1.0F);
                // Accelerate: interval × 0.85, floor 6t
                heartbeatInterval = Math.max(6, (int)(heartbeatInterval * 0.85));
                nextHeartbeatTick = ct + heartbeatInterval;
            }
        }

        // ── t=125: First sniff — it knows you're there ──
        if (ct == 125) {
            playSoundToNearbyPlayers(SoundEvents.WARDEN_SNIFF, SoundSource.HOSTILE, 0.8F, 1.0F);
        }

        // ── t=140, 165, 190: Closer sniffs — it's finding you ──
        if (ct == 140) {
            playSoundToNearbyPlayers(SoundEvents.WARDEN_SNIFF, SoundSource.HOSTILE, 1.0F, 1.0F);
            playSoundToNearbyPlayers(SoundEvents.WARDEN_TENDRIL_CLICKS, SoundSource.HOSTILE, 0.4F, 1.0F);
        }
        if (ct == 165) {
            playSoundToNearbyPlayers(SoundEvents.WARDEN_SNIFF, SoundSource.HOSTILE, 1.1F, 1.0F);
        }
        if (ct == 190) {
            playSoundToNearbyPlayers(SoundEvents.WARDEN_SNIFF, SoundSource.HOSTILE, 1.2F, 0.9F);
            // Angry growl — it's about to emerge
            playSoundToNearbyPlayers(SoundEvents.WARDEN_ANGRY, SoundSource.HOSTILE, 0.6F, 0.7F);
        }

        // ── t=150: GUARDIAN SPAWN ──
        if (ct == 150 && !guardianSpawned && !guardianSpawnFailed) {
            boolean spawned = GuardianManager.spawnGuardian(level, center, shape, igniterUUID);
            if (spawned) {
                guardianSpawned = true;
                PortalSavedData.get(level).setGuardianActive(level.dimension(), center,
                        GuardianManager.getLastSpawnedUUID());
                DirectorLog.log(level, "GUARDIAN_EMERGED", center,
                        "Guardian spawned during cinematic at " + center);
            } else {
                guardianSpawnFailed = true;
                OthersideMod.LOGGER.warn("[PORTAL] Guardian spawn failed at {} — skipping to reveal", center);
            }
        }

        // ── Abort safety: if spawn failed, skip to reveal ──
        if (guardianSpawnFailed && ct < 200) {
            if (ct >= 195) {
                removeBlindness();
                sendAudioDuck(false, 40, 32);
                done = true;
                stage = Stage.DONE;
                return;
            }
            return; // Skip other cinematic events
        }

        // ── t=200: VISION RETURNS — world fades back in ──
        if (ct == 200) {
            removeBlindness();
            // Portal hum — the gateway is alive
            level.playSound(null, center, SoundEvents.CONDUIT_AMBIENT,
                    SoundSource.BLOCKS, 0.8F, 0.8F);
        }

        // ── t=210: The Guardian's emergence sound ──
        if (ct == 210) {
            level.playSound(null, center, SoundEvents.WARDEN_EMERGE,
                    SoundSource.HOSTILE, 1.2F, 0.9F);
        }

        // ── t=220: ROAR + AUDIO RESTORE + DONE ──
        if (ct == 220) {
            level.playSound(null, center, SoundEvents.WARDEN_ROAR,
                    SoundSource.HOSTILE, 1.4F, 0.9F);
            // Apply brief Darkness pulse when vision returns — dramatic reveal
            for (ServerPlayer player : level.players()) {
                if (player.blockPosition().distSqr(center) <= 32 * 32) {
                    player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 40, 0, false, false, false));
                }
            }
            sendAudioDuck(false, 40, 32);

            OthersideMod.LOGGER.info("[PORTAL] Ignition cinematic COMPLETE at {}", center);
            done = true;
            stage = Stage.DONE;
        }
    }

    // ─── Guardian decision ───

    private boolean shouldSpawnGuardian() {
        if (!OthersideConfig.SERVER.guardianEnabled.get()) return false;

        PortalSavedData data = PortalSavedData.get(level);
        PortalSavedData.PortalEntry entry = data.getEntry(level.dimension(), center);
        if (entry == null) return false;

        if (!OthersideConfig.SERVER.guardianEveryIgnition.get()) {
            // DEFEATED = already beaten, skip
            if (entry.guardianState == PortalSavedData.GuardianState.DEFEATED) {
                return false;
            }
            // ACTIVE = guardian already alive (shouldn't happen, but just in case)
            if (entry.guardianState == PortalSavedData.GuardianState.ACTIVE) {
                return false;
            }
            // NONE = never had a guardian — ALWAYS spawn, even on re-ignition.
            // This handles the case where a portal was registered before the
            // guardian system existed, or the previous ignition was interrupted.
        }

        return true;
    }

    // ─── Blindness helper ───

    private void removeBlindness() {
        for (ServerPlayer player : level.players()) {
            if (player.blockPosition().distSqr(center) <= 32 * 32) {
                player.removeEffect(MobEffects.BLINDNESS);
                player.removeEffect(MobEffects.DARKNESS);
            }
        }
    }

    // ─── Abort ───

    private void abort(String reason) {
        OthersideMod.LOGGER.warn("[PORTAL] Ignition ABORTED: {}", reason);
        level.playSound(null, center, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(),
                SoundSource.BLOCKS, 1.0F, 1.0F);
        done = true;
        stage = Stage.DONE;
    }

    // ─── Helpers ───

    private void sendScreenFx(int fxType, int duration, double range) {
        double rangeSq = range * range;
        for (ServerPlayer player : level.players()) {
            if (player.blockPosition().distSqr(center) <= rangeSq) {
                PacketDistributor.sendToPlayer(player, new ScreenFxPayload(fxType, duration));
            }
        }
    }

    private void sendAudioDuck(boolean start, int fadeTicks, double range) {
        double rangeSq = range * range;
        for (ServerPlayer player : level.players()) {
            if (player.blockPosition().distSqr(center) <= rangeSq) {
                PacketDistributor.sendToPlayer(player, new AudioDuckPayload(start, fadeTicks));
            }
        }
    }

    /**
     * Plays a sound directly to nearby players' ears using playNotifySound.
     * This bypasses distance attenuation — critical during blindness when the
     * player has been knocked back from the portal center.
     */
    private void playSoundToNearbyPlayers(net.minecraft.sounds.SoundEvent sound, SoundSource source,
                                           float volume, float pitch) {
        for (ServerPlayer player : level.players()) {
            if (player.blockPosition().distSqr(center) <= 32 * 32) {
                player.playNotifySound(sound, source, volume, pitch);
            }
        }
    }

    // ─── Accessors ───

    public boolean isDone() { return done; }
    public ServerLevel getLevel() { return level; }
    public BlockPos getCenter() { return center; }
    public List<BlockPos> getRingPositions() { return shape.ringPositions(); }
    public List<BlockPos> getInteriorPositions() { return shape.interiorPositions(); }
    public PortalFrameShape.Result getShape() { return shape; }
    public UUID getIgniterUUID() { return igniterUUID; }
}
