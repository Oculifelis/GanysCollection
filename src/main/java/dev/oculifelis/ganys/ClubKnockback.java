package dev.oculifelis.ganys;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

/**
 * Club knockback, item data map {@code ganyscollection:club_knockback}: on a hit the target's motion is multiplied by
 * {@code keep}, then pushed away by {@code horizontal} and up by {@code vertical}, both scaled by the club's remaining durability.
 */
public record ClubKnockback(float keep, float horizontal, float vertical) {
    public static final ClubKnockback DEFAULT = new ClubKnockback(0.5F, 4.0F, 0.8F);
    public static final Codec<ClubKnockback> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.optionalFieldOf("keep", DEFAULT.keep).forGetter(ClubKnockback::keep),
            Codec.FLOAT.optionalFieldOf("horizontal", DEFAULT.horizontal).forGetter(ClubKnockback::horizontal),
            Codec.FLOAT.optionalFieldOf("vertical", DEFAULT.vertical).forGetter(ClubKnockback::vertical))
            .apply(i, ClubKnockback::new));
    public static final DataMapType<Item, ClubKnockback> DATA_MAP = DataMapType.builder(
            GanysCollection.id("club_knockback"), Registries.ITEM, CODEC).synced(CODEC, true).build();

    public static ClubKnockback of(Item item) {
        ClubKnockback k = item.builtInRegistryHolder().getData(DATA_MAP);
        return k == null ? DEFAULT : k;
    }
}
