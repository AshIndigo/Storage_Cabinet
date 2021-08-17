package com.ashindigo.storagecabinet.container;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.StorageCabinetExpectPlatform;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StorageCabinetContainer extends AbstractStorageCabinetContainer {

    public final StorageCabinetEntity entity;
    private final int tier;

    public StorageCabinetContainer(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
        this(windowId, playerInv, buf.readBlockPos(), buf.readInt());
    }

    public StorageCabinetContainer(int syncId, Inventory playerInv, BlockPos blockPos, int tier) {
        super(StorageCabinet.CABINET_CONTAINER.get(), syncId);
        this.tier = tier;
        entity = (StorageCabinetEntity) playerInv.player.level.getBlockEntity(blockPos);
        entity.startOpen();
        for (int i = 0; i < StorageCabinetBlock.getHeight(tier); ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(entity, i * 9 + j, 9 + j * 18, 18 + i * 18) {
                    @Override
                    public boolean isActive() {
                        return this.y < 91 && this.y > 0 && x < 154 && x > 0;
                    }

                    @Override
                    public boolean mayPlace(ItemStack itemStack) {
                        return entity.canPlaceItem(index, itemStack);
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
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        entity.onClose(player);
    }

    @Override
    public void scrollTo(float pos, StorageCabinetEntity entity) {
        int i = (this.entity.getContainerSize() + 9 - 1) / 9 - 5; // 25.8888888889 for 270 slots
        int j = (int) ((double) (pos * (float) i) + 0.5D);

        if (j < 0) {
            j = 0;
        }

        // Iterate through all slots
        for (int y = 0; y < StorageCabinetBlock.getHeight(tier); ++y) {
            for (int x = 0; x < StorageCabinetBlock.getWidth(); ++x) {
                if (j == 0) {
                    StorageCabinetExpectPlatform.setSlotY(slots.get(y * 9 + x), 18 + y * 18);
                } else {
                    StorageCabinetExpectPlatform.setSlotY(slots.get(y * 9 + x), 18 + (y - j) * 18);
                }

            }
        }
    }
}
