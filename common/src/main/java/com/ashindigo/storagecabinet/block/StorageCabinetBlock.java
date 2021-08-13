package com.ashindigo.storagecabinet.block;

import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class StorageCabinetBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING;
    public static final BooleanProperty OPEN;

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        OPEN = BlockStateProperties.OPEN;
    }

    private final int tier;

    public StorageCabinetBlock(Properties settings, int tier) {
        super(settings.strength(3, 5));
        this.tier = tier;
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StorageCabinetEntity) {
            ((StorageCabinetEntity) blockEntity).tick();
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(OPEN, false);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) world.getBlockEntity(pos), packetBuffer -> {
                packetBuffer.writeBlockPos(pos);
                packetBuffer.writeInt(tier);
            });
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomHoverName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StorageCabinetEntity) {
                ((StorageCabinetEntity) blockEntity).setCustomName(itemStack.getDisplayName());
            }
        }
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StorageCabinetEntity(pos, state).setTier(tier);
    }




    public static class Manager {
        public static int getWidth() { // TODO Just remove? It's always 9
            return 9;
        }

        public static int getHeight(int tier) {
            return 10 * (tier + 1);
        }
    }
}