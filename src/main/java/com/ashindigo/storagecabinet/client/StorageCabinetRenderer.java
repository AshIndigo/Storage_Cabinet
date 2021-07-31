package com.ashindigo.storagecabinet.client;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

public class StorageCabinetRenderer extends TileEntityRenderer<StorageCabinetEntity> {

    public final ItemStack KEY = new ItemStack(StorageCabinet.KEY.get());

    public StorageCabinetRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(StorageCabinetEntity entity, float tickDelta, MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        switch (entity.getBlockState().getValue(StorageCabinetBlock.FACING)) {
            case NORTH:
                matrices.mulPose(Vector3f.YP.rotationDegrees(180));
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(-.4F, 9.5F, 0);
                drawKey(entity.locked, matrices, vertexConsumers, overlay);
                matrices.translate(-4.4F, 0, 0.15F);
                drawStack(entity.getMainItemStack(), matrices, vertexConsumers, overlay);
                break;
            case SOUTH:
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(9.6F, 9.5F, 1000);
                drawKey(entity.locked, matrices, vertexConsumers, overlay);
                matrices.translate(-4.4F, 0, 0.15F);
                drawStack(entity.getMainItemStack(), matrices, vertexConsumers, overlay);
                break;
            case WEST:
                matrices.mulPose(Vector3f.YP.rotationDegrees(-90));
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(9.5F, 9.5F, 0);
                drawKey(entity.locked, matrices, vertexConsumers, overlay);
                matrices.translate(-4.2F, 0, 1);
                drawStack(entity.getMainItemStack(), matrices, vertexConsumers, overlay);
                break;
            case EAST:
                matrices.mulPose(Vector3f.YP.rotationDegrees(90));
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(-.4F, 9.5F, 1000);
                drawKey(entity.locked, matrices, vertexConsumers, overlay);
                matrices.translate(-4.2F, 0, 1);
                drawStack(entity.getMainItemStack(), matrices, vertexConsumers, overlay);
                break;
        }
        matrices.popPose();
    }

    private void drawKey(boolean locked, MatrixStack matrices, IRenderTypeBuffer provider, int overlay) {
        if (locked) {
            Minecraft.getInstance().getItemRenderer().renderStatic(KEY, ItemCameraTransforms.TransformType.GUI, 0x00f000f0, overlay, matrices, provider);
        }
    }

    private void drawStack(ItemStack stack, MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int overlay) {
        if (stack.getItem() instanceof BlockItem) {
            RenderHelper.turnOff();
        }
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.GUI, 0x00f000f0, overlay, matrices, vertexConsumers);
        if (stack.getItem() instanceof BlockItem) {
            RenderHelper.turnBackOn();
        }
    }
}
