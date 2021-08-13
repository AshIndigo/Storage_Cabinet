package com.ashindigo.storagecabinet.container;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;

public class CabinetManagerContainer extends AbstractContainerMenu {

    public final ArrayList<StorageCabinetEntity> cabinetList = new ArrayList<>();
    public final ListMultimap<StorageCabinetEntity, ExtraSlotItemHandler> CABINET_SLOT_LIST = ArrayListMultimap.create();

    public CabinetManagerContainer(int syncId, Inventory inv, FriendlyByteBuf buf) {
        this(syncId, inv, buf.readBlockPos());
    }

    public CabinetManagerContainer(int syncId, Inventory playerInv, BlockPos blockPos) {
        super(StorageCabinet.MANAGER_CONTAINER.get(), syncId);
        checkSurroundingCabinets(cabinetList, blockPos, playerInv.player.level);
        // Player Inv stuff
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
    public boolean stillValid(Player p_75145_1_) {
        return true;
    }

    private void checkSurroundingCabinets(ArrayList<StorageCabinetEntity> cabinetList, BlockPos pos, Level world) {
        for (Direction direction : Direction.values()) {
            BlockPos offsetPos = pos.offset(direction.getNormal());
            BlockEntity entity = world.getBlockEntity(offsetPos);

            if (entity instanceof StorageCabinetEntity cabinetEntity) {
                if (!cabinetList.contains(cabinetEntity)) {
                    cabinetList.add(cabinetEntity);
                    for (int i = 0; i < StorageCabinetBlock.getHeight(cabinetEntity.tier); ++i) {
                        for (int j = 0; j < 9; ++j) {
                            CABINET_SLOT_LIST.put(cabinetEntity, (ExtraSlotItemHandler) this.addSlot(new ExtraSlotItemHandler(cabinetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH).orElseThrow(() -> new NullPointerException("Source Capability was not present!")), i * 9 + j, 9 + j * 18, 18 + i * 18, entity)));
                        }
                    }
                    checkSurroundingCabinets(cabinetList, offsetPos, world);
                }
            }
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
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

    public void scrollTo(float pos, int id) {
        if (!(cabinetList.size() > id)) return;
        StorageCabinetEntity cabinetEntity = cabinetList.get(id);
        IItemHandler inv = cabinetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.NORTH).orElseThrow(() -> new NullPointerException("Source Capability was not present!"));
        int i = (inv.getSlots() + 9 - 1) / 9 - 5;
        int j = (int) ((double) (pos * (float) i) + 0.5D);
        if (j < 0) {
            j = 0;
        }

        // Iterate through all slots
        for (int y = 0; y < StorageCabinetBlock.getHeight(cabinetEntity.tier); ++y) {
            for (int x = 0; x < 9; ++x) {
                if (j == 0) {
                    CABINET_SLOT_LIST.get(cabinetEntity).get(y * 9 + x).y = 18 + y * 18; // Orig 18 + y * 18
                } else {
                    CABINET_SLOT_LIST.get(cabinetEntity).get(y * 9 + x).y = 18 + (y - j) * 18; // Orig 18 + (y * j) * 18
                }
            }
        }
    }

    public void setEnabledTab(int selectedTab) {
        for (Slot slot : slots) {
            if (slot instanceof ExtraSlotItemHandler) {
                ((ExtraSlotItemHandler) slot).setEnabled(false);
            }
        }
        if (!(cabinetList.size() > selectedTab)) return;
        StorageCabinetEntity entity = cabinetList.get(selectedTab);
        for (ExtraSlotItemHandler slot : CABINET_SLOT_LIST.get(entity)) {
            slot.setEnabled(true);
        }
    }

    static class ExtraSlotItemHandler extends SlotItemHandler {

        private final BlockEntity entity;
        private boolean enabled;

        public ExtraSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, BlockEntity entity) {
            super(itemHandler, index, xPosition, yPosition);
            this.entity = entity;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            entity.setChanged();
        }

        @Override
        public boolean isActive() {
            return isEnabled() && this.y < 91 && this.y > 0 && x < 154 && x > 0;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
