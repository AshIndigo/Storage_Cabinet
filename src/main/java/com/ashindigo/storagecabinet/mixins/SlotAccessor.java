package com.ashindigo.storagecabinet.mixins;

import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotAccessor {

    @Accessor("y")
    @Mutable
    void setY(int y);

    @Accessor("y")
    int getY();

    @Accessor("x")
    @Mutable
    void setX(int x);

    @Accessor("x")
    int getX();
}
