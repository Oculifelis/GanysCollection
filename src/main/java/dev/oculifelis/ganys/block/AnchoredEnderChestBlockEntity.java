package dev.oculifelis.ganys.block;

import dev.oculifelis.ganys.GanysSurface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * An ender chest tied to the player who placed it. While that player is online it is their ender inventory, for anyone
 * who opens it and for hoppers and pipes; while they are offline it is empty and shut.
 */
public class AnchoredEnderChestBlockEntity extends BlockEntity implements LidBlockEntity, Container {
    private final ChestLidController lid = new ChestLidController();
    private final ContainerOpenersCounter openers = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            sound(level, pos, true);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            sound(level, pos, false);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int before, int now) {
            level.blockEvent(pos, state.getBlock(), 1, now);
        }

        @Override
        public boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof ChestMenu menu && menu.getContainer() == AnchoredEnderChestBlockEntity.this;
        }
    };
    private @Nullable UUID owner;

    public AnchoredEnderChestBlockEntity(BlockPos pos, BlockState state) {
        super(GanysSurface.ANCHORED_ENDER_CHEST_ENTITY.get(), pos, state);
    }

    private static void sound(Level level, BlockPos pos, boolean open) {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, open ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE,
                SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, AnchoredEnderChestBlockEntity chest) {
        chest.lid.tickLid();
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        setChanged();
    }

    /** The owner's ender inventory, or null when there is no owner or they are not online. Server side only. */
    public @Nullable PlayerEnderChestContainer ender() {
        MinecraftServer server = level != null ? level.getServer() : null;
        if (owner == null || server == null) return null;
        ServerPlayer player = server.getPlayerList().getPlayer(owner);
        return player != null ? player.getEnderChestInventory() : null;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        owner = input.read("owner", UUIDUtil.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable("owner", UUIDUtil.CODEC, owner);
    }

    /** Breaking it never spills the owner's ender inventory. */
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
    }

    @Override
    public boolean triggerEvent(int id, int data) {
        if (id == 1) {
            lid.shouldBeOpen(data > 0);
            return true;
        }
        return super.triggerEvent(id, data);
    }

    @Override
    public float getOpenNess(float partialTick) {
        return lid.getOpenness(partialTick);
    }

    public void recheckOpen() {
        if (!remove) openers.recheckOpeners(getLevel(), getBlockPos(), getBlockState());
    }

    @Override
    public void startOpen(ContainerUser user) {
        if (!remove && !user.getLivingEntity().isSpectator())
            openers.incrementOpeners(user.getLivingEntity(), getLevel(), getBlockPos(), getBlockState(), user.getContainerInteractionRange());
    }

    @Override
    public void stopOpen(ContainerUser user) {
        if (!remove && !user.getLivingEntity().isSpectator())
            openers.decrementOpeners(user.getLivingEntity(), getLevel(), getBlockPos(), getBlockState());
    }

    @Override
    public int getContainerSize() {
        Container c = ender();
        return c != null ? c.getContainerSize() : 0;
    }

    @Override
    public boolean isEmpty() {
        Container c = ender();
        return c == null || c.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        Container c = ender();
        return c != null ? c.getItem(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        Container c = ender();
        return c != null ? c.removeItem(slot, count) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        Container c = ender();
        return c != null ? c.removeItemNoUpdate(slot) : ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        Container c = ender();
        if (c != null) c.setItem(slot, stack);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return ender() != null;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        Container c = ender();
        if (c != null) c.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return ender() != null && Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
    }
}
