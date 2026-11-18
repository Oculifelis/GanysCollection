package dev.oculifelis.ganys;

import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GanysComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, GanysCollection.MOD_ID);

    /** The inventory an endium tool sends its drops to (and an endium bow takes arrows from). */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> TAGGED_INVENTORY = COMPONENTS.registerComponentType(
            "tagged_inventory", b -> b.persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC));
    /** Head drops an ender scythe has left, charged by lightning. */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SCYTHE_CHARGES = COMPONENTS.registerComponentType(
            "scythe_charges", b -> b.persistent(Codec.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT));

    private GanysComponents() {
    }
}
