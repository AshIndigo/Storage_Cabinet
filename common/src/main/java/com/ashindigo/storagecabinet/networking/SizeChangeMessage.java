package com.ashindigo.storagecabinet.networking;

import com.ashindigo.storagecabinet.DisplayHeight;
import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.entity.ModifiableDisplaySize;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class SizeChangeMessage extends BaseC2SMessage {

    DisplayHeight displayHeight;
    BlockPos blockPos;

    public SizeChangeMessage(DisplayHeight displayHeight, BlockPos blockPos) {
        this.displayHeight = displayHeight;
        this.blockPos = blockPos;
    }

    public SizeChangeMessage(FriendlyByteBuf friendlyByteBuf) {
        displayHeight = DisplayHeight.values()[friendlyByteBuf.readInt()];
        blockPos = friendlyByteBuf.readBlockPos();
    }

    @Override
    public MessageType getType() {
        return StorageCabinet.CHANGE_SIZE;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(displayHeight.ordinal());
        buf.writeBlockPos(blockPos);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        ((ModifiableDisplaySize) context.getPlayer().level.getBlockEntity(blockPos)).setDisplayHeight(displayHeight);
        context.getPlayer().level.getBlockEntity(blockPos).setChanged();
    }
}
