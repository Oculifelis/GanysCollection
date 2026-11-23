package dev.oculifelis.ganys.item;

import dev.oculifelis.ganys.GanysMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

/**
 * Endium and blaze armour, ticking while worn by a player. Endium wears out in rain and hurts its wearer in water;
 * each piece has its own perk. Blaze armour slowly mends itself and mends fast in fire, but water eats it.
 * Imperviousness turns off the water and rain harm.
 */
public class GanysArmorItem extends ArmorItem {
    public enum Kind { ENDIUM, BLAZE }

    private final Kind kind;

    public GanysArmorItem(Kind kind, Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
        this.kind = kind;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity owner, int slotId, boolean selected) {
        if (!(world instanceof ServerLevel level) || !(owner instanceof Player player)) return;
        EquipmentSlot slot = getEquipmentSlot();
        if (player.getItemBySlot(slot) != stack) return;
        boolean impervious = impervious(stack, level);
        if (kind == Kind.ENDIUM) endium(stack, level, player, slot, impervious);
        else blaze(stack, level, player, slot, impervious);
    }

    public static boolean impervious(ItemStack stack, ServerLevel level) {
        return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(GanysMaterials.IMPERVIOUSNESS)
                .map(h -> EnchantmentHelper.getItemEnchantmentLevel(h, stack) > 0).orElse(false);
    }

    private static void endium(ItemStack stack, ServerLevel level, Player player, EquipmentSlot slot, boolean impervious) {
        if (!impervious && level.getGameTime() % 10 == 0 && level.isRainingAt(BlockPos.containing(player.getX(), player.getY() + 1, player.getZ()))) {
            stack.hurtAndBreak(1, player, slot);
        }
        if (!impervious && player.isInWater()) {
            stack.hurtAndBreak(1, player, slot);
            player.hurt(level.damageSources().generic(), 1.0F);
            if (slot == EquipmentSlot.HEAD) player.removeEffect(MobEffects.NIGHT_VISION);
            return;
        }
        switch (slot) {
            case HEAD -> {
                MobEffectInstance nv = player.getEffect(MobEffects.NIGHT_VISION);
                if (nv == null || nv.getDuration() < 210) player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 500, 0, false, false));
            }
            case LEGS -> {
                if (player.isSprinting()) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2, 10, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 2, 10, false, false));
                }
            }
            case FEET -> player.resetFallDistance();
            default -> {
            }
        }
    }

    private static void blaze(ItemStack stack, ServerLevel level, Player player, EquipmentSlot slot, boolean impervious) {
        if (level.getGameTime() % 160 == 0) repair(stack, 1);
        if (impervious) return;
        if (player.isInWater()) stack.hurtAndBreak(1, player, slot);
        else if (player.isInLava() || player.isOnFire()) repair(stack, 2);
    }

    private static void repair(ItemStack stack, int amount) {
        if (stack.isDamaged()) stack.setDamageValue(Math.max(0, stack.getDamageValue() - amount));
    }
}
