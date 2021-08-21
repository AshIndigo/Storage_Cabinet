package com.ashindigo.storagecabinet;

import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public enum DisplayHeight {

    SMALL(5, 200, 195, 118, 91, 256), // 5 slots
    MEDIUM(10, 295, 195, 208, 181, 512); // 10 slots

    private final int verticalSlotCount;
    private final int imageHeight;
    private final int imageWidth;
    private final int playerInvStart;
    private final int slotBottom;
    private final int dimension;
    private final ResourceLocation textureName;

    DisplayHeight(int verticalSlotCount, int imageHeight, int imageWidth, int playerInvStart, int slotBottom, int dimension) {
        this.verticalSlotCount = verticalSlotCount;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.playerInvStart = playerInvStart;
        this.slotBottom = slotBottom;
        this.dimension = dimension;
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

    public int getPlayerInvStart() {
        return playerInvStart;
    }

    public int getSlotBottom() {
        return slotBottom;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getDimension() {
        return dimension;
    }
}
