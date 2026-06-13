package com._jackoboy.otherside.entity;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.infection.WorldbeastState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import com._jackoboy.otherside.registry.ModSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Listening Bloom — the beast's sensory flower.
 * Invulnerable, immobile, AI-less. Tilts its dish to face the nearest player (ease-out lerp),
 * detects noise, and feeds attention. The sense→state→response loop's sensor.
 */
public class ListeningBloomEntity extends Mob {

    // ── State machine ─────────────────────────────────────────────────
    public enum BloomState {
        DORMANT, UNFURL, IDLE_LISTENING, TWITCH, SWEEP, ALERT_LOCK, FOLD
    }

    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(ListeningBloomEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SCALE =
            SynchedEntityData.defineId(ListeningBloomEntity.class, EntityDataSerializers.FLOAT);

    // ── Animation states (model reads these) ─────────────────────────
    public final AnimationState unfurlState        = new AnimationState();
    public final AnimationState foldState          = new AnimationState();
    public final AnimationState sweepState         = new AnimationState();
    public final AnimationState idleListeningState = new AnimationState();
    public final AnimationState twitchState        = new AnimationState();
    public final AnimationState alertLockState     = new AnimationState();

    // ── Server state ─────────────────────────────────────────────────
    private int stateTimer = 0;
    @Nullable private Player sensedPlayer = null;
    private int noiseBuffer = 0;
    private int noiseDecayTimer = 0;
    private boolean firstBloomLogged = false;

    // Bud maturation
    private int maturationTimer = 0;

    // Client anim init fix (same pattern as echo soul)
    private boolean clientAnimInit = false;

    // Alert cooldown (don't re-alert immediately)
    private int alertCooldown = 0;

    // Sprint detection
    private int sprintNoiseTimer = 0;

    // ── Constructor ──────────────────────────────────────────────────
    public ListeningBloomEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setInvulnerable(true);
        this.setPersistenceRequired();
    }

    // ── Attributes ───────────────────────────────────────────────────
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    // ── Immobility ───────────────────────────────────────────────────
    @Override protected void registerGoals() {} // no AI goals
    @Override public void travel(Vec3 travelVector) {} // no movement, no gravity
    @Override public boolean isPushable() { return false; }
    @Override public void push(Entity entity) {} // can't be pushed
    @Override public boolean hurt(DamageSource source, float amount) { return false; } // invulnerable
    @Override public boolean removeWhenFarAway(double distSq) { return false; }
    @Override public boolean shouldBeSaved() { return true; } // persist across reload

    // ── Head rotation (don't clamp yaw — dish is 360°) ──────────────
    @Override public int getMaxHeadYRot() { return 360; }
    @Override public int getHeadRotSpeed() { return 360; } // we drive head manually, don't let vanilla clamp

    // ── Synced data ──────────────────────────────────────────────────
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, BloomState.DORMANT.ordinal());
        builder.define(DATA_SCALE, 1.0F);
    }

    public BloomState getState() {
        return BloomState.values()[this.entityData.get(DATA_STATE)];
    }

    public void setState(BloomState newState) {
        if (getState() == newState) return;
        this.entityData.set(DATA_STATE, newState.ordinal());
        this.stateTimer = 0;
    }

    public float getBloomScale() { return this.entityData.get(DATA_SCALE); }
    public void setBloomScale(float s) { this.entityData.set(DATA_SCALE, s); }
    public boolean isBud() { return getBloomScale() < 0.6F; }

    // ── Model contract ───────────────────────────────────────────────
    /** True when the bloom is tracking a player — procedural head overrides sweep. */
    public boolean isTracking() {
        BloomState s = getState();
        return sensedPlayer != null && (s == BloomState.IDLE_LISTENING || s == BloomState.TWITCH
                || s == BloomState.ALERT_LOCK);
    }

    // ── Client animation sync ────────────────────────────────────────
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_STATE.equals(key) && this.level().isClientSide) {
            stopAllAnimations();
            startAnimationForState(getState());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && !clientAnimInit) {
            clientAnimInit = true;
            startAnimationForState(getState());
        }
    }

    private void stopAllAnimations() {
        unfurlState.stop(); foldState.stop(); sweepState.stop();
        idleListeningState.stop(); twitchState.stop(); alertLockState.stop();
    }

    /**
     * Stop all animations EXCEPT foldState — used when transitioning to DORMANT
     * so the fold clip holds its final (closed) frame.
     */
    private void stopAllAnimationsExceptFold() {
        unfurlState.stop(); sweepState.stop();
        idleListeningState.stop(); twitchState.stop(); alertLockState.stop();
    }

    private void startAnimationForState(BloomState state) {
        if (state == BloomState.DORMANT) {
            // "Closed" = FOLD held at its last keyframe. Don't stop foldState.
            // If fold wasn't already running (e.g. first spawn), start it so
            // the model renders closed, not the open rest pose.
            stopAllAnimationsExceptFold();
            if (!foldState.isStarted()) {
                foldState.start(this.tickCount);
            }
        } else {
            // Any other state: stop everything (including fold), start the new clip
            stopAllAnimations();
            switch (state) {
                case UNFURL -> unfurlState.start(this.tickCount);
                case FOLD -> foldState.start(this.tickCount);
                case SWEEP -> sweepState.start(this.tickCount);
                case IDLE_LISTENING -> idleListeningState.start(this.tickCount);
                case TWITCH -> twitchState.start(this.tickCount);
                case ALERT_LOCK -> alertLockState.start(this.tickCount);
                default -> {}
            }
        }
    }

    // ── Server tick ──────────────────────────────────────────────────
    @Override
    public void aiStep() {
        super.aiStep();
        if (!(this.level() instanceof ServerLevel level)) return;

        stateTimer++;
        if (alertCooldown > 0) alertCooldown--;

        // Noise buffer decay (1/sec = every 20 ticks)
        noiseDecayTimer++;
        if (noiseDecayTimer >= 20) {
            noiseDecayTimer = 0;
            if (noiseBuffer > 0) noiseBuffer--;
        }

        // Bud maturation
        if (isBud()) {
            maturationTimer++;
            if (maturationTimer >= OthersideConfig.SERVER.bloomBudMaturationTicks.get()) {
                setBloomScale(1.0F);
                if (!this.level().isClientSide) {
                    DirectorLog.log(level, "BLOOM_MATURE", this.blockPosition(), "bud→full");
                }
            }
        }

        // Amethyst muting
        if (OthersideConfig.SERVER.bloomAmethystMutes.get() && isNearAmethyst(this.blockPosition())) {
            if (getState() != BloomState.DORMANT && getState() != BloomState.FOLD) {
                setState(BloomState.FOLD);
            }
            sensedPlayer = null;
            return;
        }

        // Find nearest player
        int senseRadius = OthersideConfig.SERVER.bloomSenseRadius.get();
        sensedPlayer = findNearestSensablePlayer(level, senseRadius);

        // Sprint noise detection (tick-based)
        if (sensedPlayer != null && !sensedPlayer.isCrouching()) {
            Vec3 vel = sensedPlayer.getDeltaMovement();
            double speed = vel.horizontalDistanceSqr();
            if (speed > 0.04) { // ~sprinting speed
                sprintNoiseTimer++;
                if (sprintNoiseTimer >= 10) { // every 0.5s of sprinting
                    sprintNoiseTimer = 0;
                    noiseBuffer++;
                }
            } else {
                sprintNoiseTimer = 0;
            }
        } else {
            sprintNoiseTimer = 0;
        }

        // ── Ease-out head tracking (Fix 3) ───────────────────────────
        if (sensedPlayer != null && isTracking()) {
            double dx = sensedPlayer.getX() - this.getX();
            double dz = sensedPlayer.getZ() - this.getZ();
            double dy = (sensedPlayer.getEyeY()) - (this.getY() + 0.6); // head pivot ~0.6 above origin
            double horizDist = Math.sqrt(dx * dx + dz * dz);

            float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
            float targetPitch = (float) -(Mth.atan2(dy, horizDist) * (180.0 / Math.PI));
            targetPitch = Mth.clamp(targetPitch, -65.0F, 65.0F);

            // Ease-out lerp: fast when far, slows as it locks — "turning its face toward you"
            this.yHeadRot = this.yHeadRotO + Mth.wrapDegrees(targetYaw - this.yHeadRotO) * 0.12F;
            this.setXRot(this.xRotO + (targetPitch - this.xRotO) * 0.12F);
        }

        // ── Feed attention ───────────────────────────────────────────
        if (sensedPlayer != null && getState() != BloomState.DORMANT && getState() != BloomState.FOLD) {
            long gameTime = level.getGameTime();
            float sneakMul = sensedPlayer.isCrouching()
                    ? (float) OthersideConfig.SERVER.bloomSneakFactor.get().doubleValue()
                    : 1.0F;

            // Proximity trickle (per tick → /20 for per-second config value)
            float proxAttn = (float) OthersideConfig.SERVER.bloomProximityAttnPerSec.get().doubleValue() / 20.0F;
            double dist = this.distanceTo(sensedPlayer);
            float distFactor = 1.0F - (float) (dist / senseRadius); // closer = more
            distFactor = Math.max(0, distFactor);
            WorldbeastState beast = WorldbeastState.get(level);
            beast.addAttention(sensedPlayer.getUUID(), proxAttn * distFactor * sneakMul, gameTime);

            // Noise-driven attention
            if (noiseBuffer > 0 && getState() == BloomState.TWITCH) {
                float noiseAttn = (float) OthersideConfig.SERVER.bloomNoiseAttn.get().doubleValue();
                beast.addAttention(sensedPlayer.getUUID(), noiseAttn * sneakMul, gameTime);
            }
        }

        // ── State machine ────────────────────────────────────────────
        BloomState current = getState();
        switch (current) {
            case DORMANT -> tickDormant(level);
            case UNFURL -> tickUnfurl(level);
            case IDLE_LISTENING -> tickIdleListening(level);
            case TWITCH -> tickTwitch(level);
            case SWEEP -> tickSweep(level);
            case ALERT_LOCK -> tickAlertLock(level);
            case FOLD -> tickFold(level);
        }
    }

    // ── State handlers ───────────────────────────────────────────────

    private void tickDormant(ServerLevel level) {
        if (sensedPlayer != null) {
            setState(BloomState.UNFURL);
            level.playSound(null, this.blockPosition(), ModSoundEvents.BLOOM_UNFURL.get(),
                    SoundSource.HOSTILE, 0.6F, 0.8F);
            DirectorLog.log(level, "BLOOM_UNFURL", this.blockPosition(),
                    "target=" + sensedPlayer.getName().getString());
        }
    }

    private void tickUnfurl(ServerLevel level) {
        if (stateTimer >= 56) { // 2.8s = 56 ticks
            setState(BloomState.IDLE_LISTENING);
        }
    }

    private void tickIdleListening(ServerLevel level) {
        if (sensedPlayer == null) {
            setState(BloomState.FOLD);
            return;
        }

        // Noise event → TWITCH
        if (noiseBuffer > 0 && stateTimer > 20) {
            setState(BloomState.TWITCH);
            return;
        }

        // Very close + LOS → ALERT_LOCK
        double dist = this.distanceTo(sensedPlayer);
        if (dist < 6.0 && hasLineOfSight(sensedPlayer) && alertCooldown <= 0) {
            setState(BloomState.ALERT_LOCK);
            return;
        }

        // After a few idle cycles with noise, escalate to SWEEP
        if (stateTimer >= 260 && noiseBuffer > 0) { // 13s = 260t
            setState(BloomState.SWEEP);
        }
    }

    private void tickTwitch(ServerLevel level) {
        if (stateTimer == 1) {
            level.playSound(null, this.blockPosition(), ModSoundEvents.BLOOM_TWITCH.get(),
                    SoundSource.HOSTILE, 0.5F, 0.9F + this.random.nextFloat() * 0.25F);
        }
        if (stateTimer >= 18) { // 0.9s
            // Escalation: repeated noise → SWEEP
            if (noiseBuffer >= 3) {
                setState(BloomState.SWEEP);
            } else {
                setState(BloomState.IDLE_LISTENING);
            }
        }
    }

    private void tickSweep(ServerLevel level) {
        if (sensedPlayer == null) {
            setState(BloomState.FOLD);
            return;
        }

        // During sweep, check for close player → ALERT_LOCK
        double dist = this.distanceTo(sensedPlayer);
        if (dist < 6.0 && hasLineOfSight(sensedPlayer) && alertCooldown <= 0) {
            setState(BloomState.ALERT_LOCK);
            return;
        }

        // After sweep cycle (11s = 220t), go back to idle if quiet
        if (stateTimer >= 220 && noiseBuffer == 0) {
            setState(BloomState.IDLE_LISTENING);
        }
    }

    private void tickAlertLock(ServerLevel level) {
        if (stateTimer == 1) {
            level.playSound(null, this.blockPosition(), ModSoundEvents.BLOOM_ALERT.get(),
                    SoundSource.HOSTILE, 0.5F, 1.5F);

            // Big attention spike
            if (sensedPlayer != null) {
                float alertAttn = (float) OthersideConfig.SERVER.bloomAlertAttn.get().doubleValue();
                float sneakMul = sensedPlayer.isCrouching()
                        ? (float) OthersideConfig.SERVER.bloomSneakFactor.get().doubleValue()
                        : 1.0F;
                WorldbeastState beast = WorldbeastState.get(level);
                beast.addAttention(sensedPlayer.getUUID(), alertAttn * sneakMul, level.getGameTime());

                DirectorLog.log(level, "BLOOM_ALERT", this.blockPosition(),
                        "target=" + sensedPlayer.getName().getString());
            }
        }

        // Particle suppression — the "held breath" (suppress ambient particles)
        // The alert stillness is conveyed by the animation freeze + no particles

        if (stateTimer >= 60) { // 3s
            alertCooldown = 200; // 10s before next alert
            if (sensedPlayer != null) {
                setState(BloomState.IDLE_LISTENING);
            } else {
                setState(BloomState.FOLD);
            }
        }
    }

    private void tickFold(ServerLevel level) {
        if (stateTimer == 1) {
            level.playSound(null, this.blockPosition(), ModSoundEvents.BLOOM_FOLD.get(),
                    SoundSource.HOSTILE, 1.0F, 1.0F);
        }
        if (stateTimer >= 22) { // 1.1s
            setState(BloomState.DORMANT);
        }
    }

    // ── Detection helpers ────────────────────────────────────────────

    @Nullable
    private Player findNearestSensablePlayer(ServerLevel level, int senseRadius) {
        Player closest = null;
        double closestDist = senseRadius;
        for (Player player : level.players()) {
            if (player.isSpectator() || player.isCreative()) continue;
            double dist = this.distanceTo(player);
            if (dist > senseRadius) continue;
            if (dist < closestDist) {
                closestDist = dist;
                closest = player;
            }
        }
        return closest;
    }

    private static boolean isNearAmethyst(BlockPos center) {
        return false; // Stub — fully implemented when amethyst system is in
    }

    // ── Noise event interface (called by ModEventHandlers) ───────────
    /**
     * Notifies all blooms near a noise event. Called from event handlers
     * on block break, place, explosion, combat, etc.
     */
    public static void onNoiseNear(ServerLevel level, BlockPos pos) {
        int range = OthersideConfig.SERVER.bloomSenseRadius.get();
        for (ListeningBloomEntity bloom : level.getEntitiesOfClass(
                ListeningBloomEntity.class, new AABB(pos).inflate(range))) {
            bloom.noiseBuffer++;
        }
    }

    // ── Serialization ────────────────────────────────────────────────
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("bloomState", getState().ordinal());
        tag.putFloat("bloomScale", getBloomScale());
        tag.putInt("maturationTimer", maturationTimer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("bloomState")) {
            int ord = tag.getInt("bloomState");
            if (ord >= 0 && ord < BloomState.values().length) {
                this.entityData.set(DATA_STATE, ord);
            }
        }
        if (tag.contains("bloomScale")) setBloomScale(tag.getFloat("bloomScale"));
        if (tag.contains("maturationTimer")) maturationTimer = tag.getInt("maturationTimer");
    }
}
