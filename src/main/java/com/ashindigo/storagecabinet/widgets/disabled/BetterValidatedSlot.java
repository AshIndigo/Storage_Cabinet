//package com.ashindigo.storagecabinet.widgets.disabled;
//
//import com.google.common.collect.HashMultimap;
//import com.google.common.collect.Multimap;
//import io.github.cottonmc.cotton.gui.ValidatedSlot;
//import io.github.cottonmc.cotton.gui.impl.access.SlotAccessor;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.inventory.Inventory;
//import net.minecraft.item.ItemStack;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.util.Objects;
//import java.util.function.Predicate;
//
//public class BetterValidatedSlot extends ValidatedSlot {
//    private static final Logger LOGGER = LogManager.getLogger();
//    protected final Multimap<WBetterItemSlot, WBetterItemSlot.ChangeListener> listeners = HashMultimap.create();
//    private final int slotNumber;
//    public final int originalX;
//    public final int originalY;
//    private boolean insertingAllowed = true;
//    private boolean takingAllowed = true;
//    private Predicate<ItemStack> filter;
//    private boolean visible = true;
//    private boolean enabled = true;
//
//    public BetterValidatedSlot(Inventory inventory, int index, int x, int y) {
//        super(inventory, index, x, y);
//        if (inventory == null) {
//            throw new IllegalArgumentException("Can't make an itemslot from a null inventory!");
//        } else {
//            this.slotNumber = index;
//            this.originalX = x;
//            this.originalY = y;
//        }
//    }
//
//    public boolean canInsert(ItemStack stack) {
//        return this.insertingAllowed && this.inventory.isValid(this.slotNumber, stack) && this.filter.test(stack);
//    }
//
//    public boolean canTakeItems(PlayerEntity player) {
//        return this.takingAllowed && this.inventory.canPlayerUse(player);
//    }
//
//    public ItemStack getStack() {
//        if (this.inventory == null) {
//            LOGGER.warn("Prevented null-inventory from WItemSlot with slot #: {}", this.slotNumber);
//            return ItemStack.EMPTY;
//        } else {
//            ItemStack result = super.getStack();
//            if (result == null) {
//                LOGGER.warn("Prevented null-itemstack crash from: {}", this.inventory.getClass().getCanonicalName());
//                return ItemStack.EMPTY;
//            } else {
//                return result;
//            }
//        }
//    }
//
//    public void markDirty() {
//        this.listeners.forEach((slot, listener) -> listener.onStackChanged(slot, this.inventory, this.getInventoryIndex(), this.getStack()));
//        super.markDirty();
//    }
//
//    public int getInventoryIndex() {
//        return this.slotNumber;
//    }
//
//    public boolean isInsertingAllowed() {
//        return this.insertingAllowed;
//    }
//
//    public void setInsertingAllowed(boolean insertingAllowed) {
//        this.insertingAllowed = insertingAllowed;
//    }
//
//    public boolean isTakingAllowed() {
//        return this.takingAllowed;
//    }
//
//    public void setTakingAllowed(boolean takingAllowed) {
//        this.takingAllowed = takingAllowed;
//    }
//
//    public Predicate<ItemStack> getFilter() {
//        return this.filter;
//    }
//
//    public void setFilter(Predicate<ItemStack> filter) {
//        this.filter = filter;
//    }
//
//    public void addChangeListener(WBetterItemSlot owner, WBetterItemSlot.ChangeListener listener) {
//        Objects.requireNonNull(owner, "owner");
//        Objects.requireNonNull(listener, "listener");
//        this.listeners.put(owner, listener);
//    }
//
//    public boolean isVisible() {
//        return this.visible;
//    }
//
//    public void setVisible(boolean visible) {
//        if (this.visible != visible) {
//            this.visible = visible;
//            SlotAccessor accessor = (SlotAccessor) this;
//            if (visible) {
//                accessor.setX(this.originalX);
//                accessor.setY(this.originalY);
//            } else {
//                accessor.setX(-100000);
//                accessor.setY(-100000);
//            }
//        }
//    }
//
//
//    public void setEnabled(boolean withinBounds) {
//        SlotAccessor accessor = (SlotAccessor) this;
//        if (withinBounds) {
//            accessor.setX(this.originalX);
//            accessor.setY(this.originalY);
//        } else {
//            accessor.setX(-100000);
//            accessor.setY(-100000);
//        }
//        enabled = withinBounds;
//    }
//
//    @Override
//    @Environment(EnvType.CLIENT)
//    public boolean doDrawHoveringEffect() {
//        return enabled;
//    }
//}