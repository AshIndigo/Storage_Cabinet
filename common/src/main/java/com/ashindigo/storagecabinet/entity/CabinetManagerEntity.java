package com.ashindigo.storagecabinet.entity;

import com.ashindigo.storagecabinet.Constants;
import com.ashindigo.storagecabinet.DisplayHeight;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.container.CabinetManagerContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import java.util.ArrayList;

public class CabinetManagerEntity extends BlockEntity implements MenuProvider, ModifiableDisplaySize {

    public final ArrayList<StorageCabinetEntity> cabinetList = new ArrayList<>();
    private DisplayHeight displayHeight = Constants.DEFAULT_HEIGHT;
    public boolean locked = false;

    public CabinetManagerEntity(BlockPos pos, BlockState state) {
        super(StorageCabinet.CABINET_MANAGER_ENTITY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent(level.getBlockState(getBlockPos()).getBlock().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new CabinetManagerContainer(syncId, inv, getBlockPos());
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (!getLevel().getBlockTicks().willTickThisTick(getBlockPos(), StorageCabinet.CABINET_MANAGER.get()))
            getLevel().scheduleTick(getBlockPos(), StorageCabinet.CABINET_MANAGER.get(), 1, TickPriority.NORMAL);
    }

    @Override
    public DisplayHeight getDisplayHeight() {
        return displayHeight;
    }

    @Override
    public void setDisplayHeight(DisplayHeight displayHeight) {
        this.displayHeight = displayHeight;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.locked = compoundTag.getBoolean(Constants.LOCKED);
        setDisplayHeight(DisplayHeight.values()[compoundTag.getInt(Constants.DISPLAY_SIZE)]);
    }

    @Override
    public void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        prepareTag(compoundTag);
    }

    private void prepareTag(CompoundTag compoundTag) {
        compoundTag.putBoolean(Constants.LOCKED, locked);
        compoundTag.putInt(Constants.DISPLAY_SIZE, getDisplayHeight().ordinal());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        prepareTag(tag);
        return tag;
    }

    public void updateCabinetList() {
        cabinetList.clear();
        checkSurroundingCabinets(cabinetList, getBlockPos(), level);
    }

    public void checkSurroundingCabinets(ArrayList<StorageCabinetEntity> cabinetList, BlockPos pos, LevelAccessor world) {
        for (Direction direction : Direction.values()) {
            BlockPos offset = pos.offset(direction.getNormal());
            if (world.getBlockEntity(offset) instanceof StorageCabinetEntity) {
                if (!cabinetList.contains(world.getBlockEntity(offset))) {
                    StorageCabinetEntity entity = (StorageCabinetEntity) world.getBlockEntity(offset);
                    cabinetList.add(entity);
                    checkSurroundingCabinets(cabinetList, offset, world);
                }
            }
        }
    }
}
