//package com.ashindigo.storagecabinet.widgets.disabled;
//
//import io.github.cottonmc.cotton.gui.widget.*;
//import io.github.cottonmc.cotton.gui.widget.data.Axis;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.fabricmc.fabric.api.util.TriState;
//import net.minecraft.client.util.math.MatrixStack;
//
//public class WItemScrollPanel extends WClippedPanel {
//
//    private static final int SCROLL_BAR_SIZE = 8;
//    private final WBetterItemSlot widget;
//    private TriState scrollingHorizontally;
//    private TriState scrollingVertically;
//    protected WScrollBar horizontalScrollBar;
//    protected WScrollBar verticalScrollBar;
//    private int lastHorizontalScroll;
//    private int lastVerticalScroll;
//
//    public WItemScrollPanel(WBetterItemSlot widget) {
//        this.scrollingHorizontally = TriState.DEFAULT;
//        this.scrollingVertically = TriState.DEFAULT;
//        this.horizontalScrollBar = new WScrollBar(Axis.HORIZONTAL);
//        this.verticalScrollBar = new WScrollBar(Axis.VERTICAL);
//        this.lastHorizontalScroll = -1;
//        this.lastVerticalScroll = -1;
//        this.widget = widget;
//        widget.setParent(this);
//        this.horizontalScrollBar.setParent(this);
//        this.verticalScrollBar.setParent(this);
//        this.children.add(widget);
//        this.children.add(this.verticalScrollBar);
//    }
//
//    public TriState isScrollingHorizontally() {
//        return this.scrollingHorizontally;
//    }
//
//    public WItemScrollPanel setScrollingHorizontally(TriState scrollingHorizontally) {
//        if (scrollingHorizontally != this.scrollingHorizontally) {
//            this.scrollingHorizontally = scrollingHorizontally;
//            this.layout();
//        }
//
//        return this;
//    }
//
//    public TriState isScrollingVertically() {
//        return this.scrollingVertically;
//    }
//
//    public WItemScrollPanel setScrollingVertically(TriState scrollingVertically) {
//        if (scrollingVertically != this.scrollingVertically) {
//            this.scrollingVertically = scrollingVertically;
//            this.layout();
//        }
//
//        return this;
//    }
//
//    @Environment(EnvType.CLIENT)
//    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
//        if (this.verticalScrollBar.getValue() != this.lastVerticalScroll || this.horizontalScrollBar.getValue() != this.lastHorizontalScroll) {
//            this.layout();
//            this.lastHorizontalScroll = this.horizontalScrollBar.getValue();
//            this.lastVerticalScroll = this.verticalScrollBar.getValue();
//        }
//
//        super.paint(matrices, x, y, mouseX, mouseY);
//    }
//
//    public void layout() {
//        this.children.clear();
//        boolean horizontal = this.scrollingHorizontally == TriState.DEFAULT ? this.widget.getWidth() > this.width - SCROLL_BAR_SIZE : this.scrollingHorizontally.get();
//        boolean vertical = this.scrollingVertically == TriState.DEFAULT ? this.widget.getHeight() > this.height - SCROLL_BAR_SIZE : this.scrollingVertically.get();
//        int offset = horizontal && vertical ? SCROLL_BAR_SIZE : 0;
//        this.verticalScrollBar.setSize(SCROLL_BAR_SIZE, this.height - offset);
//        this.verticalScrollBar.setLocation(this.width - this.verticalScrollBar.getWidth(), 0);
//        this.horizontalScrollBar.setSize(this.width - offset, SCROLL_BAR_SIZE);
//        this.horizontalScrollBar.setLocation(0, this.height - this.horizontalScrollBar.getHeight());
//        this.children.add(this.widget);
//        int x = horizontal ? -this.horizontalScrollBar.getValue() : 0;
//        int y = vertical ? -this.verticalScrollBar.getValue() : 0;
//        this.widget.setLocation(x, y); // TODO Need to tell the item slots which ones are enabled and which arent based on position
//        widget.updateSlots(x,y);
//        this.verticalScrollBar.setWindow(this.height - (horizontal ? SCROLL_BAR_SIZE : 0));
//        this.verticalScrollBar.setMaxValue(this.widget.getHeight());
//        this.horizontalScrollBar.setWindow(this.width - (vertical ? SCROLL_BAR_SIZE : 0));
//        this.horizontalScrollBar.setMaxValue(this.widget.getWidth());
//        if (vertical) {
//            this.children.add(this.verticalScrollBar);
//        }
//
//        if (horizontal) {
//            this.children.add(this.horizontalScrollBar);
//        }
//
//    }
//}
