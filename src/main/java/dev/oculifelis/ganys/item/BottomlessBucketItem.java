package dev.oculifelis.ganys.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

/** A bucket with no floor: it deletes the water or lava block it is used on and never fills. Dispensers use it too. */
public class BottomlessBucketItem extends Item {
    public BottomlessBucketItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hit.getType() != HitResult.Type.BLOCK) return InteractionResult.PASS;
        BlockPos pos = hit.getBlockPos();
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hit.getDirection(), player.getItemInHand(hand))) {
            return InteractionResult.FAIL;
        }
        return drain(level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    /** Deletes a water or lava block, source or flowing, as the original did. */
    public static boolean drain(Level level, BlockPos pos, @Nullable Player player) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof LiquidBlock) || state.getFluidState().isEmpty()) return false;
        if (!level.isClientSide()) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 0.6F);
            level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        }
        return true;
    }

    public static final DefaultDispenseItemBehavior DISPENSE = new DefaultDispenseItemBehavior() {
        @Override
        public ItemStack execute(BlockSource source, ItemStack dispensed) {
            Direction facing = source.state().getValue(DispenserBlock.FACING);
            drain(source.level(), source.pos().relative(facing), null);
            return dispensed;
        }
    };
}
