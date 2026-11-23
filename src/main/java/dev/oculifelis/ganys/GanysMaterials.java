package dev.oculifelis.ganys;

import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

/** Gany's End endium and Gany's Nether blaze materials, with the original numbers. */
public final class GanysMaterials {
    public static final TagKey<Item> ENDIUM_TOOL_MATERIALS = item("endium_tool_materials");
    public static final TagKey<Item> REPAIRS_ENDIUM_ARMOR = item("repairs_endium_armor");
    public static final TagKey<Item> REPAIRS_BLAZE_ARMOR = item("repairs_blaze_armor");

    /** Diamond level, 131 uses, speed 12, +1.5 damage. Enchantability 0 in the original: items drop the component. */
    public static final ToolMaterial ENDIUM = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 131, 12.0F, 1.5F, 1, ENDIUM_TOOL_MATERIALS);
    public static final ToolMaterial REINFORCED_ENDIUM = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 12.0F, 1.5F, 1, ENDIUM_TOOL_MATERIALS);

    public static final ResourceKey<EquipmentAsset> ENDIUM_ASSET = asset("endium");
    public static final ResourceKey<EquipmentAsset> BLAZE_ASSET = asset("blaze");

    public static final ArmorMaterial ENDIUM_ARMOR = new ArmorMaterial(10, defense(2, 4, 2, 2), 12,
            SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, REPAIRS_ENDIUM_ARMOR, ENDIUM_ASSET);
    public static final ArmorMaterial BLAZE_ARMOR = new ArmorMaterial(3, defense(2, 6, 5, 2), 25,
            SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, REPAIRS_BLAZE_ARMOR, BLAZE_ASSET);

    public static final ResourceKey<Enchantment> IMPERVIOUSNESS = ResourceKey.create(Registries.ENCHANTMENT, GanysCollection.id("imperviousness"));

    /** Original order: helmet, chestplate, leggings, boots. */
    private static Map<ArmorType, Integer> defense(int helmet, int chest, int legs, int boots) {
        return Map.of(ArmorType.HELMET, helmet, ArmorType.CHESTPLATE, chest, ArmorType.LEGGINGS, legs, ArmorType.BOOTS, boots);
    }

    private static ResourceKey<EquipmentAsset> asset(String name) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, GanysCollection.id(name));
    }

    private static TagKey<Item> item(String name) {
        return TagKey.create(Registries.ITEM, GanysCollection.id(name));
    }

    private GanysMaterials() {
    }
}
