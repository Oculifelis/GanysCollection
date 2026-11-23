package dev.oculifelis.ganys.item;

import dev.oculifelis.ganys.GanysComponents;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;

/** Endium pickaxe, axe, shovel and sword: glint and a tooltip while tagged to an inventory, and no enchanting. */
public final class EndiumToolItem {
    private EndiumToolItem() {
    }

    public static boolean tagged(ItemStack stack) {
        return stack.has(GanysComponents.TAGGED_INVENTORY.get());
    }

    /** The original's "dim : x, y, z" line, or "not tagged". */
    public static void taggedTooltip(ItemStack stack, List<Component> tooltip) {
        GlobalPos tag = stack.get(GanysComponents.TAGGED_INVENTORY.get());
        if (tag == null) {
            tooltip.add(Component.translatable("tooltip.ganyscollection.not_tagged").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.ganyscollection.tagged", tag.dimension().location().toString(),
                    tag.pos().getX(), tag.pos().getY(), tag.pos().getZ()).withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    public static class Pickaxe extends PickaxeItem {
        public Pickaxe(Tier tier, Properties properties) {
            super(tier, properties);
        }

        @Override
        public boolean isFoil(ItemStack stack) {
            return super.isFoil(stack) || tagged(stack);
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
            taggedTooltip(stack, tooltip);
        }

        @Override
        public boolean isEnchantable(ItemStack stack) {
            return false;
        }

        @Override
        public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
            return false;
        }
    }

    public static class Axe extends AxeItem {
        public Axe(Tier tier, Properties properties) {
            super(tier, properties);
        }

        @Override
        public boolean isFoil(ItemStack stack) {
            return super.isFoil(stack) || tagged(stack);
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
            taggedTooltip(stack, tooltip);
        }

        @Override
        public boolean isEnchantable(ItemStack stack) {
            return false;
        }

        @Override
        public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
            return false;
        }
    }

    public static class Shovel extends ShovelItem {
        public Shovel(Tier tier, Properties properties) {
            super(tier, properties);
        }

        @Override
        public boolean isFoil(ItemStack stack) {
            return super.isFoil(stack) || tagged(stack);
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
            taggedTooltip(stack, tooltip);
        }

        @Override
        public boolean isEnchantable(ItemStack stack) {
            return false;
        }

        @Override
        public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
            return false;
        }
    }

    public static class Sword extends SwordItem {
        public Sword(Tier tier, Properties properties) {
            super(tier, properties);
        }

        @Override
        public boolean isFoil(ItemStack stack) {
            return super.isFoil(stack) || tagged(stack);
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
            taggedTooltip(stack, tooltip);
        }

        @Override
        public boolean isEnchantable(ItemStack stack) {
            return false;
        }

        @Override
        public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
            return false;
        }
    }
}
