package com.ashindigo.storagecabinet.mixins;

import io.github.cottonmc.cotton.gui.ValidatedSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ValidatedSlot.class)
public interface ValidatedSlotAccessor extends SlotAccessor {

    @Accessor("originalY")
    int getOrigY();
}
