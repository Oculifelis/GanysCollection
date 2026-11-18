package dev.oculifelis.ganys.block;

import com.mojang.serialization.MapCodec;
import dev.oculifelis.ganys.GanysBlocks;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Grows slowly on tilled netherrack; once ripe it hangs wither skeleton skulls on its sides. Using it picks them. */
public class WitherShrubBlock extends NetherCropBlock {
    public static final MapCodec<WitherShrubBlock> CODEC = simpleCodec(WitherShrubBlock::new);
    private static final VoxelShape[] SHAPES = new VoxelShape[8];

    static {
        for (int a = 0; a < 7; a++) SHAPES[a] = Block.column(4, 0, a * 2 + 2);
        SHAPES[7] = Block.column(16, 0, 10);
    }

    public WitherShrubBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<WitherShrubBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[getAge(state)];
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.getBlockState(pos.below()).is(GanysBlocks.TILLED_NETHERRACK.get()) || random.nextInt(40) != 0) return;
        int age = getAge(state);
        if (age < getMaxAge()) {
            level.setBlock(pos, getStateForAge(age + 1), 2);
            return;
        }
        for (Direction d : shuffled(random)) {
            BlockPos at = pos.relative(d);
            if (level.isEmptyBlock(at)) {
                level.setBlockAndUpdate(at, Blocks.WITHER_SKELETON_WALL_SKULL.defaultBlockState().setValue(WallSkullBlock.FACING, d));
                return;
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        boolean picked = false;
        for (Direction d : shuffled(level.getRandom())) {
            BlockPos at = pos.relative(d);
            BlockState there = level.getBlockState(at);
            if (there.is(Blocks.WITHER_SKELETON_WALL_SKULL) || there.is(Blocks.WITHER_SKELETON_SKULL)) {
                if (!level.isClientSide()) level.destroyBlock(at, true, player);
                picked = true;
            }
        }
        return picked ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    private static List<Direction> shuffled(RandomSource random) {
        List<Direction> list = new ArrayList<>(Direction.Plane.HORIZONTAL.stream().toList());
        for (int i = list.size() - 1; i > 0; i--) Collections.swap(list, i, random.nextInt(i + 1));
        return list;
    }
}
