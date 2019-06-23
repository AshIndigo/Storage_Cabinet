package com.ashindigo.storagecabinet;

import net.minecraft.block.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class StorageCabinetBlock extends ContainerBlock {

    private static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public StorageCabinetBlock(Properties properties) {
        super(properties);
        setRegistryName(new ResourceLocation(StorageCabinetMod.MODID, StorageCabinetMod.MODID));
        ForgeRegistries.BLOCKS.register(this);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Nonnull
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nonnull
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Nonnull
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return StorageCabinetMod.storageCabinetTileEntity.create();
    }

    public boolean onBlockActivated(BlockState blockState, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        if (!world.isRemote) {
            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) world.getTileEntity(pos), pos);
        }
        return true;
    }

    @ParametersAreNonnullByDefault
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            IItemHandler inventory = Objects.requireNonNull(Objects.requireNonNull(worldIn.getTileEntity(pos))).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(new ItemStackHandler(270));
            for (int i = 0; i < inventory.getSlots(); i++) {
                worldIn.func_217376_c(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i)));
            }
            worldIn.updateComparatorOutputLevel(pos, this);
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
