package com.ashindigo.storagecabinet.items;

import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
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

    private final int tier;

    public StorageCabinetUpgrade(Settings settings, int tier) {
        super(settings);
        this.tier = tier;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        if (state.getBlock() instanceof StorageCabinetBlock) {
            if (((StorageCabinetBlock) state.getBlock()).getTier() < tier) {
                StorageCabinetEntity oldCabinet = (StorageCabinetEntity) context.getWorld().getBlockEntity(context.getBlockPos());
                context.getWorld().setBlockState(context.getBlockPos(), getCabinet(tier).getDefaultState());
                copyItems(oldCabinet, (Inventory) context.getWorld().getBlockEntity(context.getBlockPos()));
                context.getStack().decrement(1);
            }
        }
        return super.useOnBlock(context);
    }

    public StorageCabinetBlock getCabinet(int tier) {
        return switch (tier) {
            case 1 -> (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(MODID, MODID + "_iron"));
            case 2 -> (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(MODID, MODID + "_gold"));
            case 3 -> (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(MODID, MODID + "_diamond"));
            case 4 -> (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(MODID, MODID + "_emerald"));
            default -> (StorageCabinetBlock) Registry.BLOCK.get(new Identifier(MODID, MODID + "_wood"));
        };
    }

    public void copyItems(Inventory source, Inventory target) {
        if (source.size() <= target.size()) {
            for (int i = 0; i < source.size(); i++) {
                target.setStack(i, source.getStack(i));
            }
        }
    }
}
