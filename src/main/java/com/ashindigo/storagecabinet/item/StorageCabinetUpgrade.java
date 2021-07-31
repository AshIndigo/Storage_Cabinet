package com.ashindigo.storagecabinet.item;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
    public InteractionResult useOn(UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.getBlock() instanceof StorageCabinetBlock) {
            if (((StorageCabinetBlock) state.getBlock()).getTier() < tier) {
                IItemHandler sourceInv = context.getLevel().getBlockEntity(context.getClickedPos()).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElseThrow(() -> new NullPointerException("Source Capability was not present!"));
                ArrayList<ItemStack> oldList = new ArrayList<>();
                for (int i = 0; i < sourceInv.getSlots(); i++) {
                    oldList.add(sourceInv.getStackInSlot(i));
                }
                context.getLevel().setBlockAndUpdate(context.getClickedPos(), StorageCabinet.getByTier(tier).defaultBlockState().setValue(StorageCabinetBlock.FACING, state.getValue(StorageCabinetBlock.FACING)));
                copyItems(oldList, context.getLevel().getBlockEntity(context.getClickedPos()));
                context.getItemInHand().shrink(1);
            }
        }
        return super.useOn(context);
    }

    public void copyItems(ArrayList<ItemStack> list, BlockEntity target) {
        IItemHandler targetInv = target.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElseThrow(() -> new NullPointerException("Target Capability was not present!"));
        if (list.size() <= targetInv.getSlots()) {
            for (int i = 0; i < list.size(); i++) {
                targetInv.insertItem(i, list.get(i), false);
            }
        }
    }
}
