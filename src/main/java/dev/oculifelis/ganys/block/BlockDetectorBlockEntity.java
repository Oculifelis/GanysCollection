package dev.oculifelis.ganys.block;

import dev.oculifelis.ganys.GanysSurface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Holds the block detector's filter (a copy of one item, nothing is used up) and checks the six neighbours once a second.
 * A crop or cocoa filter only counts once the plant is fully grown, as in the original; a water or lava bucket matches that fluid.
 */
public class BlockDetectorBlockEntity extends BlockEntity {
    private static final int PERIOD = 20;
    private ItemStack filter = ItemStack.EMPTY;
    private int cooldown = PERIOD;

    public BlockDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(GanysSurface.BLOCK_DETECTOR_ENTITY.get(), pos, state);
    }

    public ItemStack filter() {
        return filter;
    }

    public void setFilter(ItemStack stack) {
        filter = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        cooldown = 0;
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlockDetectorBlockEntity be) {
        if (--be.cooldown > 0) {
            return;
        }
        be.cooldown = PERIOD;
        boolean found = false;
        for (Direction d : Direction.values()) {
            if (be.matches(level, pos.relative(d))) {
                found = true;
                break;
            }
        }
        if (state.getValue(BlockDetectorBlock.POWERED) != found) {
            level.setBlock(pos, state.setValue(BlockDetectorBlock.POWERED, found), Block.UPDATE_ALL);
        }
    }

    private boolean matches(Level level, BlockPos at) {
        if (filter.isEmpty()) {
            return false;
        }
        BlockState state = level.getBlockState(at);
        if (filter.is(Items.WATER_BUCKET) || filter.is(Items.LAVA_BUCKET)) {
            FluidState fluid = level.getFluidState(at);
            return filter.is(Items.WATER_BUCKET) ? fluid.is(Fluids.WATER) || fluid.is(Fluids.FLOWING_WATER)
                    : fluid.is(Fluids.LAVA) || fluid.is(Fluids.FLOWING_LAVA);
        }
        if (state.isAir() || state.getBlock().asItem() != filter.getItem()) {
            return false;
        }
        Block block = state.getBlock();
        if (block instanceof CropBlock crop) {
            return crop.isMaxAge(state);
        }
        if (block instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
        }
        if (block instanceof CocoaBlock) {
            return state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
        }
        return true;
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.store("filter", ItemStack.OPTIONAL_CODEC, filter);
    }

    @Override
    protected void loadAdditional(ValueInput in) {
        super.loadAdditional(in);
        filter = in.read("filter", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }
}
