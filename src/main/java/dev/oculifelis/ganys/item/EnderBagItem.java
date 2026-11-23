package dev.oculifelis.ganys.item;

import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/** Opens the holder's ender chest inventory anywhere. */
public class EnderBagItem extends Item {
    private static final Component TITLE = Component.translatable("container.enderchest");

    public EnderBagItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            PlayerEnderChestContainer ender = player.getEnderChestInventory();
            ender.setActiveChest(null);
            player.openMenu(new SimpleMenuProvider((id, inv, p) -> ChestMenu.threeRows(id, inv, ender), TITLE));
            player.awardStat(Stats.OPEN_ENDERCHEST);
        }
        return InteractionResult.SUCCESS;
    }
}
