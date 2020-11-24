//package com.ashindigo.storagecabinet.widgets;
//
//import net.minecraft.client.render.VertexConsumerProvider;
//import net.minecraft.client.util.math.MatrixStack;
//import spinnery.client.render.BaseRenderer;
//import spinnery.client.utility.ScissorArea;
//import spinnery.common.utility.MouseUtilities;
//import spinnery.widget.WAbstractWidget;
//import spinnery.widget.WVerticalArrowDown;
//import spinnery.widget.WVerticalArrowUp;
//import spinnery.widget.WVerticalScrollableContainer;
//import spinnery.widget.api.Color;
//import spinnery.widget.api.Position;
//import spinnery.widget.api.Size;
//
//import java.util.Arrays;
//
///**
// * A rather dirty hack to allow for division spaces of 0, and so allows me to control positioning more
// */
//public class WVerticalScrollableContainerModified extends WVerticalScrollableContainer {
//
//    @Override
//    public void addRow(WAbstractWidget... widgetArray) {
//        float maxY = 0;
//        for (WAbstractWidget widget : getWidgets()) {
//            if (widget.getOffsetY() > maxY) {
//                maxY = widget.getOffsetY() + widget.getHeight();
//            }
//        }
//
//        for (WAbstractWidget widget : widgetArray) {
//            widget.setParent(this);
//            widget.setInterface(getInterface());
//        }
//        widgets.addAll(Arrays.asList(widgetArray));
//        onLayoutChange();
//        updateChildren();
//        updateChildrenFocus();
//    }
//
//    @Override
//    public void draw(MatrixStack matrices, VertexConsumerProvider provider) {
//        if (isHidden()) {
//            return;
//        }
//
//        if (isDragScrolling()) {
//            scroll(0, Math.pow(5, Math.abs(((MouseUtilities.mouseY - lastDragScrollY) / 100))) * ((System.currentTimeMillis() - lastDragScrollMilliseconds) * dragScrollAccelerationCoefficient) * (lastDragScrollY - MouseUtilities.mouseY > 0 ? 1 : -1));
//        }
//
//        if (kineticScrollDelta > 0.05 || kineticScrollDelta < -0.05) {
//            kineticScrollDelta = kineticScrollDelta / getKineticReductionCoefficient();
//
//            scroll(0, kineticScrollDelta * kineticReductionCoefficient * getKineticAccelerationCoefficient());
//
//            updateChildrenFocus();
//        } else {
//            kineticScrollDelta = 0;
//
//            lastScrollX = 0;
//            lastScrollY = 0;
//        }
//
//        ScissorArea area = new ScissorArea(provider, this);
//
//        for (WAbstractWidget widget : getWidgets()) {
//            widget.draw(matrices, provider);
//        }
//
//        area.destroy(provider);
//
//        if (hasFade()) {
//            Color fadeOut = getStyle().asColor("background");
//            fadeOut = Color.of("0x00" + Integer.toHexString((int) (fadeOut.R * 255)) + Integer.toHexString((int) (fadeOut.G * 255)) + Integer.toHexString((int) (fadeOut.B * 255)));
//
//            if (offsetY > 1) {
//                BaseRenderer.drawGradientQuad(matrices, provider, getX(), getY() - 1, getWideX() - getScrollbarWidth(), getY() + getFadeSpace() - 6, getZ(), getStyle().asColor("background"), fadeOut);
//                BaseRenderer.drawGradientQuad(matrices, provider, getX(), getY() - 1, getWideX() - getScrollbarWidth(), getY() + getFadeSpace() - 3, getZ(), getStyle().asColor("background"), fadeOut);
//                BaseRenderer.drawGradientQuad(matrices, provider, getX(), getY() - 1, getWideX() - getScrollbarWidth(), getY() + getFadeSpace(), getZ(), getStyle().asColor("background"), fadeOut);
//            }
//
//            if (getBottomWidgetY() > getHighY()) {
//                BaseRenderer.drawGradientQuad(matrices, provider, getX(), getHighY() - getFadeSpace() + 6, getWideX() - getScrollbarWidth(), getHighY() + 1, getZ(), fadeOut, getStyle().asColor("background"));
//                BaseRenderer.drawGradientQuad(matrices, provider, getX(), getHighY() - getFadeSpace() + 3, getWideX() - getScrollbarWidth(), getHighY() + 1, getZ(), fadeOut, getStyle().asColor("background"));
//                BaseRenderer.drawGradientQuad(matrices, provider, getX(), getHighY() - getFadeSpace(), getWideX() - getScrollbarWidth(), getHighY() + 1, getZ(), fadeOut, getStyle().asColor("background"));
//            }
//        }
//
//        scrollbar.draw(matrices, provider);
//
//        if (hasArrows()) {
//            verticalArrowUp.draw(matrices, provider);
//            verticalArrowDown.draw(matrices, provider);
//        }
//    }
//
//    @Override
//    public void updateScrollbar() {
//        float scrollBarWidth = getScrollbarWidth();
//        float scrollBarHeight = getHeight();
//
//        float scrollBarOffsetX = getWidth() - scrollBarWidth - getBorderSpace() - 4;
//        float scrollBarOffsetY = getBorderSpace();
//
//        if (hasArrows) {
//            scrollBarOffsetY += scrollbarWidth - 1;
//            scrollBarHeight -= scrollbarWidth * 2;
//
//            scrollBarHeight = Math.abs(scrollBarHeight);
//
//            if (verticalArrowUp == null)
//                verticalArrowUp = new WVerticalArrowUp().setScrollable(this).setPosition(Position.of(this, scrollBarOffsetX, 0, 0)).setSize(Size.of(scrollBarWidth));
//            else
//                verticalArrowUp.setPosition(Position.of(this, scrollBarOffsetX, 0, 0)).setSize(Size.of(scrollBarWidth));
//            if (verticalArrowDown == null)
//                verticalArrowDown = new WVerticalArrowDown().setScrollable(this).setPosition(Position.of(this, scrollBarOffsetX, scrollBarHeight + scrollbarWidth - 2, 0)).setSize(Size.of(scrollBarWidth));
//            else
//                verticalArrowDown.setPosition(Position.of(this, scrollBarOffsetX, scrollBarHeight + scrollBarWidth - 2, 0)).setSize(Size.of(scrollBarWidth));
//        } else {
//            verticalArrowUp = null;
//            verticalArrowDown = null;
//        }
//
//        scrollbar.setPosition(Position.of(this, scrollBarOffsetX, scrollBarOffsetY, 0));
//        scrollbar.setSize(Size.of(scrollBarWidth, scrollBarHeight - (2 * getBorderSpace())));
//    }
//}
