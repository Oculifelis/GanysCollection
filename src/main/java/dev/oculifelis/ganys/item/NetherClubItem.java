package dev.oculifelis.ganys.item;

import dev.oculifelis.ganys.ClubKnockback;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/** A club that sends what it hits flying; the less worn it is, the further. Numbers in the club_knockback item data map. */
public class NetherClubItem extends Item {
    public NetherClubItem(Properties properties) {
        super(properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        ClubKnockback k = ClubKnockback.of(stack.getItem());
        int max = stack.getMaxDamage();
        double frac = max > 0 ? (double) (max - stack.getDamageValue()) / max : 1.0;
        double dx = target.getX() - attacker.getX(), dz = target.getZ() - attacker.getZ();
        while (dx * dx + dz * dz < 1.0E-4) {
            dx = (Math.random() - Math.random()) * 0.01;
            dz = (Math.random() - Math.random()) * 0.01;
        }
        double len = Math.sqrt(dx * dx + dz * dz);
        Vec3 v = target.getDeltaMovement().scale(k.keep());
        target.setDeltaMovement(v.add(dx / len * k.horizontal() * frac, k.vertical() * frac, dz / len * k.horizontal() * frac));
        target.hurtMarked = true;
        target.needsSync = true;
        target.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK,
                attacker.getSoundSource(), 1.5F, target.getRandom().nextFloat() * 0.1F + 0.9F);
    }
}
