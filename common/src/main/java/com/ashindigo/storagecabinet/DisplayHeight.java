package com.ashindigo.storagecabinet;

import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public enum DisplayHeight {

    SMALL(5, 200, 118, 91, 256), // 5 slots
    MEDIUM(10, 295, 208, 181, 512); // 10 slots

    private final int verticalSlotCount;
    private final int imageHeight;
    private final int playerInvStart;
    private final int slotBottom;
    private final int dimension;
    private final ResourceLocation textureName;

    DisplayHeight(int verticalSlotCount, int imageHeight, int playerInvStart, int slotBottom, int dimension) {
        this.verticalSlotCount = verticalSlotCount;
        this.imageHeight = imageHeight;
        this.playerInvStart = playerInvStart;
        this.slotBottom = slotBottom;
        this.dimension = dimension;
        textureName = new ResourceLocation(Constants.MODID, "textures/gui/cabinet_" + name().toLowerCase(Locale.ROOT) + ".png");
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

    public int getPlayerInvStart() {
        return playerInvStart;
    }

    public int getSlotBottom() {
        return slotBottom;
    }

    public int getImageWidth() {
        return 195;
    }

    public int getDimension() {
        return dimension;
    }
}
