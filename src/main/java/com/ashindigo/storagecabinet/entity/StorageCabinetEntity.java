package com.ashindigo.storagecabinet.entity;

import com.ashindigo.storagecabinet.BlockRegistry;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.description.StorageCabinetDescription;
import com.ashindigo.storagecabinet.inventory.BasicSidedInventory;
import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;

import java.util.*;

public class StorageCabinetEntity extends BlockEntity implements BasicSidedInventory, InventoryChangedListener, ExtendedScreenHandlerFactory, BlockEntityClientSerializable, InventoryProvider {

    final List<InventoryChangedListener> listeners = new ArrayList<>();
    final List<InventoryChangedListener> clientListeners = new ArrayList<>();
    public boolean locked = false;
    public int tier = 0;
    public Item item = Items.AIR;

    DefaultedList<ItemStack> stacks;
    private int viewerCount;
    private Text customName;

    public StorageCabinetEntity(BlockPos pos, BlockState state) {
        super(StorageCabinet.STORAGE_CABINET_ENTITY, pos, state);
    }

    public StorageCabinetEntity setTier(int tier) {
        this.tier = tier;
        this.stacks = DefaultedList.ofSize(size(), ItemStack.EMPTY);
        return this;
    }

    public Collection<Identifier> getTagsFor(Item object) {
        List<Identifier> list = Lists.newArrayList();
        for (Map.Entry<Identifier, Tag<Item>> entry : ServerTagManagerHolder.getTagManager().getOrCreateTagGroup(Registry.ITEM.getKey()).getTags().entrySet()) {
            if (entry.getValue().contains(object)) {
                list.add(entry.getKey());
            }
        }

        return list;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.tier = tag.getInt("tier");
        setTier(tier);
        Inventories.readNbt(tag, stacks);
        this.locked = tag.getBoolean("locked");
        this.item = Registry.ITEM.get(Identifier.tryParse(tag.getString("item")));
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putInt("tier", tier);
        Inventories.writeNbt(tag, stacks);
        tag.putBoolean("locked", locked);
        tag.putString("item", Registry.ITEM.getId(item).toString());
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        return super.writeNbt(tag);
    }

    public void addClientOnlyListener(InventoryChangedListener... listeners) {
        this.clientListeners.addAll(Arrays.asList(listeners));
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
        clientListeners.clear();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return stacks;
    }

    @Override
    public int size() {
        return (tier + 1) * 90;
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
    public void onInventoryChanged(Inventory sender) {
        if (world != null && !world.isClient) {
            listeners.forEach(inventoryListener -> inventoryListener.onInventoryChanged(sender));
        }
        if (world != null && world.isClient) {
            clientListeners.forEach(inventoryChangedListener -> inventoryChangedListener.onInventoryChanged(sender));
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
                        Tag<Item> tag = ItemTags.getTagGroup().getTagOrEmpty(id);
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
                    Tag<Item> itemTag = ItemTags.getTagGroup().getTagOrEmpty(id);
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
        if (obj instanceof StorageCabinetEntity cabinet) {
            return cabinet.getPos().equals(this.getPos());
        }
        return false;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        BlockState blockState = this.getCachedState();
        boolean bl = blockState.get(StorageCabinetBlock.OPEN);
        if (world != null) {
            if (!bl) {
                this.world.setBlockState(this.getPos(), blockState.with(StorageCabinetBlock.OPEN, true), 3);
            }
            this.world.getBlockTickScheduler().schedule(this.getPos(), this.getCachedState().getBlock(), 5);
            viewerCount++;
        }
    }


    public void tick() {
        if (world != null) {
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

    public Text getCustomName() {
        return customName;
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
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
        return new StorageCabinetDescription(syncId, inv, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return this;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        return writeNbt(tag);
    }
}

