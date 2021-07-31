package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.client.StorageCabinetRenderer;
import com.ashindigo.storagecabinet.screen.CabinetManagerScreen;
import com.ashindigo.storagecabinet.screen.StorageCabinetScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class StorageCabinetClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(StorageCabinet.cabinetScreenHandler, StorageCabinetScreen::new);
        ScreenRegistry.register(StorageCabinet.managerScreenHandler, CabinetManagerScreen::new);
        BlockEntityRendererRegistry.INSTANCE.register(StorageCabinet.STORAGE_CABINET_ENTITY, ctx -> new StorageCabinetRenderer());
    }
}
