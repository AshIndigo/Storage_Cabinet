package com.ashindigo.storagecabinet.client;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CabinetManagerRenderer<T extends CabinetManagerEntity> implements BlockEntityRenderer<T> {

    public static final ItemStack KEY = new ItemStack(StorageCabinet.KEY.get());

    public CabinetManagerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CabinetManagerEntity entity, float v, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        switch (entity.getBlockState().getValue(StorageCabinetBlock.FACING)) {
            case NORTH -> {
                matrices.mulPose(Axis.YP.rotationDegrees(180));
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(-.4F, 9.5F, 0);
                drawKey(entity.locked, matrices, vertexConsumers, overlay, entity.getLevel());
                matrices.translate(-4.4F, 0, 0.15F);
            }
            case SOUTH -> {
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(9.6F, 9.5F, 1000);
                drawKey(entity.locked, matrices, vertexConsumers, overlay, entity.getLevel());
                matrices.translate(-4.4F, 0, 0.15F);
            }
            case WEST -> {
                matrices.mulPose(Axis.YP.rotationDegrees(-90));
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(9.5F, 9.5F, 0);
                drawKey(entity.locked, matrices, vertexConsumers, overlay, entity.getLevel());
                matrices.translate(-4.2F, 0, 1);
            }
            case EAST -> {
                matrices.mulPose(Axis.YP.rotationDegrees(90));
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(-.4F, 9.5F, 1000);
                drawKey(entity.locked, matrices, vertexConsumers, overlay, entity.getLevel());
                matrices.translate(-4.2F, 0, 1);
            }
        }
        matrices.popPose();
    }

    // TODO Something is off with the lighting here
    private static void drawKey(boolean locked, PoseStack matrices, MultiBufferSource provider, int overlay, Level level) {
        if (locked) {
            Minecraft.getInstance().getItemRenderer().renderStatic(KEY, ItemDisplayContext.GUI, 0x00f000f0, overlay, matrices, provider, level, 0);
        }
    }
}
