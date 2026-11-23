package dev.oculifelis.ganys.block;

import com.mojang.serialization.MapCodec;
import dev.oculifelis.ganys.GanysTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A nether cactus: grows up to five tall on its soil tag (netherrack in the original), needs every side clear of solid
 * blocks like a cactus, and sets anything that touches it on fire for two seconds instead of pricking it.
 */
public class BlazingCactoidBlock extends Block {
    public static final MapCodec<BlazingCactoidBlock> CODEC = simpleCodec(BlazingCactoidBlock::new);
    private static final VoxelShape COLLISION = Block.column(4.0, 0.0, 16.0);
    private static final VoxelShape SHAPE = Block.column(6.0, 0.0, 16.0);
    private static final int MAX_HEIGHT = 5;

    public BlazingCactoidBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<BlazingCactoidBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) level.destroyBlock(pos, true);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos up = pos.above();
        if (!level.isEmptyBlock(up)) return;
        int height = 1;
        while (level.getBlockState(pos.below(height)).is(this)) height++;
        if (height < MAX_HEIGHT && random.nextInt(10) == 0 && defaultBlockState().canSurvive(level, up)) {
            level.setBlockAndUpdate(up, defaultBlockState());
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (!state.canSurvive(level, pos)) ticks.scheduleTick(pos, this, 1);
        return super.updateShape(state, level, ticks, pos, direction, neighbourPos, neighbourState, random);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        for (Direction d : Direction.Plane.HORIZONTAL) {
            BlockState side = level.getBlockState(pos.relative(d));
            if (side.isSolid() || !level.getFluidState(pos.relative(d)).isEmpty()) return false;
        }
        BlockState soil = level.getBlockState(pos.below());
        return soil.is(this) || soil.is(GanysTags.BLAZING_CACTOID_SOIL);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effects, boolean intersects) {
        if (!level.isClientSide() && !entity.fireImmune()) entity.igniteForSeconds(2.0F);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
