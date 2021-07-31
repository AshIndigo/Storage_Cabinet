package com.ashindigo.storagecabinet.entity;

import com.ashindigo.storagecabinet.CabinetManagerItemHandler;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.container.CabinetManagerContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class CabinetManagerEntity extends TileEntity implements INamedContainerProvider  {

    public final ArrayList<StorageCabinetEntity> cabinetList = new ArrayList<>();

    private final CabinetManagerItemHandler itemHandler = new CabinetManagerItemHandler(this);
    final LazyOptional<IItemHandler> inventoryHandlerLazyOptional = LazyOptional.of(() -> itemHandler);

    public CabinetManagerEntity() {
        super(StorageCabinet.CABINET_MANAGER_ENTITY.get());
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(level.getBlockState(getBlockPos()).getBlock().getDescriptionId());
    }

    @Override
    public Container createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CabinetManagerContainer(syncId,inv, getBlockPos());
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (!getLevel().getBlockTicks().willTickThisTick(getBlockPos(), StorageCabinet.CABINET_MANAGER.get()))
            getLevel().getBlockTicks().scheduleTick(getBlockPos(), StorageCabinet.CABINET_MANAGER.get(), 1, TickPriority.NORMAL);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
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

    private void checkSurroundingCabinets(ArrayList<StorageCabinetEntity> cabinetList, BlockPos pos, World world) {
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
