package dev.oculifelis.ganys;

import java.util.List;
import dev.oculifelis.ganys.item.EnderScytheItem;
import dev.oculifelis.ganys.item.EndiumBowItem;
import dev.oculifelis.ganys.item.EndiumToolItem;
import dev.oculifelis.ganys.item.GanysArmorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
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
    public static final DeferredItem<Item> DIMENSIONAL_BREAD = ITEMS.registerSimpleItem("dimensional_bread", new Item.Properties().food(
            new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F)
                    .effect(() -> new MobEffectInstance(MobEffects.INVISIBILITY, 1200), 0.5F).build()));

    public static final DeferredItem<BlockItem> TILLED_NETHERRACK = ITEMS.registerSimpleBlockItem(GanysBlocks.TILLED_NETHERRACK);
    public static final DeferredItem<BlockItem> SOUL_GLASS = ITEMS.registerSimpleBlockItem(GanysBlocks.SOUL_GLASS);
    public static final DeferredItem<BlockItem> SOUL_GLASS_PANE = ITEMS.registerSimpleBlockItem(GanysBlocks.SOUL_GLASS_PANE);
    public static final DeferredItem<BlockItem> SOUL_GLASS_STAIRS = ITEMS.registerSimpleBlockItem(GanysBlocks.SOUL_GLASS_STAIRS);

    static {
        for (DeferredBlock<? extends Block> b : GanysBlocks.QUARTZ) ITEMS.registerSimpleBlockItem(b);
    }

    // Gany's End
    public static final DeferredItem<Item> ENDIUM_INGOT = ITEMS.registerSimpleItem("endium_ingot");
    public static final DeferredItem<Item> ENDIUM_NUGGET = ITEMS.registerSimpleItem("endium_nugget");
    public static final DeferredItem<Item> ENDSTONE_ROD = ITEMS.registerSimpleItem("endstone_rod");
    public static final DeferredItem<BlockItem> RAW_ENDIUM = ITEMS.registerSimpleBlockItem(GanysBlocks.RAW_ENDIUM);
    public static final DeferredItem<BlockItem> ENDIUM_BLOCK = ITEMS.registerSimpleBlockItem(GanysBlocks.ENDIUM_BLOCK);
    public static final DeferredItem<BlockItem> ENDER_PEARL_BLOCK = ITEMS.registerSimpleBlockItem(GanysBlocks.ENDER_PEARL_BLOCK);
    public static final DeferredItem<BlockItem> ENDER_PEARL_STAIRS = ITEMS.registerSimpleBlockItem(GanysBlocks.ENDER_PEARL_STAIRS);
    public static final DeferredItem<BlockItem> ENDER_PEARL_WALL = ITEMS.registerSimpleBlockItem(GanysBlocks.ENDER_PEARL_WALL);
    public static final DeferredItem<BlockItem> ENDER_FLOWER = ITEMS.registerSimpleBlockItem(GanysBlocks.ENDER_FLOWER);

    public static final DeferredItem<Item> ENDIUM_PICKAXE = tool("endium_pickaxe", GanysMaterials.ENDIUM, 0);
    public static final DeferredItem<Item> ENDIUM_AXE = tool("endium_axe", GanysMaterials.ENDIUM, 1);
    public static final DeferredItem<Item> ENDIUM_SHOVEL = tool("endium_shovel", GanysMaterials.ENDIUM, 2);
    public static final DeferredItem<Item> ENDIUM_SWORD = tool("endium_sword", GanysMaterials.ENDIUM, 3);
    public static final DeferredItem<Item> REINFORCED_ENDIUM_PICKAXE = tool("reinforced_endium_pickaxe", GanysMaterials.REINFORCED_ENDIUM, 0);
    public static final DeferredItem<Item> REINFORCED_ENDIUM_AXE = tool("reinforced_endium_axe", GanysMaterials.REINFORCED_ENDIUM, 1);
    public static final DeferredItem<Item> REINFORCED_ENDIUM_SHOVEL = tool("reinforced_endium_shovel", GanysMaterials.REINFORCED_ENDIUM, 2);
    public static final DeferredItem<Item> REINFORCED_ENDIUM_SWORD = tool("reinforced_endium_sword", GanysMaterials.REINFORCED_ENDIUM, 3);
    public static final DeferredItem<EndiumBowItem> ENDIUM_BOW = ITEMS.registerItem("endium_bow", EndiumBowItem::new,
            new Item.Properties().durability(131).rarity(Rarity.UNCOMMON));
    public static final DeferredItem<EndiumBowItem> REINFORCED_ENDIUM_BOW = ITEMS.registerItem("reinforced_endium_bow", EndiumBowItem::new,
            new Item.Properties().durability(1561).rarity(Rarity.RARE));
    public static final DeferredItem<EnderScytheItem> ENDER_SCYTHE = scythe("ender_scythe", GanysMaterials.ENDIUM);
    public static final DeferredItem<EnderScytheItem> REINFORCED_ENDER_SCYTHE = scythe("reinforced_ender_scythe", GanysMaterials.REINFORCED_ENDIUM);

    public static final DeferredItem<GanysArmorItem> ENDIUM_HELMET = armor("endium_helmet", GanysArmorItem.Kind.ENDIUM, ArmorItem.Type.HELMET);
    public static final DeferredItem<GanysArmorItem> ENDIUM_CHESTPLATE = armor("endium_chestplate", GanysArmorItem.Kind.ENDIUM, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<GanysArmorItem> ENDIUM_LEGGINGS = armor("endium_leggings", GanysArmorItem.Kind.ENDIUM, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<GanysArmorItem> ENDIUM_BOOTS = armor("endium_boots", GanysArmorItem.Kind.ENDIUM, ArmorItem.Type.BOOTS);

    // Gany's Nether blaze metal
    public static final DeferredItem<Item> RAW_BLAZE_INGOT = ITEMS.registerSimpleItem("raw_blaze_ingot");
    public static final DeferredItem<Item> BLAZE_INGOT = ITEMS.registerSimpleItem("blaze_ingot");
    public static final DeferredItem<Item> BLAZE_NUGGET = ITEMS.registerSimpleItem("blaze_nugget");
    public static final DeferredItem<BlockItem> BLAZE_BLOCK = ITEMS.registerSimpleBlockItem(GanysBlocks.BLAZE_BLOCK);
    public static final DeferredItem<GanysArmorItem> BLAZE_HELMET = armor("blaze_helmet", GanysArmorItem.Kind.BLAZE, ArmorItem.Type.HELMET);
    public static final DeferredItem<GanysArmorItem> BLAZE_CHESTPLATE = armor("blaze_chestplate", GanysArmorItem.Kind.BLAZE, ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<GanysArmorItem> BLAZE_LEGGINGS = armor("blaze_leggings", GanysArmorItem.Kind.BLAZE, ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<GanysArmorItem> BLAZE_BOOTS = armor("blaze_boots", GanysArmorItem.Kind.BLAZE, ArmorItem.Type.BOOTS);

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

    /** kind: 0 pickaxe, 1 axe, 2 shovel, 3 sword. Endium takes no enchantments, as in the original. */
    private static DeferredItem<Item> tool(String name, Tier tier, int kind) {
        Item.Properties p = new Item.Properties().rarity(Rarity.UNCOMMON);
        return ITEMS.register(name, () -> switch (kind) {
            case 0 -> new EndiumToolItem.Pickaxe(tier, p.attributes(PickaxeItem.createAttributes(tier, 1.0F, -2.8F)));
            case 1 -> new EndiumToolItem.Axe(tier, p.attributes(AxeItem.createAttributes(tier, 5.0F, -3.0F)));
            case 2 -> new EndiumToolItem.Shovel(tier, p.attributes(ShovelItem.createAttributes(tier, 1.5F, -3.0F)));
            default -> new EndiumToolItem.Sword(tier, p.attributes(SwordItem.createAttributes(tier, 3.0F, -2.4F)));
        });
    }

    private static DeferredItem<EnderScytheItem> scythe(String name, Tier tier) {
        return ITEMS.register(name, () -> new EnderScytheItem(tier, new Item.Properties().rarity(Rarity.EPIC)
                .attributes(SwordItem.createAttributes(tier, 3.0F, -2.4F))));
    }

    private static DeferredItem<GanysArmorItem> armor(String name, GanysArmorItem.Kind kind, ArmorItem.Type type) {
        Holder<ArmorMaterial> mat = kind == GanysArmorItem.Kind.ENDIUM ? GanysMaterials.ENDIUM_ARMOR : GanysMaterials.BLAZE_ARMOR;
        int durability = kind == GanysArmorItem.Kind.ENDIUM ? GanysMaterials.ENDIUM_DURABILITY : GanysMaterials.BLAZE_DURABILITY;
        Rarity rarity = kind == GanysArmorItem.Kind.ENDIUM ? Rarity.EPIC : Rarity.UNCOMMON;
        return ITEMS.register(name, () -> new GanysArmorItem(kind, mat, type,
                new Item.Properties().durability(type.getDurability(durability)).rarity(rarity)));
    }

    /** Seeds keep their own item name rather than the crop block's. */
    private static DeferredItem<BlockItem> seeds(String name, DeferredBlock<? extends Block> crop) {
        return ITEMS.register(name, () -> new BlockItem(crop.get(), new Item.Properties()) {
            @Override
            public String getDescriptionId() {
                return getOrCreateDescriptionId();
            }
        });
    }

    static class LavaBerry extends Item {
        LavaBerry(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.ganyscollection.lava_berry").withStyle(ChatFormatting.GRAY));
        }
    }

    private GanysItems() {
    }
}
