package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.tileentities.TileEntityStorageCabinet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.util.Objects;

public class GuiHandler implements IGuiHandler {

    public static final int CABINET = 0;

    @Override
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == CABINET) {
            return new ContainerStorageCabinet(player.inventory, (TileEntityStorageCabinet) Objects.requireNonNull(world.getTileEntity(new BlockPos(x, y, z))));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == CABINET) {
            return new GuiStorageCabinet(getServerGuiElement(ID, player, world, x, y, z), player);
        }
        return null;
    }
}
