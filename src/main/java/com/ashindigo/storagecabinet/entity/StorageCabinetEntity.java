package com.ashindigo.storagecabinet.entity;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.container.StorageCabinetContainer;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class StorageCabinetEntity extends TileEntity implements INamedContainerProvider {

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
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (!locked) {
                if (stacks.stream().allMatch(ItemStack::isEmpty) || stack.isEmpty()) {
                    return true;
                } else {
                    Collection<ResourceLocation> idList = getTagsFor(stack.getItem());
                    if (idList.isEmpty()) {
                        return IntStream.range(0, this.getSlots()).mapToObj(this::getStackInSlot).anyMatch(itemStack -> stack.getItem().equals(itemStack.getItem()) && itemStack.getCount() > 0);
                    } else {
                        for (ResourceLocation id : idList) {
                            ITag<Item> tag = ItemTags.getAllTags().getTagOrEmpty(id);
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
                        ITag<Item> itemTag = ItemTags.getAllTags().getTagOrEmpty(id);
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
    private ITextComponent customName;

    public StorageCabinetEntity() {
        super(StorageCabinet.CABINET_ENTITY.get());
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
        for (Map.Entry<ResourceLocation, ITag<Item>> entry : TagCollectionManager.getInstance().getItems().getAllTags().entrySet()) {
            if (entry.getValue().contains(object)) {
                list.add(entry.getKey());
            }
        }
        return list;
    }


    @Override
    public void load(BlockState state, CompoundNBT tag) {
        this.tier = tag.getInt("tier");
        //setTier(tier);
        if (tag.contains("inv")) {
            itemHandler.deserializeNBT(tag.getCompound("inv"));
        }
        super.load(state, tag);
        this.locked = tag.getBoolean("locked");
        this.item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(tag.getString("item")));
        if (tag.contains("CustomName", 8)) {
            this.customName = ITextComponent.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.putInt("tier", tier);
        tag.putBoolean("locked", locked);
        tag.putString("item", ForgeRegistries.ITEMS.getKey(item).toString());
        if (this.customName != null) {
            tag.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }
        tag.put("inv", itemHandler.serializeNBT());
        return tag;
    }

    public void setCustomName(ITextComponent text) {
        this.customName = text;
    }

    @Override
    public ITextComponent getDisplayName() {
        return customName != null ? customName : new TranslationTextComponent(level.getBlockState(getBlockPos()).getBlock().getDescriptionId());
    }

    @Override
    public Container createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new StorageCabinetContainer(syncId,inv, getBlockPos(), tier);
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
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        tag.putInt("tier", tier);
        tag.putBoolean("locked", locked);
        tag.putString("item", ForgeRegistries.ITEMS.getKey(item).toString());
        if (this.customName != null) {
            tag.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
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

    public void onClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
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
