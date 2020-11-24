package com.ashindigo.storagecabinet.description;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;

public class StorageCabinetDescription extends SyncedGuiDescription {

    public final StorageCabinetEntity cabinetEntity;

    public StorageCabinetDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext ctx) {
        super(StorageCabinet.cabinetScreenHandler, syncId, playerInventory, getBlockInventory(ctx), getBlockPropertyDelegate(ctx));
        cabinetEntity = (StorageCabinetEntity) blockInventory;
        cabinetEntity.onOpen(playerInventory.player);
        cabinetEntity.addListener(this::onContentChanged);
        setTitleVisible(true);
        WPlainPanel root = new WPlainPanel();
        root.setSize(166, 270);
        WItemSlot itemSlot = new WItemSlot(cabinetEntity, 0, StorageCabinetBlock.Manager.getWidth(), StorageCabinetBlock.Manager.getHeight(cabinetEntity.tier), false).setFilter(stack -> cabinetEntity.isValid(0, stack));
        WScrollPanel scrollPanel = new WScrollPanel(itemSlot);
        scrollPanel.setScrollingVertically(TriState.TRUE);
        scrollPanel.setScrollingHorizontally(TriState.FALSE);
        root.add(scrollPanel,0 , 10);
        root.add(new WPlayerInvPanel(playerInventory, true), 0, 194);
        setRootPanel(root);
        root.validate(this);
        scrollPanel.setSize(172, 180);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        cabinetEntity.onClose(player);
        cabinetEntity.clearListeners();
    }
}
