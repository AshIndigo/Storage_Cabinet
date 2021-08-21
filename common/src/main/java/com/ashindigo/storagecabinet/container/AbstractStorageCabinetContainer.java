package com.ashindigo.storagecabinet.container;

import com.ashindigo.storagecabinet.DisplayHeight;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.StorageCabinetExpectPlatform;
import com.ashindigo.storagecabinet.entity.ModifiableDisplaySize;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractStorageCabinetContainer extends AbstractContainerMenu implements ModifiableDisplaySize {

    public DisplayHeight heightSetting = StorageCabinet.DEFAULT_HEIGHT;

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

    public void changeSlotPositions(DisplayHeight height) {
        setDisplayHeight(height);
        for (Slot slot : slots) {
            if (slot.container instanceof Inventory) {
                if (Inventory.isHotbarSlot(slot.getContainerSlot())) {
                    StorageCabinetExpectPlatform.setSlotY(slot, height.getPlayerInvStart() + 58);
                } else {
                    StorageCabinetExpectPlatform.setSlotY(slot, height.getPlayerInvStart() + ((slot.getContainerSlot() / 9) - 1) * 18);
                }
            }
        }
    }

    public void addPlayerInv(Inventory playerInv, DisplayHeight height) {
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInv, j1 + l * 9 + 9, 9 + j1 * 18, height.getPlayerInvStart() + l * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, 9 + k * 18, height.getPlayerInvStart() + 58)); // 58 is the gap between starting slot and hotbar
        }
    }

    public abstract void scrollTo(float pos, StorageCabinetEntity entity);
}
