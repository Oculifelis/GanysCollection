package dev.oculifelis.ganys.entity;

import dev.oculifelis.ganys.BlazeGolemStats;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Gany's Nether blaze golem: a snow golem made of blaze blocks. It wanders, guards against monsters and hurts in water and
 * rain, and throws small fireballs instead of snowballs. Its numbers come from the blaze_golem data map.
 */
public class BlazeGolem extends AbstractGolem implements RangedAttackMob {
    public static final byte ATTACK_EVENT = 4;
    /** Client: the tick the last throw started, or -1. */
    public int attackStart = -1;
    private @Nullable LivingEntity pendingTarget;
    private int shotCountdown = -1;

    public BlazeGolem(EntityType<? extends BlazeGolem> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, BlazeGolemStats.DEFAULT.health())
                .add(Attributes.MOVEMENT_SPEED, BlazeGolemStats.DEFAULT.speed());
    }

    public BlazeGolemStats stats() {
        return BlazeGolemStats.of(getType());
    }

    @Override
    protected void registerGoals() {
        BlazeGolemStats s = stats();
        goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25, s.attackInterval(), s.attackRange()));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0, 1.0000001E-5F));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, (e, l) -> e instanceof Enemy));
    }

    /** Sets health and speed from the data map; called when the golem is built or spawned. */
    public void applyStats() {
        BlazeGolemStats s = stats();
        AttributeInstance health = getAttribute(Attributes.MAX_HEALTH);
        if (health != null) health.setBaseValue(s.health());
        AttributeInstance speed = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) speed.setBaseValue(s.speed());
        setHealth(getMaxHealth());
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason reason,
            @Nullable SpawnGroupData data) {
        applyStats();
        return super.finalizeSpawn(level, difficulty, reason, data);
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        if (shotCountdown >= 0) return;
        level().broadcastEntityEvent(this, ATTACK_EVENT);
        pendingTarget = target;
        shotCountdown = Math.max(0, stats().shotDelay());
        if (shotCountdown == 0) shoot();
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (shotCountdown > 0 && --shotCountdown == 0) shoot();
    }

    private void shoot() {
        shotCountdown = -1;
        LivingEntity target = pendingTarget;
        pendingTarget = null;
        if (target == null || !target.isAlive()) return;
        Vec3 forward = Vec3.directionFromRotation(0.0F, yBodyRot).scale(0.9);
        double x = getX() + forward.x, y = getY(0.62), z = getZ() + forward.z;
        double dx = target.getX() - x, dz = target.getZ() - z;
        double dy = target.getY(0.5) - y + Math.sqrt(dx * dx + dz * dz) * 0.05;
        GolemFireball ball = new GolemFireball(level(), this, new Vec3(dx, dy, dz).normalize());
        ball.setPos(x, y, z);
        level().addFreshEntity(ball);
        playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == ATTACK_EVENT) attackStart = tickCount;
        else super.handleEntityEvent(id);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLAZE_DEATH;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 240;
    }
}
