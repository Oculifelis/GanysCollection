package dev.oculifelis.ganys.block;

import com.mojang.serialization.MapCodec;
import dev.oculifelis.ganys.GanysSurface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;

/**
 * A single chest of soul sand. Items that touch it are pulled inside, anything else on it is slowed, and
 * right-clicking it with an item puts that stack in; it opens when nothing fits or the hand is empty.
 */
public class SoulChestBlock extends ChestBlock {
    public static final MapCodec<SoulChestBlock> CODEC = simpleCodec(SoulChestBlock::new);

    public SoulChestBlock(Properties properties) {
        super(() -> GanysSurface.SOUL_CHEST_ENTITY.get(), SoundEvents.CHEST_OPEN, SoundEvents.CHEST_CLOSE, properties);
    }

    @Override
    public MapCodec<SoulChestBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(TYPE, ChestType.SINGLE)
                .setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction,
                                     BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return super.updateShape(state, level, ticks, pos, direction, neighbourPos, neighbourState, random).setValue(TYPE, ChestType.SINGLE);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SoulChestBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty()) return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof SoulChestBlockEntity chest) {
            ItemStack left = ItemUtil.insertItemReturnRemaining(VanillaContainerWrapper.of(chest), stack.copy(), false, null);
            if (left.getCount() < stack.getCount()) {
                player.setItemInHand(hand, left);
                return InteractionResult.SUCCESS;
            }
        }
        return useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effects, boolean intersects) {
        if (entity instanceof ItemEntity item) {
            if (level.isClientSide() || !item.isAlive() || item.getItem().isEmpty()) return;
            if (level.getBlockEntity(pos) instanceof SoulChestBlockEntity chest) {
                ItemStack left = ItemUtil.insertItemReturnRemaining(VanillaContainerWrapper.of(chest), item.getItem().copy(), false, null);
                if (left.isEmpty()) item.discard();
                else item.setItem(left);
            }
        } else {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.4, 1.0, 0.4));
        }
    }
}
