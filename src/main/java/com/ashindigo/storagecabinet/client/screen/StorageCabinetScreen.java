package com.ashindigo.storagecabinet.client.screen;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.container.StorageCabinetContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class StorageCabinetScreen extends ContainerScreen<StorageCabinetContainer> {

    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(StorageCabinet.MODID, "textures/gui/cabinet.png");
    private float scrollOffs;
    private boolean scrolling;

    public StorageCabinetScreen(StorageCabinetContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.isQuickCrafting = true;
        this.imageHeight = 200;
        this.imageWidth = 195;
        this.inventoryLabelY = imageHeight - 92;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float pPartialTicks, int pX, int pY) {
        RenderSystem.color4f(1, 1, 1, 1);
        getMinecraft().getTextureManager().bind(BG_TEXTURE);
        this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        RenderSystem.color4f(1, 1, 1, 1);
        int i = this.leftPos + 175;
        int j = this.topPos + 18;
        int k = j + 112;
        getMinecraft().getTextureManager().bind(ItemGroup.TAB_SEARCH.getTabsImage());
        this.blit(matrixStack, i, j + (int) ((float) (k - j - 17) * this.scrollOffs), 232, 0, 12, 15);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        int i = (menu.slots.size() - 36 + 9 - 1) / 9 - 5;
        this.scrollOffs = (float) ((double) this.scrollOffs - pDelta / (double) i);
        this.scrollOffs = MathHelper.clamp(this.scrollOffs, 0.0F, 1.0F);
        this.menu.scrollTo(this.scrollOffs);
        return true;
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.scrolling) {
            int i = this.topPos + 18;
            int j = i + 112;
            this.scrollOffs = ((float) pMouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
            this.scrollOffs = MathHelper.clamp(this.scrollOffs, 0.0F, 1.0F);
            this.menu.scrollTo(this.scrollOffs);
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
    public void render(MatrixStack matrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        super.render(matrixStack, pMouseX, pMouseY, pPartialTicks);
        this.renderTooltip(matrixStack, pMouseX, pMouseY);
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();
        getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
    }
}
