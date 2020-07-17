package com.ashindigo.storagecabinet.entity;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import spinnery.common.inventory.BaseInventory;
import spinnery.common.utility.InventoryUtilities;

import java.util.*;

public class StorageCabinetEntity extends BlockEntity implements BlockEntityClientSerializable, Inventory, InventoryChangedListener {

    public int tier = 0;

    DefaultedList<ItemStack> stacks;

    final List<InventoryChangedListener> listeners = new ArrayList<>();

    public StorageCabinetEntity() {
        super(StorageCabinet.storageCabinetEntity);
    }

    public StorageCabinetEntity setTier(int tier) {
        this.tier = tier;
        this.stacks = DefaultedList.ofSize(size(), ItemStack.EMPTY);
        return this;
    }

    public Collection<Identifier> getTagsFor(Item object) {
        List<Identifier> list = Lists.newArrayList();
        for (Map.Entry<Identifier, Tag<Item>> entry : ItemTags.getContainer().getEntries().entrySet()) {
            if (entry.getValue().contains(object)) {
                list.add(entry.getKey());
            }
        }

        return list;
    }

    @Override
    public void clear() {
        markDirty();
        stacks.clear();
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.tier = tag.getInt("tier");
        setTier(tier);
        BaseInventory inv = InventoryUtilities.read(tag);
        for (int i = 0; i < inv.size(); i++) {
            setStack(i, inv.getStack(i));
        }
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
        super.fromTag(getCachedState(), tag);
        this.tier = tag.getInt("tier");
        setTier(tier);
        BaseInventory inv = InventoryUtilities.read(tag);
        for (int i = 0; i < inv.size(); i++) {
            setStack(i, inv.getStack(i));
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        InventoryUtilities.write(this, tag);
        tag.putInt("tier", tier);
        super.toTag(tag);
        return tag;
    }

    public void addListener(InventoryChangedListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    @SuppressWarnings("unused") // Keeping for potential use later
    public void removeListener(InventoryChangedListener... listeners) {
        this.listeners.removeAll(Arrays.asList(listeners));
    }

    @Override
    public int size() {
        return (tier + 1) * 90;
    }

    @Override
    public boolean isEmpty() {
        return stacks.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return stacks.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        markDirty();
        ItemStack stack = stacks.get(slot).split(amount);
        onInventoryChanged(this);
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        markDirty();
        ItemStack stack = stacks.remove(slot);
        onInventoryChanged(this);
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        markDirty();
        stacks.set(slot, stack);
        onInventoryChanged(this);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        if (world != null && !world.isClient) {
            listeners.forEach(inventoryListener -> inventoryListener.onInventoryChanged(sender));
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (isEmpty() || stack.isEmpty()) {
            return true;
        } else {
            Collection<Identifier> idList = getTagsFor(stack.getItem());
            if (idList.isEmpty()) {
                return containsAny(Collections.singleton(stack.getItem()));
            } else {
                for (Identifier id : idList) {
                    Tag<Item> tag = ItemTags.getContainer().get(id);
                    return stacks.stream().anyMatch(stack2 -> tag.contains(stack2.getItem()));
                }
            }
        }
        return false;
    }

    public ItemStack getMainItemStack() {
        return stacks.stream().filter(stack -> !stack.isEmpty()).findAny().get();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StorageCabinetEntity) {
            StorageCabinetEntity cabinet = (StorageCabinetEntity) obj;
            return cabinet.getPos().equals(this.getPos());
        }
        return false;
    }
}

