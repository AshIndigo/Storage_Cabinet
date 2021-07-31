package com.ashindigo.storagecabinet.mixins;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotAccessor {

    @Accessor("y")
    void setY(int y);

    @Accessor("y")
    int getY();
}
