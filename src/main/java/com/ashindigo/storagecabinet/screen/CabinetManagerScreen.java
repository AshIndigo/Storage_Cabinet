package com.ashindigo.storagecabinet.screen;

import com.ashindigo.storagecabinet.description.CabinetManagerDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class CabinetManagerScreen extends CottonInventoryScreen<CabinetManagerDescription> {

    public CabinetManagerScreen(CabinetManagerDescription description, PlayerInventory playerInv, Text title) {
        super(description, playerInv.player, title);
    }
}
