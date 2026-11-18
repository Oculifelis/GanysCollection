package dev.oculifelis.ganys.item;

import dev.oculifelis.ganys.GanysComponents;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

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
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        EndiumToolItem.taggedTooltip(stack, tooltip);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack bow = player.getItemInHand(hand);
        // The client can't see the tagged inventory, so a tagged bow always draws; release checks for arrows.
        if (bow.has(GanysComponents.TAGGED_INVENTORY.get()) && player.getProjectile(bow).isEmpty()) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        return super.use(level, player, hand);
    }

    @Override
    public boolean releaseUsing(ItemStack bow, Level level, LivingEntity entity, int remainingTime) {
        if (!(entity instanceof Player player) || !(level instanceof ServerLevel server)) {
            return super.releaseUsing(bow, level, entity, remainingTime);
        }
        Source source = taggedArrows(bow, server);
        if (source == null) {
            return super.releaseUsing(bow, level, entity, remainingTime);
        }
        int timeHeld = getUseDuration(bow, entity) - remainingTime;
        timeHeld = net.neoforged.neoforge.event.EventHooks.onArrowLoose(bow, level, player, timeHeld, true);
        if (timeHeld < 0) return false;
        float pow = getPowerForTime(timeHeld);
        if (pow < 0.1) return false;
        ItemStack arrow = source.resource.toStack(1);
        List<ItemStack> fired = draw(bow, arrow, player);
        if (arrow.isEmpty()) {
            try (Transaction tx = Transaction.openRoot()) {
                if (source.handler.extract(source.slot, source.resource, 1, tx) == 1) tx.commit();
            }
        }
        if (!fired.isEmpty()) {
            shoot(server, player, player.getUsedItemHand(), bow, fired, pow * 3.0F, 1.0F, pow == 1.0F, null);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + pow * 0.5F);
        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }

    private record Source(ResourceHandler<ItemResource> handler, int slot, ItemResource resource) {
    }

    /** The first arrow in the tagged inventory, when it is loaded and in this dimension. */
    private static @Nullable Source taggedArrows(ItemStack bow, ServerLevel level) {
        GlobalPos tag = bow.get(GanysComponents.TAGGED_INVENTORY.get());
        if (tag == null || tag.dimension() != level.dimension() || !level.isLoaded(tag.pos())) return null;
        ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, tag.pos(), null);
        if (handler == null) return null;
        for (int i = 0; i < handler.size(); i++) {
            ItemResource res = handler.getResource(i);
            if (!res.isEmpty() && res.toStack(1).is(ItemTags.ARROWS)) return new Source(handler, i, res);
        }
        return null;
    }
}
