package com.ashindigo.storagecabinet.item;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class CabinetDebug extends Item {

    public CabinetDebug() {
        super(new Properties().arch$tab(StorageCabinet.CABINET_GROUP).stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final Level level = context.getLevel();
        if (level.getBlockState(context.getClickedPos()).getBlock() instanceof StorageCabinetBlock) {
            if (level.getBlockEntity(context.getClickedPos()) != null && level.getBlockEntity(context.getClickedPos()) instanceof StorageCabinetEntity blockEntity) {
                context.getPlayer().displayClientMessage(Component.literal("Cabinet Item: " + blockEntity.getItem().toString()), true);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
