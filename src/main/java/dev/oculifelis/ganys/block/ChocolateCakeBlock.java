package dev.oculifelis.ganys.block;

import dev.oculifelis.ganys.CakeFood;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A cake with six slices that feeds more than the vanilla one (block data map {@code ganyscollection:cake_food}).
 * As in the original, a slice is eaten with either mouse button. Candles do not go on it.
 */
public class ChocolateCakeBlock extends CakeBlock {
    private static final int SLICES = 6;

    public ChocolateCakeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return player.canEat(false) ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        return eatSlice(level, pos, state, player);
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide()) eatSlice(level, pos, state, player);
    }

    private InteractionResult eatSlice(Level level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(false)) return InteractionResult.PASS;
        CakeFood food = CakeFood.of(this);
        if (food == null) food = new CakeFood(2, 0.1F);
        player.awardStat(Stats.EAT_CAKE_SLICE);
        player.getFoodData().eat(food.nutrition(), food.saturation());
        int bites = state.getValue(BITES);
        level.gameEvent(player, GameEvent.EAT, pos);
        if (bites + 1 < SLICES) {
            level.setBlock(pos, state.setValue(BITES, bites + 1), Block.UPDATE_ALL);
        } else {
            level.removeBlock(pos, false);
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        }
        return InteractionResult.SUCCESS;
    }
}
