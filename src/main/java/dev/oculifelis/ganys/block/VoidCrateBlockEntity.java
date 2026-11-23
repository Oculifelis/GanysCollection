package dev.oculifelis.ganys.block;

import dev.oculifelis.ganys.GanysSurface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Six rows of storage plus one hidden last slot that destroys whatever is put in it. Hoppers and pipes fill the storage
 * first and then lose the overflow into that slot, so the crate never backs up.
 */
public class VoidCrateBlockEntity extends BaseContainerBlockEntity {
    public static final int STORAGE = 54;
    private NonNullList<ItemStack> items = NonNullList.withSize(STORAGE, ItemStack.EMPTY);

    public VoidCrateBlockEntity(BlockPos pos, BlockState state) {
        super(GanysSurface.VOID_CRATE_ENTITY.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.ganyscollection.void_crate");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return ChestMenu.sixRows(id, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return STORAGE + 1;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot < STORAGE ? super.getItem(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        return slot < STORAGE ? super.removeItem(slot, count) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return slot < STORAGE ? super.removeItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < STORAGE) super.setItem(slot, stack);
    }

    @Override
    public void setItem(int slot, ItemStack stack, boolean insideTransaction) {
        if (slot < STORAGE) super.setItem(slot, stack, insideTransaction);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(STORAGE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items);
    }
}
