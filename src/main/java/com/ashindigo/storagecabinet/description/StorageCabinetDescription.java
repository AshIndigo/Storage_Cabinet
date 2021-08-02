package com.ashindigo.storagecabinet.description;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.ashindigo.storagecabinet.widgets.WItemScrollPanel;
import com.ashindigo.storagecabinet.widgets.WScrollItemSlot;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
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
        root.setSize(180, 294); // 166
        WScrollItemSlot itemSlot = new WScrollItemSlot(cabinetEntity, 0, StorageCabinetBlock.getWidth(), StorageCabinetBlock.getHeight(cabinetEntity.tier)).setFilter(stack -> cabinetEntity.isValid(0, stack));
        WItemScrollPanel scrollPanel = new WItemScrollPanel(itemSlot);
        scrollPanel.setScrollingVertically(TriState.TRUE);
        scrollPanel.setScrollingHorizontally(TriState.FALSE);
        root.add(scrollPanel,4 , 16);
        root.add(new WPlayerInvPanel(playerInventory, true), 4, 200);
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
