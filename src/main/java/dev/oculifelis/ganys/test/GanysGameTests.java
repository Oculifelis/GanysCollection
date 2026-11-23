package dev.oculifelis.ganys.test;

import dev.oculifelis.ganys.GanysBlocks;
import dev.oculifelis.ganys.GanysCollection;
import dev.oculifelis.ganys.GanysItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(GanysCollection.MOD_ID)
@PrefixGameTestTemplate(false)
public class GanysGameTests {

    @GameTest(template = "empty")
    public static void registriesLoad(GameTestHelper helper) {
        int blocks = 0;
        for (var e : GanysBlocks.BLOCKS.getEntries()) {
            Block b = e.get();
            if (b.defaultBlockState() == null) helper.fail("no default state: " + e.getId());
            blocks++;
        }
        int items = 0;
        for (var e : GanysItems.ITEMS.getEntries()) {
            Item i = e.get();
            if (i == null) helper.fail("missing item: " + e.getId());
            items++;
        }
        if (blocks == 0 || items == 0) helper.fail("nothing registered");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void soulGlassPlaces(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, GanysBlocks.SOUL_GLASS.get());
        helper.assertBlockPresent(GanysBlocks.SOUL_GLASS.get(), pos);
        helper.succeed();
    }
}
