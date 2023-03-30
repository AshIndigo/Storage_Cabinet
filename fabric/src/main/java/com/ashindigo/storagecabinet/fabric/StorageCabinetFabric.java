package com.ashindigo.storagecabinet.fabric;

import net.fabricmc.api.ModInitializer;

public class StorageCabinetFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        StorageCabinetFabricLike.init();
    }
}
