package com.ashindigo.storagecabinet.client.screen;

import com.ashindigo.storagecabinet.container.CabinetManagerContainer;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;

public class CabinetManagerScreen extends AbstractStorageCabinetScreen<CabinetManagerContainer> {

    private final CabinetTab[] TABS = new CabinetTab[menu.cabinetList.size() > 0 ? menu.cabinetList.size() : 1];
    private int selectedTab = 0;
    private int tabPage = 0;
    private int maxPages = 0;
    private Button buttonBack;
    private Button buttonForward;

    public CabinetManagerScreen(CabinetManagerContainer container, Inventory playerInv, Component name) {
        super(container, playerInv, name);
        ArrayList<StorageCabinetEntity> cabinetList = getMenu().cabinetList;
        if (cabinetList.isEmpty()) TABS[0] = new CabinetTab(0, null); // Empty Tab to show that it's empty
        for (int i = 0; i < cabinetList.size(); i++) {
            TABS[i] = new CabinetTab(i, cabinetList.get(i));
        }
    }

    @Override
    public void init() {
        super.init();
        this.menu.setEnabledTab(selectedTab);
        if (TABS.length > 6) {
            buttonBack = addRenderableWidget(new Button(leftPos, topPos - 50, 20, 20, new TextComponent("<"), b -> {
                tabPage = Math.max(tabPage - 1, 0);
                selectTab(TABS[tabPage * 6]);
            }));
            buttonForward = addRenderableWidget(new Button(leftPos + imageWidth - 20, topPos - 50, 20, 20, new TextComponent(">"), b -> {
                tabPage = Math.min(tabPage + 1, maxPages);
                selectTab(TABS[tabPage * 6]);
            }));
            maxPages = (int) Math.ceil((TABS.length - 6) / 10D);
        }
    }

    @Override
    public void renderBg(PoseStack matrixStack, float pPartialTicks, int pX, int pY) {
        RenderSystem.setShaderTexture(0, CREATIVE_INVENTORY_TABS);
        CabinetTab tab = TABS[selectedTab];
        int start = tabPage * 5;
        int end = Math.min(TABS.length, (tabPage + 1) * 5 + 1);
        if (tabPage != 0) start += 1;
        for (int idx = start; idx < end; idx++) {
            CabinetTab tab1 = TABS[idx];
            if (tab1 != null && tab1.getId() != selectedTab) {
                RenderSystem.setShaderTexture(0, CREATIVE_INVENTORY_TABS);
                this.renderTabButton(matrixStack, tab1);
            }
        }
        super.renderBg(matrixStack, pPartialTicks, pX, pY);
        if (tab.getTabPage() != tabPage)
            return;
        this.renderTabButton(matrixStack, tab);
    }

    @Override
    public void render(PoseStack matrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(matrixStack, pMouseX, pMouseY, pPartialTicks);
        int start = tabPage * 10;
        int end = Math.min(TABS.length, (tabPage + 1) * 10 + 2);
        if (tabPage != 0) start += 2;

        for (int x = start; x < end; x++) {
            CabinetTab tab = TABS[x];
            if (tab != null && this.checkTabHovering(matrixStack, tab, pMouseX, pMouseY)) {
                break;
            }
        }

        if (maxPages != 0) {
            Component page = new TextComponent(String.format("%d / %d", tabPage + 1, maxPages + 1));
            this.setBlitOffset(300);
            this.itemRenderer.blitOffset = 300.0F;
            font.drawShadow(matrixStack, page.getVisualOrderText(), leftPos + imageWidth / 2 - font.width(page) / 2, topPos - 44, -1);
            this.setBlitOffset(0);
            this.itemRenderer.blitOffset = 0.0F;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void renderLabels(PoseStack matrixStack, int pX, int pY) {
        CabinetTab tab = TABS[selectedTab];
        if (tab != null) {
            RenderSystem.disableBlend();
            this.font.draw(matrixStack, tab.getTitle(), 8.0F, 6.0F, 4210752);
        }
        this.font.draw(matrixStack, this.playerInventoryTitle, (float) this.inventoryLabelX, (float) this.inventoryLabelY, 4210752);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            double d0 = pMouseX - (double) this.leftPos;
            double d1 = pMouseY - (double) this.topPos;
            for (CabinetTab tab : TABS) {
                if (tab != null && this.checkTabClicked(tab, d0, d1)) {
                    return true;
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            double d0 = pMouseX - (double) this.leftPos;
            double d1 = pMouseY - (double) this.topPos;
            this.scrolling = false;
            for (CabinetTab tab : TABS) {
                if (tab != null && this.checkTabClicked(tab, d0, d1)) {
                    this.selectTab(tab);
                    return true;
                }
            }
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void scrollMenu(float pos) {
        if (TABS != null) {
            menu.scrollTo(pos, TABS[selectedTab].entity);
        }
    }

    @Override
    public void changeDisplaySize() {
        super.changeDisplaySize();
        if (buttonForward != null) {
            buttonForward.x = leftPos + imageWidth - 20;
            buttonForward.y = topPos - 50;
        }
        if (buttonBack != null) {
            buttonBack.x = leftPos;
            buttonBack.y = topPos - 50;
        }
    }

    private void selectTab(CabinetTab tab) {
        if (tab == null) return;
        selectedTab = tab.getId();
        this.quickCraftSlots.clear();
        this.scrollOffs = 0.0F;
        this.menu.setEnabledTab(selectedTab);
        this.menu.scrollTo(0.0F, menu.cabinetList.get(selectedTab));
    }

    private boolean checkTabClicked(CabinetTab tab, double x, double y) {
        if (tab.getTabPage() != tabPage) return false;
        int i = tab.getColumn();
        int j = 28 * i;
        int k = 0;
        if (i > 0) {
            j += i;
        }
        k = k - 32;
        return x >= (double) j && x <= (double) (j + 28) && y >= (double) k && y <= 0;
    }

    private boolean checkTabHovering(PoseStack matrixStack, CabinetTab tab, int x, int y) {
        int i = tab.getColumn();
        int j = 28 * i;
        int k = 0;
        if (i > 0) {
            j += i;
        }
        k = k - 32;
        if (this.isHovering(j + 3, k + 3, 23, 27, x, y)) {
            this.renderTooltip(matrixStack, tab.getTitle(), x, y);
            return true;
        } else {
            return false;
        }
    }

    private void renderTabButton(PoseStack matrixStack, CabinetTab tab) {
        int column = tab.getColumn();
        int j = column * 28;
        int k = 0;
        int l = this.leftPos + 28 * column;
        int i1 = this.topPos;
        if (tab.getId() == selectedTab) {
            k += 32;
        }
        if (column > 0) {
            l += column;
        }
        i1 = i1 - 28;
        // Thanks Forge for the extra fix
        RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
        this.blit(matrixStack, l, i1, j, k, 28, 32);
        this.itemRenderer.blitOffset = 100.0F;
        l = l + 6;
        i1 = i1 + 8 + 1;
        ItemStack itemstack = tab.getIcon();
        this.itemRenderer.renderAndDecorateItem(itemstack, l, i1);
        this.itemRenderer.renderGuiItemDecorations(this.font, itemstack, l, i1);
        this.itemRenderer.blitOffset = 0.0F;
    }

    public record CabinetTab(int id, StorageCabinetEntity entity) {

        public ItemStack getIcon() {
            return entity == null || entity.getMainItemStack().isEmpty() ? new ItemStack(Blocks.BARRIER) : new ItemStack(entity.getMainItemStack().getItem()); // Quick way to make sure the icon doesn't show count
        }

        public int getId() {
            return id;
        }

        public Component getTitle() {
            if (entity == null) {
                return new TranslatableComponent("text.storagecabinet.nocabinets");
            }
            return entity.getDisplayName();
        }

        public int getColumn() {
            if (id > 5) return (id - 6) % 10 % 5;
            return this.id % 6;
        }

        public int getTabPage() {
            return id < 6 ? 0 : (id - 6) / 5 + 1;
        }
    }
}
