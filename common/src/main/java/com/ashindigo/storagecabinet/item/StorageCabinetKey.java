package com.ashindigo.storagecabinet.item;

import com.ashindigo.storagecabinet.Constants;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.CabinetManagerBlock;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StorageCabinetKey extends Item {

    public enum KeyMode {
        LOCK,
        UNLOCK,
        INVERT;

        public static KeyMode getLockMode(boolean val) {
            return val ? LOCK : UNLOCK;
        }
    }

    public StorageCabinetKey() {
        super(new Properties().tab(StorageCabinet.CABINET_GROUP).stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final Level level = context.getLevel();
        // Lock an individual cabinet
        if (level.getBlockState(context.getClickedPos()).getBlock() instanceof StorageCabinetBlock) {
            if (level.getBlockEntity(context.getClickedPos()) != null && level.getBlockEntity(context.getClickedPos()) instanceof StorageCabinetEntity blockEntity) {
                lockCabinet(blockEntity, KeyMode.INVERT);
            }
            return InteractionResult.SUCCESS;
        }

        // Locking functionality for Cabinet Manager
        if (level.getBlockState(context.getClickedPos()).getBlock() instanceof CabinetManagerBlock) {
            if (level.getBlockEntity(context.getClickedPos()) != null && level.getBlockEntity(context.getClickedPos()) instanceof CabinetManagerEntity cabinetManager) {
                // If sneaking, then invert all cabinets
                ArrayList<StorageCabinetEntity> cabinetList = Lists.newArrayList();
                cabinetManager.checkSurroundingCabinets(cabinetList, cabinetManager.getBlockPos(), level);

                if (context.getPlayer().isShiftKeyDown()) {
                    // Set all to locked/unlocked
                    CompoundTag tag = cabinetManager.saveWithFullMetadata();
                    tag.putBoolean(Constants.LOCKED, !tag.getBoolean(Constants.LOCKED));
                    cabinetManager.load(tag);
                    for (StorageCabinetEntity cabinet : cabinetList) {
                        lockCabinet(cabinet, KeyMode.getLockMode(tag.getBoolean(Constants.LOCKED)));
                    }
                } else {
                    // If not sneaking, then invert all cabinets (Currently not working)
                    for (StorageCabinetEntity cabinet : cabinetList) {
                        lockCabinet(cabinet, KeyMode.INVERT);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void lockCabinet(StorageCabinetEntity cabinetEntity, KeyMode mode) {
        CompoundTag tag = cabinetEntity.saveWithFullMetadata();
        switch (mode) {
            case LOCK -> tag.putBoolean(Constants.LOCKED, true);
            case UNLOCK -> tag.putBoolean(Constants.LOCKED, false);
            case INVERT -> tag.putBoolean(Constants.LOCKED, !tag.getBoolean(Constants.LOCKED));
        }
        tag.putString(Constants.ITEM, Registry.ITEM.getKey(cabinetEntity.getMainItemStack().getItem()).toString());
        cabinetEntity.load(tag);
        cabinetEntity.setChanged();
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        list.add(new TranslatableComponent("desc.storagecabinet.item.key.1").withStyle(ChatFormatting.GRAY));
        list.add(new TranslatableComponent("desc.storagecabinet.item.key.2").withStyle(ChatFormatting.GRAY));
    }
}
