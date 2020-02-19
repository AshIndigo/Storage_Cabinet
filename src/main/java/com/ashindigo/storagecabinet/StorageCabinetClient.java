package com.ashindigo.storagecabinet;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.ashindigo.storagecabinet.StorageCabinet.MODID;

public class StorageCabinetClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, MODID),
                (id, identifier, player, buf) -> {
                    BlockPos pos = buf.readBlockPos();
                    int x = buf.readInt();
                    int y = buf.readInt();
                    int m = buf.readInt();
                    Text text = buf.readText();
                    return new StorageCabinetScreen(text, new StorageCabinetContainer(id, player.inventory, pos, x, y, m), player, x, y);
                });
    }
}
