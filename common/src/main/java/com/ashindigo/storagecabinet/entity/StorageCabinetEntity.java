package com.ashindigo.storagecabinet.entity;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.container.StorageCabinetContainer;
import com.ashindigo.storagecabinet.inventory.BasicSidedInventory;
import com.google.common.collect.Lists;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class StorageCabinetEntity extends BlockEntity implements MenuProvider, BasicSidedInventory {

    public boolean locked = false;
    public int tier = 0;
    public Item item = Items.AIR;
    private int viewerCount;
    private Component customName;
    private NonNullList<ItemStack> items;

    public StorageCabinetEntity(BlockPos pos, BlockState state) {
        super(StorageCabinet.CABINET_ENTITY.get(), pos, state);
    }

    public StorageCabinetEntity setTier(int tier) {
        this.tier = tier;
        items = NonNullList.withSize(size(), ItemStack.EMPTY);
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
        setTier(tier);
        switch (ArchitecturyTarget.getCurrentTarget()) {
            case "fabric": { // This is what I get for two differing implementations in the two versions
                if (tag.contains("Items")) {
                    ContainerHelper.loadAllItems(tag, items);
                }
            }
            case "forge": {
                if (tag.contains("inv")) {
                    ContainerHelper.loadAllItems(tag.getCompound("inv"), items);
                }
                break;
            }
        }
        // Newer loads should just hopefully auto convert it to "standard" format
        ContainerHelper.loadAllItems(tag, items);
        super.load(tag);
        this.locked = tag.getBoolean("locked");
        this.item = Registry.ITEM.get(ResourceLocation.tryParse(tag.getString("item")));
        if (tag.contains("CustomName", 8)) {
            this.customName = Component.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        tag.putInt("tier", tier);
        tag.putBoolean("locked", locked);
        tag.putString("item", Registry.ITEM.getKey(item).toString());
        if (this.customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
        return ContainerHelper.saveAllItems(tag, items);
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
        return items.stream().filter(stack -> !stack.isEmpty()).findAny().orElse(ItemStack.EMPTY);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack stack) {
        if (!locked) {
            if (items.stream().allMatch(ItemStack::isEmpty) || stack.isEmpty()) {
                return true;
            } else {
                Collection<ResourceLocation> idList = getTagsFor(stack.getItem());
                if (idList.isEmpty()) {
                    return IntStream.range(0, this.getContainerSize()).mapToObj(this::getItem).anyMatch(itemStack -> stack.getItem().equals(itemStack.getItem()) && itemStack.getCount() > 0);
                } else {
                    for (ResourceLocation id : idList) {
                        Tag<Item> tag = ItemTags.getAllTags().getTagOrEmpty(id);
                        return items.stream().anyMatch(stack2 -> tag.contains(stack2.getItem()));
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StorageCabinetEntity cabinet) {
            return cabinet.getBlockPos().equals(this.getBlockPos());
        }
        return false;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("tier", tier);
        tag.putBoolean("locked", locked);
        tag.putString("item", Registry.ITEM.getKey(item).toString());
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
    public NonNullList<ItemStack> getItems() {
        return items;
    }


//    @Override
//    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
//        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//            return inventoryHandlerLazyOptional.cast();
//        }
//        return super.getCapability(cap, side);
//    }
//
//    @Override
//    public void invalidateCaps() {
//        super.invalidateCaps();
//        inventoryHandlerLazyOptional.invalidate();
//    }
}
