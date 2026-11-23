package dev.oculifelis.ganys.client;

import dev.oculifelis.ganys.GanysItems;
import dev.oculifelis.ganys.item.EndiumBowItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class GanysClient {
    public static void init(IEventBus modBus) {
        modBus.addListener(ConnectedGlassModel::register);
        NeoForge.EVENT_BUS.addListener(GanysClient::fov);
    }

    /** Endium leggings keep sprinting from widening the view; drawing an endium bow zooms in. */
    private static void fov(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        float fov = event.getNewFovModifier();
        if (player.getItemBySlot(EquipmentSlot.LEGS).is(GanysItems.ENDIUM_LEGGINGS.get())) fov = Math.min(fov, 1.0F);
        ItemStack using = player.getUseItem();
        if (player.isUsingItem() && using.getItem() instanceof EndiumBowItem) {
            float f = Math.min(player.getTicksUsingItem() / 20.0F, 1.0F);
            fov *= 1.0F - f * f * 0.15F;
        }
        event.setNewFovModifier(fov);
    }

    private GanysClient() {
    }
}
