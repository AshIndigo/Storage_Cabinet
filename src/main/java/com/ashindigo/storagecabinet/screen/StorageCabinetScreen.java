package com.ashindigo.storagecabinet.screen;

import com.ashindigo.storagecabinet.description.StorageCabinetDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class StorageCabinetScreen extends CottonInventoryScreen<StorageCabinetDescription> {

    public StorageCabinetScreen(StorageCabinetDescription description, PlayerInventory playerInv, Text title) {
        super(description, playerInv.player, title);
    }
}
