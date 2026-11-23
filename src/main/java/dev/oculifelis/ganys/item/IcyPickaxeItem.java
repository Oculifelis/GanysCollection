package dev.oculifelis.ganys.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * The icy pickaxe. Right-click freezes the water and lava around the clicked block (water sources to ice, lava sources to
 * obsidian, flowing lava to cobblestone), one durability per block. It slowly mends in cold biomes and wears in hot ones.
 */
public class IcyPickaxeItem extends Item {
    public IcyPickaxeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockPos centre = ctx.getClickedPos();
        int frozen = 0;
        for (BlockPos p : BlockPos.betweenClosed(centre.offset(-1, -1, -1), centre.offset(1, 1, 1))) {
            if (!(level.getBlockState(p).getBlock() instanceof LiquidBlock)) {
                continue;
            }
            FluidState fluid = level.getFluidState(p);
            BlockState into = null;
            if (fluid.is(Fluids.WATER) && fluid.isSource()) {
                into = Blocks.ICE.defaultBlockState();
            } else if (fluid.is(Fluids.LAVA) || fluid.is(Fluids.FLOWING_LAVA)) {
                into = fluid.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.COBBLESTONE.defaultBlockState();
            }
            if (into != null) {
                BlockPos at = p.immutable();
                level.setBlockAndUpdate(at, into);
                level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, at, Block.getId(into));
                frozen++;
            }
        }
        if (frozen == 0) {
            return InteractionResult.PASS;
        }
        if (ctx.getPlayer() != null) {
            ctx.getItemInHand().hurtAndBreak(frozen, ctx.getPlayer(), ctx.getHand());
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity owner, EquipmentSlot slot) {
        if (!stack.isDamaged() || level.getRandom().nextInt(80) != 0) {
            return;
        }
        float t = level.getBiome(owner.blockPosition()).value().getBaseTemperature();
        int change = t < 0.2F ? -1 : t > 1.1F ? 1 : 0;
        if (change != 0) {
            stack.setDamageValue(Math.min(stack.getMaxDamage() - 1, Math.max(0, stack.getDamageValue() + change)));
        }
    }
}
