package com.ashindigo.storagecabinet.quilt;

import com.ashindigo.storagecabinet.fabric.StorageCabinetFabricLike;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class StorageCabinetQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        StorageCabinetFabricLike.init();
    }
}
