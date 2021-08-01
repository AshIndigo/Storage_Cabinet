package com.ashindigo.storagecabinet.widgets;

import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.util.math.MatrixStack;

// No this isn't meant for general use, more of my own purpose
public class WItemScrollPanel extends WClippedPanel {

    private static final int SCROLL_BAR_SIZE = 8;
    private final WScrollItemSlot widget;
    private TriState scrollingHorizontally = TriState.DEFAULT;
    private TriState scrollingVertically = TriState.DEFAULT;
    protected WScrollBar horizontalScrollBar = new WScrollBar(Axis.HORIZONTAL);
    protected WScrollBar verticalScrollBar = new WScrollBar(Axis.VERTICAL);
    private int lastHorizontalScroll = -1;
    private int lastVerticalScroll = -1;

    public WItemScrollPanel(WScrollItemSlot widget) {
        verticalScrollBar.setMaxValue(widget.getHeight() / 18);
        horizontalScrollBar.setMaxValue(widget.getWidth() / 18);
        this.widget = widget;
        widget.setParent(this);
        this.horizontalScrollBar.setParent(this);
        this.verticalScrollBar.setParent(this);
        this.children.add(widget);
        this.children.add(this.verticalScrollBar);
    }

    public TriState isScrollingHorizontally() {
        return this.scrollingHorizontally;
    }

    public WItemScrollPanel setScrollingHorizontally(TriState scrollingHorizontally) {
        if (scrollingHorizontally != this.scrollingHorizontally) {
            this.scrollingHorizontally = scrollingHorizontally;
            this.layout();
        }

        return this;
    }

    public TriState isScrollingVertically() {
        return this.scrollingVertically;
    }

    public WItemScrollPanel setScrollingVertically(TriState scrollingVertically) {
        if (scrollingVertically != this.scrollingVertically) {
            this.scrollingVertically = scrollingVertically;
            this.layout();
        }

        return this;
    }

    @Environment(EnvType.CLIENT)
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (this.verticalScrollBar.getValue() != this.lastVerticalScroll || this.horizontalScrollBar.getValue() != this.lastHorizontalScroll) {
            this.layout();
            this.lastHorizontalScroll = this.horizontalScrollBar.getValue();
            this.lastVerticalScroll = this.verticalScrollBar.getValue();
        }

        super.paint(matrices, x, y, mouseX, mouseY);
    }

    @Override
    public InputResult onMouseScroll(int x, int y, double amount) {
        return verticalScrollBar.onMouseScroll(0, 0, amount);
    }

    @Override
    public void layout() {
        this.children.clear();
        boolean horizontal = this.scrollingHorizontally == TriState.DEFAULT ? this.widget.getWidth() > this.width - SCROLL_BAR_SIZE : this.scrollingHorizontally.get();
        boolean vertical = this.scrollingVertically == TriState.DEFAULT ? this.widget.getHeight() > this.height - SCROLL_BAR_SIZE : this.scrollingVertically.get();
        int offset = horizontal && vertical ? SCROLL_BAR_SIZE : 0;
        this.verticalScrollBar.setSize(SCROLL_BAR_SIZE, this.height - offset);
        this.verticalScrollBar.setLocation(this.width - this.verticalScrollBar.getWidth(), 0);
        this.horizontalScrollBar.setSize(this.width - offset, SCROLL_BAR_SIZE);
        this.horizontalScrollBar.setLocation(0, this.height - this.horizontalScrollBar.getHeight());
        this.children.add(this.widget);
        widget.scrollVert(verticalScrollBar.getValue());
        verticalScrollBar.setWindow(10);
        horizontalScrollBar.setWindow(10); // ??
        if (vertical) {
            this.children.add(this.verticalScrollBar);
        }
        if (horizontal) {
            this.children.add(this.horizontalScrollBar);
        }
    }

    public WScrollBar getVerticalScrollBar() {
        return verticalScrollBar;
    }

    public int getBoundOffsetY() {
        return parent instanceof WCardPanel ? 46 : 0;
    }

    public int getBoundOffsetX() {
        return parent instanceof WCardPanel ? 0 : 0; // 25
    }
}
