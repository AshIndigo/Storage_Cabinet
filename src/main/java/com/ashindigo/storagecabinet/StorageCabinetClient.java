package com.ashindigo.storagecabinet;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class StorageCabinetClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(StorageCabinet.cabinetScreenHandler, StorageCabinetScreen::new);
    }
}
