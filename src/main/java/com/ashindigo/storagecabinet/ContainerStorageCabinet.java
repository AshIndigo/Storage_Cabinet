package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.tileentities.TileEntityStorageCabinet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerStorageCabinet extends Container {

    private IItemHandler inventoryCap;

    ContainerStorageCabinet(InventoryPlayer playerInv, final TileEntityStorageCabinet cabinet) {

        inventoryCap = cabinet.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);

        // 30 * 9 is 270 the numb of slots in the cap
        for (int i = 0; i < 30; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new SlotItemHandler(inventoryCap, i * 9 + j, 9 + j * 18, 18 + i * 18) {

                    @Override
                    public void onSlotChanged() {
                        cabinet.markDirty();
                    }

                    @Override
                    @SideOnly(Side.CLIENT)
                    public boolean isEnabled() {
                        // Sets if slot's enabled based on whether or not it's in the gui bounds
                        return this.yPos < 91 && this.yPos > 0 && xPos < 154 && xPos > 0;
                    }

                });

            }
        }
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlotToContainer(new Slot(playerInv, j1 + l * 9 + 9, 9 + j1 * 18, 118 + l * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(playerInv, k, 9 + k * 18, 176)); // 112 orig
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            int containerSlots = inventorySlots.size() - player.inventory.mainInventory.size();

            if (index < containerSlots) {
                if (!this.mergeItemStack(itemstack1, containerSlots, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    void scrollTo(float pos) {
        int i = (inventoryCap.getSlots() + 9 - 1) / 9 - 5; // 25.8888888889 for 270 slots
        int j = (int) ((double) (pos * (float) i) + 0.5D);

        if (j < 0) {
            j = 0;
        }

        // Iterate through all slots
        for (int y = 0; y < 30; ++y) {
            for (int x = 0; x < 9; ++x) {
                if (j == 0) {
                    inventorySlots.get(y * 9 + x).yPos = (18 + y * 18); // Orig 18 + y * 18
                } else {
                    inventorySlots.get(y * 9 + x).yPos = (18 + (y - j) * 18); // Orig 18 + (y * j) * 18
                }

            }
        }

    }
}
