package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.IntStream;

public class ManagerInventory implements SidedInventory { // The methods involving "temp" scare me

    private List<StorageCabinetEntity> cabinets;

    public ManagerInventory(List<StorageCabinetEntity> cabinets) {
        this.cabinets = cabinets;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return IntStream.range(0, size()).toArray();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.isValid(slot, stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        int temp = slot;
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
                return cabinet.isValid(temp, stack);
            } else {
                temp -= cabinet.size();
            }
        }
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public int size() {
        int i = 0;
        for (StorageCabinetEntity cabinet : cabinets) {
            i += cabinet.size();
        }
        return i;
    }

    @Override
    public boolean isEmpty() {
        for (StorageCabinetEntity cabinet : cabinets) {
            if (!cabinet.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        int temp = slot;
        ItemStack stack = ItemStack.EMPTY;
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
               return cabinet.getStack(temp);
            } else {
                temp -= cabinet.size();
            }
        }
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        int temp = slot;
        ItemStack stack = ItemStack.EMPTY;
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
               return cabinet.removeStack(temp, amount);
            } else {
                temp -= cabinet.size();
            }
        }
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        int temp = slot;
        ItemStack stack = ItemStack.EMPTY;
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
                return cabinet.removeStack(temp);
            } else {
                temp -= cabinet.size();
            }
        }
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        int temp = slot;
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
               cabinet.setStack(temp, stack);
               return;
            } else {
                temp -= cabinet.size();
            }
        }
    }

    @Override
    public void markDirty() {
        for (StorageCabinetEntity cabinet : cabinets) {
            cabinet.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        for (StorageCabinetEntity cabinet : cabinets) {
            cabinet.clear();
        }
    }
}
