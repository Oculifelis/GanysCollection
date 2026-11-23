package dev.oculifelis.ganys.block;

import dev.oculifelis.ganys.GanysSurface;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Twenty-seven slots with the vanilla chest lid and sounds; only the texture differs. */
public class SoulChestBlockEntity extends ChestBlockEntity {
    public SoulChestBlockEntity(BlockPos pos, BlockState state) {
        super(GanysSurface.SOUL_CHEST_ENTITY.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.ganyscollection.soul_chest");
    }
}
