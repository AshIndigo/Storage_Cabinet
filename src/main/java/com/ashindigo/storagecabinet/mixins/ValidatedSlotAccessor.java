package com.ashindigo.storagecabinet.mixins;

import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.impl.access.SlotAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ValidatedSlot.class)
public interface ValidatedSlotAccessor extends SlotAccessor {

    @Accessor("originalX")
    int getOrigX();

    @Accessor("originalY")
    int getOrigY();

}
