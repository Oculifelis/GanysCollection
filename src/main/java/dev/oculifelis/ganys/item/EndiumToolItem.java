package dev.oculifelis.ganys.item;

import dev.oculifelis.ganys.GanysComponents;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** Endium pickaxe, axe, shovel and sword: glint and a tooltip while tagged to an inventory. */
public class EndiumToolItem extends Item {
    public EndiumToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || stack.has(GanysComponents.TAGGED_INVENTORY.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        taggedTooltip(stack, tooltip);
    }

    /** The original's "dim : x, y, z" line, or "not tagged". */
    public static void taggedTooltip(ItemStack stack, Consumer<Component> tooltip) {
        GlobalPos tag = stack.get(GanysComponents.TAGGED_INVENTORY.get());
        if (tag == null) {
            tooltip.accept(Component.translatable("tooltip.ganyscollection.not_tagged").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.accept(Component.translatable("tooltip.ganyscollection.tagged", tag.dimension().identifier().toString(),
                    tag.pos().getX(), tag.pos().getY(), tag.pos().getZ()).withStyle(ChatFormatting.DARK_PURPLE));
        }
    }
}
