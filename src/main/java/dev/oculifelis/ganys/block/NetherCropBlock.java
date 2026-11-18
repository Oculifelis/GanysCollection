package dev.oculifelis.ganys.block;

import com.mojang.serialization.MapCodec;
import dev.oculifelis.ganys.GanysBlocks;
import dev.oculifelis.ganys.GanysItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;

/** A crop that grows only on tilled netherrack and needs no light. Bone meal adds one age. */
public class NetherCropBlock extends CropBlock {
    public static final MapCodec<NetherCropBlock> CODEC = simpleCodec(NetherCropBlock::new);

    public NetherCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends NetherCropBlock> codec() {
        return CODEC;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return GanysItems.seedOf(this);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(GanysBlocks.TILLED_NETHERRACK.get());
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return mayPlaceOn(level.getBlockState(pos.below()), level, pos.below());
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        int age = getAge(state);
        if (age < getMaxAge()) {
            float speed = getGrowthSpeed(state, level, pos);
            if (CommonHooks.canCropGrow(level, pos, state, random.nextInt((int) (25.0F / speed) + 1) == 0)) {
                level.setBlock(pos, getStateForAge(age + 1), 2);
                CommonHooks.fireCropGrowPost(level, pos, state);
            }
        }
    }

    @Override
    protected int getBonemealAgeIncrease(Level level) {
        return 1;
    }
}
