package com.ashindigo.storagecabinet.items;

import com.ashindigo.storagecabinet.BlockRegistry;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class StorageCabinetDolly extends Item {

    public StorageCabinetDolly() {
        super(new Item.Settings().group(StorageCabinet.CABINET_GROUP).maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos clickedPos = context.getBlockPos();
        ItemStack dolly = context.getStack();
        BlockPos offsetPos = clickedPos.offset(context.getSide());

        if (world.getBlockState(clickedPos).getBlock() instanceof StorageCabinetBlock) {
            if (world.getBlockEntity(clickedPos) != null && world.getBlockEntity(clickedPos) instanceof StorageCabinetEntity blockEntity) {
                NbtCompound tag = blockEntity.writeNbt(new NbtCompound());
                if (!dolly.hasNbt()) {
                    dolly.setNbt(tag);
                    world.setBlockState(clickedPos, Blocks.AIR.getDefaultState());
                    return super.useOnBlock(context);
                }
            }
        }
        if (dolly.hasNbt() && dolly.getNbt().contains("tier")) {
            if (world.isAir(offsetPos)) {
                world.setBlockState(offsetPos, BlockRegistry.getByTier(dolly.getNbt().getInt("tier")).getDefaultState().with(HorizontalFacingBlock.FACING, context.getPlayerFacing().getOpposite()));
                StorageCabinetBlock block = (StorageCabinetBlock) world.getBlockState(offsetPos).getBlock();
                BlockEntity ent =  block.createBlockEntity(offsetPos, world.getBlockState(offsetPos));
                if (ent != null) {
                    ent.readNbt(dolly.getNbt());
                    world.addBlockEntity(ent);
                    dolly.setNbt(null);
                    ent.markDirty();
                }
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt() && stack.getNbt().contains("tier")) {
            tooltip.add(new TranslatableText("text.storagecabinet.dollyhas"));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
