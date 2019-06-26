package com.ashindigo.storagecabinet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashSet;

public class ContainerStorageCabinet extends Container {

    private final TileEntityStorageCabinet inventory;

    ContainerStorageCabinet(int id, PlayerInventory playerInv, TileEntityStorageCabinet te) {
        super(null, id);
        this.inventory = te;
        for (int i = 0; i < 30; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(te.inventory, i * 9 + j, 9 + j * 18, 18 + i * 18) {
                    @Environment(EnvType.CLIENT)
                    public boolean doDrawHoveringEffect() {
                        return this.yPosition < 91 && this.yPosition > 0 && xPosition < 154 && xPosition > 0;
                    }

                    public boolean canInsert(ItemStack stack) {
                        boolean flag;
                        flag = inventory.isInvEmpty();
                        HashSet<Item> set = new HashSet<>();
                        set.add(stack.getItem());
                        if (inventory.containsAnyInInv(set)) {
                            flag = true;
                        }
                        return flag;
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
    public boolean canUse(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity p_82846_1_, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.getSlot(index);

        if (slot != null && slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 270) {
                if (!this.insertItem(itemstack1, 270, this.inventory.inventory.getInvSize(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemstack1, 0, 270, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return itemstack;
    }

    void scrollTo(float pos) {
        int i = (inventory.inventory.getInvSize() + 9 - 1) / 9 - 5; // 25.8888888889 for 270 slots
        int j = (int) ((double) (pos * (float) i) + 0.5D);

        if (j < 0) {
            j = 0;
        }
        // Iterate through all slots
        for (int y = 0; y < 30; ++y) {
            for (int x = 0; x < 9; ++x) {
                if (j == 0) {
                    slotList.get(y * 9 + x).yPosition = (18 + y * 18); // Orig 18 + y * 18
                } else {
                    slotList.get(y * 9 + x).yPosition = (18 + (y - j) * 18); // Orig 18 + (y * j) * 18
                }
            }
        }
    }
}
