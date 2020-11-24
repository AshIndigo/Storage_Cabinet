package com.ashindigo.storagecabinet.description;

import com.ashindigo.storagecabinet.ManagerInventory;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WTabPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
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

public class CabinetManagerDescription extends SyncedGuiDescription {

    public final CabinetManagerEntity managerEntity;
    final ArrayList<StorageCabinetEntity> cabinetList = new ArrayList<>();

    public CabinetManagerDescription(int synchronizationID, PlayerInventory playerInventory, ScreenHandlerContext ctx) {
        super(StorageCabinet.managerScreenHandler, synchronizationID, playerInventory, getBlockInventory(ctx), getBlockPropertyDelegate(ctx));
        WTabPanel root = new WTabPanel();
        managerEntity = ((ManagerInventory) blockInventory).getEntity();
        checkSurroundingCabinets(cabinetList, managerEntity.getPos(), world);
        if (cabinetList.isEmpty()) { // In case no cabinet's are attached
            root.add(new WText(new LiteralText("")), builder -> builder.icon(new ItemIcon(Items.BARRIER)));
        }
        cabinetList.forEach(cabinetEntity -> addCabinet(root, cabinetEntity));
        root.setSize(208, 288);
        setRootPanel(root);
        //root.validate(this);
        root.setSize(208, 288);
    }

    private void addCabinet(WTabPanel main, StorageCabinetEntity cabinetEntity) {
        WScrollPanel scrollPanel = new WScrollPanel(new WItemSlot(cabinetEntity, 0, StorageCabinetBlock.Manager.getWidth(), StorageCabinetBlock.Manager.getHeight(cabinetEntity.tier), false)).setScrollingHorizontally(TriState.FALSE);
        main.add(scrollPanel, builder -> builder.icon(new ItemIcon(cabinetEntity.isEmpty() ? Items.AIR : cabinetEntity.getMainItemStack().getItem())));
        cabinetEntity.addListener(sender -> sendContentUpdates()); // TODO Useless?
        cabinetEntity.addListener(this::onContentChanged);
        scrollPanel.validate(this);
        scrollPanel.setSize(172, 180);
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
