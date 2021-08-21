package com.ashindigo.storagecabinet.forge;

import com.ashindigo.storagecabinet.Constants;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.StorageCabinetClient;
import dev.architectury.hooks.block.BlockEntityHooks;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MODID)
public class StorageCabinetForge {

    public StorageCabinetForge() {
        EventBuses.registerModEventBus(Constants.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        StorageCabinet.init();
        StorageCabinet.CABINET_ENTITY = StorageCabinet.TILE_ENTITIES.register(Constants.MODID, () -> BlockEntityHooks.builder(StorageCabinetEntityForge::new, StorageCabinet.WOOD_CABINET.get(), StorageCabinet.IRON_CABINET.get(), StorageCabinet.GOLD_CABINET.get(), StorageCabinet.DIAMOND_CABINET.get(), StorageCabinet.EMERALD_CABINET.get()).build(null));
        StorageCabinet.CABINET_MANAGER_ENTITY = StorageCabinet.TILE_ENTITIES.register("cabinet_manager", () -> BlockEntityHooks.builder(CabinetManagerEntityForge::new, StorageCabinet.CABINET_MANAGER.get()).build(null));
        StorageCabinet.TILE_ENTITIES.register();
    }

    @SubscribeEvent
    public void registerClient(FMLClientSetupEvent event) {
        StorageCabinetClient.initClient();
    }
}
