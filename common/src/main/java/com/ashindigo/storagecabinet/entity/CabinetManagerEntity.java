package com.ashindigo.storagecabinet.entity;

import com.ashindigo.storagecabinet.CabinetManagerItemHandler;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.container.CabinetManagerContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;

public class CabinetManagerEntity extends BlockEntity implements MenuProvider {

    public final ArrayList<StorageCabinetEntity> cabinetList = new ArrayList<>();

    private final CabinetManagerItemHandler itemHandler = new CabinetManagerItemHandler(this);
    final LazyOptional<IItemHandler> inventoryHandlerLazyOptional = LazyOptional.of(() -> itemHandler);

    public CabinetManagerEntity(BlockPos pos, BlockState state) {
        super(StorageCabinet.CABINET_MANAGER_ENTITY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent(level.getBlockState(getBlockPos()).getBlock().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new CabinetManagerContainer(syncId,inv, getBlockPos());
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (!getLevel().getBlockTicks().willTickThisTick(getBlockPos(), StorageCabinet.CABINET_MANAGER.get()))
            getLevel().getBlockTicks().scheduleTick(getBlockPos(), StorageCabinet.CABINET_MANAGER.get(), 1, TickPriority.NORMAL);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryHandlerLazyOptional.invalidate();
    }

    public void updateCabinetList() {
        cabinetList.clear();
        checkSurroundingCabinets(cabinetList, getBlockPos(), level);
    }

    private void checkSurroundingCabinets(ArrayList<StorageCabinetEntity> cabinetList, BlockPos pos, Level world) {
        for (Direction direction : Direction.values()) {
            if (world.getBlockEntity(pos.offset(direction.getNormal())) instanceof StorageCabinetEntity) {
                if (!cabinetList.contains(world.getBlockEntity(pos.offset(direction.getNormal())))) {
                    StorageCabinetEntity entity = (StorageCabinetEntity) world.getBlockEntity(pos.offset(direction.getNormal()));
                    cabinetList.add(entity);
                    checkSurroundingCabinets(cabinetList, pos.offset(direction.getNormal()), world);
                }
            }
        }
    }
}
