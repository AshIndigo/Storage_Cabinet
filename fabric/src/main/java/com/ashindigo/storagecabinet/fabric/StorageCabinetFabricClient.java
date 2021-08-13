package com.ashindigo.storagecabinet.fabric;

import com.ashindigo.storagecabinet.StorageCabinetClient;
import net.fabricmc.api.ClientModInitializer;

public class StorageCabinetFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        StorageCabinetClient.initClient();
    }
}
