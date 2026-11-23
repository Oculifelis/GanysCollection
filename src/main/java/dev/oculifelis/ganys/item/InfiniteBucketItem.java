package dev.oculifelis.ganys.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * A water bucket that never runs dry. It places water like the vanilla one, tops up a cauldron, fills any block with a
 * fluid handler by one bucket, and works from a dispenser, always staying where it is.
 */
public class InfiniteBucketItem extends BucketItem {
    public InfiniteBucketItem(Properties properties) {
        super(Fluids.WATER, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        return fillBlock(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    /** Fills a cauldron or a fluid tank at pos. */
    public static boolean fillBlock(Level level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.CAULDRON) || state.is(Blocks.WATER_CAULDRON)) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3));
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return true;
        }
        var handler = level.getCapability(Capabilities.Fluid.BLOCK, pos, side);
        if (handler == null) return false;
        if (!level.isClientSide()) {
            try (Transaction tx = Transaction.openRoot()) {
                if (handler.insert(FluidResource.of(Fluids.WATER), 1000, tx) > 0) {
                    tx.commit();
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (hit.getType() != HitResult.Type.BLOCK) return InteractionResult.PASS;
        BlockPos pos = hit.getBlockPos();
        Direction dir = hit.getDirection();
        BlockPos beside = pos.relative(dir);
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(beside, dir, stack)) return InteractionResult.FAIL;
        BlockPos place = canBlockContainFluid(player, level, pos, level.getBlockState(pos)) ? pos : beside;
        if (!emptyContents(player, level, place, hit, stack)) return InteractionResult.FAIL;
        if (player instanceof ServerPlayer sp) CriteriaTriggers.PLACED_BLOCK.trigger(sp, place, stack);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResult.SUCCESS;
    }

    public static final DefaultDispenseItemBehavior DISPENSE = new DefaultDispenseItemBehavior() {
        @Override
        public ItemStack execute(BlockSource source, ItemStack dispensed) {
            Direction facing = source.state().getValue(DispenserBlock.FACING);
            BlockPos target = source.pos().relative(facing);
            if (!fillBlock(source.level(), target, facing.getOpposite()) && dispensed.getItem() instanceof InfiniteBucketItem bucket) {
                bucket.emptyContents(null, source.level(), target, null, dispensed);
            }
            return dispensed;
        }
    };
}
