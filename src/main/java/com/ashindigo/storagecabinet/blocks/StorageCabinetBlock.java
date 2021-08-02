package com.ashindigo.storagecabinet.blocks;

import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class StorageCabinetBlock extends BlockWithEntity implements InventoryProvider {

    public static final DirectionProperty FACING;
    public static final BooleanProperty OPEN;

    static {
        FACING = HorizontalFacingBlock.FACING;
        OPEN = Properties.OPEN;
    }

    private final int tier;

    public StorageCabinetBlock(FabricBlockSettings settings, int tier) {
        super(settings);
        this.tier = tier;
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(OPEN, false));
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StorageCabinetEntity) {
            ((StorageCabinetEntity) blockEntity).tick();
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite()).with(OPEN, false);
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StorageCabinetEntity(pos, state).setTier(tier);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            player.openHandledScreen((ExtendedScreenHandlerFactory) world.getBlockEntity(pos));

        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StorageCabinetEntity) {
                ((StorageCabinetEntity)blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StorageCabinetEntity) {
            ItemScatterer.spawn(world, pos, (StorageCabinetEntity) blockEntity);
            world.updateComparators(pos, this);
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return (SidedInventory) world.getBlockEntity(pos);
    }

    public int getTier() {
        return tier;
    }

    public static int getWidth() { // TODO Just remove? It's always 9
        return 9;
    }

    public static int getHeight(int tier) {
        return 10 * (tier + 1);
    }
}
