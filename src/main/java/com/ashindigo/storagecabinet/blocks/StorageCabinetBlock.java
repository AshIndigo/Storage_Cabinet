package com.ashindigo.storagecabinet.blocks;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.StorageCabinetEntity;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Objects;

public class StorageCabinetBlock extends BlockWithEntity {

    private static final DirectionProperty FACING;

    static {
        FACING = HorizontalFacingBlock.FACING;
    }

    private int tier;

    public StorageCabinetBlock(Block.Settings settings, int tier) {
        super(settings);
        this.tier = tier;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.get(FACING)));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new StorageCabinetEntity().setTier(tier);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier(StorageCabinet.modid, StorageCabinet.modid), player, (buffer) -> { buffer.writeBlockPos(pos); buffer.writeInt(Manager.getWidth()); buffer.writeInt(Manager.getHeight(tier)); buffer.writeInt(Manager.getMaximum()); buffer.writeText(new TranslatableText(this.getTranslationKey())); });
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        if (state.getBlock() != state.getBlock()) {
            StorageCabinetEntity inventory = ((StorageCabinetEntity) Objects.requireNonNull(world.getBlockEntity(pos)));
            for (int i = 0; i < inventory.getInvSize(); i++) {
                world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getInvStack(i)));
            }
            world.updateNeighbors(pos, this);
            super.afterBreak(world, player, pos, state, blockEntity, stack);
        }

    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public int getTier() {
        return tier;
    }

    public static class Manager {
        public static int getWidth() {
            return 9;
        }

        public static int getHeight(int tier) {
            return 10 * (tier + 1);
        }

        public static int getSize(int tier) {
            return getWidth() * getHeight(tier);
        }

        public static int getMaximum() {
            return 64;
        }
    }
}
