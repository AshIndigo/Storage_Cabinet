package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ManagerInventory implements SidedInventory {

    private final CabinetManagerEntity entity;
    private final List<StorageCabinetEntity> cabinets;

    public ManagerInventory(CabinetManagerEntity entity, List<StorageCabinetEntity> cabinets) {
        this.entity = entity;
        this.cabinets = cabinets;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return IntStream.range(0, size()).toArray();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return isValid(slot, stack);
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
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
                return cabinet.getStack(temp);
            } else {
                temp -= cabinet.size();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        int temp = slot;
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
                return cabinet.removeStack(temp, amount);
            } else {
                temp -= cabinet.size();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        int temp = slot;
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
                return cabinet.removeStack(temp);
            } else {
                temp -= cabinet.size();
            }
        }
        return ItemStack.EMPTY;
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
    public void markDirty() { // Probably not good for performance
        cabinets.clear();
        Helpers.checkSurroundingCabinets((ArrayList<StorageCabinetEntity>) cabinets, entity.getPos(), entity.getWorld());
        for (StorageCabinetEntity cabinet : cabinets) {
            cabinet.markDirty();
        }
        entity.markDirty();
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

    public CabinetManagerEntity getEntity() {
        return entity;
    }
}
