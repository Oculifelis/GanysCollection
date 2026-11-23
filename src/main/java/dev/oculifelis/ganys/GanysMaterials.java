package dev.oculifelis.ganys;

import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Gany's End endium and Gany's Nether blaze materials, with the original numbers. */
public final class GanysMaterials {
    public static final TagKey<Item> ENDIUM_TOOL_MATERIALS = item("endium_tool_materials");
    public static final TagKey<Item> REPAIRS_ENDIUM_ARMOR = item("repairs_endium_armor");
    public static final TagKey<Item> REPAIRS_BLAZE_ARMOR = item("repairs_blaze_armor");

    /** Diamond level, 131 uses, speed 12, +1.5 damage. Enchantability 0, as in the original. */
    public static final Tier ENDIUM = new EndiumTier(131);
    public static final Tier REINFORCED_ENDIUM = new EndiumTier(1561);

    /** Armour durability multipliers: durability is ArmorItem.Type's base times this. */
    public static final int ENDIUM_DURABILITY = 10;
    public static final int BLAZE_DURABILITY = 3;

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, GanysCollection.MOD_ID);

    public static final Holder<ArmorMaterial> ENDIUM_ARMOR = ARMOR_MATERIALS.register("endium", () -> new ArmorMaterial(
            defense(2, 4, 2, 2), 12, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(REPAIRS_ENDIUM_ARMOR),
            List.of(new ArmorMaterial.Layer(GanysCollection.id("endium"))), 0.0F, 0.0F));
    public static final Holder<ArmorMaterial> BLAZE_ARMOR = ARMOR_MATERIALS.register("blaze", () -> new ArmorMaterial(
            defense(2, 6, 5, 2), 25, SoundEvents.ARMOR_EQUIP_GOLD, () -> Ingredient.of(REPAIRS_BLAZE_ARMOR),
            List.of(new ArmorMaterial.Layer(GanysCollection.id("blaze"))), 0.0F, 0.0F));

    public static final ResourceKey<Enchantment> IMPERVIOUSNESS = ResourceKey.create(Registries.ENCHANTMENT, GanysCollection.id("imperviousness"));

    /** Original order: helmet, chestplate, leggings, boots. */
    private static Map<ArmorItem.Type, Integer> defense(int helmet, int chest, int legs, int boots) {
        return Map.of(ArmorItem.Type.HELMET, helmet, ArmorItem.Type.CHESTPLATE, chest, ArmorItem.Type.LEGGINGS, legs, ArmorItem.Type.BOOTS, boots);
    }

    private static TagKey<Item> item(String name) {
        return TagKey.create(Registries.ITEM, GanysCollection.id(name));
    }

    private record EndiumTier(int uses) implements Tier {
        @Override
        public int getUses() {
            return uses;
        }

        @Override
        public float getSpeed() {
            return 12.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 1.5F;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(ENDIUM_TOOL_MATERIALS);
        }
    }

    private GanysMaterials() {
    }
}
