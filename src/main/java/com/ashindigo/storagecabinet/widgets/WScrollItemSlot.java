package com.ashindigo.storagecabinet.widgets;

import com.ashindigo.storagecabinet.mixins.ValidatedSlotAccessor;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import java.util.*;
import java.util.function.Predicate;

public class WScrollItemSlot extends WWidget {

    private static final Predicate<ItemStack> DEFAULT_FILTER = (stack) -> true;
    private final List<ScrollValidatedSlot> peers = new ArrayList<>();
    private final Set<WScrollItemSlot.ChangeListener> listeners;
    private Inventory internalInv;
    private int startIndex = 0;
    private int slotsWide = 1;
    private int slotsHigh = 1;
    private boolean insertingAllowed = true;
    private boolean takingAllowed = true;
    private int focusedSlot = -1;
    
    @Environment(EnvType.CLIENT)
    private BackgroundPainter backgroundPainter = (left, top, panel) -> { // Yes this is just BackgroundPainter.SLOT with some small modifications to it
        WScrollItemSlot slot = (WScrollItemSlot) panel;
        for (int x = 0; x < slot.getWidth() / 18; ++x) {
            for (int y = 0; y < slot.getHeight() / 18; ++y) {
                int index = x + y * (slot.getWidth() / 18);
                int lo = 0xB8000000;
                int bg = 0x4C000000;
                int hi = 0xB8FFFFFF;
                ScreenDrawing.drawBeveledPanel((x * 18) + left, (y * 18) + top, 16 + 2, 16 + 2, lo, bg, hi);
                if (slot.getFocusedSlot() == index) {
                    int sx = (x * 18) + left;
                    int sy = (y * 18) + top;
                    ScreenDrawing.coloredRect(sx, sy, 18, 1, 0xFF_FFFFA0);
                    ScreenDrawing.coloredRect(sx, sy + 1, 1, 18 - 1, 0xFF_FFFFA0);
                    ScreenDrawing.coloredRect(sx + 18 - 1, sy + 1, 1, 18 - 1, 0xFF_FFFFA0);
                    ScreenDrawing.coloredRect(sx + 1, sy + 18 - 1, 18 - 1, 1, 0xFF_FFFFA0);
                }

            }
        }
    };
    private Predicate<ItemStack> filter;

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

        for (int y = 0; y < this.slotsHigh; ++y) {
            for (int x = 0; x < this.slotsWide; ++x) {
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
            ScreenHandler handler = (ScreenHandler) this.host;
            MinecraftClient client = MinecraftClient.getInstance();
            ScrollValidatedSlot peer = this.peers.get(this.focusedSlot);
            client.interactionManager.clickSlot(handler.syncId, peer.id, 0, SlotActionType.PICKUP, client.player);
        }

    }

    protected ScrollValidatedSlot createSlotPeer(Inventory inventory, int index, int x, int y) {
        return new ScrollValidatedSlot(inventory, index, x, y, getX()+((WItemScrollPanel)getParent()).getBoundOffsetX(), getY()+((WItemScrollPanel)getParent()).getBoundOffsetY(), getX() + getParent().getWidth()+((WItemScrollPanel)getParent()).getBoundOffsetX(), getY() + getParent().getHeight()+((WItemScrollPanel)getParent()).getBoundOffsetY());
    }

    
    @Environment(EnvType.CLIENT)
    public BackgroundPainter getBackgroundPainter() {
        return this.backgroundPainter;
    }

    @Environment(EnvType.CLIENT)
    public void setBackgroundPainter( BackgroundPainter painter) {
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
        for (ScrollValidatedSlot peer : peers) {
            ValidatedSlotAccessor slot = ((ValidatedSlotAccessor) peer);
            slot.setY(slot.getOrigY() - (18 * startIndex));
        }
    }

    @FunctionalInterface
    public interface ChangeListener {
        void onStackChanged(WScrollItemSlot var1, Inventory var2, int var3, ItemStack var4);
    }
}
