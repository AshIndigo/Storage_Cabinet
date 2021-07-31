package com.ashindigo.storagecabinet.item;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.registries.ForgeRegistries;

public class StorageCabinetKey extends Item {

    public StorageCabinetKey() {
        super(new Item.Properties().tab(StorageCabinet.CABINET_GROUP).stacksTo(1));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof StorageCabinetBlock) {
            if (context.getLevel().getBlockEntity(context.getClickedPos()) != null && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof StorageCabinetEntity) {
                StorageCabinetEntity blockEntity = (StorageCabinetEntity) context.getLevel().getBlockEntity(context.getClickedPos());
                CompoundNBT tag = blockEntity.save(new CompoundNBT());
                tag.putBoolean("locked", !tag.getBoolean("locked"));
                tag.putString("item", ForgeRegistries.ITEMS.getKey(blockEntity.getMainItemStack().getItem()).toString());
                blockEntity.load(context.getLevel().getBlockState(context.getClickedPos()), tag);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
