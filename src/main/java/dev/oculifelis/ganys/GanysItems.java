package dev.oculifelis.ganys;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GanysItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GanysCollection.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GanysCollection.MOD_ID);

    public static final DeferredItem<BlockItem> GHOST_SEEDS = seeds("ghost_seeds", GanysBlocks.SPECTRE_WHEAT);
    public static final DeferredItem<BlockItem> QUARTZ_BERRY_SEEDS = seeds("quartz_berry_seeds", GanysBlocks.QUARTZ_BERRY_BUSH);
    public static final DeferredItem<BlockItem> HELL_BUSH_SEEDS = seeds("hell_bush_seeds", GanysBlocks.HELL_BUSH);
    public static final DeferredItem<BlockItem> WITHER_SEEDS = seeds("wither_seeds", GanysBlocks.WITHER_SHRUB);
    public static final DeferredItem<BlockItem> GLOWING_REED = ITEMS.registerSimpleBlockItem(GanysBlocks.GLOWING_REED);

    public static final DeferredItem<Item> SPECTRE_WHEAT = ITEMS.registerSimpleItem("spectre_wheat");
    public static final DeferredItem<Item> QUARTZ_BERRY = ITEMS.registerSimpleItem("quartz_berry");
    public static final DeferredItem<Item> LAVA_BERRY = ITEMS.registerItem("lava_berry", LavaBerry::new);
    public static final DeferredItem<Item> FLOUR = ITEMS.registerSimpleItem("flour");
    public static final DeferredItem<Item> SPOOKY_FLOUR = ITEMS.registerSimpleItem("spooky_flour");
    public static final DeferredItem<Item> DIMENSIONAL_BREAD = ITEMS.registerSimpleItem("dimensional_bread", p -> p.food(
            new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F).build(),
            Consumables.defaultFood().onConsume(new ApplyStatusEffectsConsumeEffect(
                    new MobEffectInstance(MobEffects.INVISIBILITY, 1200), 0.5F)).build()));

    public static final DeferredItem<BlockItem> TILLED_NETHERRACK = ITEMS.registerSimpleBlockItem(GanysBlocks.TILLED_NETHERRACK);
    public static final DeferredItem<BlockItem> SOUL_GLASS = ITEMS.registerSimpleBlockItem(GanysBlocks.SOUL_GLASS);
    public static final DeferredItem<BlockItem> SOUL_GLASS_PANE = ITEMS.registerSimpleBlockItem(GanysBlocks.SOUL_GLASS_PANE);
    public static final DeferredItem<BlockItem> SOUL_GLASS_STAIRS = ITEMS.registerSimpleBlockItem(GanysBlocks.SOUL_GLASS_STAIRS);

    static {
        for (DeferredBlock<? extends Block> b : GanysBlocks.QUARTZ) ITEMS.registerSimpleBlockItem(b);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ganyscollection"))
            .icon(() -> new ItemStack(QUARTZ_BERRY.get()))
            .displayItems((params, output) -> ITEMS.getEntries().forEach(e -> output.accept(e.get())))
            .build());

    /** The seed each nether crop drops and is picked with. */
    public static ItemLike seedOf(Block crop) {
        if (crop == GanysBlocks.QUARTZ_BERRY_BUSH.get()) return QUARTZ_BERRY_SEEDS.get();
        if (crop == GanysBlocks.HELL_BUSH.get()) return HELL_BUSH_SEEDS.get();
        if (crop == GanysBlocks.WITHER_SHRUB.get()) return WITHER_SEEDS.get();
        return GHOST_SEEDS.get();
    }

    private static DeferredItem<BlockItem> seeds(String name, DeferredBlock<? extends Block> crop) {
        return ITEMS.registerItem(name, p -> new BlockItem(crop.get(), p), p -> p.useItemDescriptionPrefix());
    }

    static class LavaBerry extends Item {
        LavaBerry(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                    Consumer<Component> tooltip, TooltipFlag flag) {
            tooltip.accept(Component.translatable("tooltip.ganyscollection.lava_berry").withStyle(ChatFormatting.GRAY));
        }
    }

    private GanysItems() {
    }
}
