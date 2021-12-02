package com.ashindigo.storagecabinet.entity;

import com.ashindigo.storagecabinet.Constants;
import com.ashindigo.storagecabinet.DisplayHeight;
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

Miimport java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StorageCabinetEntity extends BlockEntity implements MenuProvider, BasicSidedInventory, ModifiableDisplaySize {

    public boolean locked = false;
    public int tier = 0;
    private Item item = Items.AIR;
    private int viewerCount;
    private Component customName;
    private NonNullList<ItemStack> items;
    private DisplayHeight displayHeight = Constants.DEFAULT_HEIGHT;
    private ItemStack cachedStack = ItemStack.EMPTY;
    private List<ResourceLocation> cachedTags = new ArrayList<>();

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

    public List<ResourceLocation> getTagsFor(Item object) {
        /*
        Notes:
        Cache for tags, looks like I might as well set a list of cached tags. That I can use to see if an item is compatible.
        Would probably have to call this atleast once to gather them all but once is better than repeated useage right?
         */
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
        this.tier = tag.getInt(Constants.TIER);
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
        this.locked = tag.getBoolean(Constants.LOCKED);
        this.setItem(Registry.ITEM.get(ResourceLocation.tryParse(tag.getString(Constants.ITEM))));
        if (tag.contains(Constants.CUSTOM_NAME, 8)) {
            this.customName = Component.Serializer.fromJson(tag.getString(Constants.CUSTOM_NAME));
        }
        setDisplayHeight(DisplayHeight.values()[tag.getInt(Constants.DISPLAY_SIZE)]);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        prepareTag(tag);
        return ContainerHelper.saveAllItems(tag, items);
    }

    private void prepareTag(CompoundTag tag) {
        tag.putInt(Constants.TIER, tier);
        tag.putBoolean(Constants.LOCKED, locked);
        tag.putString(Constants.ITEM, Registry.ITEM.getKey(getItem()).toString());
        if (this.customName != null) {
            tag.putString(Constants.CUSTOM_NAME, Component.Serializer.toJson(this.customName));
        }
        tag.putInt(Constants.DISPLAY_SIZE, getDisplayHeight().ordinal());
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

    @Override
    public boolean canPlaceItem(int i, ItemStack stack) {
        getMainItemStack();
        if ((isEmpty() || stack.isEmpty()) && !locked) { // If the inventory is empty, or the stack is empty, and it is not locked
            return true;
        }
        if (stack.getItem().equals(getItem())) {
            return true;
        }

        Collection<ResourceLocation> idList = getCachedTags();
        if (!idList.isEmpty()) {
            for (ResourceLocation id : idList) {
                Tag<Item> itemTag = ItemTags.getAllTags().getTagOrEmpty(id);
                if (itemTag.contains(stack.getItem())) {
                    return true;
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
        prepareTag(tag);
        return tag;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void setDisplayHeight(DisplayHeight displayHeight) {
        this.displayHeight = displayHeight;
    }

    @Override
    public DisplayHeight getDisplayHeight() {
        return displayHeight;
    }

    @Override
    public void setChanged() { // TODO Further optimize this, and getMainItemStack. I need to set the stack more effectively
        super.setChanged();
        if (!locked) {
            setItem(items.stream().filter(stack -> !stack.isEmpty()).findAny().orElse(ItemStack.EMPTY).getItem());
            setCachedStack(new ItemStack(getItem()));
        }
    }

    // NOT FOR EDITING
    public ItemStack getMainItemStack() {
        if (isEmpty() && !locked) {
            setItem(Items.AIR);
            setCachedStack(ItemStack.EMPTY);
        } else {
            if (getItem() == Items.AIR) {
                setItem(items.stream().filter(stack -> !stack.isEmpty()).findAny().orElse(ItemStack.EMPTY).getItem());
                setCachedStack(new ItemStack(getItem()));
            }
        }

        return getCachedStack();
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

    public void onClose(Player player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
        }
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        setCachedTags(getTagsFor(item));
    }

    public ItemStack getCachedStack() {
        return cachedStack;
    }

    public void setCachedStack(ItemStack cachedStack) {
        this.cachedStack = cachedStack;
    }

    /**
     *
     * @return Tag's for the current main item in the cabinet
     */
    public List<ResourceLocation> getCachedTags() {
        return cachedTags;
    }

    public void setCachedTags(List<ResourceLocation> cachedTags) {
        this.cachedTags = cachedTags;
    }
}
