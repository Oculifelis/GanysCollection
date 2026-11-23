package dev.oculifelis.ganys;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class GanysTags {
    public static final TagKey<Block> NETHER_CROPS = block("nether_crops");
    public static final TagKey<Block> GLOWING_REED_SOIL = block("glowing_reed_soil");
    public static final TagKey<Block> ENDER_FLOWER_SOIL = block("ender_flower_soil");
    public static final TagKey<Block> BLAZING_CACTOID_SOIL = block("blazing_cactoid_soil");
    public static final TagKey<Block> WEEPING_POD_SUPPORTS = block("weeping_pod_supports");
    /** Items that plant a weeping pod on the side of a supporting block. */
    public static final TagKey<Item> PLANTS_WEEPING_POD = TagKey.create(Registries.ITEM, GanysCollection.id("plants_weeping_pod"));
    /** Endium tools and bows that can be tagged to an inventory. */
    public static final TagKey<Item> INVENTORY_TAGGERS = TagKey.create(Registries.ITEM, GanysCollection.id("inventory_taggers"));

    private static TagKey<Block> block(String name) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(GanysCollection.MOD_ID, name));
    }

    private GanysTags() {
    }
}
