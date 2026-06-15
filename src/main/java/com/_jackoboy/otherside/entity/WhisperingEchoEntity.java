package com._jackoboy.otherside.entity;

import com._jackoboy.otherside.OthersideConfig;
import com._jackoboy.otherside.dimension.DimensionRulesManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.AnimationState;

import javax.annotation.Nullable;

/**
 * Whispering Echo — a small floating wisp companion tamable with amethyst shards.
 * <p>
 * v1: spawning + hover + tame + follow + 7 animations + minimal corruption hook.
 * Uses FlyingMoveControl/FlyingPathNavigation, never lands.
 * <p>
 * Corruption: rises in Echo dim when unwarded, recoverable at threshold (not untame).
 * Death anim: 36-tick override so the 1.8s animation plays fully.
 */
public class WhisperingEchoEntity extends TamableAnimal {

    // ── Synced data ──────────────────────────────────────────────────
    private static final EntityDataAccessor<Boolean> DATA_CORRUPTED =
            SynchedEntityData.defineId(WhisperingEchoEntity.class, EntityDataSerializers.BOOLEAN);

    // ── Animation states (client-side, but declared on entity for model access) ──
    public final AnimationState idleAnimState = new AnimationState();
    public final AnimationState followAnimState = new AnimationState();
    public final AnimationState attachmentAnimState = new AnimationState();
    public final AnimationState restAnimState = new AnimationState();
    public final AnimationState deathAnimState = new AnimationState();
    public final AnimationState corruptAnimState = new AnimationState();
    public final AnimationState attackAnimState = new AnimationState();

    // ── Corruption ───────────────────────────────────────────────────
    private float corruption = 0.0F;
    private int corruptionTickTimer = 0;
    private static final float CORRUPTION_GAIN_PER_CYCLE = 0.2F;   // per 20-tick cycle
    private static final float CORRUPTION_CURE_PER_CYCLE = 0.4F;   // faster cure
    private static final float CORRUPTION_THRESHOLD = 100.0F;
    private static final float CORRUPTION_CLEAR_THRESHOLD = 50.0F; // clears corrupted flag

    // ── Death anim ───────────────────────────────────────────────────
    private static final int DEATH_ANIM_TICKS = 36; // 1.8s at 20tps

    public WhisperingEchoEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setNoGravity(true);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
    }

    // ── Attributes ───────────────────────────────────────────────────

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FLYING_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    // ── Navigation ───────────────────────────────────────────────────

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanFloat(true);
        nav.setCanOpenDoors(false);
        nav.setCanPassDoors(true);
        return nav;
    }

    // ── AI Goals ─────────────────────────────────────────────────────

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0, 4.0F, 1.5F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    // ── Data ─────────────────────────────────────────────────────────

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CORRUPTED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Corruption", this.corruption);
        tag.putBoolean("Corrupted", this.isCorrupted());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.corruption = tag.getFloat("Corruption");
        this.setCorrupted(tag.getBoolean("Corrupted"));
    }

    // ── Corruption accessors ─────────────────────────────────────────

    public float getCorruption() { return this.corruption; }
    public void setCorruption(float value) { this.corruption = Mth.clamp(value, 0, CORRUPTION_THRESHOLD); }

    public boolean isCorrupted() { return this.entityData.get(DATA_CORRUPTED); }
    public void setCorrupted(boolean corrupted) { this.entityData.set(DATA_CORRUPTED, corrupted); }

    /** Clean hook for Core/artifact to force-corrupt. */
    public void forceCorrupt() {
        this.corruption = CORRUPTION_THRESHOLD;
        this.setCorrupted(true);
    }

    // ── Taming ───────────────────────────────────────────────────────

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!this.isTame() && stack.is(Items.AMETHYST_SHARD)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            if (!this.level().isClientSide()) {
                if (this.random.nextInt(3) == 0) {
                    // Tame success
                    this.tame(player);
                    this.setOrderedToSit(false);
                    this.level().broadcastEntityEvent(this, (byte) 7); // heart particles
                    // Play attachment animation
                    this.attachmentAnimState.start(this.tickCount);
                } else {
                    // Tame fail
                    this.level().broadcastEntityEvent(this, (byte) 6); // smoke particles
                }
            }
            return InteractionResult.SUCCESS;
        }

        // Tamed: toggle sit
        if (this.isTame() && this.isOwnedBy(player)) {
            if (!this.level().isClientSide()) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.AMETHYST_SHARD);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null; // Cannot breed
    }

    // ── Tick ──────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            tickClientAnimations();
        } else {
            tickCorruption();
        }
    }

    private void tickClientAnimations() {
        // Idle is always running when not doing something else
        if (!this.deathAnimState.isStarted() && !this.corruptAnimState.isStarted()
                && !this.attachmentAnimState.isStarted()) {

            boolean isMoving = this.getDeltaMovement().horizontalDistanceSqr() > 0.003;
            boolean isResting = this.isOrderedToSit();

            if (isResting) {
                if (!this.restAnimState.isStarted()) this.restAnimState.start(this.tickCount);
                this.idleAnimState.stop();
                this.followAnimState.stop();
            } else if (isMoving && this.isTame()) {
                if (!this.followAnimState.isStarted()) this.followAnimState.start(this.tickCount);
                this.idleAnimState.stop();
                this.restAnimState.stop();
            } else {
                if (!this.idleAnimState.isStarted()) this.idleAnimState.start(this.tickCount);
                this.followAnimState.stop();
                this.restAnimState.stop();
            }

            // Corruption overlay
            if (this.isCorrupted()) {
                if (!this.corruptAnimState.isStarted()) this.corruptAnimState.start(this.tickCount);
            } else {
                this.corruptAnimState.stop();
            }
        }
    }

    private void tickCorruption() {
        corruptionTickTimer++;
        if (corruptionTickTimer < 20) return; // 20-tick cadence
        corruptionTickTimer = 0;

        boolean inEchoDim = this.level().dimension().equals(DimensionRulesManager.OTHERSIDE_DIM);

        if (inEchoDim) {
            int blockLight = this.level().getBrightness(LightLayer.BLOCK, this.blockPosition());
            int lightThreshold = OthersideConfig.SERVER.echoSoulLightDeterLevel.get();
            boolean warded = blockLight >= lightThreshold
                    || EchoSoulEntity.isNearAmethyst(this.level(), this.blockPosition());

            if (warded) {
                // Cure while warded
                if (this.corruption > 0) {
                    this.corruption = Math.max(0, this.corruption - CORRUPTION_CURE_PER_CYCLE);
                    // Clear corrupted flag when below threshold
                    if (this.isCorrupted() && this.corruption < CORRUPTION_CLEAR_THRESHOLD) {
                        this.setCorrupted(false);
                    }
                }
            } else {
                // Gain corruption
                this.corruption = Math.min(CORRUPTION_THRESHOLD, this.corruption + CORRUPTION_GAIN_PER_CYCLE);
                // Trigger corrupted state at threshold
                if (this.corruption >= CORRUPTION_THRESHOLD && !this.isCorrupted()) {
                    this.setCorrupted(true);
                    // Corrupted: disable following but keep ownership
                    this.setOrderedToSit(true);
                }
            }
        } else {
            // Outside Echo dim: slowly recover
            if (this.corruption > 0) {
                this.corruption = Math.max(0, this.corruption - CORRUPTION_CURE_PER_CYCLE * 0.5F);
                if (this.isCorrupted() && this.corruption < CORRUPTION_CLEAR_THRESHOLD) {
                    this.setCorrupted(false);
                    this.setOrderedToSit(false); // Resume following
                }
            }
        }
    }

    // ── Death animation (hold for 36 ticks) ──────────────────────────

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 1) {
            this.deathAnimState.start(this.tickCount);
        }
        if (this.deathTime >= DEATH_ANIM_TICKS) {
            this.remove(RemovalReason.KILLED);
        }
    }

    // ── Flight helpers ────────────────────────────────────────────────

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false; // Flying — no fall damage
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        // No-op: flying entity
    }

    @Override
    public boolean onClimbable() {
        return false;
    }


    // ── Sounds ────────────────────────────────────────────────────────

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.AMETHYST_BLOCK_CHIME;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.AMETHYST_BLOCK_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }
}
