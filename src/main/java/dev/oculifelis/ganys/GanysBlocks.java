package dev.oculifelis.ganys;

import dev.oculifelis.ganys.block.BerryBushBlock;
import dev.oculifelis.ganys.block.EnderFlowerBlock;
import dev.oculifelis.ganys.block.GlowingReedBlock;
import dev.oculifelis.ganys.block.NetherCropBlock;
import dev.oculifelis.ganys.block.TilledNetherrackBlock;
import dev.oculifelis.ganys.block.WitherShrubBlock;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class GanysBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(GanysCollection.MOD_ID);

    public static final DeferredBlock<TilledNetherrackBlock> TILLED_NETHERRACK = BLOCKS.registerBlock("tilled_netherrack",
            TilledNetherrackBlock::new, () -> Properties.ofFullCopy(Blocks.NETHERRACK).strength(0.5F).sound(SoundType.GRAVEL).randomTicks());

    public static final DeferredBlock<NetherCropBlock> SPECTRE_WHEAT = BLOCKS.registerBlock("spectre_wheat",
            NetherCropBlock::new, () -> Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.COLOR_LIGHT_BLUE));
    public static final DeferredBlock<BerryBushBlock> QUARTZ_BERRY_BUSH = BLOCKS.registerBlock("quartz_berry_bush",
            BerryBushBlock::new, () -> Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.QUARTZ));
    public static final DeferredBlock<BerryBushBlock> HELL_BUSH = BLOCKS.registerBlock("hell_bush",
            BerryBushBlock::new, () -> Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.NETHER));
    public static final DeferredBlock<WitherShrubBlock> WITHER_SHRUB = BLOCKS.registerBlock("wither_shrub",
            WitherShrubBlock::new, () -> Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.COLOR_BLACK));
    public static final DeferredBlock<GlowingReedBlock> GLOWING_REED = BLOCKS.registerBlock("glowing_reed",
            GlowingReedBlock::new, () -> Properties.ofFullCopy(Blocks.SUGAR_CANE).mapColor(MapColor.COLOR_YELLOW).lightLevel(s -> 7));

    public static final DeferredBlock<TransparentBlock> SOUL_GLASS = BLOCKS.registerBlock("soul_glass",
            TransparentBlock::new, () -> Properties.ofFullCopy(Blocks.GLASS).mapColor(MapColor.COLOR_BROWN).strength(0.3F).lightLevel(s -> 4));
    public static final DeferredBlock<IronBarsBlock> SOUL_GLASS_PANE = BLOCKS.registerBlock("soul_glass_pane",
            IronBarsBlock::new, () -> Properties.ofFullCopy(Blocks.GLASS_PANE).mapColor(MapColor.COLOR_BROWN).strength(0.3F).lightLevel(s -> 4));
    public static final DeferredBlock<StairBlock> SOUL_GLASS_STAIRS = BLOCKS.registerBlock("soul_glass_stairs",
            p -> new StairBlock(SOUL_GLASS.get().defaultBlockState(), p),
            () -> Properties.ofFullCopy(Blocks.GLASS).mapColor(MapColor.COLOR_BROWN).strength(0.3F).lightLevel(s -> 4));

    /** Every coloured quartz block, chiseled block, pillar and stair, dye order. */
    public static final List<DeferredBlock<? extends Block>> QUARTZ = new ArrayList<>();

    static {
        for (DyeColor dye : DyeColor.values()) {
            String d = dye.getSerializedName();
            DeferredBlock<Block> block = BLOCKS.registerBlock(d + "_quartz_block", Block::new,
                    () -> Properties.ofFullCopy(Blocks.QUARTZ_BLOCK).mapColor(dye));
            QUARTZ.add(block);
            QUARTZ.add(BLOCKS.registerBlock(d + "_chiseled_quartz_block", Block::new,
                    () -> Properties.ofFullCopy(Blocks.CHISELED_QUARTZ_BLOCK).mapColor(dye)));
            QUARTZ.add(BLOCKS.registerBlock(d + "_quartz_pillar", RotatedPillarBlock::new,
                    () -> Properties.ofFullCopy(Blocks.QUARTZ_PILLAR).mapColor(dye)));
            QUARTZ.add(BLOCKS.registerBlock(d + "_quartz_stairs", p -> new StairBlock(block.get().defaultBlockState(), p),
                    () -> Properties.ofFullCopy(Blocks.QUARTZ_STAIRS).mapColor(dye)));
        }
    }

    // Gany's End and Gany's Nether metals. Explosion resistance is the 1.7.10 value times 3/5.
    public static final DeferredBlock<Block> RAW_ENDIUM = BLOCKS.registerBlock("raw_endium", Block::new,
            () -> Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(51.0F, 1200.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());
    public static final DeferredBlock<Block> ENDIUM_BLOCK = BLOCKS.registerBlock("endium_block", Block::new,
            () -> Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.2F, 0.3F).sound(SoundType.METAL));
    public static final DeferredBlock<Block> ENDER_PEARL_BLOCK = BLOCKS.registerBlock("ender_pearl_block", Block::new,
            () -> pearl());
    public static final DeferredBlock<StairBlock> ENDER_PEARL_STAIRS = BLOCKS.registerBlock("ender_pearl_stairs",
            p -> new StairBlock(ENDER_PEARL_BLOCK.get().defaultBlockState(), p), () -> pearl());
    public static final DeferredBlock<WallBlock> ENDER_PEARL_WALL = BLOCKS.registerBlock("ender_pearl_wall", WallBlock::new,
            () -> pearl().forceSolidOn());
    public static final DeferredBlock<EnderFlowerBlock> ENDER_FLOWER = BLOCKS.registerBlock("ender_flower", EnderFlowerBlock::new,
            () -> Properties.ofFullCopy(Blocks.DANDELION).mapColor(MapColor.COLOR_PURPLE).lightLevel(s -> 4));
    public static final DeferredBlock<dev.oculifelis.ganys.block.BlazeBlock> BLAZE_BLOCK = BLOCKS.registerBlock("blaze_block", dev.oculifelis.ganys.block.BlazeBlock::new,
            () -> Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops());

    private static Properties pearl() {
        return Properties.of().mapColor(MapColor.COLOR_CYAN).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5F, 6.0F).requiresCorrectToolForDrops();
    }

    private GanysBlocks() {
    }
}
