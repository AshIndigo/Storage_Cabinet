package com.ashindigo.storagecabinet.items;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;

public class StorageCabinetKey extends Item {
    public StorageCabinetKey() {
        super(new Item.Settings().group(StorageCabinet.CABINET_GROUP).maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof StorageCabinetBlock) {
            if (context.getWorld().getBlockEntity(context.getBlockPos()) != null && context.getWorld().getBlockEntity(context.getBlockPos()) instanceof StorageCabinetEntity blockEntity) {
                NbtCompound tag = blockEntity.writeNbt(new NbtCompound());
                tag.putBoolean("locked", !tag.getBoolean("locked"));
                tag.putString("item", Registry.ITEM.getId(blockEntity.getMainItemStack().getItem()).toString());
                blockEntity.readNbt(tag);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
