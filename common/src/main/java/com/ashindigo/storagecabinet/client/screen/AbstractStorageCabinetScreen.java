package com.ashindigo.storagecabinet.client.screen;

import com.ashindigo.storagecabinet.DisplayHeight;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.container.AbstractStorageCabinetContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractStorageCabinetScreen<T extends AbstractStorageCabinetContainer> extends AbstractContainerScreen<T> {
    public static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    public float scrollOffs;
    public boolean scrolling;
    DisplayHeight selectedHeight;
    Button sizeButton;

    public AbstractStorageCabinetScreen(T container, Inventory inv, Component name) {
        super(container, inv, name);
        this.isQuickCrafting = true;
        selectedHeight = menu.getDisplayHeight();
    }

    @Override
    protected void init() {
        super.init();
        //minecraft.keyboardHandler.setSendRepeatsToGui(true);
        sizeButton = addRenderableWidget(Button.builder(Component.translatable("text.storagecabinet.size"), button -> {
            selectedHeight = switch (selectedHeight) {
                case SMALL -> DisplayHeight.MEDIUM;
                case MEDIUM -> DisplayHeight.SMALL;
            };
            changeDisplaySize();
        }).pos(leftPos - 24, topPos).size(24,20).build());
//        sizeButton = addRenderableWidget(new Button(leftPos - 24, topPos, 24, 20, Component.translatable("text.storagecabinet.size"), button -> {
//            selectedHeight = switch (selectedHeight) {
//                case SMALL -> DisplayHeight.MEDIUM;
//                case MEDIUM -> DisplayHeight.SMALL;
//            };
//            changeDisplaySize();
//        }));
        changeDisplaySize();
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float pPartialTicks, int pX, int pY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, selectedHeight.getTextureName());
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, selectedHeight.getDimension(), selectedHeight.getDimension());
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
        int i = (menu.slots.size() - 36 + StorageCabinetBlock.getWidth() - 1) / StorageCabinetBlock.getWidth() - selectedHeight.getVerticalSlotCount();
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

    public void changeDisplaySize() {
        this.imageHeight = selectedHeight.getImageHeight();
        this.imageWidth = selectedHeight.getImageWidth();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.inventoryLabelY = selectedHeight.getPlayerInvStart() - 10;
        menu.changeSlotPositions(selectedHeight);
        sizeButton.setX(leftPos - 24);
        sizeButton.setY(topPos);
        scrollMenu(scrollOffs);
    }
}
