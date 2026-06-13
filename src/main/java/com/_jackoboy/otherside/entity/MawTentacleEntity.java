package com._jackoboy.otherside.entity;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.OthersideMod;
import com._jackoboy.otherside.registry.ModDamageTypes;
import com._jackoboy.otherside.registry.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Maw Tentacle — a rooted, unkillable limb of the Worldbeast.
 *
 * State machine: EMERGING → IDLE → STRIKING → GRABBING → DRAGGING → RETRACTING
 *                                                                    ↑ RECOILING (interrupt)
 *
 * Design laws:
 * - Never dies. Damage → recoil, never health depletion.
 * - No AI goals. Server state machine only.
 * - Non-persistent (won't save to chunk). MawManager sweeps orphans on load.
 * - Not pushable, doesn't push others, immune to suffocation.
 */
public class MawTentacleEntity extends Mob {

    // ── State machine ─────────────────────────────────────────────────
    public enum TentacleState {
        EMERGING, IDLE, STRIKING, GRABBING, DRAGGING, RETRACTING, RECOILING
    }

    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(MawTentacleEntity.class, EntityDataSerializers.INT);

    // ── Animation states (client-side) ───────────────────────────────
    public final AnimationState emergeAnimState = new AnimationState();
    public final AnimationState idleSwayAnimState = new AnimationState();
    public final AnimationState strikeAnimState = new AnimationState();
    public final AnimationState grabAnimState = new AnimationState();
    public final AnimationState retractAnimState = new AnimationState();
    public final AnimationState recoilAnimState = new AnimationState();

    // ── Server state ─────────────────────────────────────────────────
    private BlockPos mawCenter = BlockPos.ZERO;
    @Nullable private UUID grabTargetUUID = null;
    @Nullable private Entity cachedGrabTarget = null;
    private int stateTimer = 0;
    private float cumulativeDamage = 0;
    private boolean retractWhenRecoilDone = false;

    public MawTentacleEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.setNoAi(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0) // effectively infinite
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    // ── Persistence: disabled ─────────────────────────────────────────
    @Override
    public boolean isPersistenceRequired() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double dist) {
        return false; // MawManager controls removal, not distance
    }

    @Override
    public boolean shouldBeSaved() {
        return false; // Non-persistent: don't write to chunk NBT
    }

    // ── Physics: rooted, non-pushable ────────────────────────────────
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushEntities() {
        // Don't push other entities
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive(); // Can be HIT (for recoil) but NOT a solid wall
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return false; // Never collide with anything
    }

    @Override
    public void push(double x, double y, double z) {
        // Cannot be pushed
    }

    @Override
    public void knockback(double strength, double x, double z) {
        // Cannot be knocked back
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    // ── Suffocation immunity ─────────────────────────────────────────
    @Override
    public boolean isInWall() {
        return false; // Never suffocate
    }

    // ── Damage handling: recoil, never death ─────────────────────────
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide) return false;
        if (source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) return false;

        // Accumulate damage for escape mechanic
        cumulativeDamage += amount;

        // Transition to RECOILING (releases any grab)
        TentacleState current = getState();
        if (current != TentacleState.RETRACTING) {
            releaseGrab();
            if (current == TentacleState.GRABBING || current == TentacleState.DRAGGING) {
                // If accumulated enough damage, this tentacle won't re-engage
                if (cumulativeDamage >= OthersideConfig.SERVER.mawEscapeDamage.get()) {
                    retractWhenRecoilDone = true;
                }
            }
            setState(TentacleState.RECOILING);
        }

        // Play hurt effect but NEVER reduce health
        this.level().playSound(null, this.blockPosition(), SoundEvents.SCULK_BLOCK_BREAK,
                SoundSource.HOSTILE, 0.8f, 0.6f);
        return true;
    }

    @Override
    public void die(DamageSource source) {
        // Tentacles never die — override to prevent
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        // Invulnerable to everything EXCEPT direct entity attacks (so players can trigger recoil)
        if (source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) return true;
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) return true;
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_DROWNING)) return true;
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FALL)) return true;
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)) return true;
        // Allow mob_attack, player_attack, generic
        return false;
    }

    protected void dropAllDeathLoot(DamageSource source) {
        // No drops ever
    }

    public int getExperienceReward() {
        return 0;
    }

    // ── Expanded culling bounding box ────────────────────────────────
    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(8.0);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double dist) {
        return dist < 256 * 256; // Render at extended range
    }

    // ── Data sync ────────────────────────────────────────────────────
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_STATE, TentacleState.EMERGING.ordinal());
    }

    public TentacleState getState() {
        return TentacleState.values()[this.entityData.get(DATA_STATE)];
    }

    public void setState(TentacleState state) {
        TentacleState old = getState();
        if (old == state) return;
        this.entityData.set(DATA_STATE, state.ordinal());
        stateTimer = 0;
        cumulativeDamage = (state == TentacleState.RECOILING) ? cumulativeDamage : 0;

        if (this.level().isClientSide) {
            stopAllAnimations();
            switch (state) {
                case EMERGING -> emergeAnimState.start(this.tickCount);
                case IDLE -> idleSwayAnimState.start(this.tickCount);
                case STRIKING -> strikeAnimState.start(this.tickCount);
                case GRABBING -> grabAnimState.start(this.tickCount);
                case RETRACTING -> retractAnimState.start(this.tickCount);
                case RECOILING -> recoilAnimState.start(this.tickCount);
                default -> {}
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(DATA_STATE) && this.level().isClientSide) {
            TentacleState state = getState();
            stopAllAnimations();
            switch (state) {
                case EMERGING -> emergeAnimState.start(this.tickCount);
                case IDLE -> idleSwayAnimState.start(this.tickCount);
                case STRIKING -> strikeAnimState.start(this.tickCount);
                case GRABBING -> grabAnimState.start(this.tickCount);
                case RETRACTING -> retractAnimState.start(this.tickCount);
                case RECOILING -> recoilAnimState.start(this.tickCount);
                default -> {}
            }
        }
    }

    private void stopAllAnimations() {
        emergeAnimState.stop();
        idleSwayAnimState.stop();
        strikeAnimState.stop();
        grabAnimState.stop();
        retractAnimState.stop();
        recoilAnimState.stop();
    }

    // ── Server tick: state machine ───────────────────────────────────
    @Override
    public void aiStep() {
        super.aiStep();
        if (!(this.level() instanceof ServerLevel level)) return;

        // Guard: if no MawManager set us up (manual /summon), do nothing
        if (mawCenter.equals(BlockPos.ZERO)) return;

        stateTimer++;

        switch (getState()) {
            case EMERGING -> tickEmerging(level);
            case IDLE -> tickIdle(level);
            case STRIKING -> tickStriking(level);
            case GRABBING -> tickGrabbing(level);
            case DRAGGING -> tickDragging(level);
            case RETRACTING -> tickRetracting(level);
            case RECOILING -> tickRecoiling(level);
        }
    }

    // ── State duration helpers (will use AnimationDefinition.lengthInSeconds) ──
    // Placeholder tick counts — replaced when animation constants arrive
    private int getEmergeDuration() { return 92; }   // 4.6s placeholder
    private int getStrikeDuration() { return 21; }    // 1.05s
    private int getGrabDuration() { return 56; }      // 2.8s
    private int getRetractDuration() { return 30; }   // 1.5s
    private int getRecoilDuration() { return 24; }    // 1.2s

    private void tickEmerging(ServerLevel level) {
        if (stateTimer >= getEmergeDuration()) {
            setState(TentacleState.IDLE);
        }
    }

    private void tickIdle(ServerLevel level) {
        // Scan for grab target: prioritize player, then large mobs
        if (stateTimer % 10 == 0) { // scan every half-second
            Entity target = findGrabTarget(level);
            if (target != null) {
                grabTargetUUID = target.getUUID();
                cachedGrabTarget = target;
                setState(TentacleState.STRIKING);
            }
        }
    }

    private void tickStriking(ServerLevel level) {
        if (stateTimer >= getStrikeDuration()) {
            // If target still in range, grab
            Entity target = resolveGrabTarget(level);
            if (target != null && target.distanceTo(this) < 8.0) {
                setState(TentacleState.GRABBING);
            } else {
                releaseGrab();
                setState(TentacleState.IDLE);
            }
        }
    }

    private void tickGrabbing(ServerLevel level) {
        Entity target = resolveGrabTarget(level);
        if (target == null || !target.isAlive()) {
            releaseGrab();
            setState(TentacleState.IDLE);
            return;
        }

        // Drag target toward throat
        dragTowardThroat(target);

        if (stateTimer >= getGrabDuration()) {
            setState(TentacleState.DRAGGING);
        }
    }

    private void tickDragging(ServerLevel level) {
        Entity target = resolveGrabTarget(level);
        if (target == null || !target.isAlive()) {
            releaseGrab();
            setState(TentacleState.IDLE);
            return;
        }

        dragTowardThroat(target);

        // Check if reached throat
        double distToThroat = target.distanceToSqr(
                mawCenter.getX() + 0.5, mawCenter.getY(), mawCenter.getZ() + 0.5);
        if (distToThroat < 4.0) { // within 2 blocks
            // Entity will be swallowed by MawManager
            releaseGrab();
            setState(TentacleState.IDLE);
        }
    }

    private void tickRetracting(ServerLevel level) {
        if (stateTimer >= getRetractDuration()) {
            this.discard(); // Remove at animation end, NEVER before
        }
    }

    private void tickRecoiling(ServerLevel level) {
        if (stateTimer >= getRecoilDuration()) {
            if (retractWhenRecoilDone) {
                setState(TentacleState.RETRACTING);
            } else {
                setState(TentacleState.IDLE);
            }
        }
    }

    // ── Grab target resolution ───────────────────────────────────────
    @Nullable
    private Entity findGrabTarget(ServerLevel level) {
        double range = 6.5;
        AABB searchBox = this.getBoundingBox().inflate(range);

        // Priority 1: players (not amethyst-protected)
        for (net.minecraft.server.level.ServerPlayer player : level.players()) {
            if (player.distanceTo(this) <= range && !isAmethystProtected(player)) {
                return player;
            }
        }

        // Priority 2: large living entities
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, searchBox,
                e -> e != this && e.isAlive() && !(e instanceof MawTentacleEntity)
                        && e.getBbHeight() >= 1.0f)) {
            if (!isAmethystProtected(entity)) {
                return entity;
            }
        }

        return null;
    }

    @Nullable
    private Entity resolveGrabTarget(ServerLevel level) {
        if (grabTargetUUID == null) return null;
        if (cachedGrabTarget != null && cachedGrabTarget.isAlive()
                && cachedGrabTarget.getUUID().equals(grabTargetUUID)) {
            return cachedGrabTarget;
        }
        // Re-resolve
        cachedGrabTarget = level.getEntity(grabTargetUUID.hashCode()); // rough lookup
        if (cachedGrabTarget == null || !cachedGrabTarget.getUUID().equals(grabTargetUUID)) {
            // Full scan fallback
            for (Entity e : level.getAllEntities()) {
                if (e.getUUID().equals(grabTargetUUID)) {
                    cachedGrabTarget = e;
                    return e;
                }
            }
            cachedGrabTarget = null;
            grabTargetUUID = null;
        }
        return cachedGrabTarget;
    }

    private void releaseGrab() {
        grabTargetUUID = null;
        cachedGrabTarget = null;
    }

    // ── Drag mechanics ───────────────────────────────────────────────
    private void dragTowardThroat(Entity target) {
        double dragSpeed = OthersideConfig.SERVER.mawDragPerTick.get();
        Vec3 throatCenter = new Vec3(mawCenter.getX() + 0.5, mawCenter.getY(), mawCenter.getZ() + 0.5);
        Vec3 targetPos = target.position();
        Vec3 direction = throatCenter.subtract(targetPos);
        double dist = direction.length();
        if (dist < 0.5) return;

        Vec3 velocity = direction.normalize().scale(Math.min(dragSpeed, dist));
        // Add slight spiral
        double angle = (target.tickCount % 40) * Math.PI * 2.0 / 40.0;
        velocity = velocity.add(Math.sin(angle) * 0.03, -0.02, Math.cos(angle) * 0.03);

        target.setDeltaMovement(velocity);
        target.hurtMarked = true;
    }

    // ── Amethyst protection check ────────────────────────────────────
    // Law: amethyst protects only when PLACED/anchored — not by holding.
    // Standing on or adjacent to placed amethyst wards a player/entity.
    public static boolean isAmethystProtected(Entity entity) {
        BlockPos pos = entity.blockPosition();
        // Check block at feet and below
        if (isAmethystBlock(entity.level().getBlockState(pos).getBlock())) return true;
        if (isAmethystBlock(entity.level().getBlockState(pos.below()).getBlock())) return true;
        // Check 4 horizontal neighbors at feet level
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.Plane.HORIZONTAL) {
            if (isAmethystBlock(entity.level().getBlockState(pos.relative(dir)).getBlock())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAmethystBlock(net.minecraft.world.level.block.Block block) {
        return block == net.minecraft.world.level.block.Blocks.AMETHYST_BLOCK
                || block == net.minecraft.world.level.block.Blocks.BUDDING_AMETHYST
                || block == net.minecraft.world.level.block.Blocks.AMETHYST_CLUSTER
                || block == net.minecraft.world.level.block.Blocks.LARGE_AMETHYST_BUD
                || block == net.minecraft.world.level.block.Blocks.MEDIUM_AMETHYST_BUD
                || block == net.minecraft.world.level.block.Blocks.SMALL_AMETHYST_BUD;
    }

    public static boolean isAmethystAnchored(BlockPos pos, Level level) {
        if (isAmethystBlock(level.getBlockState(pos).getBlock())) return true;
        // Check 6 faces
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            if (isAmethystBlock(level.getBlockState(pos.relative(dir)).getBlock())) {
                return true;
            }
        }
        return false;
    }

    // ── Setters/Getters ──────────────────────────────────────────────
    public void setMawCenter(BlockPos center) { this.mawCenter = center; }
    public BlockPos getMawCenter() { return mawCenter; }
    public boolean isGrabbing() { return grabTargetUUID != null; }
    @Nullable public UUID getGrabTargetUUID() { return grabTargetUUID; }
    public float getCumulativeDamage() { return cumulativeDamage; }

    public void requestRetract() {
        if (getState() != TentacleState.RETRACTING) {
            releaseGrab();
            setState(TentacleState.RETRACTING);
        }
    }

    // ── NBT (minimal — non-persistent, but needed for spawn data) ────
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong("mawCenter", mawCenter.asLong());
        tag.putInt("tentacleState", getState().ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("mawCenter")) mawCenter = BlockPos.of(tag.getLong("mawCenter"));
        if (tag.contains("tentacleState")) {
            setState(TentacleState.values()[tag.getInt("tentacleState")]);
        }
    }

    // ── Movement override: rooted ────────────────────────────────────
    @Override
    public void travel(Vec3 travelVector) {
        // Rooted — no movement
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.BlockState state, BlockPos pos) {
        // No fall damage
    }
}
