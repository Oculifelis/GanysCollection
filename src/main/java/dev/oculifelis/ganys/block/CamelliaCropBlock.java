package dev.oculifelis.ganys.block;

import com.mojang.serialization.MapCodec;
import dev.oculifelis.ganys.GanysSurface;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;

/** The tea plant. A plain farmland crop with eight ages, drawn in four stages, that drops tea leaves when grown. */
public class CamelliaCropBlock extends CropBlock {
    public static final MapCodec<CamelliaCropBlock> CODEC = simpleCodec(CamelliaCropBlock::new);

    public CamelliaCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends CamelliaCropBlock> codec() {
        return CODEC;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return GanysSurface.CAMELLIA_SEEDS.get();
    }
}
