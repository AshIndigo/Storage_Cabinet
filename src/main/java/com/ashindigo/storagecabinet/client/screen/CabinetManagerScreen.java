package com.ashindigo.storagecabinet.client.screen;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.container.CabinetManagerContainer;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;

public class CabinetManagerScreen extends AbstractContainerScreen<CabinetManagerContainer> {

    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(StorageCabinet.MODID, "textures/gui/cabinet.png");
    private static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private final CabinetTab[] TABS = new CabinetTab[menu.cabinetList.size() > 0 ? menu.cabinetList.size() : 1];
    private float scrollOffs;
    private boolean scrolling;
    private int selectedTab = 0;
    private int tabPage = 0;
    private int maxPages = 0;

    public CabinetManagerScreen(CabinetManagerContainer container, Inventory playerInv, Component name) {
        super(container, playerInv, name);
        this.imageHeight = 200;
        this.imageWidth = 195;
        this.inventoryLabelY = imageHeight - 92;

        ArrayList<StorageCabinetEntity> cabinetList = getMenu().cabinetList;
        if (cabinetList.isEmpty()) TABS[0] = new CabinetTab(0, null); // Empty Tab to show that it's empty
        for (int i = 0; i < cabinetList.size(); i++) {
            TABS[i] = new CabinetTab(i, cabinetList.get(i));
        }
    }

    @Override
    public void init() {
        super.init();
        getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
        this.menu.setEnabledTab(selectedTab);
        if (TABS.length > 6) {
            addWidget(new Button(leftPos, topPos - 50, 20, 20, new TextComponent("<"), b -> {
                tabPage = Math.max(tabPage - 1, 0);
                selectTab(TABS[tabPage * 6]);
            }));
            addWidget(new Button(leftPos + imageWidth - 20, topPos - 50, 20, 20, new TextComponent(">"), b -> {
                tabPage = Math.min(tabPage + 1, maxPages);
                selectTab(TABS[tabPage * 6]);
            }));
            maxPages = (int) Math.ceil((TABS.length - 6) / 10D);
        }
    }

    @Override
    public void renderBg(PoseStack matrixStack, float pPartialTicks, int pX, int pY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        CabinetTab tab = TABS[selectedTab];
        TextureManager textureManager = getMinecraft().getTextureManager();

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
        RenderSystem.setShaderTexture(0, BG_TEXTURE);
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.leftPos + 175;
        int j = this.topPos + 18;
        int k = j + 112;
        RenderSystem.setShaderTexture(0, CREATIVE_INVENTORY_TABS);
        this.blit(matrixStack, i, j + (int) ((float) (k - j - 17) * this.scrollOffs), 232, 0, 12, 15);
        if (tab.getTabPage() != tabPage)
            return;
        this.renderTabButton(matrixStack, tab);
    }

    @Override
    public void render(PoseStack matrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        this.renderBackground(matrixStack);
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
            //RenderSystem.disableLighting();
            this.setBlitOffset(300);
            this.itemRenderer.blitOffset = 300.0F;
            font.drawShadow(matrixStack, page.getVisualOrderText(), leftPos + imageWidth / 2 - font.width(page) / 2, topPos - 44, -1);
            this.setBlitOffset(0);
            this.itemRenderer.blitOffset = 0.0F;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderTooltip(matrixStack, pMouseX, pMouseY);
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
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (menu.slots.size() - 36 == 0) return true; // Do nothing no cabinets
        int i = (menu.slots.size() - 36 + 9 - 1) / 9 - 5;
        this.scrollOffs = (float) ((double) this.scrollOffs - pDelta / (double) i);
        this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
        this.menu.scrollTo(this.scrollOffs, selectedTab);
        return true;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.scrolling) {
            int i = this.topPos + 18;
            int j = i + 112;
            this.scrollOffs = ((float) pMouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.menu.scrollTo(this.scrollOffs, selectedTab);
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
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

            if (this.insideScrollbar(pMouseX, pMouseY)) {
                this.scrolling = this.menu.slots.size() > 90;
                return true;
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

    private void selectTab(CabinetTab tab) {
        if (tab == null) return;
        selectedTab = tab.getId();
        this.quickCraftSlots.clear();
        this.scrollOffs = 0.0F;
        this.menu.setEnabledTab(selectedTab);
        this.menu.scrollTo(0.0F, selectedTab);
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

    private boolean insideScrollbar(double x, double y) {
        int i = this.leftPos;
        int j = this.topPos;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;
        return x >= (double) k && y >= (double) l && x < (double) i1 && y < (double) j1;
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

    public static class CabinetTab {
        final int id;
        final StorageCabinetEntity entity;

        public CabinetTab(int id, StorageCabinetEntity entity) {
            this.id = id;
            this.entity = entity;
        }

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
