package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;

public class Helpers {

    public static void checkSurroundingCabinets(ArrayList<StorageCabinetEntity> cabinetList, BlockPos pos, WorldAccess world) {
        for (Direction direction : Direction.values()) {
            if (world.getBlockEntity(pos.offset(direction)) instanceof StorageCabinetEntity) {
                if (!cabinetList.contains(world.getBlockEntity(pos.offset(direction)))) {
                    cabinetList.add((StorageCabinetEntity) world.getBlockEntity(pos.offset(direction)));
                    checkSurroundingCabinets(cabinetList, pos.offset(direction), world);
                }
            }
        }
    }
}
