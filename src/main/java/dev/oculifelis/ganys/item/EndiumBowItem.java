package dev.oculifelis.ganys.item;

import dev.oculifelis.ganys.GanysComponents;
import java.util.List;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

/** Endium bow: while tagged it takes arrows from the tagged inventory before the player's own. */
public class EndiumBowItem extends BowItem {
    public EndiumBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || stack.has(GanysComponents.TAGGED_INVENTORY.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        EndiumToolItem.taggedTooltip(stack, tooltip);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bow = player.getItemInHand(hand);
        // The client can't see the tagged inventory, so a tagged bow always draws; release checks for arrows.
        if (bow.has(GanysComponents.TAGGED_INVENTORY.get()) && player.getProjectile(bow).isEmpty()) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(bow);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void releaseUsing(ItemStack bow, Level level, LivingEntity entity, int remainingTime) {
        if (!(entity instanceof Player player) || !(level instanceof ServerLevel server)) {
            super.releaseUsing(bow, level, entity, remainingTime);
            return;
        }
        Source source = taggedArrows(bow, server);
        if (source == null) {
            super.releaseUsing(bow, level, entity, remainingTime);
            return;
        }
        int timeHeld = getUseDuration(bow, entity) - remainingTime;
        timeHeld = net.neoforged.neoforge.event.EventHooks.onArrowLoose(bow, level, player, timeHeld, true);
        if (timeHeld < 0) return;
        float pow = getPowerForTime(timeHeld);
        if (pow < 0.1) return;
        ItemStack arrow = source.handler.getStackInSlot(source.slot).copyWithCount(1);
        List<ItemStack> fired = draw(bow, arrow, player);
        if (arrow.isEmpty()) {
            source.handler.extractItem(source.slot, 1, false);
        }
        if (!fired.isEmpty()) {
            shoot(server, player, player.getUsedItemHand(), bow, fired, pow * 3.0F, 1.0F, pow == 1.0F, null);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + pow * 0.5F);
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    private record Source(IItemHandler handler, int slot) {
    }

    /** The first arrow in the tagged inventory, when it is loaded and in this dimension. */
    private static @Nullable Source taggedArrows(ItemStack bow, ServerLevel level) {
        GlobalPos tag = bow.get(GanysComponents.TAGGED_INVENTORY.get());
        if (tag == null || tag.dimension() != level.dimension() || !level.isLoaded(tag.pos())) return null;
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, tag.pos(), null);
        if (handler == null) return null;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && stack.is(ItemTags.ARROWS)) return new Source(handler, i);
        }
        return null;
    }
}
