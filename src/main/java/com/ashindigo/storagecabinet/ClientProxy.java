package com.ashindigo.storagecabinet;

import net.minecraft.client.gui.ScreenManager;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerGui() {
        ScreenManager.registerFactory(StorageCabinetMod.cabinetType, GuiStorageCabinet::new);
    }
}
