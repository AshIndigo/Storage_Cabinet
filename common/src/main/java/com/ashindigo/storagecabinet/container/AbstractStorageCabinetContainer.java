package com.ashindigo.storagecabinet.container;

import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractStorageCabinetContainer extends AbstractContainerMenu {

    protected AbstractStorageCabinetContainer(@Nullable MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();

            int containerSlots = slots.size() - player.getInventory().items.size();

            if (index < containerSlots) {
                if (!this.moveItemStackTo(itemStack1, containerSlots, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack1.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack1);
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

   public abstract void scrollTo(float pos, StorageCabinetEntity entity);
}
