package com.ashindigo.storagecabinet.items;

import com.ashindigo.storagecabinet.StorageCabinetEntity;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import spinnery.util.InventoryUtilities;

import static com.ashindigo.storagecabinet.StorageCabinet.modid;

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
                StorageCabinetEntity newCabinet = new StorageCabinetEntity().setTier(tier);
                copyItems((Inventory) context.getWorld().getBlockEntity(context.getBlockPos()), newCabinet);
                ((Inventory) context.getWorld().getBlockEntity(context.getBlockPos())).clear();
                context.getWorld().setBlockState(context.getBlockPos(), getCabinet(tier).getDefaultState());
                context.getWorld().setBlockEntity(context.getBlockPos(), new StorageCabinetEntity().setTier(tier));
            }
        }
        return super.useOnBlock(context);
    }

    public StorageCabinetBlock getCabinet(int tier) {
        switch(tier) {
            default:
            case 0: return (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(modid, modid + "_wood"));
            case 1: return (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(modid, modid + "_iron"));
            case 2: return (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(modid, modid + "_gold"));
            case 3: return (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(modid, modid + "_diamond"));
            case 4: return (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(modid, modid + "_emerald"));
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
