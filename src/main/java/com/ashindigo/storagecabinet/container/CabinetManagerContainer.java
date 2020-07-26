package com.ashindigo.storagecabinet.container;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.widgets.WSlotCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import spinnery.common.handler.BaseScreenHandler;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;

import java.util.ArrayList;

public class CabinetManagerContainer extends BaseScreenHandler {

    public final CabinetManagerEntity managerEntity;

    public CabinetManagerContainer(int synchronizationID, PlayerInventory playerInventory, BlockPos pos) {
        super(synchronizationID, playerInventory);
        WInterface mainInterface = getInterface();
        WSlot.addHeadlessPlayerInventory(mainInterface);
        managerEntity = (CabinetManagerEntity) world.getBlockEntity(pos);
        ArrayList<StorageCabinetEntity> cabinetList = new ArrayList<>();
        final int[] i = {1};
        checkSurroundingCabinets(cabinetList, managerEntity.getPos(), getWorld());
        cabinetList.forEach(cabinetEntity -> {
            addCabinet(mainInterface, cabinetEntity, i[0]);
            i[0]++;
        });

    }

    private void addCabinet(WInterface mainInterface, StorageCabinetEntity cabinetEntity, int i) {
        getInventories().put(i, cabinetEntity);
        cabinetEntity.addListener(sender -> sendContentUpdates()); // TODO Useless?
        cabinetEntity.addListener(this::onContentChanged);
        for (int y = 0; y < StorageCabinetBlock.Manager.getHeight(cabinetEntity.tier); ++y) {
            for (int x = 0; x < StorageCabinetBlock.Manager.getWidth(); ++x) {
                mainInterface.createChild(WSlotCabinet::new).setSlotNumber(y * StorageCabinetBlock.Manager.getWidth() + x).setInventoryNumber(i).setWhitelist().setInterface(mainInterface);
            }
        }
    }

    private void checkSurroundingCabinets(ArrayList<StorageCabinetEntity> cabinetList, BlockPos pos, World world) {
        for (Direction direction : Direction.values()) {
            if (world.getBlockEntity(pos.offset(direction)) instanceof StorageCabinetEntity) {
                if (!cabinetList.contains(world.getBlockEntity(pos.offset(direction)))) {
                    cabinetList.add((StorageCabinetEntity) world.getBlockEntity(pos.offset(direction)));
                    checkSurroundingCabinets(cabinetList, pos.offset(direction), world);
                }
            }
        }
    }


    @Override
    public ScreenHandlerType<?> getType() {
        return StorageCabinet.managerScreenHandler;
    }
}
