package dev.oculifelis.ganys;

import dev.oculifelis.ganys.client.GanysClient;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import dev.oculifelis.ganys.item.BottomlessBucketItem;
import dev.oculifelis.ganys.item.InfiniteBucketItem;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

@Mod(GanysCollection.MOD_ID)
public class GanysCollection {
    public static final String MOD_ID = "ganyscollection";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public GanysCollection(IEventBus modBus) {
        GanysBlocks.BLOCKS.register(modBus);
        GanysItems.ITEMS.register(modBus);
        GanysSurface.BLOCKS.register(modBus);
        GanysSurface.ITEMS.register(modBus);
        GanysSurface.BLOCK_ENTITIES.register(modBus);
        GanysItems.TABS.register(modBus);
        GanysComponents.COMPONENTS.register(modBus);
        GanysEvents.register(modBus);
        NeoForge.EVENT_BUS.addListener(GanysCollection::till);
        modBus.addListener(GanysCollection::setup);
        if (FMLEnvironment.getDist() == Dist.CLIENT) GanysClient.init(modBus);
    }

    private static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DispenserBlock.registerBehavior(GanysItems.BOTTOMLESS_BUCKET.get(), BottomlessBucketItem.DISPENSE);
            DispenserBlock.registerBehavior(GanysItems.INFINITE_BUCKET.get(), InfiniteBucketItem.DISPENSE);
        });
    }

    /** A hoe turns netherrack with air above it into tilled netherrack, unless another mod already claimed it. */
    private static void till(BlockEvent.BlockToolModificationEvent event) {
        if (event.getItemAbility() != ItemAbilities.HOE_TILL || !event.getState().is(Blocks.NETHERRACK)) return;
        if (event.getFinalState() != event.getState()) return;
        UseOnContext context = event.getContext();
        if (context.getClickedFace() == Direction.DOWN) return;
        if (!context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) return;
        event.setFinalState(GanysBlocks.TILLED_NETHERRACK.get().defaultBlockState());
    }
}
