package com.ashindigo.storagecabinet.widgets;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Modified version of WTabPanel because I needed pages and a bit more access
 */
public class WPagedTabPanel extends WPanel {

    private static final int TAB_PADDING = 4;
    private static final int TAB_WIDTH = 28;
    private static final int TAB_HEIGHT = 30;
    private static final int PANEL_PADDING = 8;
    private static final int ICON_SIZE = 16;
    private final WBox tabRibbon = new WBox(Axis.HORIZONTAL).setSpacing(1);
    private final List<WTab> tabWidgets = new ArrayList<>();
    private final WCardPanel mainPanel = new WCardPanel();
    int page = 0;
    boolean hasButtons = true;
    WButton next = new WButton(new LiteralText(">")).setOnClick(() -> {
        page = Math.min(getPageCount() - 1, page + 1);
        updateTabs();
    });
    WButton back = new WButton(new LiteralText("<")).setOnClick(() -> {
        page = Math.max(0, page - 1);
        updateTabs();
    });

    public WPagedTabPanel() {
        add(tabRibbon, 0, 0);
        add(mainPanel, PANEL_PADDING, TAB_HEIGHT + PANEL_PADDING);
    }

    public List<WTab> getTabWidgets() {
        return tabWidgets;
    }

    @Override
    public void validate(GuiDescription c) {
        add(next, 0, 0);
        add(back, 0, 5);
        updateTabs();
        super.validate(c);
    }

    private void add(WWidget widget, int x, int y) {
        children.add(widget);
        widget.setParent(this);
        widget.setLocation(x, y);
        expandToFit(widget);
    }

    public void add(Tab tab) {
        WTab tabWidget = new WTab(tab);

        if (tabWidgets.isEmpty()) {
            tabWidget.selected = true;
        }

        tabWidgets.add(tabWidget);
        tabRibbon.add(tabWidget, TAB_WIDTH, TAB_HEIGHT + TAB_PADDING);
        mainPanel.add(tab.getWidget());
    }

    public void add(WWidget widget, Consumer<Tab.Builder> configurator) {
        Tab.Builder builder = new Tab.Builder(widget);
        configurator.accept(builder);
        add(builder.build());
    }

    @Override
    public void setSize(int x, int y) {
        super.setSize(x, y);
        tabRibbon.setSize(x, TAB_HEIGHT);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void addPainters() {
        super.addPainters();
        mainPanel.setBackgroundPainter(BackgroundPainter.VANILLA);
    }

    public int getPageCount() {
        int initSize = tabWidgets.size() / getTabsPerPage();
        if (tabWidgets.size() % getTabsPerPage() > 0) {
            initSize++;
        }
        return initSize;
    }

    public int getTabsPerPage() {
        return getWidth() / 24;
    }

    private Iterable<WTab> getTabsOnPage(int page) {
        ArrayList<WTab> tabList = new ArrayList<>();
        for (int i = getTabsPerPage() * page; i < (getTabsPerPage() * page) + getTabsPerPage(); i++) {
            if (i >= tabWidgets.size()) {
                return tabList;
            }
            tabList.add(tabWidgets.get(i));
        }
        return tabList;
    }

    protected void updateTabs() {
        if (next != null && back != null) {
            if (getPageCount() > 1) {
                hasButtons = true;
            } else {
                hasButtons = false;
                next.setEnabled(true);
                back.setEnabled(true);
            }
            next.setLocation(-20, -10);
            back.setLocation(-40, -10);
        }
        // Copy and pasted from updateTabs();
        if (tabWidgets.size() == 0) return;
        int tabSize = 24;
        int tabOffset = 0;
        for (WTab tab : tabWidgets) {
            tab.onShown();
        }
        for (WTab tab : getTabsOnPage(page)) {
            tab.setSize(tabSize, tab.getHeight());
            tab.setLocation(getX() + tabOffset, getY());
            tabOffset += tabSize;
            tab.onHidden();
        }
        mainPanel.setSelectedCard(tabWidgets.get(getTabsPerPage() * page).data.widget);
    }

    public static class Tab {
        @Nullable
        private final Text title;
        private final WWidget widget;
        @Nullable
        private final Consumer<TooltipBuilder> tooltip;
        @Nullable
        private Icon icon;

        @SuppressWarnings("DeprecatedIsStillUsed")
        @Deprecated
        public Tab(@Nullable Text title, @Nullable Icon icon, WWidget widget, @Nullable Consumer<TooltipBuilder> tooltip) {
            if (title == null && icon == null) {
                throw new IllegalArgumentException("A tab must have a title or an icon");
            }

            this.title = title;
            this.icon = icon;
            this.widget = Objects.requireNonNull(widget, "widget");
            this.tooltip = tooltip;
        }

        @Nullable
        public Text getTitle() {
            return title;
        }

        @Nullable
        public Icon getIcon() {
            return icon;
        }

        public void setIcon(Icon icon) {
            this.icon = icon;
        }

        public WWidget getWidget() {
            return widget;
        }

        @Environment(EnvType.CLIENT)
        public void addTooltip(TooltipBuilder tooltip) {
            if (this.tooltip != null) {
                this.tooltip.accept(tooltip);
            }
        }

        public static final class Builder {
            private final WWidget widget;
            private final List<Text> tooltip = new ArrayList<>();
            @Nullable
            private Text title;
            @Nullable
            private Icon icon;

            public Builder(WWidget widget) {
                this.widget = Objects.requireNonNull(widget, "widget");
            }

            public Builder title(Text title) {
                this.title = Objects.requireNonNull(title, "title");
                return this;
            }

            public Builder icon(Icon icon) {
                this.icon = Objects.requireNonNull(icon, "icon");
                return this;
            }

            public Builder tooltip(Text... lines) {
                Objects.requireNonNull(lines, "lines");
                Collections.addAll(tooltip, lines);

                return this;
            }

            public Builder tooltip(Collection<? extends Text> lines) {
                Objects.requireNonNull(lines, "lines");
                tooltip.addAll(lines);
                return this;
            }

            public Tab build() {
                Consumer<TooltipBuilder> tooltip = null;

                if (!this.tooltip.isEmpty()) {
                    //noinspection Convert2Lambda
                    tooltip = new Consumer<TooltipBuilder>() {
                        @Environment(EnvType.CLIENT)
                        @Override
                        public void accept(TooltipBuilder builder) {
                            builder.add(Tab.Builder.this.tooltip.toArray(new Text[0]));
                        }
                    };
                }

                return new Tab(title, icon, widget, tooltip);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    final static class Painters {
        static final BackgroundPainter SELECTED_TAB = BackgroundPainter.createLightDarkVariants(
                BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/tab/selected_light.png")).setTopPadding(2),
                BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/tab/selected_dark.png")).setTopPadding(2)
        );

        static final BackgroundPainter UNSELECTED_TAB = BackgroundPainter.createLightDarkVariants(
                BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/tab/unselected_light.png")),
                BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/tab/unselected_dark.png"))
        );

        static final BackgroundPainter SELECTED_TAB_FOCUS_BORDER = BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/tab/focus.png")).setTopPadding(2);
        static final BackgroundPainter UNSELECTED_TAB_FOCUS_BORDER = BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/tab/focus.png"));
    }

    public class WTab extends WWidget {
        private final Tab data;
        boolean selected = false;

        WTab(Tab data) {
            this.data = data;
        }

        @Override
        public boolean canFocus() {
            return true;
        }

        public Tab getData() {
            return data;
        }

        @Environment(EnvType.CLIENT)
        @Override
        public void onClick(int x, int y, int button) {
            super.onClick(x, y, button);

            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            for (WTab tab : tabWidgets) {
                tab.selected = (tab == this);
            }

            mainPanel.setSelectedCard(data.getWidget());
            WPagedTabPanel.this.layout();
        }

        @Environment(EnvType.CLIENT)
        @Override
        public void onKeyPressed(int ch, int key, int modifiers) {
            if (isActivationKey(ch)) {
                onClick(0, 0, 0);
            }
        }

        @Environment(EnvType.CLIENT)
        @Override
        public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            Text title = data.getTitle();
            Icon icon = data.getIcon();

            if (title != null) {
                int width = TAB_WIDTH + renderer.getWidth(title);
                if (icon == null) width = Math.max(TAB_WIDTH, width - ICON_SIZE);

                if (this.width != width) {
                    setSize(width, this.height);
                    getParent().layout();
                }
            }

            (selected ? Painters.SELECTED_TAB : Painters.UNSELECTED_TAB).paintBackground(x, y, this);
            if (isFocused()) {
                (selected ? Painters.SELECTED_TAB_FOCUS_BORDER : Painters.UNSELECTED_TAB_FOCUS_BORDER).paintBackground(x, y, this);
            }

            int iconX = 6;

            if (title != null) {
                int titleX = (icon != null) ? iconX + ICON_SIZE + 1 : 0;
                int titleY = (height - TAB_PADDING - renderer.fontHeight) / 2 + 1;
                int width = (icon != null) ? this.width - iconX - ICON_SIZE : this.width;
                HorizontalAlignment align = (icon != null) ? HorizontalAlignment.LEFT : HorizontalAlignment.CENTER;

                int color;
                if (LibGuiClient.config.darkMode) {
                    color = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;
                } else {
                    color = selected ? WLabel.DEFAULT_TEXT_COLOR : 0xEEEEEE;
                }

                ScreenDrawing.drawString(matrices, title.asOrderedText(), align, x + titleX, y + titleY, width, color);
            }

            if (icon != null) {
                icon.paint(matrices, x + iconX, y + (height - TAB_PADDING - ICON_SIZE) / 2, ICON_SIZE);
            }
        }

        @Override
        public void addTooltip(TooltipBuilder tooltip) {
            data.addTooltip(tooltip);
        }
    }
}
