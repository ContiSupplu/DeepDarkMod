package com._jackoboy.otherside.entity;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Echo Soul — the beast's spawned hunter spirit.
 * <p>
 * Emerges from claimed ground, drifts, snaps its head to lock onto a player, and rushes.
 * Killable — the fightable layer of the unfightable beast (Law 2).
 * <p>
 * State machine: EMERGING → WANDER ⇄ STALK → LOCK → CHASE → ATTACK ↻
 *                any → HURT (flinch)   triggered → SCREAM   death → DISSIPATE
 * <p>
 * Non-persistent (manager-owned); no drops on death.
 */
public class EchoSoulEntity extends Monster {

    // ── State machine ─────────────────────────────────────────────────
    public enum SoulState {
        EMERGING, WANDER, STALK, LOCK, CHASE,
        ATTACK_SWIPE, ATTACK_SLAM, ATTACK_LUNGE, ATTACK_FLURRY,
        SCREAM, HURT, DISSIPATING
    }

    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(EchoSoulEntity.class, EntityDataSerializers.INT);

    // ── Animation states (client-side) — matches EchoSoulModel contract ──
    public final AnimationState idleFloatState   = new AnimationState();
    public final AnimationState yearnState       = new AnimationState();
    public final AnimationState stalkState       = new AnimationState();
    public final AnimationState detectLockState  = new AnimationState();
    public final AnimationState chaseState       = new AnimationState();
    public final AnimationState swipeState       = new AnimationState();
    public final AnimationState slamState        = new AnimationState();
    public final AnimationState lungeState       = new AnimationState();
    public final AnimationState flurryState      = new AnimationState();
    public final AnimationState screamState      = new AnimationState();
    public final AnimationState hurtState        = new AnimationState();
    public final AnimationState spawnState       = new AnimationState();
    public final AnimationState dissipateState   = new AnimationState();

    // ── Server state ─────────────────────────────────────────────────
    private int stateTimer = 0;
    @Nullable private UUID targetUUID = null;
    @Nullable private Player cachedTarget = null;
    private SoulState lastAttack = null; // prevent same attack twice in a row
    private boolean hasScreamed = false;
    private boolean useYearn = false; // alternate idle_float / yearn in wander
    private int wanderCycleCount = 0;
    private boolean isDissipating = false; // flag to suppress default death
    private int reabsorbTimer = 0; // ticks since last near a player
    private boolean spawnedAware = false; // danger-spawned souls start in STALK

    // ── Spawn mode (set by manager before addFreshEntity) ────────────
    public enum SpawnMode { DANGER, NATURAL }
    private SpawnMode spawnMode = SpawnMode.NATURAL;

    public EchoSoulEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        // No gravity — spirits float
        this.setNoGravity(true);
    }

    // ── Attributes ───────────────────────────────────────────────────
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 14.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1);
    }

    // ── Registration helpers ─────────────────────────────────────────
    @Override
    public boolean shouldBeSaved() { return false; }

    @Override
    public boolean removeWhenFarAway(double distSq) { return true; }

    @Override
    protected boolean shouldDespawnInPeaceful() { return false; }

    // ── No drops ─────────────────────────────────────────────────────
    @Override
    protected void dropAllDeathLoot(ServerLevel level, DamageSource source) {
        // Echo souls leave no items
    }

    @Override
    protected boolean shouldDropLoot() { return false; }

    // ── Synced data ──────────────────────────────────────────────────
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, SoulState.EMERGING.ordinal());
    }

    public SoulState getState() {
        return SoulState.values()[this.entityData.get(DATA_STATE)];
    }

    public void setState(SoulState newState) {
        if (getState() == newState) return;
        this.entityData.set(DATA_STATE, newState.ordinal());
        this.stateTimer = 0;
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

    private void stopAllAnimations() {
        idleFloatState.stop(); yearnState.stop(); stalkState.stop();
        detectLockState.stop(); chaseState.stop();
        swipeState.stop(); slamState.stop(); lungeState.stop(); flurryState.stop();
        screamState.stop(); hurtState.stop(); spawnState.stop(); dissipateState.stop();
    }

    private void startAnimationForState(SoulState state) {
        switch (state) {
            case EMERGING -> spawnState.start(this.tickCount);
            case WANDER -> {
                if (useYearn) yearnState.start(this.tickCount);
                else idleFloatState.start(this.tickCount);
            }
            case STALK -> stalkState.start(this.tickCount);
            case LOCK -> detectLockState.start(this.tickCount);
            case CHASE -> chaseState.start(this.tickCount);
            case ATTACK_SWIPE -> swipeState.start(this.tickCount);
            case ATTACK_SLAM -> slamState.start(this.tickCount);
            case ATTACK_LUNGE -> lungeState.start(this.tickCount);
            case ATTACK_FLURRY -> flurryState.start(this.tickCount);
            case SCREAM -> screamState.start(this.tickCount);
            case HURT -> hurtState.start(this.tickCount);
            case DISSIPATING -> dissipateState.start(this.tickCount);
        }
    }

    // ── Dynamic head contract (for EchoSoulModel) ────────────────────
    /**
     * True when the head should procedurally track the target (STALK/LOCK/CHASE/ATTACK).
     */
    public boolean isAwareOfTarget() {
        SoulState s = getState();
        return s == SoulState.STALK || s == SoulState.LOCK || s == SoulState.CHASE
                || s == SoulState.ATTACK_SWIPE || s == SoulState.ATTACK_SLAM
                || s == SoulState.ATTACK_LUNGE || s == SoulState.ATTACK_FLURRY;
    }

    /**
     * True during LOCK — triggers the creepy up-tilt + head-cock + twitch in the model.
     */
    public boolean isLocked() {
        return getState() == SoulState.LOCK;
    }

    // Head-turn SPEED (the snap velocity — not just range)
    @Override
    public int getMaxHeadYRot() { return 80; }

    @Override
    public int getHeadRotSpeed() { return 80; }

    // ── Spawn configuration (called by manager before addFreshEntity) ──
    public void setSpawnMode(SpawnMode mode) {
        this.spawnMode = mode;
        if (mode == SpawnMode.DANGER) {
            this.spawnedAware = true;
        }
    }

    public SpawnMode getSpawnMode() { return spawnMode; }

    public void setInitialTarget(@Nullable Player player) {
        if (player != null) {
            this.targetUUID = player.getUUID();
            this.cachedTarget = player;
        }
    }

    // ── Damage / death ───────────────────────────────────────────────
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isDissipating) return false;

        boolean result = super.hurt(source, amount);
        if (result && !this.level().isClientSide) {
            // Cancel attack windups on hit (the player's stagger window)
            SoulState s = getState();
            if (s == SoulState.ATTACK_SWIPE || s == SoulState.ATTACK_SLAM
                    || s == SoulState.ATTACK_LUNGE || s == SoulState.ATTACK_FLURRY) {
                setState(SoulState.HURT);
            } else if (s != SoulState.HURT && s != SoulState.DISSIPATING && s != SoulState.EMERGING) {
                setState(SoulState.HURT);
            }

            // Scream when badly hurt (below 30% hp) and haven't screamed yet
            if (this.getHealth() < this.getMaxHealth() * 0.3f && !hasScreamed) {
                // Will transition to SCREAM after HURT finishes
                hasScreamed = true;
            }
        }
        return result;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && !isDissipating) {
            isDissipating = true;
            setState(SoulState.DISSIPATING);

            // Log the kill
            if (source.getEntity() instanceof Player player) {
                DirectorLog.log((ServerLevel) this.level(), "ECHO_SOUL_KILLED", this.blockPosition(),
                        "by=" + player.getName().getString());
            } else {
                DirectorLog.log((ServerLevel) this.level(), "ECHO_SOUL_KILLED", this.blockPosition(), "natural");
            }

            // Don't call super.die() — we hold the entity through the dissipate animation
            this.setHealth(1); // keep alive through the anim
            this.setInvulnerable(true);
        }
    }

    // ── Server tick ──────────────────────────────────────────────────
    @Override
    public void aiStep() {
        super.aiStep();
        if (!(this.level() instanceof ServerLevel level)) return;

        stateTimer++;

        // Resolve cached target
        if (cachedTarget == null && targetUUID != null) {
            cachedTarget = level.getPlayerByUUID(targetUUID);
        }
        if (cachedTarget != null && (cachedTarget.isRemoved() || cachedTarget.isDeadOrDying() || cachedTarget.isSpectator())) {
            cachedTarget = null;
            targetUUID = null;
        }

        // Track target with look control every aware tick (feeds netHeadYaw/headPitch for the model)
        if (isAwareOfTarget() && cachedTarget != null) {
            this.getLookControl().setLookAt(cachedTarget, (float) getMaxHeadYRot(), (float) getMaxHeadXRot());
        }

        // Reabsorption timer — souls far from all players dissipate
        if (getState() != SoulState.DISSIPATING && getState() != SoulState.EMERGING) {
            boolean nearPlayer = false;
            for (Player p : level.players()) {
                if (p.distanceToSqr(this) < 48 * 48) {
                    nearPlayer = true;
                    break;
                }
            }
            if (!nearPlayer) {
                reabsorbTimer++;
                int timeout = OthersideConfig.SERVER.echoSoulReabsorbTimeoutTicks.get();
                if (reabsorbTimer >= timeout) {
                    startDissipate("reabsorb_timeout");
                    return;
                }
            } else {
                reabsorbTimer = 0;
            }
        }

        // State machine
        switch (getState()) {
            case EMERGING -> tickEmerging(level);
            case WANDER -> tickWander(level);
            case STALK -> tickStalk(level);
            case LOCK -> tickLock(level);
            case CHASE -> tickChase(level);
            case ATTACK_SWIPE -> tickAttack(level, 16); // 0.8s
            case ATTACK_SLAM -> tickAttack(level, 20);  // 1.0s
            case ATTACK_LUNGE -> tickAttack(level, 14);  // 0.72s
            case ATTACK_FLURRY -> tickAttack(level, 26); // 1.3s
            case SCREAM -> tickScream(level);
            case HURT -> tickHurt(level);
            case DISSIPATING -> tickDissipate(level);
        }
    }

    // ── State handlers ───────────────────────────────────────────────

    private void tickEmerging(ServerLevel level) {
        // SPAWN_EMERGE = 6.5s = 130 ticks
        if (stateTimer >= 130) {
            if (spawnedAware && cachedTarget != null) {
                setState(SoulState.STALK);
            } else {
                setState(SoulState.WANDER);
            }
        }
    }

    private void tickWander(ServerLevel level) {
        // Drift slowly — no pathfinding target, gentle float
        // Detection check every 10 ticks
        if (stateTimer % 10 == 0) {
            Player detected = detectPlayer(level);
            if (detected != null) {
                targetUUID = detected.getUUID();
                cachedTarget = detected;
                setState(SoulState.STALK);
                return;
            }
        }

        // Alternate idle_float / yearn every cycle (4s / 6s)
        int cycleDuration = useYearn ? 120 : 80;
        if (stateTimer >= cycleDuration) {
            useYearn = !useYearn;
            wanderCycleCount++;
            setState(SoulState.WANDER); // restart with new anim

            // Gentle random drift
            if (wanderCycleCount % 2 == 0) {
                double dx = (this.random.nextDouble() - 0.5) * 8;
                double dz = (this.random.nextDouble() - 0.5) * 8;
                this.getNavigation().moveTo(this.getX() + dx, this.getY(), this.getZ() + dz, 0.5);
            }
        }
    }

    private void tickStalk(ServerLevel level) {
        if (cachedTarget == null) {
            setState(SoulState.WANDER);
            return;
        }

        // Check amethyst / light counterplay
        if (isNearAmethyst(cachedTarget.blockPosition())) {
            // Amethyst repels — drop target and wander
            cachedTarget = null;
            targetUUID = null;
            setState(SoulState.WANDER);
            return;
        }

        // Move toward target slowly
        this.getNavigation().moveTo(cachedTarget, 0.6);

        // Check LOS + range for lock
        double dist = this.distanceTo(cachedTarget);
        int detectRange = OthersideConfig.SERVER.echoSoulDetectRange.get();

        if (dist > detectRange * 1.5) {
            // Lost target — too far
            cachedTarget = null;
            targetUUID = null;
            setState(SoulState.WANDER);
            return;
        }

        if (dist < detectRange && hasLineOfSight(cachedTarget)) {
            // Close enough with LOS — LOCK
            setState(SoulState.LOCK);
            DirectorLog.log(level, "ECHO_SOUL_LOCK", this.blockPosition(),
                    "target=" + cachedTarget.getName().getString());

            // Play lock sound
            level.playSound(null, this.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK,
                    SoundSource.HOSTILE, 0.6F, 1.8F);
        }
    }

    private void tickLock(ServerLevel level) {
        if (cachedTarget == null || cachedTarget.isRemoved()) {
            setState(SoulState.WANDER);
            return;
        }

        // LOS broken → drop to STALK
        if (!hasLineOfSight(cachedTarget)) {
            setState(SoulState.STALK);
            return;
        }

        // Amethyst breaks lock
        if (isNearAmethyst(cachedTarget.blockPosition())) {
            cachedTarget = null;
            targetUUID = null;
            setState(SoulState.WANDER);
            return;
        }

        // Hold the lock for the configured duration (~1.1s = 22 ticks)
        int lockDuration = OthersideConfig.SERVER.echoSoulLockHoldTicks.get();
        if (stateTimer >= lockDuration) {
            setState(SoulState.CHASE);
        }
    }

    private void tickChase(ServerLevel level) {
        if (cachedTarget == null || cachedTarget.isRemoved()) {
            setState(SoulState.WANDER);
            return;
        }

        // Chase at full speed
        this.getNavigation().moveTo(cachedTarget, 1.0);

        double dist = this.distanceTo(cachedTarget);

        // LOS check — lose sight → back to stalk
        if (stateTimer % 20 == 0 && !hasLineOfSight(cachedTarget)) {
            setState(SoulState.STALK);
            return;
        }

        // Light deters — if the soul is in a bright area, hesitate
        int lightLevel = level.getMaxLocalRawBrightness(this.blockPosition());
        if (lightLevel >= OthersideConfig.SERVER.echoSoulLightDeterLevel.get()) {
            // Slow down in bright areas
            this.getNavigation().moveTo(cachedTarget, 0.3);
        }

        // In melee range → attack
        if (dist < 2.5) {
            pickAttack(dist);
        }
    }

    private void pickAttack(double dist) {
        // Contextual attack selection — don't repeat the same one
        List<SoulState> options = new ArrayList<>();
        options.add(SoulState.ATTACK_SWIPE); // default

        if (dist > 1.5) options.add(SoulState.ATTACK_LUNGE); // gap-close
        options.add(SoulState.ATTACK_SLAM); // heavy, telegraphed

        // Flurry if target is low health (cornered/finisher)
        if (cachedTarget != null && cachedTarget.getHealth() < cachedTarget.getMaxHealth() * 0.4f) {
            options.add(SoulState.ATTACK_FLURRY);
        }

        // Remove last attack to prevent repeats
        if (lastAttack != null) options.remove(lastAttack);

        SoulState chosen = options.get(this.random.nextInt(options.size()));
        lastAttack = chosen;
        setState(chosen);
    }

    private void tickAttack(ServerLevel level, int durationTicks) {
        if (cachedTarget == null) {
            setState(SoulState.CHASE);
            return;
        }

        // Deal damage at roughly 60% through the animation (the hit frame)
        int hitFrame = (int) (durationTicks * 0.6);
        if (stateTimer == hitFrame) {
            if (this.distanceTo(cachedTarget) < 3.0) {
                float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                // Slam does extra damage
                if (getState() == SoulState.ATTACK_SLAM) damage *= 1.5f;
                cachedTarget.hurt(this.damageSources().mobAttack(this), damage);
            }
        }

        // Flurry hits twice
        if (getState() == SoulState.ATTACK_FLURRY) {
            int secondHit = (int) (durationTicks * 0.85);
            if (stateTimer == secondHit && this.distanceTo(cachedTarget) < 3.0) {
                float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.7f;
                cachedTarget.hurt(this.damageSources().mobAttack(this), damage);
            }
        }

        if (stateTimer >= durationTicks) {
            // After attack, chase again (or scream if badly hurt)
            if (hasScreamed && this.getHealth() < this.getMaxHealth() * 0.3f) {
                // Already screamed flag is set in hurt() — transition handled there
            }
            setState(SoulState.CHASE);
        }
    }

    private void tickScream(ServerLevel level) {
        // SCREAM_WAIL = 1.9s = 38 ticks
        if (stateTimer == 1) {
            // Play scream sound
            level.playSound(null, this.blockPosition(), SoundEvents.WARDEN_ROAR,
                    SoundSource.HOSTILE, 1.5F, 1.6F);
            DirectorLog.log(level, "ECHO_SOUL_SCREAM", this.blockPosition(), "");

            // Alert nearby souls — they converge on our target
            if (cachedTarget != null) {
                AABB area = this.getBoundingBox().inflate(32);
                for (EchoSoulEntity soul : level.getEntitiesOfClass(EchoSoulEntity.class, area)) {
                    if (soul != this && soul.getState() == SoulState.WANDER) {
                        soul.setInitialTarget(cachedTarget);
                        soul.setState(SoulState.STALK);
                    }
                }
            }

            // Gentle dread pulse — short darkness effect on nearby players (2s)
            for (Player player : level.players()) {
                if (player.distanceToSqr(this) < 16 * 16) {
                    // Apply brief darkness effect (40 ticks = 2s)
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.DARKNESS, 40, 0, false, false, true));
                }
            }
        }

        if (stateTimer >= 38) {
            setState(SoulState.CHASE);
        }
    }

    private void tickHurt(ServerLevel level) {
        // HURT = 0.45s = 9 ticks
        if (stateTimer >= 9) {
            // If badly hurt and hasn't screamed, scream
            if (this.getHealth() < this.getMaxHealth() * 0.3f && hasScreamed) {
                setState(SoulState.SCREAM);
                hasScreamed = false; // allow one more scream on re-injury
            } else if (cachedTarget != null) {
                setState(SoulState.CHASE);
            } else {
                setState(SoulState.WANDER);
            }
        }
    }

    private void tickDissipate(ServerLevel level) {
        // DISSIPATE = 2.2s = 44 ticks
        if (stateTimer >= 44) {
            // Spawn echo dust particles
            for (int i = 0; i < 15; i++) {
                level.sendParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getY() + 1, this.getZ(),
                        1, 0.3, 0.5, 0.3, 0.02);
            }
            this.discard();
        }
    }

    // ── Detection ────────────────────────────────────────────────────
    @Nullable
    private Player detectPlayer(ServerLevel level) {
        int range = OthersideConfig.SERVER.echoSoulDetectRange.get();
        int lightDeter = OthersideConfig.SERVER.echoSoulLightDeterLevel.get();

        // Don't detect in bright light
        if (level.getMaxLocalRawBrightness(this.blockPosition()) >= lightDeter) {
            return null;
        }

        Player closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Player player : level.players()) {
            if (player.isSpectator() || player.isCreative()) continue;
            double dist = this.distanceTo(player);
            if (dist > range) continue;
            if (!hasLineOfSight(player)) continue;

            // Amethyst ward — player near amethyst is not detectable
            if (isNearAmethyst(player.blockPosition())) continue;

            if (dist < closestDist) {
                closestDist = dist;
                closest = player;
            }
        }
        return closest;
    }

    // ── Counterplay helpers ──────────────────────────────────────────
    /**
     * Check if a position is near placed amethyst (within 3 blocks).
     */
    public static boolean isNearAmethyst(BlockPos center) {
        // (Stub — same pattern as MawTentacleEntity.isAmethystAnchored)
        return false; // Will be fully implemented in Phase B/C
    }

    /**
     * Check if any amethyst blocks are within range. Full implementation.
     */
    public boolean isNearAmethystFull(BlockPos center, Level level) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockState bs = level.getBlockState(center.offset(dx, dy, dz));
                    if (bs.getBlock() instanceof AmethystBlock
                            || bs.getBlock() instanceof AmethystClusterBlock
                            || bs.getBlock() instanceof BuddingAmethystBlock) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // ── Dissipate helper ─────────────────────────────────────────────
    public void startDissipate(String reason) {
        if (isDissipating) return;
        isDissipating = true;
        setInvulnerable(true);
        setState(SoulState.DISSIPATING);
        if (!this.level().isClientSide) {
            DirectorLog.log((ServerLevel) this.level(), "ECHO_SOUL_DISSIPATE", this.blockPosition(),
                    "reason=" + reason);
        }
    }

    // ── Gravity override (spirits float) ─────────────────────────────
    @Override
    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance()) {
            // Move like normal but ignore gravity — gentle float
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9)); // friction
        }
    }

    // ── Serialization (non-persistent, minimal) ──────────────────────
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        // Non-persistent — no meaningful data to save
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    // ── Client-side init ─────────────────────────────────────────────
    @Override
    public void handleEntityEvent(byte id) {
        super.handleEntityEvent(id);
    }

    // Suppress mob AI goals — we drive everything from the state machine
    @Override
    protected void registerGoals() {
        // No goals — state machine drives all behavior
    }
}
