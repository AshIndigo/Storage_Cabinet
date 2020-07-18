package com.ashindigo.storagecabinet.entity;

import com.ashindigo.storagecabinet.StorageCabinet;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;

public class CabinetManagerEntity extends BlockEntity {

    public CabinetManagerEntity() {
        super(StorageCabinet.cabinetManagerEntity);
    }
}
