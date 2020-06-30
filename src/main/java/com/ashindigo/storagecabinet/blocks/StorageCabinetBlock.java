package com.ashindigo.storagecabinet.blocks;

import com.ashindigo.storagecabinet.BlockRegistry;
import com.ashindigo.storagecabinet.StorageCabinetContainer;
import com.ashindigo.storagecabinet.StorageCabinetEntity;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
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

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack) {
        if (state.getBlock() != state.getBlock()) {
            StorageCabinetEntity inventory = ((StorageCabinetEntity) Objects.requireNonNull(world.getBlockEntity(pos)));
            for (int i = 0; i < inventory.size(); i++) {
                world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), inventory.getStack(i)));
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
