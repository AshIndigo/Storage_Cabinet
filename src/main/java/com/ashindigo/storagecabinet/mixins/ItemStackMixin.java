package com.ashindigo.storagecabinet.mixins;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Shadow
    private boolean empty;

    @Final
    @Shadow
    private Item item;

    @Overwrite()
    public Item getItem() {
        if (this.empty || this.item == null) {
            return Items.AIR;
        }
        return this.item;
    }
}
