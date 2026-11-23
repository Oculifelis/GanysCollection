package dev.oculifelis.ganys.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** A powered rail that holds carts to a crawl (0.05 blocks a tick), for stations and lookouts. */
public class SlowRailBlock extends PoweredRailBlock {
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final MapCodec<PoweredRailBlock> CODEC = (MapCodec) simpleCodec(SlowRailBlock::new);
    public static final double MAX_SPEED = 0.05;

    public SlowRailBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<PoweredRailBlock> codec() {
        return CODEC;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effects, boolean moved) {
        if (entity instanceof AbstractMinecart cart) {
            Vec3 v = cart.getDeltaMovement();
            double h = Math.sqrt(v.x * v.x + v.z * v.z);
            if (h > MAX_SPEED) {
                cart.setDeltaMovement(v.x / h * MAX_SPEED, v.y, v.z / h * MAX_SPEED);
            }
        }
    }
}
