package com.ashindigo.storagecabinet.widgets;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import net.minecraft.inventory.Inventory;

import java.util.Objects;

public class ScrollValidatedSlot extends ValidatedSlot {

    protected final Multimap<WScrollItemSlot, WScrollItemSlot.ChangeListener> listeners = HashMultimap.create();
    private final int boundUX;
    private final int boundUY;
    private final int boundLX;
    private final int boundLY;

    public ScrollValidatedSlot(Inventory inventory, int index, int x, int y, int boundLX, int boundLY, int boundUX, int boundUY) {
        super(inventory, index, x, y);
        this.boundUX = boundUX;
        this.boundUY = boundUY;
        this.boundLX = boundLX;
        this.boundLY = boundLY;
    }

    public void addChangeListener(WScrollItemSlot owner, WScrollItemSlot.ChangeListener listener) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(listener, "listener");
        this.listeners.put(owner, listener);
    }

    @Override
    public void markDirty() {
        this.listeners.forEach((slot, listener) -> listener.onStackChanged(slot, this.inventory, this.getInventoryIndex(), this.getStack()));
        super.markDirty();
    }

    @Override
    public boolean isEnabled() {
        if (boundUX >= x && x >= boundLX) {
            return boundUY >= y && y >= boundLY;
        }
        return false;
    }
}
