package com.ashindigo.storagecabinet;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import spinnery.util.InventoryUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorageCabinetEntity extends BlockEntity implements BlockEntityClientSerializable, Inventory, InventoryListener {

    public int tier = 0;

    DefaultedList<ItemStack> stacks;

    List<InventoryListener> listeners = new ArrayList<>();

    public StorageCabinetEntity() {
        super(StorageCabinet.storageCabinetEntity);
    }

    public StorageCabinetEntity setTier(int tier) {
        this.tier = tier;
        this.stacks = DefaultedList.ofSize(getInvSize(), ItemStack.EMPTY);
        return this;
    }

    @Override
    public int getInvSize() {
        return (tier + 1) * 90;
    }

    @Override
    public boolean isInvEmpty() {
        return stacks.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getInvStack(int slot) {
        return stacks.get(slot);
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount) {
        markDirty();
        ItemStack stack = stacks.get(slot).split(amount);
        onInvChange(this);
        return stack;
    }

    @Override
    public ItemStack removeInvStack(int slot) {
        markDirty();
        ItemStack stack = stacks.remove(slot);
        onInvChange(this);
        return stack;
    }

    @Override
    public void setInvStack(int slot, ItemStack stack) {
        markDirty();
        stacks.set(slot, stack);
        onInvChange(this);
    }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        markDirty();
        stacks.clear();
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.tier = tag.getInt("tier");
        setTier(tier);
        InventoryUtilities.read(this, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        InventoryUtilities.write(this, tag);
        tag.putInt("tier", tier);
        super.toTag(tag);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        super.fromTag(tag);
        this.tier = tag.getInt("tier");
        setTier(tier);
        InventoryUtilities.read(this, tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        InventoryUtilities.write(this, tag);
        tag.putInt("tier", tier);
        super.toTag(tag);
        return tag;
    }


    @Override
    public void onInvChange(Inventory inventory) {
        if (world != null && !world.isClient) {
            listeners.forEach(inventoryListener -> inventoryListener.onInvChange(inventory));
        }
    }

    public void addListener(InventoryListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public void removeListener(InventoryListener... listeners) {
        this.listeners.removeAll(Arrays.asList(listeners));
    }
}
