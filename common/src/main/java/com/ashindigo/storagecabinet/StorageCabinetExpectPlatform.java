package com.ashindigo.storagecabinet;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.inventory.Slot;

public class StorageCabinetExpectPlatform {

    @ExpectPlatform
    public static void setSlotY(Slot slot, int y) {
        throw new AssertionError(); // Shouldn't run
    }

}
