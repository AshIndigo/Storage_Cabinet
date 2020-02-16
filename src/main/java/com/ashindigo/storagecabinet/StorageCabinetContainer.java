package com.ashindigo.storagecabinet;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import spinnery.common.BaseContainer;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;

public class StorageCabinetContainer extends BaseContainer {

    public static final int INVENTORY = 1;

    StorageCabinetEntity cabinetEntity;

    public StorageCabinetContainer(int synchronizationID, PlayerInventory playerInventory, BlockPos pos, int x, int y, int m) {
        super(synchronizationID, playerInventory);
        cabinetEntity = ((StorageCabinetEntity) getWorld().getBlockEntity(pos));
        WInterface mainInterface = getInterface();
        getInventories().put(INVENTORY, cabinetEntity);
        cabinetEntity.addListener(this::onContentChanged);
        WSlot.addHeadlessArray(mainInterface, 0, INVENTORY, x, y);
        WSlot.addHeadlessPlayerInventory(mainInterface);
    }
}
