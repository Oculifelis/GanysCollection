package dev.oculifelis.ganys.client;

import dev.oculifelis.ganys.GanysCollection;
import dev.oculifelis.ganys.block.SoulChestBlockEntity;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;

/** The vanilla chest model with the soul chest texture (textures/entity/chest/soul_chest.png). */
public class SoulChestRenderer extends ChestRenderer<SoulChestBlockEntity> {
    public static final SpriteId SPRITE = Sheets.CHEST_MAPPER.apply(GanysCollection.id("soul_chest"));

    public SoulChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SpriteId getCustomSprite(SoulChestBlockEntity chest, ChestRenderState state) {
        return SPRITE;
    }
}
