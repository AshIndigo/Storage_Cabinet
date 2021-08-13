package com.ashindigo.storagecabinet.container;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class StorageCabinetContainer extends AbstractContainerMenu {

    final StorageCabinetEntity entity;
    final IItemHandler inv;
    private final int tier;

    public StorageCabinetContainer(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
        this(windowId, playerInv, buf.readBlockPos(), buf.readInt());
    }

    public StorageCabinetContainer(int syncId, Inventory playerInv, BlockPos blockPos, int tier) {
        super(StorageCabinet.CABINET_CONTAINER.get(), syncId);
        this.tier = tier;
        entity = (StorageCabinetEntity) playerInv.player.level.getBlockEntity(blockPos);
        entity.startOpen();
        inv = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH).orElseThrow(() -> new NullPointerException("Source Capability was not present!"));
        for (int i = 0; i < StorageCabinetBlock.getHeight(tier); ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new SlotItemHandler(inv, i * 9 + j, 9 + j * 18, 18 + i * 18) {
                    @Override
                    public void setChanged() {
                        super.setChanged();
                        entity.setChanged();
                    }

                    @Override
                    public boolean isActive() {
                        return this.y < 91 && this.y > 0 && x < 154 && x > 0;
                    }
                });
            }
        }

        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInv, j1 + l * 9 + 9, 9 + j1 * 18, 118 + l * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, 9 + k * 18, 176)); // 112 orig
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();

            int containerSlots = slots.size() - player.getInventory().items.size();

            if (index < containerSlots) {
                if (!this.moveItemStackTo(itemStack1, containerSlots, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack1, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack1.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack1);
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        entity.onClose(player);
    }

    public void scrollTo(float pos) {
        int i = (inv.getSlots() + 9 - 1) / 9 - 5; // 25.8888888889 for 270 slots
        int j = (int) ((double) (pos * (float) i) + 0.5D);

        if (j < 0) {
            j = 0;
        }

        // Iterate through all slots
        for (int y = 0; y < StorageCabinetBlock.getHeight(tier); ++y) {
            for (int x = 0; x < 9; ++x) {
                if (j == 0) {
                    slots.get(y * 9 + x).y = 18 + y * 18; // Orig 18 + y * 18
                } else {
                    slots.get(y * 9 + x).y = 18 + (y - j) * 18; // Orig 18 + (y * j) * 18
                }

            }
        }
    }
}
