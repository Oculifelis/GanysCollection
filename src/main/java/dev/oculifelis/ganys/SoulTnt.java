package dev.oculifelis.ganys;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

/**
 * Soul TNT numbers, block data map {@code ganyscollection:soul_tnt}: fuse ticks, blast power, whether it sets fires,
 * and the slowness it puts on every living thing within {@code slow_range} times the power.
 */
public record SoulTnt(int fuse, float power, boolean fire, float slowRange, int slowDuration, int slowAmplifier) {
    public static final SoulTnt DEFAULT = new SoulTnt(80, 4.0F, true, 2.0F, 3000, 2);
    public static final Codec<SoulTnt> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("fuse", DEFAULT.fuse).forGetter(SoulTnt::fuse),
            Codec.FLOAT.optionalFieldOf("power", DEFAULT.power).forGetter(SoulTnt::power),
            Codec.BOOL.optionalFieldOf("fire", DEFAULT.fire).forGetter(SoulTnt::fire),
            Codec.FLOAT.optionalFieldOf("slow_range", DEFAULT.slowRange).forGetter(SoulTnt::slowRange),
            Codec.INT.optionalFieldOf("slow_duration", DEFAULT.slowDuration).forGetter(SoulTnt::slowDuration),
            Codec.INT.optionalFieldOf("slow_amplifier", DEFAULT.slowAmplifier).forGetter(SoulTnt::slowAmplifier))
            .apply(i, SoulTnt::new));
    public static final DataMapType<Block, SoulTnt> DATA_MAP = DataMapType.builder(
            GanysCollection.id("soul_tnt"), Registries.BLOCK, CODEC).synced(CODEC, true).build();

    public static SoulTnt of(Block block) {
        SoulTnt t = block.builtInRegistryHolder().getData(DATA_MAP);
        return t == null ? DEFAULT : t;
    }
}
