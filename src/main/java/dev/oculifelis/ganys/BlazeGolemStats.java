package dev.oculifelis.ganys;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

/**
 * Blaze golem numbers, entity type data map {@code ganyscollection:blaze_golem}: health, walking speed, ticks between
 * shots, shooting range in blocks, and the ticks from the start of the throw to the fireball leaving the hand.
 */
public record BlazeGolemStats(float health, float speed, int attackInterval, float attackRange, int shotDelay) {
    public static final BlazeGolemStats DEFAULT = new BlazeGolemStats(4.0F, 0.2F, 30, 10.0F, 10);
    public static final Codec<BlazeGolemStats> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.optionalFieldOf("health", DEFAULT.health).forGetter(BlazeGolemStats::health),
            Codec.FLOAT.optionalFieldOf("speed", DEFAULT.speed).forGetter(BlazeGolemStats::speed),
            Codec.INT.optionalFieldOf("attack_interval", DEFAULT.attackInterval).forGetter(BlazeGolemStats::attackInterval),
            Codec.FLOAT.optionalFieldOf("attack_range", DEFAULT.attackRange).forGetter(BlazeGolemStats::attackRange),
            Codec.INT.optionalFieldOf("shot_delay", DEFAULT.shotDelay).forGetter(BlazeGolemStats::shotDelay))
            .apply(i, BlazeGolemStats::new));
    public static final DataMapType<EntityType<?>, BlazeGolemStats> DATA_MAP = DataMapType.builder(
            GanysCollection.id("blaze_golem"), Registries.ENTITY_TYPE, CODEC).synced(CODEC, true).build();

    @SuppressWarnings("deprecation")
    public static BlazeGolemStats of(EntityType<?> type) {
        BlazeGolemStats s = type.builtInRegistryHolder().getData(DATA_MAP);
        return s == null ? DEFAULT : s;
    }
}
