package dev.oculifelis.ganys;

import dev.oculifelis.ganys.item.EnderScytheItem;
import dev.oculifelis.ganys.block.WeepingPodBlock;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jspecify.annotations.Nullable;

/** Gany's End game events: inventory tagging, head drops, scythe charging, the endium chestplate and ender flower bone meal. */
public final class GanysEvents {
    public static void register(IEventBus modBus) {
        modBus.addListener((RegisterDataMapTypesEvent e) -> {
            e.register(MobHead.DATA_MAP);
            e.register(CakeFood.DATA_MAP);
            e.register(SoulTnt.DATA_MAP);
            e.register(ClubKnockback.DATA_MAP);
            e.register(BlazeGolemStats.DATA_MAP);
        });
        modBus.addListener((net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent e) ->
                e.put(GanysSurface.BLAZE_GOLEM.get(), dev.oculifelis.ganys.entity.BlazeGolem.createAttributes().build()));
        modBus.addListener((RegisterCapabilitiesEvent e) -> {
            e.registerBlockEntity(Capabilities.Item.BLOCK, GanysSurface.SOUL_CHEST_ENTITY.get(), (be, side) -> VanillaContainerWrapper.of(be));
            e.registerBlockEntity(Capabilities.Item.BLOCK, GanysSurface.VOID_CRATE_ENTITY.get(), (be, side) -> VanillaContainerWrapper.of(be));
            e.registerBlockEntity(Capabilities.Item.BLOCK, GanysSurface.ANCHORED_ENDER_CHEST_ENTITY.get(),
                    (be, side) -> be.ender() != null ? VanillaContainerWrapper.of(be) : null);
        });
        IEventBus bus = NeoForge.EVENT_BUS;
        bus.addListener(GanysEvents::tag);
        bus.addListener(GanysEvents::plantPod);
        bus.addListener(EventPriority.LOWEST, GanysEvents::livingDrops);
        bus.addListener(EventPriority.LOWEST, GanysEvents::blockDrops);
        bus.addListener(GanysEvents::lightning);
        bus.addListener(GanysEvents::bonemeal);
        bus.addListener(GanysEvents::fireproofChest);
    }

    /** Right-clicking the side of obsidian with a ghast tear plants a weeping pod there. */
    private static void plantPod(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(GanysTags.PLANTS_WEEPING_POD) || event.getFace() == null) return;
        Level level = event.getLevel();
        BlockPos pos = event.getPos().relative(event.getFace());
        if (!WeepingPodBlock.plant(level, pos, event.getFace().getOpposite())) return;
        if (!level.isClientSide()) stack.consume(1, event.getEntity());
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    /** Sneak + right-click an inventory with an endium tool or bow: its drops go there from now on. */
    private static void tag(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        if (!player.isShiftKeyDown() || !stack.is(GanysTags.INVENTORY_TAGGERS)) return;
        Level level = event.getLevel();
        if (level.isClientSide()) {
            // The client has no item capabilities for most blocks; let the server decide and just swing.
            if (level.getBlockEntity(event.getPos()) == null) return;
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }
        if (level.getCapability(Capabilities.Item.BLOCK, event.getPos(), null) == null) return;
        stack.set(GanysComponents.TAGGED_INVENTORY.get(), GlobalPos.of(level.dimension(), event.getPos().immutable()));
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    /** The handler a tagged item points at, when it is loaded and in this dimension. */
    public static @Nullable ResourceHandler<ItemResource> taggedHandler(ItemStack stack, ServerLevel level) {
        if (!stack.is(GanysTags.INVENTORY_TAGGERS)) return null;
        GlobalPos tag = stack.get(GanysComponents.TAGGED_INVENTORY.get());
        if (tag == null || tag.dimension() != level.dimension() || !level.isLoaded(tag.pos())) return null;
        return level.getCapability(Capabilities.Item.BLOCK, tag.pos(), null);
    }

    /** Moves each drop into the handler; drops that fit whole are removed, the rest keep what is left over. */
    private static void insert(ResourceHandler<ItemResource> handler, Collection<ItemEntity> drops) {
        for (Iterator<ItemEntity> it = drops.iterator(); it.hasNext(); ) {
            ItemEntity drop = it.next();
            ItemStack left = ItemUtil.insertItemReturnRemaining(handler, drop.getItem(), false, null);
            if (left.isEmpty()) {
                it.remove();
                drop.discard();
            } else {
                drop.setItem(left);
            }
        }
    }

    private static void livingDrops(LivingDropsEvent event) {
        LivingEntity victim = event.getEntity();
        if (!(victim.level() instanceof ServerLevel level)) return;
        ItemStack weapon = event.getSource().getEntity() instanceof Player p ? p.getMainHandItem() : ItemStack.EMPTY;
        MobHead head = MobHead.of(victim.getType());
        if (head != null) {
            int looting = weapon.isEmpty() ? 0 : level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .get(Enchantments.LOOTING).map(h -> EnchantmentHelper.getItemEnchantmentLevel(h, weapon)).orElse(0);
            if (head.random() && victim.getRandom().nextInt(Math.max(200 - 50 * looting, 50)) == 0) {
                addHead(event, victim, head);
            }
            if (weapon.getItem() instanceof EnderScytheItem) {
                int charges = EnderScytheItem.charges(weapon);
                if (charges > 0) {
                    addHead(event, victim, head);
                    EnderScytheItem.setCharges(weapon, charges - 1);
                }
            }
        }
        ResourceHandler<ItemResource> handler = taggedHandler(weapon, level);
        if (handler != null) insert(handler, event.getDrops());
    }

    private static void addHead(LivingDropsEvent event, LivingEntity victim, MobHead head) {
        ItemStack stack = new ItemStack(head.item());
        if (victim instanceof Player killed) stack.set(DataComponents.PROFILE, ResolvableProfile.createResolved(killed.getGameProfile()));
        ItemEntity entity = new ItemEntity(victim.level(), victim.getX(), victim.getY(), victim.getZ(), stack);
        entity.setDefaultPickUpDelay();
        event.getDrops().add(entity);
    }

    private static void blockDrops(BlockDropsEvent event) {
        ResourceHandler<ItemResource> handler = taggedHandler(event.getTool(), event.getLevel());
        if (handler != null) insert(handler, event.getDrops());
    }

    /** Each strike has a 50 % chance to add a charge to every ender scythe the player carries. */
    private static void lightning(EntityStruckByLightningEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) return;
        if (player.getRandom().nextBoolean()) return;
        for (ItemStack stack : player.getInventory()) {
            if (stack.getItem() instanceof EnderScytheItem) EnderScytheItem.setCharges(stack, EnderScytheItem.charges(stack) + 1);
        }
    }

    /** Bone meal on end stone in the End, with air above, grows an ender flower. */
    private static void bonemeal(BonemealEvent event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (level.dimension() != Level.END || !event.getState().is(Blocks.END_STONE) || !level.getBlockState(pos.above()).isAir()) return;
        if (!level.isClientSide()) {
            level.setBlockAndUpdate(pos.above(), GanysBlocks.ENDER_FLOWER.get().defaultBlockState());
            event.getStack().shrink(1);
        }
        event.setSuccessful(true);
    }

    /** The endium chestplate keeps out fire and lava damage. */
    private static void fireproofChest(LivingIncomingDamageEvent event) {
        if (event.getSource().is(DamageTypeTags.IS_FIRE)
                && event.getEntity().getItemBySlot(EquipmentSlot.CHEST).is(GanysItems.ENDIUM_CHESTPLATE.get())) {
            event.setCanceled(true);
        }
    }

    private GanysEvents() {
    }
}
