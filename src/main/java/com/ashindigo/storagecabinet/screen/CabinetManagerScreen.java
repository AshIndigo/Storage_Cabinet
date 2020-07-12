package com.ashindigo.storagecabinet.screen;

import com.ashindigo.storagecabinet.WSlotCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.container.CabinetManagerContainer;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.ashindigo.storagecabinet.widgets.WPagedTabHolder;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import spinnery.client.screen.BaseHandledScreen;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;
import spinnery.widget.WTabHolder;
import spinnery.widget.WVerticalScrollableContainer;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

import java.util.ArrayList;

public class CabinetManagerScreen extends BaseHandledScreen<CabinetManagerContainer> {

    public CabinetManagerScreen(CabinetManagerContainer linkedContainer, PlayerInventory playerInv, Text name) {
        super(name, linkedContainer, playerInv.player);
        WInterface mainInterface = getInterface();
        WPagedTabHolder tabHolder = mainInterface.createChild(WPagedTabHolder::new, Position.of(mainInterface), Size.of((10 * 18) + 28, 162 + (5 * 18) + 18 + 18));
        tabHolder.center();
        ArrayList<StorageCabinetEntity> cabinetList = new ArrayList<>();
        final int[] i = {1};
        checkSurroundingCabinets(cabinetList, linkedContainer.managerEntity.getPos(), getHandler().getWorld());
        cabinetList.forEach(cabinetEntity -> {
            addCabinet(tabHolder, cabinetEntity, i[0]);
            i[0]++;
        });
        WSlot.addPlayerInventory(Position.of(tabHolder).add(6, 162 + (18 * 2), 1), Size.of(18, 18), mainInterface);
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

    private void addCabinet(WTabHolder tabHolder, StorageCabinetEntity cabinetEntity, int invNumb) {
        WTabHolder.WTab tab = tabHolder.addTab(cabinetEntity.isEmpty() ? Items.AIR : cabinetEntity.getMainItemStack().getItem());
        WVerticalScrollableContainer panel = tab.getBody().createChild(WVerticalScrollableContainer::new, Position.ofBottomLeft(tabHolder).add(4, -(162 + (5 * 18) + 8), 1), Size.of((10 * 18) + 18, 162));
        panel.setInterface(getInterface());
        Size size = Size.of(18, 18);
        Position position = Position.of(panel, 6, 1, 0);
        for (int y = 0; y < StorageCabinetBlock.Manager.getHeight(cabinetEntity.tier); ++y) {
            WSlotCabinet[] slotArr = new WSlotCabinet[StorageCabinetBlock.Manager.getWidth()];
            for (int x = 0; x < StorageCabinetBlock.Manager.getWidth(); ++x) {
                slotArr[x] = new WSlotCabinet().setInventoryNumber(invNumb).setSlotNumber(y * StorageCabinetBlock.Manager.getWidth() + x).setWhitelist().setOnMouseReleased((widget, mouseX, mouseY, mouseButton) -> tab.setSymbol(cabinetEntity.isEmpty() ? Items.AIR : cabinetEntity.getMainItemStack().getItem())).setPosition(position.add(size.getWidth() * x, size.getHeight() * y, 1)).setSize(size);
            }
            panel.addRow(slotArr);
        }
    }
}
