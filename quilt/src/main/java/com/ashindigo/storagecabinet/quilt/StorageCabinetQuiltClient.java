package com.ashindigo.storagecabinet.quilt;

import com.ashindigo.storagecabinet.StorageCabinetClient;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class StorageCabinetQuiltClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {
        StorageCabinetClient.initClient();
    }
}
