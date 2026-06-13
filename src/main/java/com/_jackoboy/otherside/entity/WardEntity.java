package com._jackoboy.otherside.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class WardEntity extends Zombie {
    private int biteCooldown = 0;
    private int leapCooldown = 0;

    public WardEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 35.0)
                .add(Attributes.ATTACK_DAMAGE, 0.0) // Damage comes from skills
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 32.0)
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
            if (biteCooldown > 0) biteCooldown--;
            if (leapCooldown > 0) leapCooldown--;

            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                double dist = this.distanceTo(target);

                // Bite: within 4 blocks, every 1 second (20 ticks)
                if (dist <= 4.0 && biteCooldown <= 0) {
                    performBite();
                    biteCooldown = 20;
                }

                // Leap: target 6-16 blocks away, every 12 seconds (240 ticks)
                if (dist >= 6.0 && dist <= 16.0 && leapCooldown <= 0 && this.onGround()) {
                    performLeap(target);
                    leapCooldown = 240;
                }
            }
        }
    }

    private void performBite() {
        Level level = this.level();
        AABB area = this.getBoundingBox().inflate(4.0);
        List<LivingEntity> nearby = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != this && !(e instanceof WardEntity));

        DamageSource biteSource = this.damageSources().mobAttack(this);
        for (LivingEntity entity : nearby) {
            entity.hurt(biteSource, 12.0F);
        }

        this.playSound(SoundEvents.PLAYER_ATTACK_STRONG, 1.5F, 2.0F);
    }

    private void performLeap(LivingEntity target) {
        // Calculate leap vector toward target (breeze-like)
        Vec3 direction = target.position().subtract(this.position());
        double horizontalDist = direction.horizontalDistance();

        if (horizontalDist > 0) {
            // Normalize horizontal direction, scale for leap
            double leapSpeed = 1.2;
            double leapHeight = 0.6;

            Vec3 leapVec = new Vec3(
                    direction.x / horizontalDist * leapSpeed,
                    leapHeight,
                    direction.z / horizontalDist * leapSpeed
            );

            this.setDeltaMovement(leapVec);
            this.hurtMarked = true;
        }

        this.playSound(SoundEvents.BREEZE_JUMP, 1.5F, 2.0F);
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

    @Override
    protected boolean isSunBurnTick() {
        return false; // sunburn-proof
    }

    public int getExperienceReward() {
        return 30 + this.random.nextInt(41); // 30-70 XP
    }

    @Override
    protected boolean convertsInWater() { return false; }

    @Override
    public boolean isBaby() { return false; }
}
