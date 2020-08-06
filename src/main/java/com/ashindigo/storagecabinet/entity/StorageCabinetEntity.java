package com.ashindigo.storagecabinet.entity;

import com.ashindigo.storagecabinet.BlockRegistry;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.handler.StorageCabinetHandler;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainers;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import spinnery.common.inventory.BaseInventory;
import spinnery.common.utility.InventoryUtilities;

import java.util.*;

public class StorageCabinetEntity extends BlockEntity implements BlockEntityClientSerializable, Inventory, InventoryChangedListener, ExtendedScreenHandlerFactory {

    private int viewerCount;
    public boolean locked = false;
    public int tier = 0;
    public Item item = Items.AIR;

    DefaultedList<ItemStack> stacks;

    final List<InventoryChangedListener> listeners = new ArrayList<>();
    private Text customName;

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
        for (Map.Entry<Identifier, Tag<Item>> entry : TagContainers.instance().items().getEntries().entrySet()) {
            if (entry.getValue().contains(object)) {
                list.add(entry.getKey());
            }
        }

        return list;
    }

    @Override
    public void clear() {
        stacks.clear();
        markDirty();
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.tier = tag.getInt("tier");
        this.locked = tag.getBoolean("locked");
        this.item = Registry.ITEM.get(Identifier.tryParse(tag.getString("item")));
        setTier(tier);
        BaseInventory inv = InventoryUtilities.read(tag);
        for (int i = 0; i < inv.size(); i++) {
            setStack(i, inv.getStack(i));
        }
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        InventoryUtilities.write(this, tag);
        tag.putInt("tier", tier);
        tag.putBoolean("locked", locked);
        tag.putString("item", Registry.ITEM.getId(item).toString());
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        super.toTag(tag);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        super.fromTag(getCachedState(), tag);
        this.tier = tag.getInt("tier");
        this.locked = tag.getBoolean("locked");
        this.item = Registry.ITEM.get(Identifier.tryParse(tag.getString("item")));
        setTier(tier);
        BaseInventory inv = InventoryUtilities.read(tag);
        for (int i = 0; i < inv.size(); i++) {
            setStack(i, inv.getStack(i));
        }
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        InventoryUtilities.write(this, tag);
        tag.putInt("tier", tier);
        tag.putBoolean("locked", locked);
        tag.putString("item", Registry.ITEM.getId(item).toString());
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
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

    public void clearListeners() {
        listeners.clear();
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
        ItemStack stack = stacks.get(slot).split(amount);
        markDirty();
        onInventoryChanged(this);
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = stacks.remove(slot);
        markDirty();
        onInventoryChanged(this);
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stacks.set(slot, stack);
        markDirty();
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
        if (item.equals(Items.AIR)) {
            item = getMainItemStack().getItem();
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (!locked) {
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
        } else {
            if (item.equals(Items.AIR)) {
                return true;
            }
            Collection<Identifier> idList = getTagsFor(item);
            if (idList.isEmpty()) {
                return stack.getItem().equals(item);
            } else {
                for (Identifier id : idList) {
                    Tag<Item> itemTag = ItemTags.getContainer().get(id);
                    if (itemTag.contains(stack.getItem())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ItemStack getMainItemStack() {
        if (locked) {
            return new ItemStack(item);
        }
        return stacks.stream().filter(stack -> !stack.isEmpty()).findAny().orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StorageCabinetEntity) {
            StorageCabinetEntity cabinet = (StorageCabinetEntity) obj;
            return cabinet.getPos().equals(this.getPos());
        }
        return false;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        BlockState blockState = this.getCachedState();
        boolean bl = blockState.get(StorageCabinetBlock.OPEN);
        if (!bl) {
            this.world.setBlockState(this.getPos(), blockState.with(StorageCabinetBlock.OPEN, true), 3);
        }
        this.world.getBlockTickScheduler().schedule(this.getPos(), this.getCachedState().getBlock(), 5);
        viewerCount++;
    }


    public void tick() {
        if (this.viewerCount > 0) {
            this.world.getBlockTickScheduler().schedule(this.getPos(), this.getCachedState().getBlock(), 5);
        } else {
            BlockState blockState = this.getCachedState();
            if (!blockState.isOf(BlockRegistry.getByTier(tier))) {
                this.markRemoved();
                return;
            }

            boolean bl = blockState.get(StorageCabinetBlock.OPEN);
            if (bl) {
                this.world.setBlockState(this.getPos(), blockState.with(StorageCabinetBlock.OPEN, false), 3);
            }
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
        }

    }

    public boolean hasCustomName() {
        return customName != null;
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
    }

    public Text getCustomName() {
        return customName;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public Text getDisplayName() {
        return hasCustomName() ? getCustomName() : new TranslatableText(BlockRegistry.getByTier(tier).getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new StorageCabinetHandler(syncId, inv, pos);
    }
}

