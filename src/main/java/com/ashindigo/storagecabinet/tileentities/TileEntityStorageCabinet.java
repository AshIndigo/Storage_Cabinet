package com.ashindigo.storagecabinet.tileentities;

import com.ashindigo.storagecabinet.StorageCabinetConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityStorageCabinet extends TileEntity {

    private ItemStackHandler inventory = new ItemStackHandler(270) {
        // TODO Probably unoptimized - Especially that config check
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            int e = 0;
            for (int i = 0; i < inventory.getSlots(); i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    e++;
                }
            }
            if (e == 270) {
                if (StorageCabinetConfig.onlyNonStackables) {
                    if (stack.getMaxStackSize() == 1) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return true;
                }
            } else {
                for (int i = 0; i < inventory.getSlots(); i++) {

                    if (StorageCabinetConfig.onlyNonStackables) {
                        if (!inventory.getStackInSlot(i).isEmpty()) {
                            if (inventory.getStackInSlot(i).getItem().equals(stack.getItem())) {
                                if (stack.getMaxStackSize() == 1) {
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }
                    } else {
                        if (!inventory.getStackInSlot(i).isEmpty()) {
                            if (inventory.getStackInSlot(i).getItem().equals(stack.getItem())) {
                                return true;
                            }
                        }

                    }
                }
            }
            return false;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (isItemValid(slot, stack)) {
                return super.insertItem(slot, stack, simulate);
            }
            return stack;
        }
    };

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("inventory", inventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        inventory.deserializeNBT(compound.getCompoundTag("inventory"));
        super.readFromNBT(compound);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) inventory;
        }
        return super.getCapability(capability, facing);
    }
}
