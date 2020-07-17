package com.ashindigo.storagecabinet.widgets;

import net.minecraft.item.ItemStack;
import spinnery.widget.WSlot;

import java.util.Arrays;

/**
 * A quick hack to make it so it uses my isValidInvStack code
 */
public class WSlotCabinet extends WSlot {

    @Override
    public boolean accepts(ItemStack... stacks) {
        if (isWhitelist) {
            return Arrays.stream(stacks).allMatch(stack -> this.getLinkedInventory().isValid(0, stack));
        } else {
            return Arrays.stream(stacks).noneMatch(stack -> (denyItems.contains(stack.getItem()) || denyTags.stream().anyMatch(tag -> tag.contains(stack.getItem()))));
        }
    }

}
