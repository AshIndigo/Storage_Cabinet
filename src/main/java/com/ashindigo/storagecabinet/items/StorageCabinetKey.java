package com.ashindigo.storagecabinet.items;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;

public class StorageCabinetKey extends Item {
    public StorageCabinetKey() {
        super(new Item.Settings().group(StorageCabinet.CABINET_GROUP).maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof StorageCabinetBlock) {
            if (context.getWorld().getBlockEntity(context.getBlockPos()) != null && context.getWorld().getBlockEntity(context.getBlockPos()) instanceof StorageCabinetEntity) {
                StorageCabinetEntity blockEntity = (StorageCabinetEntity) context.getWorld().getBlockEntity(context.getBlockPos());
                if (blockEntity != null) {
                    CompoundTag tag = blockEntity.toTag(new CompoundTag());
                    tag.putBoolean("locked", !tag.getBoolean("locked"));
                    tag.putString("item", Registry.ITEM.getId(blockEntity.getMainItemStack().getItem()).toString());
                    blockEntity.fromTag(blockEntity.getCachedState(), tag);
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
