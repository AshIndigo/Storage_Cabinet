//package com.ashindigo.storagecabinet.widgets.disabled;
//
//import io.github.cottonmc.cotton.gui.GuiDescription;
//import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
//import io.github.cottonmc.cotton.gui.widget.WWidget;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.inventory.Inventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.screen.ScreenHandler;
//import net.minecraft.screen.slot.SlotActionType;
//
//import javax.annotation.Nullable;
//import java.util.*;
//import java.util.function.Predicate;
//
//public class WBetterItemSlot extends WWidget {
//
//    private static final Predicate<ItemStack> DEFAULT_FILTER = (stack) -> true;
//    private final List<BetterValidatedSlot> peers = new ArrayList();
//    @Nullable
//    @Environment(EnvType.CLIENT)
//    private BackgroundPainter backgroundPainter = null;
//    private Inventory inventory;
//    private int startIndex = 0;
//    private int slotsWide = 1;
//    private int slotsHigh = 1;
//    private boolean big = false;
//    private boolean insertingAllowed = true;
//    private boolean takingAllowed = true;
//    private int focusedSlot = -1;
//    private Predicate<ItemStack> filter;
//    private final Set<WBetterItemSlot.ChangeListener> listeners;
//
//    public WBetterItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big) {
//        this.filter = DEFAULT_FILTER;
//        this.listeners = new HashSet();
//        this.inventory = inventory;
//        this.startIndex = startIndex;
//        this.slotsWide = slotsWide;
//        this.slotsHigh = slotsHigh;
//        this.big = big;
//    }
//
//    private WBetterItemSlot() {
//        this.filter = DEFAULT_FILTER;
//        this.listeners = new HashSet();
//    }
//
//    public static WBetterItemSlot of(Inventory inventory, int index) {
//        WBetterItemSlot w = new WBetterItemSlot();
//        w.inventory = inventory;
//        w.startIndex = index;
//        return w;
//    }
//
//    public static WBetterItemSlot of(Inventory inventory, int startIndex, int slotsWide, int slotsHigh) {
//        WBetterItemSlot w = new WBetterItemSlot();
//        w.inventory = inventory;
//        w.startIndex = startIndex;
//        w.slotsWide = slotsWide;
//        w.slotsHigh = slotsHigh;
//        return w;
//    }
//
//    public static WBetterItemSlot outputOf(Inventory inventory, int index) {
//        WBetterItemSlot w = new WBetterItemSlot();
//        w.inventory = inventory;
//        w.startIndex = index;
//        w.big = true;
//        return w;
//    }
//
//    public static WBetterItemSlot ofPlayerStorage(Inventory inventory) {
//        WBetterItemSlot w = new WBetterItemSlot();
//        w.inventory = inventory;
//        w.startIndex = 9;
//        w.slotsWide = 9;
//        w.slotsHigh = 3;
//        return w;
//    }
//
//    public int getWidth() {
//        return this.slotsWide * 18;
//    }
//
//    public int getHeight() {
//        return this.slotsHigh * 18;
//    }
//
//    public boolean canFocus() {
//        return true;
//    }
//
//    public boolean isBigSlot() {
//        return this.big;
//    }
//
//    public boolean isModifiable() {
//        return this.takingAllowed || this.insertingAllowed;
//    }
//
//    public WBetterItemSlot setModifiable(boolean modifiable) {
//        this.insertingAllowed = modifiable;
//        this.takingAllowed = modifiable;
//
//        for (BetterValidatedSlot peer : this.peers) {
//            peer.setInsertingAllowed(modifiable);
//            peer.setTakingAllowed(modifiable);
//        }
//
//        return this;
//    }
//
//    public boolean isInsertingAllowed() {
//        return this.insertingAllowed;
//    }
//
//    public WBetterItemSlot setInsertingAllowed(boolean insertingAllowed) {
//        this.insertingAllowed = insertingAllowed;
//
//        for (BetterValidatedSlot peer : this.peers) {
//            peer.setInsertingAllowed(insertingAllowed);
//        }
//
//        return this;
//    }
//
//    public boolean isTakingAllowed() {
//        return this.takingAllowed;
//    }
//
//    public WBetterItemSlot setTakingAllowed(boolean takingAllowed) {
//        this.takingAllowed = takingAllowed;
//
//        for (BetterValidatedSlot peer : this.peers) {
//            peer.setTakingAllowed(takingAllowed);
//        }
//
//        return this;
//    }
//
//    public int getFocusedSlot() {
//        return this.focusedSlot;
//    }
//
//    public void validate(GuiDescription host) {
//        super.validate(host);
//        this.peers.clear();
//        int index = this.startIndex;
//
//        for(int y = 0; y < this.slotsHigh; ++y) {
//            for(int x = 0; x < this.slotsWide; ++x) {
//                BetterValidatedSlot slot = this.createSlotPeer(this.inventory, index, this.getAbsoluteX() + x * 18 + 1, this.getAbsoluteY() + y * 18 + 1);
//                slot.setInsertingAllowed(this.insertingAllowed);
//                slot.setTakingAllowed(this.takingAllowed);
//                slot.setFilter(this.filter);
//
//                for (ChangeListener listener : this.listeners) {
//                    slot.addChangeListener(this, listener);
//                }
//
//                this.peers.add(slot);
//                host.addSlotPeer(slot);
//                ++index;
//            }
//        }
//
//    }
//
//    @Environment(EnvType.CLIENT)
//    public void onKeyPressed(int ch, int key, int modifiers) {
//        if (isActivationKey(ch) && this.host instanceof ScreenHandler && this.focusedSlot >= 0) {
//            ScreenHandler handler = (ScreenHandler)this.host;
//            MinecraftClient client = MinecraftClient.getInstance();
//            BetterValidatedSlot peer = (BetterValidatedSlot)this.peers.get(this.focusedSlot);
//            client.interactionManager.clickSlot(handler.syncId, peer.id, 0, SlotActionType.PICKUP, client.player);
//        }
//
//    }
//
//    protected BetterValidatedSlot createSlotPeer(Inventory inventory, int index, int x, int y) {
//        return new BetterValidatedSlot(inventory, index, x, y);
//    }
//
//    @Nullable
//    @Environment(EnvType.CLIENT)
//    public BackgroundPainter getBackgroundPainter() {
//        return this.backgroundPainter;
//    }
//
//    @Environment(EnvType.CLIENT)
//    public void setBackgroundPainter(@Nullable BackgroundPainter painter) {
//        this.backgroundPainter = painter;
//    }
//
//    public Predicate<ItemStack> getFilter() {
//        return this.filter;
//    }
//
//    public WBetterItemSlot setFilter(Predicate<ItemStack> filter) {
//        this.filter = filter;
//
//        for (BetterValidatedSlot peer : this.peers) {
//            peer.setFilter(filter);
//        }
//
//        return this;
//    }
//
//    @Environment(EnvType.CLIENT)
//    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
//        if (this.backgroundPainter != null) {
//            this.backgroundPainter.paintBackground(x, y, this);
//        }
//
//    }
//
//    @Nullable
//    public WWidget cycleFocus(boolean lookForwards) {
//        if (this.focusedSlot < 0) {
//            this.focusedSlot = lookForwards ? 0 : this.slotsWide * this.slotsHigh - 1;
//            return this;
//        } else if (lookForwards) {
//            ++this.focusedSlot;
//            if (this.focusedSlot >= this.slotsWide * this.slotsHigh) {
//                this.focusedSlot = -1;
//                return null;
//            } else {
//                return this;
//            }
//        } else {
//            --this.focusedSlot;
//            return this.focusedSlot >= 0 ? this : null;
//        }
//    }
//
//    public void addChangeListener(WBetterItemSlot.ChangeListener listener) {
//        Objects.requireNonNull(listener, "listener");
//        this.listeners.add(listener);
//
//        for (BetterValidatedSlot peer : this.peers) {
//            peer.addChangeListener(this, listener);
//        }
//
//    }
//
//    public void onShown() {
//
//        for (BetterValidatedSlot peer : this.peers) {
//            peer.setVisible(true);
//        }
//
//    }
//
//    public void onHidden() {
//        super.onHidden();
//
//        for (BetterValidatedSlot peer : this.peers) {
//            peer.setVisible(false);
//        }
//
//    }
//
//    @Environment(EnvType.CLIENT)
//    public void addPainters() {
//        this.backgroundPainter = BackgroundPainter.SLOT;
//    }
//
//    @FunctionalInterface
//    public interface ChangeListener {
//        void onStackChanged(WBetterItemSlot var1, Inventory var2, int var3, ItemStack var4);
//    }
//
//
//    public void updateSlots(int x, int y) {
//        for (BetterValidatedSlot slot : peers) {
//            slot.setEnabled(withinBounds(x,y ,slot.originalX , slot.originalY));
//        }
//    }
//
//    public boolean withinBounds(int startX, int startY, int wX, int wY) {
//        if (startX < wX && startX+getWidth() > wX) {
//            if (startY < wY && startY+getHeight() > wY) {
//                return true;
//            }
//        }
//        return false;
//    }
//}
