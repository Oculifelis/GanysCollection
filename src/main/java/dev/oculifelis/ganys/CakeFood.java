package dev.oculifelis.ganys;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jspecify.annotations.Nullable;

/** What one slice of a cake block feeds, block data map {@code ganyscollection:cake_food}. */
public record CakeFood(int nutrition, float saturation) {
    public static final Codec<CakeFood> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("nutrition").forGetter(CakeFood::nutrition),
            Codec.FLOAT.fieldOf("saturation").forGetter(CakeFood::saturation)).apply(i, CakeFood::new));
    public static final DataMapType<Block, CakeFood> DATA_MAP = DataMapType.builder(
            GanysCollection.id("cake_food"), Registries.BLOCK, CODEC).synced(CODEC, true).build();

    public static @Nullable CakeFood of(Block block) {
        return block.builtInRegistryHolder().getData(DATA_MAP);
    }
}
