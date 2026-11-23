package dev.oculifelis.ganys.client;

import dev.oculifelis.ganys.GanysItems;
import dev.oculifelis.ganys.GanysSurface;
import dev.oculifelis.ganys.item.EndiumBowItem;
import dev.oculifelis.ganys.block.ColouredRedstoneWireBlock;
import java.util.List;
import java.util.Set;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class GanysClient {
    public static void init(IEventBus modBus) {
        modBus.addListener(ConnectedGlassModel::register);
        modBus.addListener(GanysClient::tints);
        modBus.addListener(GanysClient::renderers);
        NeoForge.EVENT_BUS.addListener(GanysClient::fov);
    }

    private static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(GanysSurface.SOUL_CHEST_ENTITY.get(), SoulChestRenderer::new);
    }

    /** Leaf walls take the tint of the leaf they are made from; coloured dust takes its dye colour, dimmed by power. */
    private static void tints(RegisterColorHandlersEvent.BlockTintSources event) {
        GanysSurface.LEAF_WALLS.forEach((leaf, wall) ->
                event.register(event.getBlockColors().getTintSources(leaf.defaultBlockState()), wall.get()));
        GanysSurface.COLOURED_WIRES.values().forEach(wire -> event.register(List.of(new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                return ((ColouredRedstoneWireBlock) state.getBlock()).colourForPower(state.getValue(RedStoneWireBlock.POWER));
            }

            @Override
            public Set<Property<?>> relevantProperties() {
                return Set.of(RedStoneWireBlock.POWER);
            }
        }), wire.get()));
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
