package com.ashindigo.storagecabinet;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StorageCabinet implements ModInitializer {

    public static final String MODID = "storagecabinet";
    public static BlockEntityType<?> storageCabinetEntity;
    public static ItemGroup CABINET_GROUP;

    @Override
    public void onInitialize() {
        CABINET_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, MODID), () -> new ItemStack(BlockRegistry.IRON_CABINET));
        BlockRegistry.init();
        ItemRegistry.init();
        ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, MODID), (syncId, id, player, buffer) -> new StorageCabinetContainer(syncId, player.inventory, buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readInt()));
        storageCabinetEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, MODID + ":" + MODID, BlockEntityType.Builder.create(StorageCabinetEntity::new, BlockRegistry.WOOD_CABINET).build(null));
    }
}
