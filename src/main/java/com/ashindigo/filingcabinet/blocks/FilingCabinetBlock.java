package com.ashindigo.filingcabinet.blocks;

import com.ashindigo.filingcabinet.FilingCabinetMod;
import com.ashindigo.filingcabinet.GuiHandler;
import com.ashindigo.filingcabinet.tileentities.TileEntityFilingCabinet;
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

public class FilingCabinetBlock extends Block {

    public FilingCabinetBlock(Material materialIn) {
        super(materialIn);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setUnlocalizedName("filingcabinet");
        setHardness(3.0F);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (!player.isSneaking()) {
                player.openGui(FilingCabinetMod.instance, GuiHandler.CABINET, world, pos.getX(), pos.getY(), pos.getZ());
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
    public TileEntityFilingCabinet createTileEntity(World world, IBlockState state) {
        return new TileEntityFilingCabinet();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
            TileEntityFilingCabinet tile = (TileEntityFilingCabinet) world.getTileEntity(pos);
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
