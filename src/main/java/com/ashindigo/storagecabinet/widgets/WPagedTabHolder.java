package com.ashindigo.storagecabinet.widgets;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import spinnery.widget.*;
import spinnery.widget.api.Position;
import spinnery.widget.api.Size;
import java.util.ArrayList;

/**
 * A tab holder that splits tabs into pages, only works with item/sprite icons as far as I know
 *
 * @author Ash Indigo
 */
@SuppressWarnings({"unchecked"})
public class WPagedTabHolder extends WTabHolder {

    int page = 0;
    boolean hasButtons = true;
    WButton next;
    WButton back;

    @Override
    public <W extends WAbstractWidget> W setInterface(WInterface linkedInterface) {
        this.linkedInterface = linkedInterface;
        next = getInterface().createChild(WButton::new).setLabel(">").setSize(Size.of(10, 10)).setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            page = Math.min(getPageCount()-1, page + 1);
            updateTabs();
        });
        back = getInterface().createChild(WButton::new).setLabel("<").setSize(Size.of(10, 10)).setOnMouseClicked((widget, mouseX, mouseY, mouseButton) -> {
            page = Math.max(0, page - 1);
            updateTabs();
        });
        return (W) this;
    }

    @Override
    protected void updateTabs() {
        if (next != null && back != null) {
            if (getPageCount() > 1) {
                hasButtons = true;
                next.setPosition(Position.of(this, getWidth() - 10, -10, 0)).setHidden(false);
                back.setPosition(Position.of(this, getWidth() - 20, -10, 0)).setHidden(false);
            } else {
                hasButtons = false;
                next.setHidden(true);
                back.setHidden(true);
            }
        }
        // Copy and pasted from updateTabs();
        if (tabs.size() == 0) return;
        float tabSize = (float) 24;
        float tabOffset = 0;
        for (WTab tab : tabs) {
            tab.setHidden(true);
        }
        for (WTab tab : getTabsOnPage(page)) {
            tab.setWidth(tabSize);
            tab.setPosition(Position.of(this, tabOffset, 0, 0));
            WTabToggle toggle = tab.getToggle();
            toggle.setWidth(tabSize);
            toggle.setPosition(Position.of(this, tabOffset, 0, 0));
            tabOffset += tabSize;
            tab.setHidden(false);
        }
        selectTab(tabs.get(getTabsPerPage() * page).getNumber());
    }

    @Override
    public void onLayoutChange() {
        super.onLayoutChange();
        updateTabs();
    }

    private Iterable<WTab> getTabsOnPage(int page) {
        ArrayList<WTab> tabList = new ArrayList<>();
        for (int i = getTabsPerPage() * page; i < (getTabsPerPage() * page) + getTabsPerPage(); i++) {
            if (i >= tabs.size()) {
                return tabList;
            }
            tabList.add(tabs.get(i));
        }
        return tabList;
    }

    @Override
    public void draw(MatrixStack matrices, VertexConsumerProvider provider) {
        if (isHidden()) {
            return;
        }

        if (hasButtons) {
            next.draw(matrices, provider);
            back.draw(matrices, provider);
        }

        for (WTab tab : tabs) {
            tab.draw(matrices, provider);
        }


        for (WTab tab : tabs) {
            tab.getToggle().draw(matrices, provider);
        }
    }

    public int getPageCount() {
        int initSize = tabs.size() / getTabsPerPage();
        if (tabs.size() % getTabsPerPage() > 0) {
            initSize++;
        }
        return initSize;
    }

    public int getTabsPerPage() {
        return (int) (getWidth() / 24);
    }

}
