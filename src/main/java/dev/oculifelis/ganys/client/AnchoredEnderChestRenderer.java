package dev.oculifelis.ganys.client;

import dev.oculifelis.ganys.block.AnchoredEnderChestBlockEntity;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;

/** The vanilla chest model with the ender chest texture. */
public class AnchoredEnderChestRenderer extends ChestRenderer<AnchoredEnderChestBlockEntity> {
    public AnchoredEnderChestRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SpriteId getCustomSprite(AnchoredEnderChestBlockEntity chest, ChestRenderState state) {
        return Sheets.ENDER_CHEST_LOCATION;
    }
}
