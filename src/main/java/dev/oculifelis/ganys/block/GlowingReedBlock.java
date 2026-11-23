package dev.oculifelis.ganys.block;

import com.mojang.serialization.MapCodec;
import dev.oculifelis.ganys.GanysTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Nether sugar cane: grows three tall on netherrack or soul sand beside lava, and glows. */
public class GlowingReedBlock extends Block {
    public static final MapCodec<GlowingReedBlock> CODEC = simpleCodec(GlowingReedBlock::new);
    private static final VoxelShape SHAPE = BerryBushBlock.column(12.0, 0.0, 16.0);

    public GlowingReedBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<GlowingReedBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) level.destroyBlock(pos, true);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isEmptyBlock(pos.above())) return;
        int height = 1;
        while (level.getBlockState(pos.below(height)).is(this)) height++;
        if (height < 3 && random.nextInt(25) == 0) level.setBlockAndUpdate(pos.above(), defaultBlockState());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor level,
                                     BlockPos pos, BlockPos neighbourPos) {
        if (!state.canSurvive(level, pos)) level.scheduleTick(pos, this, 1);
        return super.updateShape(state, direction, neighbourState, level, pos, neighbourPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState soil = level.getBlockState(below);
        if (soil.is(this)) return true;
        if (!soil.is(GanysTags.GLOWING_REED_SOIL)) return false;
        for (Direction d : Direction.Plane.HORIZONTAL) {
            if (level.getFluidState(below.relative(d)).is(FluidTags.LAVA)) return true;
        }
        return false;
    }
}
