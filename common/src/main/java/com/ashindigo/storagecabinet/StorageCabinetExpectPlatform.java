package com.ashindigo.storagecabinet;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class StorageCabinetExpectPlatform {

    @ExpectPlatform
    public static void registerMenuType() {
        throw new AssertionError(); // Shouldn't run
    }
}
