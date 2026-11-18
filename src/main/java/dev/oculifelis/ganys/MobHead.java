package dev.oculifelis.ganys;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jspecify.annotations.Nullable;

/**
 * The head an entity type drops, entity type data map {@code ganyscollection:mob_heads}. {@code random} false keeps a
 * head out of the rare random drop (the wither skeleton skull in the original); a charged ender scythe still takes it.
 */
public record MobHead(Item item, boolean random) {
    public static final Codec<MobHead> CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(MobHead::item),
            Codec.BOOL.optionalFieldOf("random", true).forGetter(MobHead::random)).apply(i, MobHead::new));
    public static final DataMapType<EntityType<?>, MobHead> DATA_MAP = DataMapType.builder(
            GanysCollection.id("mob_heads"), Registries.ENTITY_TYPE, CODEC).synced(CODEC, true).build();

    public static @Nullable MobHead of(EntityType<?> type) {
        return type.builtInRegistryHolder().getData(DATA_MAP);
    }
}
