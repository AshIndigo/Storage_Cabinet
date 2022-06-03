package com.ashindigo.storagecabinet.block;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.ashindigo.storagecabinet.inventory.ManagerInventory;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.Random;

public class CabinetManagerBlock extends BaseEntityBlock implements WorldlyContainerHolder {

    public static final DirectionProperty FACING;

    static {
        FACING = HorizontalDirectionalBlock.FACING;
    }

    public CabinetManagerBlock(Properties of) {
        super(of.strength(1, 5));
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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
        builder.add(FACING);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        if (world.isClientSide) {
            return;
        }

        CabinetManagerEntity te = (CabinetManagerEntity) world.getBlockEntity(pos);
        if (te == null) {
            return;
        }

        te.updateCabinetList();

        world.scheduleTick(pos, this, 100); // 5 secs
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide && player.getMainHandItem().getItem() != StorageCabinet.KEY.get()) {
            MenuRegistry.openExtendedMenu((ServerPlayer) player, (MenuProvider) world.getBlockEntity(pos), packetBuffer -> packetBuffer.writeBlockPos(pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CabinetManagerEntity(blockPos, blockState);
    }

    @Override
    public WorldlyContainer getContainer(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos) {
        ArrayList<StorageCabinetEntity> list = new ArrayList<>();
        CabinetManagerEntity entity = (CabinetManagerEntity) levelAccessor.getBlockEntity(blockPos);
        entity.checkSurroundingCabinets(list, blockPos, levelAccessor);
        return new ManagerInventory(entity, list);
    }
}
