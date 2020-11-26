package com.ashindigo.storagecabinet.description;

import com.ashindigo.storagecabinet.ManagerInventory;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;

// TODO So two options(?)
// One: A plain panel as the backend, with a tab panel existing for the cabinets that also shows their inventory and then player inv is added to the plain panel
// Two: Root is tab panel, which is then a plain panel with a scroll panel and the player inv panel
public class CabinetManagerDescription extends SyncedGuiDescription {

    public final CabinetManagerEntity managerEntity;
    final ArrayList<StorageCabinetEntity> cabinetList = new ArrayList<>();
    final WPlayerInvPanel playerInvPanel;
    final ArrayList<WScrollPanel> cabinetPanels = new ArrayList<>();
    final int width = 170;

    public CabinetManagerDescription(int synchronizationID, PlayerInventory playerInventory, ScreenHandlerContext ctx) {
        super(StorageCabinet.managerScreenHandler, synchronizationID, playerInventory, getBlockInventory(ctx), getBlockPropertyDelegate(ctx));
        // Initial set up stuff
        managerEntity = ((ManagerInventory) blockInventory).getEntity();
        playerInvPanel = new WPlayerInvPanel(playerInventory, true);
        checkSurroundingCabinets(cabinetList, managerEntity.getPos(), world);
        //Panel
        WPlainPanel root = new WPlainPanel();
        WTabPanel cabinetTabs = new WTabPanel();
        root.setSize(width + 14, 270);
        root.add(cabinetTabs, 0, 16);
        root.add(playerInvPanel, 0, 244);
        setRootPanel(root);
        if (cabinetList.isEmpty()) { // In case no cabinet's are attached
            cabinetTabs.add(new WText(new LiteralText("")), builder -> builder.icon(new ItemIcon(Items.BARRIER)));
        }
        cabinetList.forEach(cabinetEntity -> addCabinet(cabinetTabs, cabinetEntity));
        root.validate(this);
        cabinetTabs.setSize(width, 270);
        cabinetPanels.forEach(panel -> panel.setSize(width, 180));

    }

    private void addCabinet(WTabPanel main, StorageCabinetEntity cabinetEntity) {
        WScrollPanel scrollPanel = new WScrollPanel(new WItemSlot(cabinetEntity, 0, StorageCabinetBlock.Manager.getWidth(), StorageCabinetBlock.Manager.getHeight(cabinetEntity.tier), false));
        scrollPanel.setScrollingHorizontally(TriState.FALSE);
        scrollPanel.setScrollingVertically(TriState.TRUE);
        main.add(scrollPanel, builder -> builder.icon(new ItemIcon(cabinetEntity.isEmpty() ? Items.AIR : cabinetEntity.getMainItemStack().getItem())));
        cabinetEntity.addListener(sender -> sendContentUpdates()); // TODO Useless?
        cabinetEntity.addListener(this::onContentChanged);
        cabinetPanels.add(scrollPanel);
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
