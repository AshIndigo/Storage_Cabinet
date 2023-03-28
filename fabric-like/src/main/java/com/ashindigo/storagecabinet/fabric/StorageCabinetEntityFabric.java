package com.ashindigo.storagecabinet.fabric;

import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class StorageCabinetEntityFabric extends StorageCabinetEntity implements SidedStorageBlockEntity {
    public StorageCabinetEntityFabric(BlockPos pos, BlockState state) {
        super(pos, state);
    }
}
