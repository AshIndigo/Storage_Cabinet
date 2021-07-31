package com.ashindigo.storagecabinet.item;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;

public class StorageCabinetUpgrade extends Item {

    private final int tier;

    public StorageCabinetUpgrade(int tier) {
        super(new Item.Properties().tab(StorageCabinet.CABINET_GROUP));
        this.tier = tier;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.getBlock() instanceof StorageCabinetBlock) {
            if (((StorageCabinetBlock) state.getBlock()).getTier() < tier) {
                IItemHandler sourceInv = context.getLevel().getBlockEntity(context.getClickedPos()).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElseThrow(() -> new NullPointerException("Source Capability was not present!"));
                ArrayList<ItemStack> oldList = new ArrayList<>();
                for (int i = 0; i < sourceInv.getSlots(); i++) {
                    oldList.add(sourceInv.getStackInSlot(i));
                }
                context.getLevel().setBlockAndUpdate(context.getClickedPos(), getCabinet(tier).defaultBlockState().setValue(StorageCabinetBlock.FACING, state.getValue(StorageCabinetBlock.FACING)));
                copyItems(oldList, context.getLevel().getBlockEntity(context.getClickedPos()));
                context.getItemInHand().shrink(1);
            }
        }
        return super.useOn(context);
    }

    public Block getCabinet(int tier) {
        switch (tier) {
            default:
            case 0:
                return StorageCabinet.WOOD_CABINET.get();
            case 1:
                return StorageCabinet.IRON_CABINET.get();
            case 2:
                return StorageCabinet.GOLD_CABINET.get();
            case 3:
                return StorageCabinet.DIAMOND_CABINET.get();
            case 4:
                return StorageCabinet.EMERALD_CABINET.get();
        }
    }

    public void copyItems(ArrayList<ItemStack> list, TileEntity target) {
        IItemHandler targetInv = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElseThrow(() -> new NullPointerException("Target Capability was not present!"));
        if (list.size() <= targetInv.getSlots()) {
            for (int i = 0; i < list.size(); i++) {
                targetInv.insertItem(i, list.get(i), false);
            }
        }
    }
}
