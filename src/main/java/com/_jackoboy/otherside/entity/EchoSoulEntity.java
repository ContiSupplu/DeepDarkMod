package com._jackoboy.otherside.entity;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.director.DirectorLog;
import com._jackoboy.otherside.corruption.Corruption;
import com._jackoboy.otherside.infection.WorldbeastState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Echo Soul — the beast's spawned hunter spirit.
 * v4: emerge anim fix, death wail, cascade spawning, frontier leash, no-target despawn.
 */
public class EchoSoulEntity extends Monster {

    // ── State machine ─────────────────────────────────────────────────
    public enum SoulState {
        EMERGING, WANDER, STALK, LOCK, CHASE,
        ATTACK_SWIPE, ATTACK_SLAM, ATTACK_LUNGE, ATTACK_FLURRY,
        SCREAM, HURT, DISSIPATING,
        DEATH_WAIL  // §1: wail before dissolving (at the END so ordinals don't shift)
    }

    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(EchoSoulEntity.class, EntityDataSerializers.INT);

    // ── Animation states ─────────────────────────────────────────────
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
    private SoulState lastAttack = null;
    private boolean hasScreamed = false;
    private boolean useYearn = false;
    private int wanderCycleCount = 0;
    private boolean isDissipating = false;
    private int reabsorbTimer = 0;
    private boolean spawnedAware = false;

    // v2: anti-thrash
    private boolean hasLockedThisTarget = false;
    private int losLostTimer = 0;

    // v2: teleport
    private int teleportCooldown = 0;

    // v2: ambient sound
    private int ambientSoundTimer = 0;

    // v4 §0: client anim init fix
    private boolean clientAnimInit = false;

    // v4 §2: cascade
    private boolean hasSplit = false;

    // v4 §4: no-target despawn
    private int noTargetTimer = 0;

    // v4 §3: frontier leash
    @Nullable private BlockPos homeAnchor = null;
    private int leashStrainTimer = 0;

    // ── Spawn mode ───────────────────────────────────────────────────
    public enum SpawnMode { DANGER, NATURAL }
    private SpawnMode spawnMode = SpawnMode.NATURAL;

    public EchoSoulEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    // ── Attributes ───────────────────────────────────────────────────
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 14.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1);
    }

    // ── Registration helpers ─────────────────────────────────────────
    @Override public boolean shouldBeSaved() { return false; }
    @Override public boolean removeWhenFarAway(double distSq) { return true; }
    @Override protected boolean shouldDespawnInPeaceful() { return false; }
    @Override protected void dropAllDeathLoot(ServerLevel level, DamageSource source) {}
    @Override protected boolean shouldDropLoot() { return false; }

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

    // §0: start initial animation on first client tick
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && !clientAnimInit) {
            clientAnimInit = true;
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
            case WANDER -> { if (useYearn) yearnState.start(this.tickCount); else idleFloatState.start(this.tickCount); }
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
            case DEATH_WAIL -> screamState.start(this.tickCount); // reuse scream clip for death wail
        }
    }

    // ── Dynamic head contract ────────────────────────────────────────
    public boolean isAwareOfTarget() {
        SoulState s = getState();
        return s == SoulState.STALK || s == SoulState.LOCK || s == SoulState.CHASE
                || s == SoulState.ATTACK_SWIPE || s == SoulState.ATTACK_SLAM
                || s == SoulState.ATTACK_LUNGE || s == SoulState.ATTACK_FLURRY;
    }

    public boolean isLocked() { return getState() == SoulState.LOCK; }
    @Override public int getMaxHeadYRot() { return 80; }
    @Override public int getHeadRotSpeed() { return 80; }

    // ── Spawn configuration ──────────────────────────────────────────
    public void setSpawnMode(SpawnMode mode) {
        this.spawnMode = mode;
        if (mode == SpawnMode.DANGER) this.spawnedAware = true;
    }
    public SpawnMode getSpawnMode() { return spawnMode; }

    public void setHomeAnchor(BlockPos pos) { this.homeAnchor = pos; }

    public void setInitialTarget(@Nullable Player player) {
        if (player != null) { this.targetUUID = player.getUUID(); this.cachedTarget = player; }
    }

    /** Expose the current target for cascade spawn (manager hands children the same prey). */
    @Nullable
    public Player getCurrentTargetPlayer() { return cachedTarget; }

    // ── Damage / death ───────────────────────────────────────────────
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isDissipating) return false;
        boolean result = super.hurt(source, amount);
        if (result && !this.level().isClientSide) {
            ServerLevel level = (ServerLevel) this.level();
            level.playSound(null, this.blockPosition(), SoundEvents.WARDEN_HURT,
                    SoundSource.HOSTILE, 0.8F, 1.5F);

            SoulState s = getState();
            if (s != SoulState.HURT && s != SoulState.DISSIPATING
                    && s != SoulState.EMERGING && s != SoulState.DEATH_WAIL) {
                setState(SoulState.HURT);
            }

            // §2: cascade at low health
            float cascadePct = (float) OthersideConfig.SERVER.echoSoulCascadeHealthPct.get().doubleValue();
            if (this.getHealth() < this.getMaxHealth() * cascadePct
                    && !hasSplit && OthersideConfig.SERVER.echoSoulCascadeEnabled.get()) {
                hasSplit = true;
                setState(SoulState.SCREAM);
                WorldbeastState beast = WorldbeastState.get(level);
                beast.getEchoSoulManager().cascadeSpawn(level, this,
                        OthersideConfig.SERVER.echoSoulCascadeCount.get());
            }

            if (this.getHealth() < this.getMaxHealth() * 0.3f && !hasScreamed) {
                hasScreamed = true;
            }
        }
        return result;
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && !isDissipating) {
            isDissipating = true;
            // §1: death wail before dissolving
            setState(SoulState.DEATH_WAIL);
            ServerLevel level = (ServerLevel) this.level();
            level.playSound(null, this.blockPosition(), SoundEvents.WARDEN_DEATH,
                    SoundSource.HOSTILE, 1.2F, 0.7F);

            if (source.getEntity() instanceof Player player) {
                DirectorLog.log(level, "ECHO_SOUL_KILLED", this.blockPosition(),
                        "by=" + player.getName().getString());
            } else {
                DirectorLog.log(level, "ECHO_SOUL_KILLED", this.blockPosition(), "natural");
            }
            this.setHealth(1);
            this.setInvulnerable(true);
        }
    }

    // ── Server tick ──────────────────────────────────────────────────
    @Override
    public void aiStep() {
        super.aiStep();
        if (!(this.level() instanceof ServerLevel level)) return;

        stateTimer++;
        if (teleportCooldown > 0) teleportCooldown--;

        // Resolve cached target
        if (cachedTarget == null && targetUUID != null) {
            cachedTarget = level.getPlayerByUUID(targetUUID);
        }
        if (cachedTarget != null && (cachedTarget.isRemoved() || cachedTarget.isDeadOrDying() || cachedTarget.isSpectator())) {
            cachedTarget = null;
            targetUUID = null;
            hasLockedThisTarget = false;
        }

        // Track target with look control every aware tick
        if (isAwareOfTarget() && cachedTarget != null) {
            this.getLookControl().setLookAt(cachedTarget, (float) getMaxHeadYRot(), (float) getMaxHeadXRot());
        }

        // Ambient sound whispers
        ambientSoundTimer++;
        SoulState currentState = getState();
        if ((currentState == SoulState.WANDER || currentState == SoulState.STALK || currentState == SoulState.CHASE)
                && ambientSoundTimer >= 80 + this.random.nextInt(40)) {
            level.playSound(null, this.blockPosition(), SoundEvents.SOUL_ESCAPE.value(),
                    SoundSource.HOSTILE, 0.3F, 0.6F + this.random.nextFloat() * 0.4F);
            ambientSoundTimer = 0;
        }

        // Reabsorption timer (far from all players)
        if (currentState != SoulState.DISSIPATING && currentState != SoulState.EMERGING && currentState != SoulState.DEATH_WAIL) {
            boolean nearPlayer = false;
            for (Player p : level.players()) {
                if (p.distanceToSqr(this) < 48 * 48) { nearPlayer = true; break; }
            }
            if (!nearPlayer) {
                reabsorbTimer++;
                if (reabsorbTimer >= OthersideConfig.SERVER.echoSoulReabsorbTimeoutTicks.get()) {
                    startDissipate("reabsorb_timeout");
                    return;
                }
            } else {
                reabsorbTimer = 0;
            }
        }

        // §3: global pull-back if outside claimed territory
        if (currentState != SoulState.DISSIPATING && currentState != SoulState.EMERGING
                && currentState != SoulState.DEATH_WAIL
                && OthersideConfig.SERVER.echoSoulLeashToClaimed.get()
                && !inClaimedTerritory(this.blockPosition())) {
            // Outside the frontier — abandon the hunt and return home
            if (homeAnchor != null) {
                this.getNavigation().moveTo(homeAnchor.getX() + 0.5, homeAnchor.getY(), homeAnchor.getZ() + 0.5, 1.0);
            }
            cachedTarget = null; targetUUID = null; hasLockedThisTarget = false;
            return; // don't run normal state logic while returning
        }

        // State machine
        switch (currentState) {
            case EMERGING -> tickEmerging(level);
            case WANDER -> tickWander(level);
            case STALK -> tickStalk(level);
            case LOCK -> tickLock(level);
            case CHASE -> tickChase(level);
            case ATTACK_SWIPE -> tickAttack(level, 16);
            case ATTACK_SLAM -> tickAttack(level, 20);
            case ATTACK_LUNGE -> tickAttack(level, 14);
            case ATTACK_FLURRY -> tickAttack(level, 26);
            case SCREAM -> tickScream(level);
            case HURT -> tickHurt(level);
            case DISSIPATING -> tickDissipate(level);
            case DEATH_WAIL -> tickDeathWail(level);
        }
    }

    // ── State handlers ───────────────────────────────────────────────

    private void tickEmerging(ServerLevel level) {
        // Emerge sound at the start
        if (stateTimer == 1) {
            level.playSound(null, this.blockPosition(), SoundEvents.SCULK_CATALYST_BLOOM,
                    SoundSource.HOSTILE, 1.0F, 0.7F);
        }
        if (stateTimer >= 130) {
            if (spawnedAware && cachedTarget != null) {
                setState(SoulState.STALK);
            } else {
                setState(SoulState.WANDER);
            }
        }
    }

    private void tickWander(ServerLevel level) {
        // Detection every 5 ticks
        if (stateTimer % 5 == 0) {
            Player detected = detectPlayer(level);
            if (detected != null) {
                targetUUID = detected.getUUID();
                cachedTarget = detected;
                hasLockedThisTarget = false;
                noTargetTimer = 0;
                setState(SoulState.STALK);
                return;
            }
        }

        // §4: no-target despawn
        if (cachedTarget == null) {
            noTargetTimer++;
            if (noTargetTimer >= OthersideConfig.SERVER.echoSoulNoTargetDespawnTicks.get()) {
                startDissipate("no_target");
                return;
            }
        } else {
            noTargetTimer = 0;
        }

        // Alternate idle_float / yearn
        int cycleDuration = useYearn ? 120 : 80;
        if (stateTimer >= cycleDuration) {
            useYearn = !useYearn;
            wanderCycleCount++;
            setState(SoulState.WANDER);

            // Bias drift toward nearest player in claimed territory
            Player nearestPlayer = null;
            double nearestDist = 48 * 48;
            for (Player p : level.players()) {
                if (p.isSpectator() || p.isCreative()) continue;
                double d = p.distanceToSqr(this);
                if (d < nearestDist) { nearestDist = d; nearestPlayer = p; }
            }

            if (nearestPlayer != null && wanderCycleCount % 2 == 0) {
                double dx = (nearestPlayer.getX() - this.getX()) * 0.3 + (this.random.nextDouble() - 0.5) * 4;
                double dz = (nearestPlayer.getZ() - this.getZ()) * 0.3 + (this.random.nextDouble() - 0.5) * 4;
                // §3: don't wander out of claimed territory
                BlockPos driftTarget = BlockPos.containing(this.getX() + dx, this.getY(), this.getZ() + dz);
                if (inClaimedTerritory(driftTarget)) {
                    this.getNavigation().moveTo(driftTarget.getX(), driftTarget.getY(), driftTarget.getZ(), 0.5);
                }
            } else if (wanderCycleCount % 2 == 0) {
                double dx = (this.random.nextDouble() - 0.5) * 8;
                double dz = (this.random.nextDouble() - 0.5) * 8;
                BlockPos driftTarget = BlockPos.containing(this.getX() + dx, this.getY(), this.getZ() + dz);
                if (inClaimedTerritory(driftTarget)) {
                    this.getNavigation().moveTo(driftTarget.getX(), driftTarget.getY(), driftTarget.getZ(), 0.5);
                }
            }
        }
    }

    private void tickStalk(ServerLevel level) {
        if (cachedTarget == null) { setState(SoulState.WANDER); return; }

        // §3: strain at frontier — stop and stare, then give up
        if (!inClaimedTerritory(cachedTarget.blockPosition())) {
            this.getNavigation().stop();
            this.getLookControl().setLookAt(cachedTarget, (float) getMaxHeadYRot(), (float) getMaxHeadXRot());
            leashStrainTimer++;
            if (leashStrainTimer >= OthersideConfig.SERVER.echoSoulLeashGiveUpTicks.get()) {
                leashStrainTimer = 0;
                cachedTarget = null; targetUUID = null; hasLockedThisTarget = false;
                setState(SoulState.WANDER);
            }
            return;
        } else {
            leashStrainTimer = 0;
        }

        if (isNearAmethyst(cachedTarget.blockPosition())) {
            cachedTarget = null; targetUUID = null; hasLockedThisTarget = false;
            setState(SoulState.WANDER); return;
        }

        this.getNavigation().moveTo(cachedTarget, 0.6);
        double dist = this.distanceTo(cachedTarget);
        int detectRange = OthersideConfig.SERVER.echoSoulDetectRange.get();

        if (dist > detectRange * 1.5) {
            cachedTarget = null; targetUUID = null; hasLockedThisTarget = false;
            setState(SoulState.WANDER); return;
        }

        if (dist < detectRange && hasLineOfSight(cachedTarget)) {
            if (!hasLockedThisTarget) {
                setState(SoulState.LOCK);
                hasLockedThisTarget = true;
                DirectorLog.log(level, "ECHO_SOUL_LOCK", this.blockPosition(),
                        "target=" + cachedTarget.getName().getString());
                level.playSound(null, this.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK,
                        SoundSource.HOSTILE, 0.6F, 1.8F);
            } else {
                level.playSound(null, this.blockPosition(), SoundEvents.SOUL_ESCAPE.value(),
                        SoundSource.HOSTILE, 0.8F, 1.4F);
                setState(SoulState.CHASE);
            }
        }
    }

    private void tickLock(ServerLevel level) {
        if (cachedTarget == null || cachedTarget.isRemoved()) {
            hasLockedThisTarget = false;
            setState(SoulState.WANDER); return;
        }
        if (!hasLineOfSight(cachedTarget)) { setState(SoulState.STALK); return; }
        if (isNearAmethyst(cachedTarget.blockPosition())) {
            cachedTarget = null; targetUUID = null; hasLockedThisTarget = false;
            setState(SoulState.WANDER); return;
        }

        int lockDuration = OthersideConfig.SERVER.echoSoulLockHoldTicks.get();
        if (stateTimer >= lockDuration) {
            level.playSound(null, this.blockPosition(), SoundEvents.SOUL_ESCAPE.value(),
                    SoundSource.HOSTILE, 0.8F, 1.4F);
            setState(SoulState.CHASE);
        }
    }

    private void tickChase(ServerLevel level) {
        if (cachedTarget == null || cachedTarget.isRemoved()) {
            hasLockedThisTarget = false;
            setState(SoulState.WANDER); return;
        }

        // §3: strain at frontier — stop and stare, then give up
        if (!inClaimedTerritory(cachedTarget.blockPosition())) {
            this.getNavigation().stop();
            this.getLookControl().setLookAt(cachedTarget, (float) getMaxHeadYRot(), (float) getMaxHeadXRot());
            leashStrainTimer++;
            if (leashStrainTimer >= OthersideConfig.SERVER.echoSoulLeashGiveUpTicks.get()) {
                leashStrainTimer = 0;
                cachedTarget = null; targetUUID = null; hasLockedThisTarget = false;
                setState(SoulState.WANDER);
            }
            return;
        } else {
            leashStrainTimer = 0;
        }

        double dist = this.distanceTo(cachedTarget);

        // LOS-loss grace (40 ticks before downgrading)
        if (!hasLineOfSight(cachedTarget)) {
            losLostTimer++;
            if (losLostTimer >= 40) {
                losLostTimer = 0;
                setState(SoulState.STALK);
                return;
            }
        } else {
            losLostTimer = 0;
        }

        // Light deters — only block light
        int lightLevel = level.getBrightness(LightLayer.BLOCK, this.blockPosition());
        if (lightLevel >= OthersideConfig.SERVER.echoSoulLightDeterLevel.get()) {
            this.getNavigation().moveTo(cachedTarget, 0.3);
        } else {
            // Teleport when far or path stalled
            int triggerDist = OthersideConfig.SERVER.echoSoulTeleportTriggerDist.get();
            if ((dist > triggerDist || this.getNavigation().isDone()) && dist > 3.0) {
                if (tryTeleportNear(level, cachedTarget)) return;
            }
            if (stateTimer % 20 == 0 && this.random.nextFloat() < 0.15f && dist > 4.0) {
                if (tryTeleportNear(level, cachedTarget)) return;
            }
            this.getNavigation().moveTo(cachedTarget, 1.0);
        }

        // Attack ranges
        if (dist < 4.5 && dist > 2.5) {
            if (lastAttack != SoulState.ATTACK_LUNGE) {
                lastAttack = SoulState.ATTACK_LUNGE;
                this.getNavigation().stop();
                setState(SoulState.ATTACK_LUNGE);
                return;
            }
        }
        if (dist < 3.0) {
            this.getNavigation().stop();
            pickAttack(dist);
        }
    }

    private void pickAttack(double dist) {
        List<SoulState> options = new ArrayList<>();
        options.add(SoulState.ATTACK_SWIPE);
        options.add(SoulState.ATTACK_SLAM);
        if (cachedTarget != null && cachedTarget.getHealth() < cachedTarget.getMaxHealth() * 0.4f) {
            options.add(SoulState.ATTACK_FLURRY);
        }
        if (lastAttack != null) options.remove(lastAttack);
        if (options.isEmpty()) options.add(SoulState.ATTACK_SWIPE);

        SoulState chosen = options.get(this.random.nextInt(options.size()));
        lastAttack = chosen;

        ServerLevel level = (ServerLevel) this.level();
        level.playSound(null, this.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.HOSTILE, 0.7F, 0.8F + this.random.nextFloat() * 0.4F);
        setState(chosen);
    }

    private void tickAttack(ServerLevel level, int durationTicks) {
        if (cachedTarget == null) { setState(SoulState.CHASE); return; }

        int hitFrame = (int) (durationTicks * 0.6);
        if (stateTimer == hitFrame) {
            if (this.distanceTo(cachedTarget) < 3.5) {
                float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                if (getState() == SoulState.ATTACK_SLAM) damage *= 1.5f;
                cachedTarget.hurt(this.damageSources().mobAttack(this), damage);
                // Corruption spike on echo soul hit
                if (cachedTarget instanceof ServerPlayer sp) {
                    Corruption.add(sp, OthersideConfig.SERVER.corruptionSoulHit.get().floatValue());
                }
                level.playSound(null, cachedTarget.blockPosition(), SoundEvents.WARDEN_ATTACK_IMPACT,
                        SoundSource.HOSTILE, 0.8F, 1.0F);
            }
        }

        if (getState() == SoulState.ATTACK_FLURRY) {
            int secondHit = (int) (durationTicks * 0.85);
            if (stateTimer == secondHit && this.distanceTo(cachedTarget) < 3.5) {
                float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.7f;
                cachedTarget.hurt(this.damageSources().mobAttack(this), damage);
                // Corruption spike on echo soul flurry hit
                if (cachedTarget instanceof ServerPlayer sp) {
                    Corruption.add(sp, OthersideConfig.SERVER.corruptionSoulHit.get().floatValue());
                }
                level.playSound(null, cachedTarget.blockPosition(), SoundEvents.WARDEN_ATTACK_IMPACT,
                        SoundSource.HOSTILE, 0.6F, 1.1F);
            }
        }

        if (stateTimer >= durationTicks) { setState(SoulState.CHASE); }
    }

    private void tickScream(ServerLevel level) {
        if (stateTimer == 1) {
            level.playSound(null, this.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK,
                    SoundSource.HOSTILE, 1.2F, 0.8F);
            DirectorLog.log(level, "ECHO_SOUL_SCREAM", this.blockPosition(), "");

            if (cachedTarget != null) {
                for (EchoSoulEntity soul : level.getEntitiesOfClass(EchoSoulEntity.class,
                        this.getBoundingBox().inflate(32))) {
                    if (soul != this && soul.getState() == SoulState.WANDER) {
                        soul.setInitialTarget(cachedTarget);
                        soul.hasLockedThisTarget = false;
                        soul.setState(SoulState.STALK);
                    }
                }
            }

            for (Player player : level.players()) {
                if (player.distanceToSqr(this) < 16 * 16) {
                    player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.DARKNESS, 40, 0, false, false, true));
                }
            }
        }
        if (stateTimer >= 38) { setState(SoulState.CHASE); }
    }

    private void tickHurt(ServerLevel level) {
        if (stateTimer >= 9) {
            if (cachedTarget != null) {
                tryTeleportNear(level, cachedTarget);
            }
            if (this.getHealth() < this.getMaxHealth() * 0.3f && hasScreamed) {
                setState(SoulState.SCREAM);
                hasScreamed = false;
            } else if (cachedTarget != null) {
                setState(SoulState.CHASE);
            } else {
                setState(SoulState.WANDER);
            }
        }
    }

    // §1: death wail — wail then dissolve
    private void tickDeathWail(ServerLevel level) {
        if (stateTimer >= 38) { // SCREAM_WAIL = 38 ticks
            setState(SoulState.DISSIPATING);
        }
    }

    private void tickDissipate(ServerLevel level) {
        if (stateTimer == 1) {
            // Dissipate sounds (in case we arrived here from death_wail or startDissipate)
            level.playSound(null, this.blockPosition(), SoundEvents.SOUL_ESCAPE.value(),
                    SoundSource.HOSTILE, 0.8F, 0.6F);
        }
        if (stateTimer >= 44) {
            for (int i = 0; i < 15; i++) {
                level.sendParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getY() + 1, this.getZ(),
                        1, 0.3, 0.5, 0.3, 0.02);
            }
            this.discard();
        }
    }

    // ── Teleport ─────────────────────────────────────────────────────

    private boolean tryTeleportNear(ServerLevel level, Player target) {
        if (!OthersideConfig.SERVER.echoSoulTeleportEnabled.get()) return false;
        if (teleportCooldown > 0) return false;

        int range = OthersideConfig.SERVER.echoSoulTeleportRange.get();
        for (int attempt = 0; attempt < 12; attempt++) {
            double ang = this.random.nextDouble() * Math.PI * 2;
            double r = 3 + this.random.nextDouble() * 4;
            int tx = Mth.floor(target.getX() + Math.cos(ang) * r);
            int tz = Mth.floor(target.getZ() + Math.sin(ang) * r);
            int ty = level.getHeight(Heightmap.Types.MOTION_BLOCKING, tx, tz);
            BlockPos dest = new BlockPos(tx, ty, tz);

            if (dest.distToCenterSqr(target.getX(), target.getY(), target.getZ()) > range * range) continue;
            if (!level.getBlockState(dest).getCollisionShape(level, dest).isEmpty()) continue;
            if (isNearAmethyst(dest)) continue;
            if (level.getBrightness(LightLayer.BLOCK, dest) >= OthersideConfig.SERVER.echoSoulLightDeterLevel.get()) continue;
            // §3: don't teleport outside claimed territory
            if (!inClaimedTerritory(dest)) continue;

            spawnBlinkFx(level, this.blockPosition());
            this.teleportTo(tx + 0.5, ty, tz + 0.5);
            spawnBlinkFx(level, dest);
            level.playSound(null, dest, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.2F);

            teleportCooldown = OthersideConfig.SERVER.echoSoulTeleportCooldownTicks.get();
            return true;
        }
        return false;
    }

    private void spawnBlinkFx(ServerLevel level, BlockPos p) {
        level.sendParticles(ParticleTypes.SCULK_SOUL,
                p.getX() + 0.5, p.getY() + 1, p.getZ() + 0.5,
                18, 0.3, 0.6, 0.3, 0.02);
    }

    // ── Detection ────────────────────────────────────────────────────
    @Nullable
    private Player detectPlayer(ServerLevel level) {
        int range = OthersideConfig.SERVER.echoSoulDetectRange.get();
        int lightDeter = OthersideConfig.SERVER.echoSoulLightDeterLevel.get();

        if (level.getBrightness(LightLayer.BLOCK, this.blockPosition()) >= lightDeter) {
            return null;
        }

        Player closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Player player : level.players()) {
            if (player.isSpectator() || player.isCreative()) continue;
            if (!inClaimedTerritory(player.blockPosition())) continue; // Fix A: can't acquire past the frontier
            double dist = this.distanceTo(player);
            if (dist > range) continue;
            if (!hasLineOfSight(player)) continue;
            if (isNearAmethyst(player.blockPosition())) continue;
            if (dist < closestDist) { closestDist = dist; closest = player; }
        }
        return closest;
    }

    // ── Frontier leash (beast domain = claimed chunks ∪ sores ∪ breaches) ──
    private boolean inClaimedTerritory(BlockPos pos) {
        if (!OthersideConfig.SERVER.echoSoulLeashToClaimed.get()) return true;
        if (!(this.level() instanceof ServerLevel level)) return true;
        return WorldbeastState.get(level).isInBeastDomain(pos, level);
    }

    // ── Counterplay helpers ──────────────────────────────────────────
    public static boolean isNearAmethyst(BlockPos center) {
        return false; // Stub — fully implemented when amethyst system is in
    }

    // ── Dissipate helper ─────────────────────────────────────────────
    public void startDissipate(String reason) {
        if (isDissipating) return;
        isDissipating = true;
        setInvulnerable(true);
        setState(SoulState.DISSIPATING);
        if (!this.level().isClientSide) {
            ServerLevel level = (ServerLevel) this.level();
            level.playSound(null, this.blockPosition(), SoundEvents.SOUL_ESCAPE.value(),
                    SoundSource.HOSTILE, 0.8F, 0.6F);
            DirectorLog.log(level, "ECHO_SOUL_DISSIPATE", this.blockPosition(), "reason=" + reason);
        }
    }

    // ── Serialization ────────────────────────────────────────────────
    @Override public void addAdditionalSaveData(CompoundTag tag) { super.addAdditionalSaveData(tag); }
    @Override public void readAdditionalSaveData(CompoundTag tag) { super.readAdditionalSaveData(tag); }
    @Override public void handleEntityEvent(byte id) { super.handleEntityEvent(id); }
    @Override protected void registerGoals() {}
}
