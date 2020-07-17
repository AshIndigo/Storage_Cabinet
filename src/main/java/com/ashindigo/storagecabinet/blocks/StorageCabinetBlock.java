package com.ashindigo.storagecabinet.blocks;

import com.ashindigo.storagecabinet.BlockRegistry;
import com.ashindigo.storagecabinet.container.StorageCabinetContainer;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
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

    private final int tier;

    public StorageCabinetBlock(FabricBlockSettings settings, int tier) {
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
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeBlockPos(pos);
                }

                @Override
                public Text getDisplayName() {
                    return new TranslatableText(BlockRegistry.getByTier(tier).getTranslationKey());
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new StorageCabinetContainer(syncId, inv, pos);
                }
            });

        }
        return ActionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation") // onStateReplaced is deprecated for whatever reason
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StorageCabinetEntity) {
                ItemScatterer.spawn(world, pos, (StorageCabinetEntity) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public int getTier() {
        return tier;
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
