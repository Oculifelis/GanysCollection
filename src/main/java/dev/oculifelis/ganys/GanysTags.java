package dev.oculifelis.ganys;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class GanysTags {
    public static final TagKey<Block> NETHER_CROPS = block("nether_crops");
    public static final TagKey<Block> GLOWING_REED_SOIL = block("glowing_reed_soil");

    private static TagKey<Block> block(String name) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(GanysCollection.MOD_ID, name));
    }

    private GanysTags() {
    }
}
