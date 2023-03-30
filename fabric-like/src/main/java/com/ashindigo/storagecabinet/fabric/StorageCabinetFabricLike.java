package com.ashindigo.storagecabinet.fabric;

import com.ashindigo.storagecabinet.Constants;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;

public class StorageCabinetFabricLike {

    public static void init() {
        StorageCabinet.init();
        StorageCabinet.CABINET_ENTITY = StorageCabinet.TILE_ENTITIES.register(Constants.MODID, () -> BlockEntityType.Builder.of(StorageCabinetEntity::new, StorageCabinet.WOOD_CABINET.get(), StorageCabinet.IRON_CABINET.get(), StorageCabinet.GOLD_CABINET.get(), StorageCabinet.DIAMOND_CABINET.get(), StorageCabinet.EMERALD_CABINET.get()).build(null));
        StorageCabinet.CABINET_MANAGER_ENTITY = StorageCabinet.TILE_ENTITIES.register("cabinet_manager", () -> BlockEntityType.Builder.of(CabinetManagerEntity::new, StorageCabinet.CABINET_MANAGER.get()).build(null));
        StorageCabinet.TILE_ENTITIES.register();
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, context) -> InventoryStorage.of(blockEntity, null), StorageCabinet.CABINET_ENTITY.get());
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, context) -> {
            List<Storage<ItemVariant>> combList = new ArrayList<>();
            for (StorageCabinetEntity entity : blockEntity.cabinetList) {
                combList.add(InventoryStorage.of(entity, null));
            }
            return new CombinedStorage<>(combList);
        }, StorageCabinet.CABINET_MANAGER_ENTITY.get());
    }
}
