package com.ashindigo.filingcabinet;

import com.ashindigo.filingcabinet.tileentities.TileEntityFilingCabinet;
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
        switch (ID) {
            case CABINET:
                return new ContainerFilingCabinet(player.inventory, (TileEntityFilingCabinet) Objects.requireNonNull(world.getTileEntity(new BlockPos(x, y, z))));
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case CABINET:
                return new GuiFilingCabinet(getServerGuiElement(ID, player, world, x, y, z), player);
            default:
                return null;
        }
    }
}
