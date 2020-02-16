package com.ashindigo.storagecabinet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import spinnery.common.BaseContainerScreen;
import spinnery.widget.WInterface;
import spinnery.widget.WPanel;
import spinnery.widget.WSlot;
import spinnery.widget.WVerticalScrollableContainer;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;

public class StorageCabinetScreen extends BaseContainerScreen<StorageCabinetContainer> {

    public StorageCabinetScreen(Text name, StorageCabinetContainer linkedContainer, PlayerEntity player, int x, int y) {
        super(name, linkedContainer, player);
        WInterface mainInterface = getInterface();
        WPanel mainPanel = mainInterface.createChild(WPanel.class, Position.of(mainInterface), Size.of((9 * 18) + 24, 162 + (5 * 18) + 18));
        mainPanel.center();
        mainPanel.setLabel(name);
        WVerticalScrollableContainer panel = mainInterface.createChild(WVerticalScrollableContainer.class, Position.of(mainPanel).add(0, 18, 1), Size.of((9 * 18) + 18, 162));
        WSlot.addPlayerInventory(Position.of(mainPanel).add(6, 162 + 27, 1), Size.of(18, 18), mainPanel);
        WSlot.addArray(Position.of(panel, 6, 1, 1), Size.of(18, 18), panel, 0, StorageCabinetContainer.INVENTORY, x, y);
    }
}
