package com.ashindigo.storagecabinet.client;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class StorageCabinetRenderer implements BlockEntityRenderer<StorageCabinetEntity> {

    public static final ItemStack KEY = new ItemStack(StorageCabinet.KEY.get());

    public StorageCabinetRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(StorageCabinetEntity entity, float v, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        switch (entity.getBlockState().getValue(StorageCabinetBlock.FACING)) {
            case NORTH -> {
                matrices.mulPose(Vector3f.YP.rotationDegrees(180));
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(-.4F, 9.5F, 0);
                drawKey(entity.locked, matrices, vertexConsumers, overlay);
                matrices.translate(-4.4F, 0, 0.15F);
                drawStack(entity.getMainItemStack(), matrices, vertexConsumers, overlay);
            }
            case SOUTH -> {
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(9.6F, 9.5F, 1000);
                drawKey(entity.locked, matrices, vertexConsumers, overlay);
                matrices.translate(-4.4F, 0, 0.15F);
                drawStack(entity.getMainItemStack(), matrices, vertexConsumers, overlay);
            }
            case WEST -> {
                matrices.mulPose(Vector3f.YP.rotationDegrees(-90));
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(9.5F, 9.5F, 0);
                drawKey(entity.locked, matrices, vertexConsumers, overlay);
                matrices.translate(-4.2F, 0, 1);
                drawStack(entity.getMainItemStack(), matrices, vertexConsumers, overlay);
            }
            case EAST -> {
                matrices.mulPose(Vector3f.YP.rotationDegrees(90));
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(-.4F, 9.5F, 1000);
                drawKey(entity.locked, matrices, vertexConsumers, overlay);
                matrices.translate(-4.2F, 0, 1);
                drawStack(entity.getMainItemStack(), matrices, vertexConsumers, overlay);
            }
        }
        matrices.popPose();
    }

    private static void drawKey(boolean locked, PoseStack matrices, MultiBufferSource provider, int overlay) {
        if (locked) {
            Minecraft.getInstance().getItemRenderer().renderStatic(KEY, ItemTransforms.TransformType.GUI, 0x00f000f0, overlay, matrices, provider, 0);
        }
    }

    private static void drawStack(ItemStack stack, PoseStack matrices, MultiBufferSource vertexConsumers, int overlay) {
        if (stack.getItem() instanceof BlockItem) {
            //Lighting.turnOff();
        }
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GUI, 0x00f000f0, overlay, matrices, vertexConsumers, 0);
        if (stack.getItem() instanceof BlockItem) {
            //Lighting.turnBackOn();
        }
    }

}
