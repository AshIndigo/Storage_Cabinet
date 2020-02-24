package com.ashindigo.storagecabinet;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import spinnery.util.InventoryUtilities;

import java.util.*;

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
    public boolean isValidInvStack(int slot, ItemStack stack) {
        if (isInvEmpty() || stack.isEmpty()) {
            return true;
        } else {
            Collection<Identifier> idList = getTagsFor(stack.getItem());
            if (idList.isEmpty()) {
                return containsAnyInInv(Collections.singleton(stack.getItem()));
            } else {
                for (Identifier id : idList) {
                    Tag<Item> tag = ItemTags.getContainer().get(id);
                    return stacks.stream().anyMatch(stack2 -> tag.contains(stack2.getItem()));
                }
            }
        }
        return false;
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
