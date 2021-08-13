//package com.ashindigo.storagecabinet;
//
//import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
//import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
//import net.minecraft.core.Direction;
//import net.minecraft.world.item.ItemStack;
//import net.minecraftforge.items.*;
//
//import java.util.ArrayList;
//
//public class CabinetManagerItemHandler implements IItemHandler, IItemHandlerModifiable {
//
//    private final ArrayList<StorageCabinetEntity> cabinets;
//
//    public CabinetManagerItemHandler(CabinetManagerEntity entity) {
//        cabinets = entity.cabinetList;
//    }
//
//    @Override
//    public void setStackInSlot(int slot, ItemStack stack) {
//        validateSlotIndex(slot);
//        int temp = slot;
//        for (StorageCabinetEntity cabinet : cabinets) {
//            if (cabinet.size() - 1 >= temp) {
//                getInv(cabinet).setStackInSlot(temp, stack);
//                return;
//            } else {
//                temp -= cabinet.size();
//            }
//        }
//        onContentsChanged(slot);
//    }
//
//    @Override
//    public int getSlots() {
//        int i = 0;
//        for (StorageCabinetEntity cabinet : cabinets) {
//            i += cabinet.size();
//        }
//        return i;
//    }
//
//    @Override
//    public ItemStack getStackInSlot(int slot) {
//        validateSlotIndex(slot);
//        int temp = slot;
//        for (StorageCabinetEntity cabinet : cabinets) {
//            if (cabinet.size() - 1 >= temp) {
//                return getInv(cabinet).getStackInSlot(temp);
//            } else {
//                temp -= cabinet.size();
//            }
//        }
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
//        if (stack.isEmpty())
//            return ItemStack.EMPTY;
//
//        if (!isItemValid(slot, stack))
//            return stack;
//
//        validateSlotIndex(slot);
//
//        ItemStack existing = this.getStackInSlot(slot);
//
//        int limit = getStackLimit(slot, stack);
//
//        if (!existing.isEmpty()) {
//            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
//                return stack;
//
//            limit -= existing.getCount();
//        }
//
//        if (limit <= 0)
//            return stack;
//
//        boolean reachedLimit = stack.getCount() > limit;
//
//        if (!simulate) {
//            if (existing.isEmpty()) {
//                this.setStackInSlot(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
//            } else {
//                existing.grow(reachedLimit ? limit : stack.getCount());
//            }
//            onContentsChanged(slot);
//        }
//
//        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
//    }
//
//    @Override
//    public ItemStack extractItem(int slot, int amount, boolean simulate) {
//        if (amount == 0)
//            return ItemStack.EMPTY;
//
//        validateSlotIndex(slot);
//
//        ItemStack existing = this.getStackInSlot(slot);
//
//        if (existing.isEmpty())
//            return ItemStack.EMPTY;
//
//        int toExtract = Math.min(amount, existing.getMaxStackSize());
//
//        if (existing.getCount() <= toExtract) {
//            if (!simulate) {
//                this.setStackInSlot(slot, ItemStack.EMPTY);
//                onContentsChanged(slot);
//                return existing;
//            } else {
//                return existing.copy();
//            }
//        } else {
//            if (!simulate) {
//                this.setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
//                onContentsChanged(slot);
//            }
//
//            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
//        }
//    }
//
//    @Override
//    public int getSlotLimit(int slot) {
//        return 64;
//    }
//
//    protected int getStackLimit(int slot, ItemStack stack) {
//        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
//    }
//
//    @Override
//    public boolean isItemValid(int slot, ItemStack stack) {
//        int temp = slot;
//        for (StorageCabinetEntity cabinet : cabinets) {
//            if (cabinet.size() - 1 >= temp) {
//                return getInv(cabinet).isItemValid(temp, stack);
//            } else {
//                temp -= cabinet.size();
//            }
//        }
//        return false;
//    }
//
//    protected void validateSlotIndex(int slot) {
//        if (slot < 0 || slot >= getSlots())
//            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
//    }
//
//    private ItemStackHandler getInv(StorageCabinetEntity cabinet) {
//        return (ItemStackHandler) cabinet.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH).orElseThrow(() -> new NullPointerException("Source Capability was not present!"));
//    }
//
//    protected void onContentsChanged(int slot) {
//        int temp = slot;
//        for (StorageCabinetEntity cabinet : cabinets) {
//            if (cabinet.size() - 1 >= temp) {
//                cabinet.setChanged();
//            } else {
//                temp -= cabinet.size();
//            }
//        }
//    }
//}
