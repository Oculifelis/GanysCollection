package dev.oculifelis.ganys.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/** A block of coloured redstone: full power to everything around it except vanilla dust and dust of other colours. */
public class ColouredRedstoneBlock extends Block {
    private final DyeColor colour;

    public ColouredRedstoneBlock(DyeColor colour, Properties properties) {
        super(properties);
        this.colour = colour;
    }

    public DyeColor colour() {
        return colour;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    /** The receiver sits opposite the given direction. */
    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return ColouredRedstoneWireBlock.wireApart(level.getBlockState(pos.relative(direction.getOpposite())), colour) ? 0 : 15;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        if (direction == null) return false;
        return !ColouredRedstoneWireBlock.wireApart(level.getBlockState(pos.relative(direction.getOpposite())), colour);
    }
}
