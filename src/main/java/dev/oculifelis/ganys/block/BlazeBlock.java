package dev.oculifelis.ganys.block;

import dev.oculifelis.ganys.GanysSurface;
import dev.oculifelis.ganys.GanysTags;
import dev.oculifelis.ganys.entity.BlazeGolem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

/** Block of blaze. Two stacked with a pumpkin or jack o'lantern on top wake a blaze golem. */
public class BlazeBlock extends Block {
    public BlazeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moved) {
        super.onPlace(state, level, pos, old, moved);
        tryBuild(level, pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean moved) {
        super.neighborChanged(state, level, pos, block, orientation, moved);
        tryBuild(level, pos);
    }

    /** Checks the pattern with {@code pos} as the upper blaze block. */
    private void tryBuild(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel server)) return;
        BlockPos below = pos.below(), above = pos.above();
        if (!level.getBlockState(below).is(this) || !level.getBlockState(above).is(GanysTags.BLAZE_GOLEM_HEADS)) return;
        BlazeGolem golem = GanysSurface.BLAZE_GOLEM.get().create(server, EntitySpawnReason.TRIGGERED);
        if (golem == null) return;
        for (BlockPos p : new BlockPos[]{above, pos, below}) {
            level.levelEvent(2001, p, Block.getId(level.getBlockState(p)));
            level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        golem.snapTo(below.getX() + 0.5, below.getY(), below.getZ() + 0.5, 0.0F, 0.0F);
        golem.applyStats();
        server.addFreshEntity(golem);
        for (ServerPlayer player : server.getEntitiesOfClass(ServerPlayer.class, golem.getBoundingBox().inflate(5.0)))
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, golem);
        for (BlockPos p : new BlockPos[]{above, pos, below}) level.updateNeighborsAt(p, Blocks.AIR);
    }
}
