package com.ashindigo.storagecabinet.item;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

public class StorageCabinetUpgrade extends Item {

    private final int tier;

    public StorageCabinetUpgrade(int tier) {
        super(new Properties().tab(StorageCabinet.CABINET_GROUP));
        this.tier = tier;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.getBlock() instanceof StorageCabinetBlock) {
            if (((StorageCabinetBlock) state.getBlock()).getTier() < tier) {
                Container sourceInv = (StorageCabinetEntity) context.getLevel().getBlockEntity(context.getClickedPos());
                ArrayList<ItemStack> oldList = new ArrayList<>();
                for (int i = 0; i < sourceInv.getContainerSize(); i++) {
                    oldList.add(sourceInv.getItem(i));
                }
                context.getLevel().setBlockAndUpdate(context.getClickedPos(), StorageCabinet.getByTier(tier).defaultBlockState().setValue(StorageCabinetBlock.FACING, state.getValue(StorageCabinetBlock.FACING)));
                // Bug fix for disappearing items
                sourceInv = (StorageCabinetEntity) context.getLevel().getBlockEntity(context.getClickedPos());
                copyItems(oldList, sourceInv);
                context.getItemInHand().shrink(1);
            }
        }
        return super.useOn(context);
    }

    public void copyItems(ArrayList<ItemStack> list, Container target) {

        if (list.size() <= target.getContainerSize()) {
            for (int i = 0; i < list.size(); i++) {
                target.setItem(i, list.get(i));
            }
        }
    }
}
