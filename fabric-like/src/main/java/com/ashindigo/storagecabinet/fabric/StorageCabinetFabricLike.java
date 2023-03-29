package com.ashindigo.storagecabinet.fabric;

import com.ashindigo.storagecabinet.Constants;
import com.ashindigo.storagecabinet.StorageCabinet;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class StorageCabinetFabricLike {

    public static void init() {
        StorageCabinet.init();
        StorageCabinet.CABINET_ENTITY = StorageCabinet.TILE_ENTITIES.register(Constants.MODID, () -> BlockEntityType.Builder.of(StorageCabinetEntityFabric::new, StorageCabinet.WOOD_CABINET.get(), StorageCabinet.IRON_CABINET.get(), StorageCabinet.GOLD_CABINET.get(), StorageCabinet.DIAMOND_CABINET.get(), StorageCabinet.EMERALD_CABINET.get()).build(null));
        StorageCabinet.CABINET_MANAGER_ENTITY = StorageCabinet.TILE_ENTITIES.register("cabinet_manager", () -> BlockEntityType.Builder.of(CabinetManagerEntityFabric::new, StorageCabinet.CABINET_MANAGER.get()).build(null));
        StorageCabinet.TILE_ENTITIES.register();
        ItemStorage.SIDED.registerForBlockEntities((blockEntity, context) -> {
            if (blockEntity instanceof StorageCabinetEntityFabric cabinet) {
                return cabinet.getStorage();
            }
            return null;
        }, StorageCabinet.CABINET_ENTITY.get());
        ItemStorage.SIDED.registerForBlockEntities((blockEntity, context) -> {
            if (blockEntity instanceof CabinetManagerEntityFabric cabinet) {
                return cabinet.getStorage();
            }
            return null;
        }, StorageCabinet.CABINET_MANAGER_ENTITY.get());
    }
}
