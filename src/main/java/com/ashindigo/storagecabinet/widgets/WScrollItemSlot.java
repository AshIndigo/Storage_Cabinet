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
    @Environment(EnvType.CLIENT)
    public static BackgroundPainter backgroundPainter = (matrices, left, top, panel) -> {
        if (panel instanceof WScrollItemSlot slot) {
            for (int x = 0; x < slot.getWidth() / 18; ++x) {
                for (int y = 0; y < slot.getHeight() / 18; ++y) {
                    int index = x + y * (slot.getWidth() / 18);
                    int lo = 0xB8000000;
                    int bg = 0x4C000000;
                    int hi = 0xB8FFFFFF;
                    ScreenDrawing.drawBeveledPanel(matrices, (x * 18) + left, (y * 18) + top, 16 + 2, 16 + 2, lo, bg, hi);
                    if (slot.getFocusedSlot() == index) {
                        int sx = (x * 18) + left;
                        int sy = (y * 18) + top;
                        ScreenDrawing.coloredRect(matrices, sx, sy, 18, 1, 0xFF_FFFFA0);
                        ScreenDrawing.coloredRect(matrices, sx, sy + 1, 1, 18 - 1, 0xFF_FFFFA0);
                        ScreenDrawing.coloredRect(matrices, sx + 18 - 1, sy + 1, 1, 18 - 1, 0xFF_FFFFA0);
                        ScreenDrawing.coloredRect(matrices, sx + 1, sy + 18 - 1, 18 - 1, 1, 0xFF_FFFFA0);
                    }
                }
            }
        }
    };
    private final List<ScrollValidatedSlot> peers = new ArrayList<>();
    private final Set<WScrollItemSlot.ChangeListener> listeners;
    private final Inventory internalInv;
    private final int startIndex;
    private final int slotsWide;
    private final int slotsHigh;
    private int focusedSlot = -1;
    private Predicate<ItemStack> filter;

    public WScrollItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh) {
        this.filter = DEFAULT_FILTER;
        this.listeners = new HashSet<>();
        this.internalInv = inventory;
        this.startIndex = startIndex;
        this.slotsWide = slotsWide;
        this.slotsHigh = slotsHigh;
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
                slot.setInsertingAllowed(true);
                slot.setTakingAllowed(true);
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
        if (isActivationKey(ch) && this.host instanceof ScreenHandler handler && this.focusedSlot >= 0) {
            MinecraftClient client = MinecraftClient.getInstance();
            ScrollValidatedSlot peer = this.peers.get(this.focusedSlot);
            client.interactionManager.clickSlot(handler.syncId, peer.id, 0, SlotActionType.PICKUP, client.player);
        }

    }

    protected ScrollValidatedSlot createSlotPeer(Inventory inventory, int index, int x, int y) {
        return new ScrollValidatedSlot(inventory, index, x, y, getX() + ((WItemScrollPanel) getParent()).getBoundOffsetX(), getY() + ((WItemScrollPanel) getParent()).getBoundOffsetY(), getX() + getParent().getWidth() + ((WItemScrollPanel) getParent()).getBoundOffsetX(), getY() + getParent().getHeight() + ((WItemScrollPanel) getParent()).getBoundOffsetY());
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
        if (backgroundPainter != null) {
            backgroundPainter.paintBackground(matrices, x, y, this);
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

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onShown() {
        for (ScrollValidatedSlot peer : this.peers) {
            peer.setVisible(true);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
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
