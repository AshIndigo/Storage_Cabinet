package com.ashindigo.storagecabinet.client;

import com.ashindigo.storagecabinet.ItemRegistry;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class StorageCabinetRenderer extends BlockEntityRenderer<StorageCabinetEntity> {

    public final ItemStack KEY = new ItemStack(ItemRegistry.KEY);

    public StorageCabinetRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(StorageCabinetEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) { // I hate numbers
        matrices.push();
        switch (entity.getCachedState().get(StorageCabinetBlock.FACING)) {
            case NORTH:
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
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
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90));
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(9.5F, 9.5F, 0);
                drawKey(entity.locked, matrices, vertexConsumers, overlay);
                matrices.translate(-4.2F, 0, 1);
                drawStack(entity.getMainItemStack(), matrices, vertexConsumers, overlay);
                break;
            case EAST:
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
                matrices.scale(.1F, .1F, 0.001F);
                matrices.translate(-.4F, 9.5F, 1000);
                drawKey(entity.locked, matrices, vertexConsumers, overlay);
                matrices.translate(-4.2F, 0, 1);
                drawStack(entity.getMainItemStack(), matrices, vertexConsumers, overlay);
                break;
        }
        matrices.pop();
    }

    private void drawKey(boolean locked, MatrixStack matrices, VertexConsumerProvider provider, int overlay) {
        if (locked) {
            MinecraftClient.getInstance().getItemRenderer().renderItem(KEY, ModelTransformation.Mode.GUI, 0x00f000f0, overlay, matrices, provider);
        }
    }

    private void drawStack(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int overlay) {
        if (stack.getItem() instanceof BlockItem) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GUI, 0x00f000f0, overlay, matrices, vertexConsumers);
        if (stack.getItem() instanceof BlockItem) {
            DiffuseLighting.enableGuiDepthLighting();
        }
    }
}
