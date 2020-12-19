package com.ashindigo.storagecabinet.widgets;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class WScrollItemSlot extends WWidget {

    private static final Predicate<ItemStack> DEFAULT_FILTER = (stack) -> true;
    private final List<ScrollValidatedSlot> peers = new ArrayList<>();
    @Nullable
    @Environment(EnvType.CLIENT)
    private BackgroundPainter backgroundPainter = null;
    private Inventory internalInv;
    private int startIndex = 0;
    private int slotsWide = 1;
    private int slotsHigh = 1;
    private boolean insertingAllowed = true;
    private boolean takingAllowed = true;
    private int focusedSlot = -1;
    private Predicate<ItemStack> filter;
    private final Set<WScrollItemSlot.ChangeListener> listeners;

    public WScrollItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh) {
        this.filter = DEFAULT_FILTER;
        this.listeners = new HashSet<>();
        this.internalInv = inventory;
        this.startIndex = startIndex;
        this.slotsWide = slotsWide;
        this.slotsHigh = slotsHigh;
    }

    private WScrollItemSlot() {
        this.filter = DEFAULT_FILTER;
        this.listeners = new HashSet<>();
    }

    public static WScrollItemSlot of(Inventory inventory, int index) {
        WScrollItemSlot w = new WScrollItemSlot();
        w.internalInv = inventory;
        w.startIndex = index;
        return w;
    }

    public static WScrollItemSlot of(Inventory inventory, int startIndex, int slotsWide, int slotsHigh) {
        WScrollItemSlot w = new WScrollItemSlot();
        w.internalInv = inventory;
        w.startIndex = startIndex;
        w.slotsWide = slotsWide;
        w.slotsHigh = slotsHigh;
        return w;
    }

    public static WScrollItemSlot outputOf(Inventory inventory, int index) {
        WScrollItemSlot w = new WScrollItemSlot();
        w.internalInv = inventory;
        w.startIndex = index;
        return w;
    }

    public static WScrollItemSlot ofPlayerStorage(Inventory inventory) {
        WScrollItemSlot w = new WScrollItemSlot();
        w.internalInv = inventory;
        w.startIndex = 9;
        w.slotsWide = 9;
        w.slotsHigh = 3;
        return w;
    }

    public int getWidth() {
        return this.slotsWide * 18;
    }

    public int getHeight() {
        return this.slotsHigh * 18;
    }

    public boolean canFocus() {
        return true;
    }

    public boolean isModifiable() {
        return this.takingAllowed || this.insertingAllowed;
    }

    public WScrollItemSlot setModifiable(boolean modifiable) {
        this.insertingAllowed = modifiable;
        this.takingAllowed = modifiable;

        for (ScrollValidatedSlot peer : this.peers) {
            peer.setInsertingAllowed(modifiable);
            peer.setTakingAllowed(modifiable);
        }

        return this;
    }

    public boolean isInsertingAllowed() {
        return this.insertingAllowed;
    }

    public WScrollItemSlot setInsertingAllowed(boolean insertingAllowed) {
        this.insertingAllowed = insertingAllowed;

        for (ScrollValidatedSlot peer : this.peers) {
            peer.setInsertingAllowed(insertingAllowed);
        }

        return this;
    }

    public boolean isTakingAllowed() {
        return this.takingAllowed;
    }

    public WScrollItemSlot setTakingAllowed(boolean takingAllowed) {
        this.takingAllowed = takingAllowed;

        for (ScrollValidatedSlot peer : this.peers) {
            peer.setTakingAllowed(takingAllowed);
        }

        return this;
    }

    public int getFocusedSlot() {
        return this.focusedSlot;
    }

    public void validate(GuiDescription host) {
        super.validate(host);
        this.peers.clear();
        int index = this.startIndex;

        for(int y = 0; y < this.slotsHigh; ++y) {
            for(int x = 0; x < this.slotsWide; ++x) {
                ScrollValidatedSlot slot = this.createSlotPeer(this.internalInv, index, this.getAbsoluteX() + x * 18 + 1, this.getAbsoluteY() + y * 18 + 1);
                slot.setInsertingAllowed(this.insertingAllowed);
                slot.setTakingAllowed(this.takingAllowed);
                slot.setFilter(this.filter);

                for (ChangeListener listener : this.listeners) {
                    slot.addChangeListener(this, listener);
                }

                this.peers.add(slot);
                host.addSlotPeer(slot);
                ++index;
            }
        }

    }

    @Environment(EnvType.CLIENT)
    public void onKeyPressed(int ch, int key, int modifiers) {
        if (isActivationKey(ch) && this.host instanceof ScreenHandler && this.focusedSlot >= 0) {
            ScreenHandler handler = (ScreenHandler)this.host;
            MinecraftClient client = MinecraftClient.getInstance();
            ScrollValidatedSlot peer = this.peers.get(this.focusedSlot);
            client.interactionManager.clickSlot(handler.syncId, peer.id, 0, SlotActionType.PICKUP, client.player);
        }

    }

    protected ScrollValidatedSlot createSlotPeer(Inventory inventory, int index, int x, int y) {
        return new ScrollValidatedSlot(inventory, index, x, y);
    }

    @Nullable
    @Environment(EnvType.CLIENT)
    public BackgroundPainter getBackgroundPainter() {
        return this.backgroundPainter;
    }

    @Environment(EnvType.CLIENT)
    public void setBackgroundPainter(@Nullable BackgroundPainter painter) {
        this.backgroundPainter = painter;
    }

    public Predicate<ItemStack> getFilter() {
        return this.filter;
    }

    public WScrollItemSlot setFilter(Predicate<ItemStack> filter) {
        this.filter = filter;

        for (ScrollValidatedSlot peer : this.peers) {
            peer.setFilter(filter);
        }

        return this;
    }

    @Environment(EnvType.CLIENT)
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (this.backgroundPainter != null) {
            this.backgroundPainter.paintBackground(x, y, this);
        }

    }

    @Nullable
    public WWidget cycleFocus(boolean lookForwards) {
        if (this.focusedSlot < 0) {
            this.focusedSlot = lookForwards ? 0 : this.slotsWide * this.slotsHigh - 1;
            return this;
        } else if (lookForwards) {
            ++this.focusedSlot;
            if (this.focusedSlot >= this.slotsWide * this.slotsHigh) {
                this.focusedSlot = -1;
                return null;
            } else {
                return this;
            }
        } else {
            --this.focusedSlot;
            return this.focusedSlot >= 0 ? this : null;
        }
    }

    public void addChangeListener(WScrollItemSlot.ChangeListener listener) {
        Objects.requireNonNull(listener, "listener");
        this.listeners.add(listener);

        for (ScrollValidatedSlot peer : this.peers) {
            peer.addChangeListener(this, listener);
        }

    }

    public void onShown() {

        for (ScrollValidatedSlot peer : this.peers) {
            peer.setVisible(true);
        }

    }

    public void onHidden() {
        super.onHidden();
        for (ScrollValidatedSlot peer : this.peers) {
            peer.setVisible(false);
        }
    }

    public void scrollVert(int startIndex) {
            this.startIndex = startIndex;
    }

    @Environment(EnvType.CLIENT)
    public void addPainters() {
        this.backgroundPainter = BackgroundPainter.SLOT;
    }

    @FunctionalInterface
    public interface ChangeListener {
        void onStackChanged(WScrollItemSlot var1, Inventory var2, int var3, ItemStack var4);
    }
}
