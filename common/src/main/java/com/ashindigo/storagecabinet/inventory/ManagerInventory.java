package com.ashindigo.storagecabinet.inventory;

import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public record ManagerInventory(CabinetManagerEntity entity, List<StorageCabinetEntity> cabinets) implements WorldlyContainer { // The methods involving "temp" scare me

    @Override
    public int[] getSlotsForFace(Direction side) {
        return IntStream.range(0, getContainerSize()).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.canPlaceItem(slot, stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        int temp = slot;
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
                return cabinet.canPlaceItem(temp, stack);
            } else {
                temp -= cabinet.size();
            }
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public int getContainerSize() {
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
    public ItemStack getItem(int slot) {
        int temp = slot;
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
                return cabinet.getItem(temp);
            } else {
                temp -= cabinet.size();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        int temp = slot;
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
                return cabinet.removeItem(temp, amount);
            } else {
                temp -= cabinet.size();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        int temp = slot;
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
                return cabinet.removeItemNoUpdate(temp);
            } else {
                temp -= cabinet.size();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        int temp = slot;
        for (StorageCabinetEntity cabinet : cabinets) {
            if (cabinet.size() - 1 >= temp) {
                cabinet.setItem(temp, stack);
                return;
            } else {
                temp -= cabinet.size();
            }
        }
    }

    @Override
    public void setChanged() { // Probably not good for performance
        cabinets.clear();
        checkSurroundingCabinets((ArrayList<StorageCabinetEntity>) cabinets, entity.getBlockPos(), entity.getLevel());
        for (StorageCabinetEntity cabinet : cabinets) {
            cabinet.setChanged();
        }
        entity.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (StorageCabinetEntity cabinet : cabinets) {
            cabinet.clearContent();
        }
    }

    private void checkSurroundingCabinets(ArrayList<StorageCabinetEntity> cabinetList, BlockPos pos, Level world) {
        for (Direction direction : Direction.values()) {
            BlockPos offsetPos = pos.relative(direction);
            if (world.getBlockEntity(offsetPos) instanceof StorageCabinetEntity) {
                if (!cabinetList.contains(world.getBlockEntity(offsetPos))) {
                    cabinetList.add((StorageCabinetEntity) world.getBlockEntity(offsetPos));
                    checkSurroundingCabinets(cabinetList, offsetPos, world);
                }
            }
        }
    }

    public CabinetManagerEntity getEntity() {
        return entity;
    }
}
