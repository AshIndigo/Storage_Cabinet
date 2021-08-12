package com.ashindigo.storagecabinet.block;

import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class StorageCabinetBlock extends ContainerBlock {

    public static final DirectionProperty FACING;
    public static final BooleanProperty OPEN;

    static {
        FACING = HorizontalBlock.FACING;
        OPEN = BlockStateProperties.OPEN;
    }

    private final int tier;

    public StorageCabinetBlock(Properties settings, int tier) {
        super(settings.strength(3, 5));
        this.tier = tier;
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }

    public static int getWidth() { // TODO Just remove? It's always 9
        return 9;
    }

    public static int getHeight(int tier) {
        return 10 * (tier + 1);
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        TileEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StorageCabinetEntity) {
            ((StorageCabinetEntity) blockEntity).tick();
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(OPEN, false);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new StorageCabinetEntity().setTier(tier);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) world.getBlockEntity(pos), packetBuffer -> {
                packetBuffer.writeBlockPos(pos);
                packetBuffer.writeInt(tier);
            });
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomHoverName()) {
            TileEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StorageCabinetEntity) {
                ((StorageCabinetEntity)blockEntity).setCustomName(itemStack.getDisplayName());
            }
        }
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StorageCabinetEntity) {
            IItemHandler inventory = world.getBlockEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(() -> new NullPointerException("Source Capability was not present!"));
            for (int i = 0; i < inventory.getSlots(); i++) {
                world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStackInSlot(i)));
            }
            world.updateNeighbourForOutputSignal(pos, this);
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    public int getTier() {
        return tier;
    }

    @Nullable
    @Override
     public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
        return null; // TODO Remains null
    }
}
