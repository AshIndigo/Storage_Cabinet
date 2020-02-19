package com.ashindigo.storagecabinet;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import spinnery.common.BaseContainer;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;

public class StorageCabinetContainer extends BaseContainer {

    public static final int INVENTORY = 1;

    StorageCabinetEntity cabinetEntity;

    public StorageCabinetContainer(int synchronizationID, PlayerInventory playerInventory, BlockPos pos, int arrayWidth, int arrayHeight, int m) {
        super(synchronizationID, playerInventory);
        cabinetEntity = ((StorageCabinetEntity) getWorld().getBlockEntity(pos));
        WInterface mainInterface = getInterface();
        getInventories().put(INVENTORY, cabinetEntity);
        cabinetEntity.addListener(this::onContentChanged);
        //WSlot.addHeadlessArray(mainInterface, 0, INVENTORY, x, y);
        for (int y = 0; y < arrayHeight; ++y) {
            for (int x = 0; x < arrayWidth; ++x) {
                mainInterface.createChild(WSlotCabinet.class).setSlotNumber(y * arrayWidth + x).setInventoryNumber(INVENTORY).setWhitelist();
            }
        }
        WSlot.addHeadlessPlayerInventory(mainInterface);
    }
}
