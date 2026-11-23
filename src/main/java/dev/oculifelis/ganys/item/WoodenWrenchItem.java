package dev.oculifelis.ganys.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/** Right-click turns a log, pillar or other axis block to its next axis (y, then x, then z), as the original did. */
public class WoodenWrenchItem extends Item {
    public WoodenWrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.hasProperty(BlockStateProperties.AXIS)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            Direction.Axis next = switch (state.getValue(BlockStateProperties.AXIS)) {
                case Y -> Direction.Axis.X;
                case X -> Direction.Axis.Z;
                case Z -> Direction.Axis.Y;
            };
            level.setBlock(pos, state.setValue(BlockStateProperties.AXIS, next), 2);
        }
        return InteractionResult.SUCCESS;
    }
}
