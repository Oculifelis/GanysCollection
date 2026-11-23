package dev.oculifelis.ganys;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class GanysTags {
    public static final TagKey<Block> NETHER_CROPS = block("nether_crops");
    public static final TagKey<Block> GLOWING_REED_SOIL = block("glowing_reed_soil");
    public static final TagKey<Block> ENDER_FLOWER_SOIL = block("ender_flower_soil");
    /** Endium tools and bows that can be tagged to an inventory. */
    public static final TagKey<Item> INVENTORY_TAGGERS = TagKey.create(Registries.ITEM, GanysCollection.id("inventory_taggers"));

    private static TagKey<Block> block(String name) {
        return TagKey.create(Registries.BLOCK, GanysCollection.id(name));
    }

    private GanysTags() {
    }
}
