package com.ashindigo.storagecabinet.items;

import com.ashindigo.storagecabinet.StorageCabinetEntity;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.ashindigo.storagecabinet.StorageCabinet.MODID;

public class StorageCabinetUpgrade extends Item {

    private int tier;

    public StorageCabinetUpgrade(Settings settings, int tier) {
        super(settings);
        this.tier = tier;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        if (state.getBlock() instanceof StorageCabinetBlock) {
            if (((StorageCabinetBlock) state.getBlock()).getTier() < tier) {
                //if (!context.getWorld().isClient) {
                StorageCabinetEntity oldCabinet = (StorageCabinetEntity) context.getWorld().getBlockEntity(context.getBlockPos());
                context.getWorld().setBlockState(context.getBlockPos(), getCabinet(tier).getDefaultState());
                copyItems(oldCabinet, (Inventory) context.getWorld().getBlockEntity(context.getBlockPos()));
                context.getStack().decrement(1);
                //}
            }
        }
        return super.useOnBlock(context);
    }

    public StorageCabinetBlock getCabinet(int tier) {
        switch (tier) {
            default:
            case 0:
                return (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(MODID, MODID + "_wood"));
            case 1:
                return (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(MODID, MODID + "_iron"));
            case 2:
                return (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(MODID, MODID + "_gold"));
            case 3:
                return (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(MODID, MODID + "_diamond"));
            case 4:
                return (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(MODID, MODID + "_emerald"));
        }
    }

    public void copyItems(Inventory source, Inventory target) {
        if (source.getInvSize() <= target.getInvSize()) {
            for (int i = 0; i < source.getInvSize(); i++) {
                target.setInvStack(i, source.getInvStack(i));
            }
        }
    }
}
