/*
package com.ashindigo.storagecabinet;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiScrollingList;

public class GuiStorageCabinet extends GuiContainer {

    private InventoryPlayer playerInv;

    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(StorageCabinetMod.MODID, "textures/gui/cabinet.png");

    public GuiStorageCabinet(Container container, InventoryPlayer playerInv) {
        super(container);
        this.playerInv = playerInv;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(BG_TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String name = I18n.format(StorageCabinetMod.storageCabinetBlock.getUnlocalizedName() + ".name");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, ySize - 94, 0x404040);
    }
}
*/
package com.ashindigo.storagecabinet;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import java.util.Objects;

public class GuiStorageCabinet extends ContainerScreen<ContainerStorageCabinet> {

    private static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(StorageCabinetMod.MODID, "textures/gui/cabinet.png");
    private float currentScroll;
    private boolean isScrolling;
    private boolean wasClicking;
    private PlayerInventory playerInv;
    private ContainerStorageCabinet container;

    GuiStorageCabinet(ContainerStorageCabinet container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.passEvents = true;
        this.ySize = 200;
        this.xSize = 195;
        playerInv = inventory;
        this.container = container;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.getMinecraft().getTextureManager().bindTexture(BG_TEXTURE);
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        int i = this.guiLeft + 175;
        int j = this.guiTop + 18;
        int k = j + 112;
        this.getMinecraft().getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);
        this.blit(i, j + (int) ((float) (k - j - 17) * this.currentScroll), 232, 0, 12, 15);
    }

    // Draw labels
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        font.drawString(I18n.format("block.storagecabinet.storagecabinet"), xSize / 2 - font.getStringWidth(I18n.format("block.storagecabinet.storagecabinet")) / 2, 6, 0x404040);
        font.drawString(playerInv.getDisplayName().getUnformattedComponentText(), 8, ySize - 92, 0x404040);
    }

    @Override
    public void init() {
        super.init();
        this.buttons.clear();
        this.getMinecraft().keyboardListener.enableRepeatEvents(true);
    }

    @Override
    public boolean mouseScrolled(double mouseScrolled, double mouseScroll2, double mouseScroll3) {
        int i = (270 + 9 - 1) / 9 - 5;
        this.currentScroll = (float) ((double) this.currentScroll - mouseScroll3 / (double) i);
        this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
        this.field_147002_h.scrollTo(this.currentScroll);
        return true;
    }

    //@Override
    private boolean func_195376_a(double mouseX, double mouseY) {
        int i = this.guiLeft;
        int j = this.guiTop;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;
        //return !this.wasClicking && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1;
        return mouseX >= (double)k && mouseY >= (double)l && mouseX < (double)i1 && mouseY < (double)j1;
    }

    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        if (this.isScrolling) {
            int i = this.guiTop + 18;
            int j = i + 112;
            this.currentScroll = ((float) p_mouseDragged_3_ - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
            this.field_147002_h.scrollTo(this.currentScroll);
            return true;
        } else {
            return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
        }
    }

    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (p_mouseClicked_5_ == 0) {
            if (this.func_195376_a(p_mouseClicked_1_, p_mouseClicked_3_)) {
                this.isScrolling = true;
                return true;
            }
        }
        return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
    }

    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        if (p_mouseReleased_5_ == 0) {
            this.isScrolling = false;
        }

        return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        boolean flag = new MouseHelper(Objects.requireNonNull(minecraft)).isLeftDown();
        int i = this.guiLeft;
        int j = this.guiTop;
        int k = i + 175;
        int l = j + 18;
        int i1 = k + 14;
        int j1 = l + 112;
        if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1) {
            this.isScrolling = true;
        }
        this.wasClicking = flag;
        if (this.isScrolling) {
            this.currentScroll = ((float) (mouseY - l) - 7.5F) / ((float) (j1 - l) - 15.0F);
            this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
            container.scrollTo(this.currentScroll);
        }

        super.render(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        this.renderHoveredToolTip(mouseX, mouseY);

    }
}