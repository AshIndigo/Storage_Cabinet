package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.client.CabinetManagerRenderer;
import com.ashindigo.storagecabinet.client.StorageCabinetRenderer;
import com.ashindigo.storagecabinet.client.screen.CabinetManagerScreen;
import com.ashindigo.storagecabinet.client.screen.StorageCabinetScreen;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;

public class StorageCabinetClient {

    public static void initClient() {
        BlockEntityRendererRegistry.register(StorageCabinet.CABINET_ENTITY.get(), StorageCabinetRenderer::new);
        BlockEntityRendererRegistry.register(StorageCabinet.CABINET_MANAGER_ENTITY.get(), CabinetManagerRenderer::new);
        MenuRegistry.registerScreenFactory(StorageCabinet.CABINET_CONTAINER.get(), StorageCabinetScreen::new);
        MenuRegistry.registerScreenFactory(StorageCabinet.MANAGER_CONTAINER.get(), CabinetManagerScreen::new);
    }
}
