package com.ashindigo.storagecabinet;

import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class StorageCabinetInventory extends BasicInventory implements SidedInventory {

    public StorageCabinetInventory() {
        super(270);
    }

    @Override
    public int[] getInvAvailableSlots(Direction var1) {
        return new int[270];
    }

    @Override
    public boolean canInsertInvStack(int var1, ItemStack var2, Direction var3) {
        return true;
    }

    @Override
    public boolean canExtractInvStack(int var1, ItemStack var2, Direction var3) {
        return true;
    }
}
