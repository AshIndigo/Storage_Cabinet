package com.ashindigo.storagecabinet.fabric;

import com.ashindigo.storagecabinet.StorageCabinet;
import net.fabricmc.api.ModInitializer;

public class StorageCabinetFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        StorageCabinet.init();
    }
}
