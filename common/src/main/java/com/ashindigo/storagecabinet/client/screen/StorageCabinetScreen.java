package com.ashindigo.storagecabinet.client.screen;

import com.ashindigo.storagecabinet.container.StorageCabinetContainer;
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

}
