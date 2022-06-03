package com.ashindigo.storagecabinet.fabric;

import com.ashindigo.storagecabinet.Constants;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class StorageCabinetFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        StorageCabinet.init();
        StorageCabinet.CABINET_ENTITY = StorageCabinet.TILE_ENTITIES.register(Constants.MODID, () -> BlockEntityType.Builder.of(StorageCabinetEntity::new, StorageCabinet.WOOD_CABINET.get(), StorageCabinet.IRON_CABINET.get(), StorageCabinet.GOLD_CABINET.get(), StorageCabinet.DIAMOND_CABINET.get(), StorageCabinet.EMERALD_CABINET.get()).build(null));
        StorageCabinet.CABINET_MANAGER_ENTITY = StorageCabinet.TILE_ENTITIES.register("cabinet_manager", () -> BlockEntityType.Builder.of(CabinetManagerEntity::new, StorageCabinet.CABINET_MANAGER.get()).build(null));
        StorageCabinet.TILE_ENTITIES.register();
    }
}
