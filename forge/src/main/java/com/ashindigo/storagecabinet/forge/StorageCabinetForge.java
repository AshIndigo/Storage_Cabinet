package com.ashindigo.storagecabinet.forge;

import com.ashindigo.storagecabinet.StorageCabinet;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(StorageCabinet.MODID)
public class StorageCabinetForge {

    public StorageCabinetForge() {
        EventBuses.registerModEventBus(StorageCabinet.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        StorageCabinet.init();
    }
}
