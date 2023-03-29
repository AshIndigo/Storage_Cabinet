package com.ashindigo.storagecabinet.fabric;

import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CabinetManagerEntityFabric extends CabinetManagerEntity {

    public CabinetManagerEntityFabric(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    private final List<Storage<ItemVariant>> combList = new ArrayList<>();

    public final CombinedStorage<ItemVariant,Storage<ItemVariant>> combinedStorage = new CombinedStorage<>(combList);


    @Override
    public void updateCabinetList() {
        combList.clear();
        super.updateCabinetList();
        for (StorageCabinetEntity entity : cabinetList) {
            if (entity instanceof StorageCabinetEntityFabric fabCab) {
                combList.add(fabCab.getStorage());
            }
        }
        combinedStorage.parts = combList;
    }

    public Storage<ItemVariant> getStorage() {
        return combinedStorage;
    }
}
