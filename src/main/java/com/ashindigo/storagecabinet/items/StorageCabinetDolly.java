package com.ashindigo.storagecabinet.items;

import com.ashindigo.storagecabinet.BlockRegistry;
import com.ashindigo.storagecabinet.StorageCabinetEntity;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class StorageCabinetDolly extends Item {
    public StorageCabinetDolly(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof StorageCabinetBlock) {
            if (context.getWorld().getBlockEntity(context.getBlockPos()) != null && context.getWorld().getBlockEntity(context.getBlockPos()) instanceof StorageCabinetEntity) {
                StorageCabinetEntity blockEntity = (StorageCabinetEntity) context.getWorld().getBlockEntity(context.getBlockPos());
                CompoundTag tag = blockEntity.toTag(new CompoundTag());
                if (!context.getStack().hasTag()) {
                    context.getStack().setTag(tag);
                    context.getWorld().setBlockState(context.getBlockPos(), Blocks.AIR.getDefaultState());
                    return super.useOnBlock(context);
                }
            }
        }
        if (context.getStack().hasTag()) {
            if (context.getWorld().isAir(context.getBlockPos().offset(context.getSide()))) {
                context.getWorld().setBlockState(context.getBlockPos().offset(context.getSide()), BlockRegistry.getByTier(context.getStack().getTag().getInt("tier")).getDefaultState().with(HorizontalFacingBlock.FACING, context.getPlayerFacing().getOpposite()));
                StorageCabinetBlock block = (StorageCabinetBlock) context.getWorld().getBlockState(context.getBlockPos().offset(context.getSide())).getBlock();
                BlockEntity ent =  block.createBlockEntity(null);
                ent.fromTag(context.getStack().getTag());
                context.getWorld().setBlockEntity(context.getBlockPos().offset(context.getSide()), ent);
                context.getStack().setTag(null);
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasTag()) {
            tooltip.add(new TranslatableText("text.storagecabinet.dollyhas"));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
