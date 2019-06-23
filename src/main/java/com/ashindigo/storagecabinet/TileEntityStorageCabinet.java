package com.ashindigo.storagecabinet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class TileEntityStorageCabinet extends TileEntity implements INamedContainerProvider {


    TileEntityStorageCabinet() {
        super(StorageCabinetMod.storageCabinetTileEntity);
    }

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
                if (false) { // StorageCabinetConfig.INSTANCE.onlyNonStackables.get()
                    return stack.getMaxStackSize() == 1;
                } else {
                    return true;
                }
            } else {
                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack itemstack = inventory.getStackInSlot(i);
                    if (false) { // StorageCabinetConfig.INSTANCE.onlyNonStackables.get()
                        if (!itemstack.isEmpty()) {
                            if (itemstack.getItem().equals(stack.getItem())) {
                                return stack.getMaxStackSize() == 1;
                            }
                        }
                    } else {
                        if (!itemstack.isEmpty()) {
                            if (itemstack.getItem().equals(stack.getItem())) {
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> inventory).cast(); // magic
        }
        return super.getCapability(cap, side);
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("tile.storagecabinet.name");
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        if (compound.contains("inv"))
            inventory.deserializeNBT(compound.getCompound("inv"));
    }

    @Override
    @Nonnull
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("inv", inventory.serializeNBT());
        return super.write(compound);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity playerEntity) {
        return new ContainerStorageCabinet(id, playerEntity.inventory, this);
    }

}
