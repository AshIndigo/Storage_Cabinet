package com.ashindigo.storagecabinet.container;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.WSlotCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import spinnery.common.handler.BaseScreenHandler;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;

public class StorageCabinetContainer extends BaseScreenHandler {

    public static final int INVENTORY = 1;

    final StorageCabinetEntity cabinetEntity;
    public final int arrayHeight;
    public final int arrayWidth; // Isn't it always 9.

    public StorageCabinetContainer(int synchronizationID, PlayerInventory playerInventory, BlockPos pos) {
        super(synchronizationID, playerInventory);
        cabinetEntity = ((StorageCabinetEntity) getWorld().getBlockEntity(pos));
        WInterface mainInterface = getInterface();
        getInventories().put(INVENTORY, cabinetEntity);
        cabinetEntity.addListener(this::onContentChanged);
        arrayHeight = StorageCabinetBlock.Manager.getHeight(cabinetEntity.tier);
        arrayWidth = StorageCabinetBlock.Manager.getWidth();
        for (int y = 0; y < arrayHeight; ++y) {
            for (int x = 0; x < arrayWidth; ++x) {
                mainInterface.createChild(WSlotCabinet::new).setSlotNumber(y * arrayWidth + x).setInventoryNumber(INVENTORY).setWhitelist();
            }
        }
        WSlot.addHeadlessPlayerInventory(mainInterface);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return StorageCabinet.cabinetScreenHandler;
    }
}
