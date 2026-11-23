package dev.oculifelis.ganys;

import dev.oculifelis.ganys.block.BlazingCactoidBlock;
import dev.oculifelis.ganys.block.BlockDetectorBlock;
import dev.oculifelis.ganys.block.AnchoredEnderChestBlock;
import dev.oculifelis.ganys.block.AnchoredEnderChestBlockEntity;
import dev.oculifelis.ganys.block.VoidCrateBlock;
import dev.oculifelis.ganys.block.VoidCrateBlockEntity;
import dev.oculifelis.ganys.item.EnderBagItem;
import dev.oculifelis.ganys.block.GlowBoxBlock;
import dev.oculifelis.ganys.block.WeepingPodBlock;
import dev.oculifelis.ganys.block.SoulTntBlock;
import dev.oculifelis.ganys.entity.SoulPrimedTnt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import dev.oculifelis.ganys.block.SoulChestBlock;
import dev.oculifelis.ganys.block.SoulChestBlockEntity;
import dev.oculifelis.ganys.block.ChocolateCakeBlock;
import dev.oculifelis.ganys.block.BlockDetectorBlockEntity;
import dev.oculifelis.ganys.block.CamelliaCropBlock;
import dev.oculifelis.ganys.block.ColouredRedstoneBlock;
import dev.oculifelis.ganys.block.ColouredRedstoneWireBlock;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import dev.oculifelis.ganys.block.CushionBlock;
import dev.oculifelis.ganys.block.InvertedLampBlock;
import dev.oculifelis.ganys.block.SlowRailBlock;
import dev.oculifelis.ganys.item.IcyPickaxeItem;
import dev.oculifelis.ganys.item.WoodenWrenchItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ToolMaterial;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.core.HolderSet;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Phase 3: Gany's Surface food, the tea line, fertiliser and simple storage blocks, plus Gany's Nether mob drops. */
public final class GanysSurface {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GanysCollection.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GanysCollection.MOD_ID);

    // blocks
    public static final DeferredBlock<CamelliaCropBlock> CAMELLIA = BLOCKS.registerBlock("camellia",
            CamelliaCropBlock::new, () -> Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.PLANT));
    public static final DeferredBlock<Block> CHARCOAL_BLOCK = BLOCKS.registerBlock("charcoal_block", Block::new,
            () -> Properties.ofFullCopy(Blocks.COAL_BLOCK).mapColor(MapColor.COLOR_GRAY));
    public static final DeferredBlock<Block> BLOCK_OF_POOP = BLOCKS.registerBlock("block_of_poop", Block::new,
            () -> Properties.of().mapColor(MapColor.DIRT).strength(0.5F).sound(SoundType.MUD));

    // tea line
    public static final DeferredItem<BlockItem> CAMELLIA_SEEDS = ITEMS.registerItem("camellia_seeds",
            p -> new BlockItem(CAMELLIA.get(), p), p -> p.useItemDescriptionPrefix());
    public static final DeferredItem<Item> TEA_LEAVES = ITEMS.registerSimpleItem("tea_leaves");
    public static final DeferredItem<Item> TEA_BAG = ITEMS.registerSimpleItem("tea_bag");
    public static final DeferredItem<Item> CLAY_MUG = ITEMS.registerSimpleItem("clay_mug");
    public static final DeferredItem<Item> MUG = ITEMS.registerSimpleItem("mug");
    /** Clears slowness, mining fatigue and weakness, then gives Speed IV for a minute. */
    public static final DeferredItem<Item> CUP_OF_TEA = ITEMS.registerSimpleItem("cup_of_tea", p -> p.stacksTo(1)
            .usingConvertsTo(MUG.get())
            .food(new FoodProperties.Builder().nutrition(0).saturationModifier(0).alwaysEdible().build(), tea()
                    .onConsume(new RemoveStatusEffectsConsumeEffect(HolderSet.direct(MobEffects.SLOWNESS, MobEffects.MINING_FATIGUE, MobEffects.WEAKNESS)))
                    .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, 1200, 3))).build()));
    /** Tea made the wrong way round: clears speed, haste and jump boost and hands out the bad ones. */
    public static final DeferredItem<Item> MANKY_CUP_OF_TEA = ITEMS.registerSimpleItem("manky_cup_of_tea", p -> p.stacksTo(1)
            .usingConvertsTo(MUG.get())
            .food(new FoodProperties.Builder().nutrition(0).saturationModifier(0).alwaysEdible().build(), tea()
                    .onConsume(new RemoveStatusEffectsConsumeEffect(HolderSet.direct(MobEffects.SPEED, MobEffects.HASTE, MobEffects.JUMP_BOOST)))
                    .onConsume(new ApplyStatusEffectsConsumeEffect(java.util.List.of(
                            new MobEffectInstance(MobEffects.SLOWNESS, 1200, 3), new MobEffectInstance(MobEffects.MINING_FATIGUE, 1200),
                            new MobEffectInstance(MobEffects.WEAKNESS, 1200, 3), new MobEffectInstance(MobEffects.HUNGER, 900)))).build()));

    // food (nutrition and saturation from the original)
    public static final DeferredItem<Item> PINE_CONE = ITEMS.registerSimpleItem("pine_cone");
    public static final DeferredItem<Item> PINE_NUTS = food("pine_nuts", 4, 0.3F);
    public static final DeferredItem<Item> CHOCOLATE_BAR = food("chocolate_bar", 3, 1.2F);
    public static final DeferredItem<Item> COOKED_EGG = ITEMS.registerSimpleItem("cooked_egg", p -> p.stacksTo(16)
            .food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.6F).build()));
    public static final DeferredItem<Item> ROASTED_SQUID = food("roasted_squid", 6, 0.5F);
    public static final DeferredItem<Item> BAT_STEW = ITEMS.registerSimpleItem("bat_stew", p -> p.stacksTo(1)
            .usingConvertsTo(Items.BOWL).food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.6F).build()));

    // fertiliser line: rot and fertiliser work as bone meal, as in the original
    public static final DeferredItem<BoneMealItem> ROT = ITEMS.registerItem("rot", BoneMealItem::new);
    public static final DeferredItem<BoneMealItem> FERTILISER = ITEMS.registerItem("fertiliser", BoneMealItem::new);
    public static final DeferredItem<Item> POOP = ITEMS.registerSimpleItem("poop");
    public static final DeferredItem<Item> BAT_POOP = ITEMS.registerSimpleItem("bat_poop");
    public static final DeferredItem<BlockItem> BLOCK_OF_POOP_ITEM = ITEMS.registerSimpleBlockItem(BLOCK_OF_POOP);
    public static final DeferredItem<BlockItem> CHARCOAL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(CHARCOAL_BLOCK);

    // Gany's Nether mob drops and materials
    public static final DeferredItem<Item> BAT_WING = ITEMS.registerSimpleItem("bat_wing");
    public static final DeferredItem<Item> COOKED_BAT_WING = food("cooked_bat_wing", 1, 0.6F);
    public static final DeferredItem<Item> WOLF_TEETH = ITEMS.registerSimpleItem("wolf_teeth");
    public static final DeferredItem<Item> SILVERFISH_SCALE = ITEMS.registerSimpleItem("silverfish_scale");
    public static final DeferredItem<Item> LIVING_SOUL = ITEMS.registerSimpleItem("living_soul");
    public static final DeferredItem<Item> NETHER_CORE = ITEMS.registerSimpleItem("nether_core");

    // redstone, rails and tools
    public static final DeferredBlock<InvertedLampBlock> INVERTED_REDSTONE_LAMP = BLOCKS.registerBlock("inverted_redstone_lamp",
            InvertedLampBlock::new, () -> Properties.ofFullCopy(Blocks.REDSTONE_LAMP)
                    .lightLevel(s -> s.getValue(InvertedLampBlock.LIT) ? 15 : 0));
    public static final DeferredBlock<CushionBlock> CUSHION = BLOCKS.registerBlock("cushion", CushionBlock::new,
            () -> Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.2F).sound(SoundType.WOOL));
    public static final DeferredBlock<SlowRailBlock> SLOW_RAIL = BLOCKS.registerBlock("slow_rail", SlowRailBlock::new,
            () -> Properties.ofFullCopy(Blocks.POWERED_RAIL));
    public static final DeferredItem<BlockItem> INVERTED_REDSTONE_LAMP_ITEM = ITEMS.registerSimpleBlockItem(INVERTED_REDSTONE_LAMP);
    public static final DeferredItem<BlockItem> CUSHION_ITEM = ITEMS.registerSimpleBlockItem(CUSHION);
    public static final DeferredItem<BlockItem> SLOW_RAIL_ITEM = ITEMS.registerSimpleBlockItem(SLOW_RAIL);

    /** Iron-tier ice: 512 uses, speed 6, enchantability 14, mended with ice (values from the original). */
    public static final TagKey<Item> ICE_TOOL_MATERIALS = TagKey.create(Registries.ITEM, GanysCollection.id("ice_tool_materials"));
    public static final ToolMaterial ICE = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 512, 6.0F, 2.0F, 14, ICE_TOOL_MATERIALS);
    public static final DeferredItem<IcyPickaxeItem> ICY_PICKAXE = ITEMS.registerItem("icy_pickaxe", IcyPickaxeItem::new,
            p -> p.pickaxe(ICE, 1.0F, -2.8F));
    public static final DeferredItem<WoodenWrenchItem> WOODEN_WRENCH = ITEMS.registerItem("wooden_wrench", WoodenWrenchItem::new,
            p -> p.stacksTo(1));

    // dye blocks: one solid block per dye colour, nine dyes each
    public static final Map<DyeColor, DeferredBlock<Block>> DYE_BLOCKS = new EnumMap<>(DyeColor.class);
    public static final Map<DyeColor, DeferredItem<BlockItem>> DYE_BLOCK_ITEMS = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor c : DyeColor.values()) {
            DeferredBlock<Block> b = BLOCKS.registerBlock(c.getName() + "_dye_block", Block::new,
                    () -> Properties.of().mapColor(c.getMapColor()).strength(0.8F).sound(SoundType.SAND));
            DYE_BLOCKS.put(c, b);
            DYE_BLOCK_ITEMS.put(c, ITEMS.registerSimpleBlockItem(b));
        }
    }

    // coloured redstone: dust and blocks per dye colour (red is vanilla), each colour its own circuit
    public static final Map<DyeColor, DeferredBlock<ColouredRedstoneWireBlock>> COLOURED_WIRES = new EnumMap<>(DyeColor.class);
    public static final Map<DyeColor, DeferredItem<BlockItem>> COLOURED_REDSTONE = new EnumMap<>(DyeColor.class);
    public static final Map<DyeColor, DeferredBlock<ColouredRedstoneBlock>> COLOURED_REDSTONE_BLOCKS = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor c : DyeColor.values()) {
            if (c == DyeColor.RED) continue;
            DeferredBlock<ColouredRedstoneWireBlock> wire = BLOCKS.registerBlock(c.getName() + "_redstone_wire",
                    p -> new ColouredRedstoneWireBlock(c, p), () -> Properties.of().noCollision().instabreak().pushReaction(PushReaction.DESTROY));
            COLOURED_WIRES.put(c, wire);
            COLOURED_REDSTONE.put(c, ITEMS.registerItem(c.getName() + "_redstone", p -> new BlockItem(wire.get(), p), p -> p.useItemDescriptionPrefix()));
            DeferredBlock<ColouredRedstoneBlock> block = BLOCKS.registerBlock(c.getName() + "_redstone_block",
                    p -> new ColouredRedstoneBlock(c, p), () -> Properties.of().mapColor(c.getMapColor()).requiresCorrectToolForDrops()
                            .strength(5.0F, 6.0F).sound(SoundType.METAL).isRedstoneConductor((st, lv, ps) -> false));
            COLOURED_REDSTONE_BLOCKS.put(c, block);
            ITEMS.registerSimpleBlockItem(block);
        }
    }

    // block detector: signals while a neighbour matches its filter item
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, GanysCollection.MOD_ID);
    public static final DeferredBlock<BlockDetectorBlock> BLOCK_DETECTOR = BLOCKS.registerBlock("block_detector", BlockDetectorBlock::new,
            () -> Properties.of().mapColor(MapColor.STONE).strength(2.0F).requiresCorrectToolForDrops());
    public static final DeferredItem<BlockItem> BLOCK_DETECTOR_ITEM = ITEMS.registerSimpleBlockItem(BLOCK_DETECTOR);
    public static final Supplier<BlockEntityType<BlockDetectorBlockEntity>> BLOCK_DETECTOR_ENTITY = BLOCK_ENTITIES.register("block_detector",
            () -> new BlockEntityType<>(BlockDetectorBlockEntity::new, BLOCK_DETECTOR.get()));

    // disguised trapdoors: a wooden trapdoor that looks like the planks around it
    public static final Map<String, DeferredBlock<TrapDoorBlock>> DISGUISED_TRAPDOORS = new LinkedHashMap<>();

    static {
        Object[][] woods = {{"oak", BlockSetType.OAK, Blocks.OAK_TRAPDOOR}, {"spruce", BlockSetType.SPRUCE, Blocks.SPRUCE_TRAPDOOR},
                {"birch", BlockSetType.BIRCH, Blocks.BIRCH_TRAPDOOR}, {"jungle", BlockSetType.JUNGLE, Blocks.JUNGLE_TRAPDOOR},
                {"acacia", BlockSetType.ACACIA, Blocks.ACACIA_TRAPDOOR}, {"dark_oak", BlockSetType.DARK_OAK, Blocks.DARK_OAK_TRAPDOOR},
                {"mangrove", BlockSetType.MANGROVE, Blocks.MANGROVE_TRAPDOOR}, {"cherry", BlockSetType.CHERRY, Blocks.CHERRY_TRAPDOOR},
                {"pale_oak", BlockSetType.PALE_OAK, Blocks.PALE_OAK_TRAPDOOR}, {"bamboo", BlockSetType.BAMBOO, Blocks.BAMBOO_TRAPDOOR},
                {"crimson", BlockSetType.CRIMSON, Blocks.CRIMSON_TRAPDOOR}, {"warped", BlockSetType.WARPED, Blocks.WARPED_TRAPDOOR}};
        for (Object[] w : woods) {
            BlockSetType type = (BlockSetType) w[1];
            Block kin = (Block) w[2];
            DeferredBlock<TrapDoorBlock> b = BLOCKS.registerBlock(w[0] + "_disguised_trapdoor", p -> new TrapDoorBlock(type, p),
                    () -> Properties.ofFullCopy(kin));
            DISGUISED_TRAPDOORS.put((String) w[0], b);
            ITEMS.registerSimpleBlockItem(b);
        }
    }

    /** Gany's Surface leaf walls: one wall per vanilla leaf, keyed by that leaf (its tint is borrowed on the client). */
    public static final Map<Block, DeferredBlock<WallBlock>> LEAF_WALLS = new LinkedHashMap<>();

    static {
        Object[][] leaves = {{"oak", Blocks.OAK_LEAVES}, {"spruce", Blocks.SPRUCE_LEAVES}, {"birch", Blocks.BIRCH_LEAVES},
                {"jungle", Blocks.JUNGLE_LEAVES}, {"acacia", Blocks.ACACIA_LEAVES}, {"dark_oak", Blocks.DARK_OAK_LEAVES},
                {"mangrove", Blocks.MANGROVE_LEAVES}, {"cherry", Blocks.CHERRY_LEAVES}, {"pale_oak", Blocks.PALE_OAK_LEAVES},
                {"azalea", Blocks.AZALEA_LEAVES}, {"flowering_azalea", Blocks.FLOWERING_AZALEA_LEAVES}};
        for (Object[] l : leaves) {
            Block kin = (Block) l[1];
            DeferredBlock<WallBlock> b = BLOCKS.registerBlock(l[0] + "_leaf_wall", WallBlock::new,
                    p -> p.mapColor(kin.defaultMapColor()).strength(1.0F).sound(kin.defaultBlockState().getSoundType())
                            .noOcclusion().isSuffocating((st, lv, ps) -> false).isViewBlocking((st, lv, ps) -> false)
                            .ignitedByLava());
            LEAF_WALLS.put(kin, b);
            ITEMS.registerSimpleBlockItem(b);
        }
    }

    // tranche C
    /** Nether cactus that grows on netherrack and sets what touches it alight. */
    public static final DeferredBlock<BlazingCactoidBlock> BLAZING_CACTOID = BLOCKS.registerBlock("blazing_cactoid", BlazingCactoidBlock::new,
            () -> Properties.of().mapColor(MapColor.NETHER).strength(0.4F).lightLevel(s -> 12).sound(SoundType.WOOL)
                    .randomTicks().noOcclusion().pushReaction(PushReaction.DESTROY));
    public static final DeferredItem<BlockItem> BLAZING_CACTOID_ITEM = ITEMS.registerSimpleBlockItem(BLAZING_CACTOID);

    /** Six slices, eaten with either mouse button; what a slice feeds is the block data map ganyscollection:cake_food. */
    public static final DeferredBlock<ChocolateCakeBlock> CHOCOLATE_CAKE = BLOCKS.registerBlock("chocolate_cake", ChocolateCakeBlock::new,
            () -> Properties.ofFullCopy(Blocks.CAKE).mapColor(MapColor.COLOR_BROWN));
    public static final DeferredItem<BlockItem> CHOCOLATE_CAKE_ITEM = ITEMS.registerItem("chocolate_cake",
            p -> new BlockItem(CHOCOLATE_CAKE.get(), p), p -> p.stacksTo(1).useBlockDescriptionPrefix());

    /** Soul sand chest: pulls in items that touch it and slows anything else on it. */
    public static final DeferredBlock<SoulChestBlock> SOUL_CHEST = BLOCKS.registerBlock("soul_chest", SoulChestBlock::new,
            () -> Properties.of().mapColor(MapColor.COLOR_BROWN).strength(2.5F).sound(SoundType.SOUL_SAND));
    public static final DeferredItem<BlockItem> SOUL_CHEST_ITEM = ITEMS.registerSimpleBlockItem(SOUL_CHEST);
    public static final Supplier<BlockEntityType<SoulChestBlockEntity>> SOUL_CHEST_ENTITY = BLOCK_ENTITIES.register("soul_chest",
            () -> new BlockEntityType<>(SoulChestBlockEntity::new, SOUL_CHEST.get()));

    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(GanysCollection.MOD_ID);

    /** Soul TNT: a fiery blast that leaves everything nearby slowed for minutes; numbers in the soul_tnt data map. */
    public static final DeferredBlock<SoulTntBlock> SOUL_TNT = BLOCKS.registerBlock("soul_tnt", SoulTntBlock::new,
            () -> Properties.of().mapColor(MapColor.COLOR_BROWN).instabreak().sound(SoundType.GRASS).ignitedByLava());
    public static final DeferredItem<BlockItem> SOUL_TNT_ITEM = ITEMS.registerSimpleBlockItem(SOUL_TNT);
    public static final Supplier<EntityType<SoulPrimedTnt>> SOUL_TNT_ENTITY = ENTITIES.registerEntityType("soul_tnt",
            SoulPrimedTnt::new, MobCategory.MISC, b -> b.sized(0.98F, 0.98F).fireImmune().clientTrackingRange(10).updateInterval(10));

    /** Weeping pod: grows on obsidian from a ghast tear and drops ghast tears; no item of its own. */
    public static final DeferredBlock<WeepingPodBlock> WEEPING_POD = BLOCKS.registerBlock("weeping_pod", WeepingPodBlock::new,
            () -> Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.2F, 5.0F).sound(SoundType.WOOD).randomTicks()
                    .noOcclusion().pushReaction(PushReaction.DESTROY));

    /** Glow boxes: a light-15 glass post in each dye colour. */
    public static final Map<DyeColor, DeferredBlock<GlowBoxBlock>> GLOW_BOXES = new EnumMap<>(DyeColor.class);
    public static final Map<DyeColor, DeferredItem<BlockItem>> GLOW_BOX_ITEMS = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor c : DyeColor.values()) {
            DeferredBlock<GlowBoxBlock> b = BLOCKS.registerBlock(c.getName() + "_glow_box", GlowBoxBlock::new,
                    () -> Properties.of().mapColor(c.getMapColor()).strength(0.2F).sound(SoundType.GLASS).lightLevel(s -> 15).noOcclusion());
            GLOW_BOXES.put(c, b);
            GLOW_BOX_ITEMS.put(c, ITEMS.registerSimpleBlockItem(b));
        }
    }

    /** Ender bag: opens the holder's ender chest anywhere. */
    public static final DeferredItem<EnderBagItem> ENDER_BAG = ITEMS.registerItem("ender_bag", EnderBagItem::new, p -> p.stacksTo(1));

    /** Anchored ender chest: its placer's ender inventory, for anyone, while that player is online. */
    public static final DeferredBlock<AnchoredEnderChestBlock> ANCHORED_ENDER_CHEST = BLOCKS.registerBlock("anchored_ender_chest",
            AnchoredEnderChestBlock::new, () -> Properties.ofFullCopy(Blocks.ENDER_CHEST));
    public static final DeferredItem<BlockItem> ANCHORED_ENDER_CHEST_ITEM = ITEMS.registerSimpleBlockItem(ANCHORED_ENDER_CHEST);
    public static final Supplier<BlockEntityType<AnchoredEnderChestBlockEntity>> ANCHORED_ENDER_CHEST_ENTITY = BLOCK_ENTITIES.register(
            "anchored_ender_chest", () -> new BlockEntityType<>(AnchoredEnderChestBlockEntity::new, ANCHORED_ENDER_CHEST.get()));

    /** Void crate: storage that destroys overflow from hoppers and pipes. */
    public static final DeferredBlock<VoidCrateBlock> VOID_CRATE = BLOCKS.registerBlock("void_crate", VoidCrateBlock::new,
            () -> Properties.of().mapColor(MapColor.METAL).strength(2.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredItem<BlockItem> VOID_CRATE_ITEM = ITEMS.registerSimpleBlockItem(VOID_CRATE);
    public static final Supplier<BlockEntityType<VoidCrateBlockEntity>> VOID_CRATE_ENTITY = BLOCK_ENTITIES.register("void_crate",
            () -> new BlockEntityType<>(VoidCrateBlockEntity::new, VOID_CRATE.get()));

    private static Consumable.Builder tea() {
        return Consumables.defaultDrink().consumeSeconds(1.75F);
    }

    private static DeferredItem<Item> food(String name, int nutrition, float saturation) {
        return ITEMS.registerSimpleItem(name, p -> p.food(new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build()));
    }

    private GanysSurface() {
    }
}
