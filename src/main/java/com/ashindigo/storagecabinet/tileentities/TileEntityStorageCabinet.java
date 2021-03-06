package com.ashindigo.storagecabinet.tileentities;

import com.ashindigo.storagecabinet.StorageCabinetConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class TileEntityStorageCabinet extends TileEntity {

    private ItemStackHandler inventory = new ItemStackHandler(270) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            int e = 0;
            for (int i = 0; i < inventory.getSlots(); i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    e++;
                }
            }
            if (e == 270) {
                if (StorageCabinetConfig.onlyNonStackables) {
                    return stack.getMaxStackSize() == 1;
                } else {
                    return true;
                }
            } else {
                for (int i = 0; i < inventory.getSlots(); i++) {
                    if (StorageCabinetConfig.onlyNonStackables) {
                        if (!inventory.getStackInSlot(i).isEmpty()) {
                            if (inventory.getStackInSlot(i).getItem().equals(stack.getItem())) {
                                return stack.getMaxStackSize() == 1;
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
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (isItemValid(slot, stack)) {
                return super.insertItem(slot, stack, simulate);
            }
            return stack;
        }
    };

    @Override
    @Nonnull
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
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) inventory;
        }
        return super.getCapability(capability, facing);
    }
}
