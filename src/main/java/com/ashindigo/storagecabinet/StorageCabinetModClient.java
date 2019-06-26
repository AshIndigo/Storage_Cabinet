package com.ashindigo.storagecabinet;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.util.Identifier;

public class StorageCabinetModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(StorageCabinetMod.modid, StorageCabinetMod.modid), (syncId, identifier, player, buf) -> new GuiStorageCabinet(new ContainerStorageCabinet(syncId, player.inventory, (TileEntityStorageCabinet) player.world.getBlockEntity(buf.readBlockPos())), player.inventory));
    }
}
