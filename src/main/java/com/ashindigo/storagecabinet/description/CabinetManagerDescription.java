package com.ashindigo.storagecabinet.description;

import com.ashindigo.storagecabinet.inventory.ManagerInventory;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.ashindigo.storagecabinet.widgets.WItemScrollPanel;
import com.ashindigo.storagecabinet.widgets.WPagedTabPanel;
import com.ashindigo.storagecabinet.widgets.WScrollItemSlot;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;

// TODO Shift clicking isn't working for some reason
public class CabinetManagerDescription extends SyncedGuiDescription {

    public final CabinetManagerEntity managerEntity;
    final ArrayList<StorageCabinetEntity> cabinetList = new ArrayList<>();
    final ArrayList<WItemScrollPanel> cabinetPanels = new ArrayList<>();
    final WPagedTabPanel cabinetTabs;
    final int width = 184;

    public CabinetManagerDescription(int synchronizationID, PlayerInventory playerInventory, ScreenHandlerContext ctx) {
        super(StorageCabinet.managerScreenHandler, synchronizationID, playerInventory, getBlockInventory(ctx), getBlockPropertyDelegate(ctx));
        // Initial set up stuff
        managerEntity = ((ManagerInventory) blockInventory).getEntity();
        checkSurroundingCabinets(cabinetList, managerEntity.getPos(), world);
        //Panel
        WPlainPanel root = new WPlainPanel();
        cabinetTabs = new WPagedTabPanel();
        cabinetTabs.setSize(width-6, 260);
        root.setSize(width+12, 270+60);
        root.add(cabinetTabs, 4, 16);
        root.add(new WPlayerInvPanel(playerInventory, true), 4, 236);
        setRootPanel(root);
        if (cabinetList.isEmpty()) { // In case no cabinet's are attached
            cabinetTabs.add(new WText(new LiteralText("")), builder -> builder.icon(new ItemIcon(Items.BARRIER)));
        }
        cabinetList.forEach(cabinetEntity -> addCabinet(cabinetTabs, cabinetEntity));
        cabinetPanels.forEach(panel -> panel.setSize(width-4, 180));
        for (WItemScrollPanel panel : cabinetPanels) {
            panel.setLocation(panel.getX()-8, panel.getY());
        }
        root.validate(this);
    }

    private void addCabinet(WPagedTabPanel main, StorageCabinetEntity cabinetEntity) {
        WScrollItemSlot itemSlot = new WScrollItemSlot(cabinetEntity, 0, StorageCabinetBlock.getWidth(), StorageCabinetBlock.getHeight(cabinetEntity.tier)).setFilter(stack -> cabinetEntity.isValid(0, stack));
        WItemScrollPanel scrollPanel = new WItemScrollPanel(itemSlot);
        scrollPanel.setScrollingHorizontally(TriState.FALSE);
        scrollPanel.setScrollingVertically(TriState.TRUE);
        main.add(scrollPanel, builder -> builder.icon(new ItemIcon(cabinetEntity.isEmpty() ? Items.AIR : cabinetEntity.getMainItemStack().getItem())));
        cabinetEntity.addListener(this::onContentChanged);
        cabinetEntity.addClientOnlyListener(this::iconChangeListener); // Yep
        cabinetPanels.add(scrollPanel);
    }

    private void iconChangeListener(Inventory inventory) {
        if (inventory instanceof StorageCabinetEntity storageCabinetEntity) {
            for (int i = 0; i < cabinetList.size(); i++) {
                StorageCabinetEntity cabinet = cabinetList.get(i);
                if (cabinet.getPos().equals(storageCabinetEntity.getPos())) {
                    cabinetTabs.getTabWidgets().get(i).getData().setIcon(new ItemIcon(storageCabinetEntity.getMainItemStack()));
                }
            }
        }
    }

    private void checkSurroundingCabinets(ArrayList<StorageCabinetEntity> cabinetList, BlockPos pos, World world) {
        for (Direction direction : Direction.values()) {
            if (world.getBlockEntity(pos.offset(direction)) instanceof StorageCabinetEntity) {
                if (!cabinetList.contains(world.getBlockEntity(pos.offset(direction)))) {
                    cabinetList.add((StorageCabinetEntity) world.getBlockEntity(pos.offset(direction)));
                    checkSurroundingCabinets(cabinetList, pos.offset(direction), world);
                }
            }
        }
    }

    @Override
    public void close(PlayerEntity player) {
        cabinetList.forEach(StorageCabinetEntity::clearListeners);
        super.close(player);
    }
}
