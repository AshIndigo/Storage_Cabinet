package com.ashindigo.storagecabinet.client.screen;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.container.AbstractStorageCabinetContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.Locale;

public abstract class AbstractStorageCabinetScreen<T extends AbstractStorageCabinetContainer> extends AbstractContainerScreen<T> {

    public static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    DisplayHeight selectedHeight = DisplayHeight.SMALL;
    public float scrollOffs;
    public boolean scrolling;

    public AbstractStorageCabinetScreen(T container, Inventory inv, Component name) {
        super(container, inv, name);
        this.isQuickCrafting = true;
        this.imageHeight = selectedHeight.getImageHeight();
        this.imageWidth = 195; // Final, width doesn't change at all
        this.inventoryLabelY = imageHeight - 92;
    }

    @Override
    protected void init() {
        super.init();
        minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.addWidget(new Button(leftPos, topPos - 50, 20, 20, new TextComponent("Size"), button -> {
            selectedHeight = switch (selectedHeight) {
                case SMALL -> DisplayHeight.MEDIUM;
                case MEDIUM -> DisplayHeight.SMALL;
            };
        }));
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float pPartialTicks, int pX, int pY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, DisplayHeight.SMALL.getTextureName());
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        int i = this.leftPos + 175;
        int j = this.topPos + 18;
        int k = j + 112;
        RenderSystem.setShaderTexture(0, CREATIVE_INVENTORY_TABS);
        this.blit(matrixStack, i, j + (int) ((float) (k - j - 17) * this.scrollOffs), 232, 0, 12, 15);
    }

    @Override
    public void render(PoseStack matrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, pMouseX, pMouseY, pPartialTicks);
        this.renderTooltip(matrixStack, pMouseX, pMouseY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
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
            this.scrolling = false;
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (menu.slots.size() - 36 == 0) return true;
        int i = (menu.slots.size() - 36 + StorageCabinetBlock.getWidth() - 1) / StorageCabinetBlock.getWidth() - 5;
        setScrollOffs((float) ((double) this.scrollOffs - pDelta / (double) i));
        scrollMenu(scrollOffs);
        return true;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.scrolling) {
            int i = this.topPos + 18;
            int j = i + 112;
            setScrollOffs(((float) pMouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F));
            scrollMenu(scrollOffs);
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    protected boolean insideScrollbar(double x, double y) {
        int i = this.leftPos;
        int j = this.topPos;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;
        return x >= (double) k && y >= (double) l && x < (double) i1 && y < (double) j1;
    }

    protected void setScrollOffs(float val) {
        this.scrollOffs = Mth.clamp(val, 0.0F, 1.0F);
    }

    public abstract void scrollMenu(float pos);

    public enum DisplayHeight {

        SMALL(5, 200), // 5 slots
        MEDIUM(10, 295); // 10 slots

        private final int verticalSlotCount;
        private final int imageHeight;
        private final ResourceLocation textureName;

        DisplayHeight(int verticalSlotCount, int imageHeight) {
            this.verticalSlotCount = verticalSlotCount;
            this.imageHeight = imageHeight;
            textureName = new ResourceLocation(StorageCabinet.MODID, "textures/gui/cabinet_" + name().toLowerCase(Locale.ROOT) + ".png");
        }

        public int getVerticalSlotCount() {
            return verticalSlotCount;
        }

        public ResourceLocation getTextureName() {
            return textureName;
        }

        public int getImageHeight() {
            return imageHeight;
        }
    }
}
