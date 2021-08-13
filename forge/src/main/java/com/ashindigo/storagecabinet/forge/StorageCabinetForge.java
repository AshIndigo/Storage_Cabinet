package com.ashindigo.storagecabinet.forge;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.StorageCabinetClient;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(StorageCabinet.MODID)
public class StorageCabinetForge {

    public StorageCabinetForge() {
        EventBuses.registerModEventBus(StorageCabinet.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        StorageCabinet.init();
    }

    @SubscribeEvent
    public void registerClient(FMLClientSetupEvent event) {
        StorageCabinetClient.initClient();
    }
}
