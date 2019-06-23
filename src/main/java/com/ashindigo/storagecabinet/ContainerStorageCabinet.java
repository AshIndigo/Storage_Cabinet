package com.ashindigo.storagecabinet;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class ContainerStorageCabinet extends Container {

    private IItemHandler inventoryCap;

    ContainerStorageCabinet(int id, PlayerInventory playerInv, PacketBuffer extraData) {
        this(id, playerInv, (TileEntityStorageCabinet) Objects.requireNonNull(Minecraft.getInstance().world.getTileEntity(extraData.readBlockPos())));
        for (int i1 = 0; i1 < 3; ++i1) {
            for (int k1 = 0; k1 < 9; ++k1) {
                super.addSlot(new Slot(playerInv.player.inventory, k1 + i1 * 9 + 9, 9 + k1 * 18, 118 + i1 * 18));
            }
        }
        for (int j1 = 0; j1 < 9; ++j1) {
            super.addSlot(new Slot(playerInv.player.inventory, j1, 9 + j1 * 18, 176));
        }
    }

    ContainerStorageCabinet(int id, PlayerInventory playerInv, TileEntityStorageCabinet te) {
        super(StorageCabinetMod.cabinetType, id);
        inventoryCap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(new ItemStackHandler(270)); // Something went wrong, lets just return an empty handler

         //30 * 9 is 270 the numb of slots in the cap
        for (int i = 0; i < 30; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new SlotItemHandler(inventoryCap, i * 9 + j, 9 + j * 18, 18 + i * 18) {

                    @Override
                    public void onSlotChanged() {
                        te.markDirty();
                    }

                    @Override
                    @OnlyIn(Dist.CLIENT)
                    public boolean isEnabled() {
                        return this.yPos < 91 && this.yPos > 0 && xPos < 154 && xPos > 0;
                    }
                });
            }
        }

        for (int i1 = 0; i1 < 3; ++i1) {
            for (int k1 = 0; k1 < 9; ++k1) {
                super.addSlot(new Slot(playerInv.player.inventory, k1 + i1 * 9 + 9, 9 + k1 * 18, 118 + i1 * 18));
            }
        }

    for (int j1 = 0; j1 < 9; ++j1) {
            super.addSlot(new Slot(playerInv.player.inventory, j1, 9 + j1 * 18, 176));
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack transferStackInSlot(PlayerEntity p_82846_1_, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 270) {
                if (!this.mergeItemStack(itemstack1, 270, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 270, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
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
