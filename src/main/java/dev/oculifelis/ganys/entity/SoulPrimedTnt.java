package dev.oculifelis.ganys.entity;

import dev.oculifelis.ganys.GanysSurface;
import dev.oculifelis.ganys.SoulTnt;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

/**
 * Primed soul TNT: vanilla TNT motion, a fiery blast, and long Slowness on every living thing nearby that stacks with
 * Slowness of the same level already on it. Numbers come from the {@link SoulTnt} data map of the block it shows.
 */
public class SoulPrimedTnt extends PrimedTnt {
    private @Nullable LivingEntity igniter;

    public SoulPrimedTnt(EntityType<? extends SoulPrimedTnt> type, Level level) {
        super(type, level);
        setBlockState(GanysSurface.SOUL_TNT.get().defaultBlockState());
    }

    public static SoulPrimedTnt create(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity igniter) {
        SoulPrimedTnt tnt = new SoulPrimedTnt(GanysSurface.SOUL_TNT_ENTITY.get(), level);
        double x = pos.getX() + 0.5, y = pos.getY(), z = pos.getZ() + 0.5;
        tnt.setPos(x, y, z);
        double a = level.getRandom().nextDouble() * Math.PI * 2;
        tnt.setDeltaMovement(-Math.sin(a) * 0.02, 0.2, -Math.cos(a) * 0.02);
        tnt.xo = x;
        tnt.yo = y;
        tnt.zo = z;
        tnt.setBlockState(state);
        tnt.igniter = igniter;
        return tnt;
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        return igniter != null ? igniter : super.getOwner();
    }

    @Override
    protected void explode() {
        if (!(level() instanceof ServerLevel level) || !level.getGameRules().get(GameRules.TNT_EXPLODES)) return;
        SoulTnt t = SoulTnt.of(getBlockState().getBlock());
        level.explode(this, Explosion.getDefaultDamageSource(level, this), null, getX(), getY(0.0625), getZ(), t.power(), t.fire(),
                Level.ExplosionInteraction.TNT);
        double r = t.power() * t.slowRange();
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(position(), position()).inflate(r))) {
            int duration = t.slowDuration();
            MobEffectInstance old = e.getEffect(MobEffects.SLOWNESS);
            if (old != null && old.getAmplifier() == t.slowAmplifier()) duration += old.getDuration();
            e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration, t.slowAmplifier()), this);
        }
    }
}
