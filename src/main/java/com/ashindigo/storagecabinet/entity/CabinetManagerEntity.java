package com.ashindigo.storagecabinet.entity;

import com.ashindigo.storagecabinet.BlockRegistry;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.description.CabinetManagerDescription;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class CabinetManagerEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    public CabinetManagerEntity(BlockPos pos, BlockState state) {
        super(StorageCabinet.cabinetManagerEntity, pos, state);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(BlockRegistry.CABINET_MANAGER.getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CabinetManagerDescription(syncId, inv, ScreenHandlerContext.create(world, pos));
    }
}
