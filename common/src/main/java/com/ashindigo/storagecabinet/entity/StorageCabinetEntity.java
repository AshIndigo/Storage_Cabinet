package com.ashindigo.storagecabinet.entity;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.container.StorageCabinetContainer;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class StorageCabinetEntity extends BlockEntity implements MenuProvider {

    public boolean locked = false;
    public int tier = 0;
    public Item item = Items.AIR;
    private final ItemStackHandler itemHandler = new ItemStackHandler(size()) {

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (!locked) {
                if (stacks.stream().allMatch(ItemStack::isEmpty) || stack.isEmpty()) {
                    return true;
                } else {
                    Collection<ResourceLocation> idList = getTagsFor(stack.getItem());
                    if (idList.isEmpty()) {
                        return IntStream.range(0, this.getSlots()).mapToObj(this::getStackInSlot).anyMatch(itemStack -> stack.getItem().equals(itemStack.getItem()) && itemStack.getCount() > 0);
                    } else {
                        for (ResourceLocation id : idList) {
                            Tag<Item> tag = ItemTags.getAllTags().getTagOrEmpty(id);
                            return stacks.stream().anyMatch(stack2 -> tag.contains(stack2.getItem()));
                        }
                    }
                }
            } else {
                if (item.equals(Items.AIR)) {
                    return true;
                }
                Collection<ResourceLocation> idList = getTagsFor(item);
                if (idList.isEmpty()) {
                    return stack.getItem().equals(item);
                } else {
                    for (ResourceLocation id : idList) {
                        Tag<Item> itemTag = ItemTags.getAllTags().getTagOrEmpty(id);
                        if (itemTag.contains(stack.getItem())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    };
    final LazyOptional<IItemHandler> inventoryHandlerLazyOptional = LazyOptional.of(() -> itemHandler);
    private int viewerCount;
    private Component customName;

    public StorageCabinetEntity(BlockPos pos, BlockState state) {
        super(StorageCabinet.CABINET_ENTITY.get(), pos, state);
    }

    public StorageCabinetEntity setTier(int tier) {
        this.tier = tier;
        itemHandler.setSize(size());
        return this;
    }

    public int size() {
        return (tier + 1) * 90;
    }

    public Collection<ResourceLocation> getTagsFor(Item object) {
        List<ResourceLocation> list = Lists.newArrayList();
        for (Map.Entry<ResourceLocation, Tag<Item>> entry : SerializationTags.getInstance().getOrEmpty(Registry.ITEM_REGISTRY).getAllTags().entrySet()) {
            if (entry.getValue().contains(object)) {
                list.add(entry.getKey());
            }
        }
        return list;
    }


    @Override
    public void load(CompoundTag tag) {
        this.tier = tag.getInt("tier");
        //setTier(tier);
        if (tag.contains("inv")) {
            itemHandler.deserializeNBT(tag.getCompound("inv"));
        }
        super.load(tag);
        this.locked = tag.getBoolean("locked");
        this.item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(tag.getString("item")));
        if (tag.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putInt("tier", tier);
        tag.putBoolean("locked", locked);
        tag.putString("item", ForgeRegistries.ITEMS.getKey(item).toString());
        if (this.customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
        tag.put("inv", itemHandler.serializeNBT());
        return tag;
    }

    public void setCustomName(Component text) {
        this.customName = text;
    }

    @Override
    public Component getDisplayName() {
        return customName != null ? customName : new TranslatableComponent(level.getBlockState(getBlockPos()).getBlock().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new StorageCabinetContainer(syncId, inv, getBlockPos(), tier);
    }

    public ItemStack getMainItemStack() {
        if (locked) {
            return new ItemStack(item);
        }
        ItemStack stack = ItemStack.EMPTY;
        IItemHandler inv = inventoryHandlerLazyOptional.orElseThrow(() -> new NullPointerException("Source Capability was not present!"));
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stackInSlot = inv.getStackInSlot(i);
            if (!stackInSlot.isEmpty()) {
                stack = stackInSlot;
            }
        }
        return stack;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StorageCabinetEntity) {
            StorageCabinetEntity cabinet = (StorageCabinetEntity) obj;
            return cabinet.getBlockPos().equals(this.getBlockPos());
        }
        return false;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("tier", tier);
        tag.putBoolean("locked", locked);
        tag.putString("item", ForgeRegistries.ITEMS.getKey(item).toString());
        if (this.customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
        return tag;
    }

    public void startOpen() {
        BlockState blockState = this.getBlockState();
        boolean bl = blockState.getValue(StorageCabinetBlock.OPEN);
        if (level != null) {
            if (!bl) {
                this.level.setBlock(this.getBlockPos(), blockState.setValue(StorageCabinetBlock.OPEN, true), 3);
            }
            this.level.getBlockTicks().scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 5);
            viewerCount++;
        }
    }

    public void tick() {
        if (level != null) {
            if (this.viewerCount > 0) {
                this.level.getBlockTicks().scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 5);
            } else {
                BlockState blockState = this.getBlockState();
                if (!blockState.is(StorageCabinet.getByTier(tier))) {
                    this.setRemoved();
                    return;
                }

                boolean bl = blockState.getValue(StorageCabinetBlock.OPEN);
                if (bl) {
                    this.level.setBlock(this.getBlockPos(), blockState.setValue(StorageCabinetBlock.OPEN, false), 3);
                }
            }
        }
    }

    public void onClose(Player player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
        }
    }


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryHandlerLazyOptional.invalidate();
    }
}
