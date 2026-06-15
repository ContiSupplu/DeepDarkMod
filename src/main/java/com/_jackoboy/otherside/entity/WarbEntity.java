package com._jackoboy.otherside.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Warb — heavy sculk brute. AOE slam deals 18 damage in 3-block radius.
 * Scream boosts speed 1.5x for 8 seconds. Massive asymmetric arms, tendrils.
 */
public class WarbEntity extends Monster {
    private static final ResourceLocation SCREAM_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath("otherside", "warb_scream_speed");

    /** 0 = idle, 1 = slamming, 2 = screaming */
    private static final EntityDataAccessor<Integer> ATTACK_STATE =
            SynchedEntityData.defineId(WarbEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TICKS =
            SynchedEntityData.defineId(WarbEntity.class, EntityDataSerializers.INT);

    private int slamCooldown = 0;
    private int screamCooldown = 0;
    private int speedBoostTicks = 0;

    public WarbEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_STATE, 0);
        builder.define(ATTACK_ANIM_TICKS, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.ATTACK_DAMAGE, 0.0) // Damage comes from skills
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            // Decrement cooldowns
            if (slamCooldown > 0) slamCooldown--;
            if (screamCooldown > 0) screamCooldown--;
            if (speedBoostTicks > 0) {
                speedBoostTicks--;
                if (speedBoostTicks == 0) {
                    removeSpeedBoost();
                }
            }

            // Tick down animation
            int anim = this.entityData.get(ATTACK_ANIM_TICKS);
            if (anim > 0) {
                this.entityData.set(ATTACK_ANIM_TICKS, anim - 1);
                if (anim - 1 <= 0) {
                    this.entityData.set(ATTACK_STATE, 0);
                }
            }

            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                double dist = this.distanceTo(target);

                // AOE Slam: within 4 blocks, every 3 seconds (60 ticks)
                if (dist <= 4.0 && slamCooldown <= 0) {
                    performAoeSlam();
                    slamCooldown = 60;
                }

                // Scream: target 6-16 blocks away, every 32 seconds (640 ticks)
                if (dist >= 6.0 && dist <= 16.0 && screamCooldown <= 0) {
                    performScream();
                    screamCooldown = 640;
                }
            }
        } else {
            // Client-side: tick down anim
            int anim = this.entityData.get(ATTACK_ANIM_TICKS);
            if (anim > 0) {
                this.entityData.set(ATTACK_ANIM_TICKS, anim - 1);
            }
        }
    }

    private void performAoeSlam() {
        // Set slam animation
        this.entityData.set(ATTACK_STATE, 1);
        this.entityData.set(ATTACK_ANIM_TICKS, 12);

        Level level = this.level();
        AABB area = this.getBoundingBox().inflate(3.0);
        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != this && !(e instanceof WarbEntity));

        DamageSource slamSource = this.damageSources().mobAttack(this);
        for (LivingEntity entity : nearby) {
            entity.hurt(slamSource, 18.0F);

            // Throw target upward and away
            Vec3 knockDir = entity.position().subtract(this.position()).normalize();
            entity.setDeltaMovement(
                    knockDir.x * 0.6,
                    0.1,
                    knockDir.z * 0.6
            );
            entity.hurtMarked = true;
        }

        // Slam particles
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    1, 0.0, 0.0, 0.0, 0.0);
        }

        this.playSound(SoundEvents.WARDEN_ATTACK_IMPACT, 1.5F, 2.0F);
    }

    private void performScream() {
        // Set scream animation
        this.entityData.set(ATTACK_STATE, 2);
        this.entityData.set(ATTACK_ANIM_TICKS, 20);

        // Play warden roar
        this.level().playSound(null, this.blockPosition(),
                SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.0F, 2.0F);

        // Apply speed boost for 8 seconds (160 ticks)
        applySpeedBoost();
        speedBoostTicks = 160;

        // Teal/sculk particles
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                    this.getX(), this.getY() + 1.0, this.getZ(),
                    20, 0.5, 0.5, 0.5, 0.05);
        }
    }

    private void applySpeedBoost() {
        AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(SCREAM_SPEED_ID);
            speedAttr.addTransientModifier(new AttributeModifier(
                    SCREAM_SPEED_ID, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    private void removeSpeedBoost() {
        AttributeInstance speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(SCREAM_SPEED_ID);
        }
    }

    /** Returns the current attack state: 0=idle, 1=slamming, 2=screaming */
    public int getAttackState() {
        return this.entityData.get(ATTACK_STATE);
    }

    /** Returns 0.0-1.0 animation progress (1.0 = just started). */
    public float getAttackAnimProgress() {
        int state = getAttackState();
        int ticks = this.entityData.get(ATTACK_ANIM_TICKS);
        float max = state == 2 ? 20.0F : 12.0F;
        return max > 0 ? ticks / max : 0;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 160;
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    @Override
    public float getVoicePitch() {
        return 2.0F;
    }

    public int getExperienceReward() {
        return 20 + this.random.nextInt(41); // 20-60 XP
    }
}
