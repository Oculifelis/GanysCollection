package dev.oculifelis.ganys.block;

import dev.oculifelis.ganys.GanysSurface;
import dev.oculifelis.ganys.GanysTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.CommonHooks;

/**
 * A cocoa-like pod that grows on the side of obsidian (block tag {@code weeping_pod_supports}) and drops ghast tears.
 * Ghast tears plant it, by hand or from a dispenser; bone meal does nothing, a living soul grows it one stage.
 */
public class WeepingPodBlock extends CocoaBlock {
    /** Dispensing a ghast tear at an empty spot in front of obsidian plants a pod; otherwise it is thrown out as usual. */
    public static final DefaultDispenseItemBehavior DISPENSE = new DefaultDispenseItemBehavior() {
        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            Direction facing = source.state().getValue(DispenserBlock.FACING);
            BlockPos pos = source.pos().relative(facing);
            if (plant(source.level(), pos, facing)) {
                stack.shrink(1);
                return stack;
            }
            return super.execute(source, stack);
        }
    };

    public WeepingPodBlock(Properties properties) {
        super(properties);
    }

    /** Places a fresh pod at {@code pos} hanging on the block in {@code toSupport}, if there is room and support. */
    public static boolean plant(Level level, BlockPos pos, Direction toSupport) {
        if (toSupport.getAxis().isVertical() || !level.getBlockState(pos).canBeReplaced()) return false;
        BlockState pod = GanysSurface.WEEPING_POD.get().defaultBlockState().setValue(FACING, toSupport);
        if (!pod.canSurvive(level, pos)) return false;
        if (!level.isClientSide()) {
            level.setBlock(pos, pod, 3);
            level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
            level.gameEvent(null, GameEvent.BLOCK_PLACE, pos);
        }
        return true;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.relative(state.getValue(FACING))).is(GanysTags.WEEPING_POD_SUPPORTS);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age < MAX_AGE && CommonHooks.canCropGrow(level, pos, state, random.nextInt(10) == 0)) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2);
            CommonHooks.fireCropGrowPost(level, pos, state);
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(GanysSurface.LIVING_SOUL.get()) || state.getValue(AGE) >= MAX_AGE) return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), 2);
            stack.consume(1, player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(Items.GHAST_TEAR);
    }
}
