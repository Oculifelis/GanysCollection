package dev.oculifelis.ganys.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A nether bush that starts as a small box and fills out to a full block as it ripens. */
public class BerryBushBlock extends NetherCropBlock {
    public static final MapCodec<BerryBushBlock> CODEC = simpleCodec(BerryBushBlock::new);
    private static final VoxelShape[] SHAPES = new VoxelShape[8];

    static {
        for (int a = 0; a < 8; a++) SHAPES[a] = column(2 * (a + 1), 0, 2 * (a + 1));
    }

    /** A box w wide, centred on the block, from y0 to y1. */
    static VoxelShape column(double w, double y0, double y1) {
        return Block.box(8 - w / 2, y0, 8 - w / 2, 8 + w / 2, y1, 8 + w / 2);
    }

    public BerryBushBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<BerryBushBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[getAge(state)];
    }
}
