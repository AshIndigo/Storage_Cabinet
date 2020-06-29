package com.ashindigo.storagecabinet;

import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import spinnery.client.screen.BaseContainerScreen;
import spinnery.widget.WInterface;
import spinnery.widget.WPanel;
import spinnery.widget.WSlot;
import spinnery.widget.WVerticalScrollableContainer;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

import static com.ashindigo.storagecabinet.StorageCabinetContainer.INVENTORY;

public class StorageCabinetScreen extends BaseContainerScreen<StorageCabinetContainer> implements ScreenHandlerProvider<StorageCabinetContainer> {

    public StorageCabinetScreen(StorageCabinetContainer linkedContainer, PlayerInventory playerInv, Text name) {
        super(name, linkedContainer, playerInv.player);
        WInterface mainInterface = getInterface();
        WPanel mainPanel = mainInterface.createChild(WPanel::new, Position.of(mainInterface), Size.of((9 * 18) + 24, 162 + (5 * 18) + 18));
        mainPanel.center();
        mainPanel.setLabel(name);
        WVerticalScrollableContainer panel = mainInterface.createChild(WVerticalScrollableContainer::new, Position.of(mainPanel).add(0, 18, 1), Size.of((9 * 18) + 18, 162));
        Size size = Size.of(18, 18);
        Position position = Position.of(panel, 6, 1, 1);
        for (int y = 0; y < linkedContainer.arrayHeight; ++y) {
            for (int x = 0; x < linkedContainer.arrayWidth; ++x) {
                panel.createChild(WSlotCabinet::new, position.add(size.getWidth() * x, size.getHeight() * y, 1), size).setSlotNumber(y * linkedContainer.arrayWidth + x).setWhitelist().setInventoryNumber(INVENTORY);
            }
        }
        WSlot.addPlayerInventory(Position.of(mainPanel).add(6, 162 + 27, 1), Size.of(18, 18), mainPanel);
    }


}
