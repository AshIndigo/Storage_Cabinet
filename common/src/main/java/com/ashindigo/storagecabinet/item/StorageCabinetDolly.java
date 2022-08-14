package com.ashindigo.storagecabinet.item;

import com.ashindigo.storagecabinet.Constants;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class StorageCabinetDolly extends Item {

    public StorageCabinetDolly() {
        super(new Properties().tab(StorageCabinet.CABINET_GROUP).stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        ItemStack dolly = context.getItemInHand();
        BlockPos offsetPos = clickedPos.relative(context.getClickedFace());
        if (!level.isClientSide) {
            if (level.getBlockState(clickedPos).getBlock() instanceof StorageCabinetBlock) {
                if (level.getBlockEntity(clickedPos) != null && level.getBlockEntity(clickedPos) instanceof StorageCabinetEntity) {
                    CompoundTag tag = level.getBlockEntity(clickedPos).saveWithFullMetadata();
                    if (!dolly.hasTag() || dolly.hasTag() && !dolly.getTag().contains(Constants.TIER)) {
                        dolly.setTag(tag);
                        level.setBlockAndUpdate(clickedPos, Blocks.AIR.defaultBlockState());
                        return super.useOn(context);
                    }
                }
            }

            if (dolly.hasTag() && dolly.getTag().contains(Constants.TIER)) {
                if (level.isEmptyBlock(offsetPos)) {
                    level.setBlockAndUpdate(offsetPos, StorageCabinet.getByTier(dolly.getTag().getInt(Constants.TIER)).defaultBlockState().setValue(StorageCabinetBlock.FACING, context.getHorizontalDirection().getOpposite()));
                    StorageCabinetEntity ent = (StorageCabinetEntity) level.getBlockEntity(offsetPos);
                    if (ent != null) {
                        dolly.getTag().putInt("x", offsetPos.getX());
                        dolly.getTag().putInt("y", offsetPos.getY());
                        dolly.getTag().putInt("z", offsetPos.getZ());
                        ent.load(dolly.getTag());
                        dolly.setTag(null);
                        ent.setChanged();
                    }
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        if (stack.hasTag()) {
            tooltip.add(Component.translatable("text.storagecabinet.dollyhas"));
        }
        super.appendHoverText(stack, world, tooltip, flag);
    }

}
