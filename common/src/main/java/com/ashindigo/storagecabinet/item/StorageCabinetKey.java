package com.ashindigo.storagecabinet.item;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class StorageCabinetKey extends Item {

    public StorageCabinetKey() {
        super(new Properties().tab(StorageCabinet.CABINET_GROUP).stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof StorageCabinetBlock) {
            if (context.getLevel().getBlockEntity(context.getClickedPos()) != null && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof StorageCabinetEntity blockEntity) {
                CompoundTag tag = blockEntity.save(new CompoundTag());
                tag.putBoolean("locked", !tag.getBoolean("locked"));
                tag.putString("item", Registry.ITEM.getKey(blockEntity.getMainItemStack().getItem()).toString());
                blockEntity.load(tag);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
