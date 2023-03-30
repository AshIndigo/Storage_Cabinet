package com.ashindigo.storagecabinet.forge;

import com.ashindigo.storagecabinet.block.CabinetManagerBlock;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class CabinetManagerEntityForge extends CabinetManagerEntity {

    private LazyOptional<IItemHandlerModifiable> handler;

    public CabinetManagerEntityForge(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!this.remove && cap == ForgeCapabilities.ITEM_HANDLER) {
            if (this.handler == null) {
                this.handler = LazyOptional.of(this::createHandler);
            }
            return this.handler.cast();
        } else {
            return super.getCapability(cap, side);
        }
    }

    private IItemHandlerModifiable createHandler() {
        return new InvWrapper(((CabinetManagerBlock) getBlockState().getBlock()).getContainer(getBlockState(), getLevel(), getBlockPos()));
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        if (this.handler != null) {
            this.handler.invalidate();
        }
    }
}
