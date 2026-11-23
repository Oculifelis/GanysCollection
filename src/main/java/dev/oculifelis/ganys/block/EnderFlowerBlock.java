package dev.oculifelis.ganys.block;

import com.mojang.serialization.MapCodec;
import dev.oculifelis.ganys.GanysTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** The ender flower grows on end stone (block tag {@code ganyscollection:ender_flower_soil}). */
public class EnderFlowerBlock extends VegetationBlock {
    public static final MapCodec<EnderFlowerBlock> CODEC = simpleCodec(EnderFlowerBlock::new);
    private static final VoxelShape SHAPE = column(6.0, 0.0, 10.0);

    public EnderFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<EnderFlowerBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(GanysTags.ENDER_FLOWER_SOIL);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
