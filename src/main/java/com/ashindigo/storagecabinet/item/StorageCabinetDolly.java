package com.ashindigo.storagecabinet.item;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class StorageCabinetDolly extends Item {

    public StorageCabinetDolly() {
        super(new Item.Properties().tab(StorageCabinet.CABINET_GROUP).stacksTo(1));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) { // Need to sync up with client
        World level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        ItemStack dolly = context.getItemInHand();
        BlockPos offsetPos = clickedPos.relative(context.getClickedFace());
        if (!level.isClientSide) {
            if (level.getBlockState(clickedPos).getBlock() instanceof StorageCabinetBlock) {
                if (level.getBlockEntity(clickedPos) != null && level.getBlockEntity(clickedPos) instanceof StorageCabinetEntity) {
                    CompoundNBT tag = level.getBlockEntity(clickedPos).save(new CompoundNBT());
                    if (!dolly.hasTag() || dolly.hasTag() && !dolly.getTag().contains("tier")) {
                        dolly.setTag(tag);
                        level.setBlockAndUpdate(clickedPos, Blocks.AIR.defaultBlockState());
                        return super.useOn(context);
                    }
                }
            }

            if (dolly.hasTag() && dolly.getTag().contains("tier")) {
                if (level.isEmptyBlock(offsetPos)) {
                    level.setBlockAndUpdate(offsetPos, StorageCabinet.getByTier(dolly.getTag().getInt("tier")).defaultBlockState().setValue(StorageCabinetBlock.FACING, context.getHorizontalDirection().getOpposite()));
                    StorageCabinetEntity ent = (StorageCabinetEntity) level.getBlockEntity(offsetPos);
                    if (ent != null) {
                        ent.load(level.getBlockState(offsetPos), dolly.getTag());
                        ent.setPosition(offsetPos);
                        dolly.setTag(null);
                        ent.setChanged();
                    }
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (stack.hasTag()) {
            tooltip.add(new TranslationTextComponent("text.storagecabinet.dollyhas"));
        }
        super.appendHoverText(stack, world, tooltip, flag);
    }

}
