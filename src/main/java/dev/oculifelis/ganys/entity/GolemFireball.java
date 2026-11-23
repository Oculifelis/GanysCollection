package dev.oculifelis.ganys.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** A blaze golem's small fireball: it burns what it hits but never sets blocks alight. */
public class GolemFireball extends SmallFireball {
    public GolemFireball(Level level, LivingEntity owner, Vec3 movement) {
        super(level, owner, movement);
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
    }
}
