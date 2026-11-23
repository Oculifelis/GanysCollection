package dev.oculifelis.ganys.item;

import dev.oculifelis.ganys.GanysComponents;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** Ender scythe: lightning charges it, and each charged kill drops the victim's head. */
public class EnderScytheItem extends Item {
    public EnderScytheItem(Properties properties) {
        super(properties);
    }

    public static int charges(ItemStack stack) {
        return stack.getOrDefault(GanysComponents.SCYTHE_CHARGES.get(), 0);
    }

    public static void setCharges(ItemStack stack, int charges) {
        if (charges <= 0) {
            stack.remove(GanysComponents.SCYTHE_CHARGES.get());
        } else {
            stack.set(GanysComponents.SCYTHE_CHARGES.get(), charges);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || charges(stack) > 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.ganyscollection.charges", charges(stack)).withStyle(ChatFormatting.GRAY));
    }
}
