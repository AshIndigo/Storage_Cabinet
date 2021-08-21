package com.ashindigo.storagecabinet.client.screen;

import com.ashindigo.storagecabinet.container.StorageCabinetContainer;
import com.ashindigo.storagecabinet.networking.SizeChangeMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class StorageCabinetScreen extends AbstractStorageCabinetScreen<StorageCabinetContainer> {

    public StorageCabinetScreen(StorageCabinetContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void scrollMenu(float pos) {
        menu.scrollTo(pos, menu.entity);
    }

    @Override
    public void changeDisplaySize() {
        super.changeDisplaySize();
        if (menu.entity.getBlockPos() != null) {
            new SizeChangeMessage(selectedHeight, menu.entity.getBlockPos()).sendToServer();
        }
    }
}
