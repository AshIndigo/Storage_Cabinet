package com.ashindigo.storagecabinet.blocks;

import com.ashindigo.storagecabinet.StorageCabinetMod;
import com.ashindigo.storagecabinet.GuiHandler;
import com.ashindigo.storagecabinet.tileentities.TileEntityStorageCabinet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class StorageCabinetBlock extends Block {

    public StorageCabinetBlock(Material materialIn) {
        super(materialIn);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setUnlocalizedName("storagecabinet");
        setHardness(3.0F);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (!player.isSneaking()) {
                player.openGui(StorageCabinetMod.instance, GuiHandler.CABINET, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

   @Override
   public boolean hasTileEntity(IBlockState state) {
        return true;
   }

    @Nullable
    @Override
    public TileEntityStorageCabinet createTileEntity(World world, IBlockState state) {
        return new TileEntityStorageCabinet();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
            TileEntityStorageCabinet tile = (TileEntityStorageCabinet) world.getTileEntity(pos);
            IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    EntityItem item = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                    world.spawnEntity(item);
                }
            }
            super.breakBlock(world, pos, state);
    }
}
