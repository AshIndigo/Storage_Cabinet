package com.ashindigo.storagecabinet;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StorageCabinetMod implements ModInitializer {

    static final String modid = "storagecabinet";
    private static final StorageCabinetBlock storageCabinet = new StorageCabinetBlock(Block.Settings.of(Material.METAL, MaterialColor.GRAY).strength(5.0F, 6.0F));
    static BlockEntityType<?> storageCabinetTileEntity;

    @Override
    public void onInitialize() {
        ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier(modid, modid), (syncId, id, player, buf) -> new ContainerStorageCabinet(syncId, player.inventory, (TileEntityStorageCabinet) player.world.getBlockEntity(buf.readBlockPos())));
        storageCabinetTileEntity = Registry.register(Registry.BLOCK_ENTITY, modid + ":" + modid, BlockEntityType.Builder.create(TileEntityStorageCabinet::new, storageCabinet).build(null));
        Registry.register(Registry.BLOCK, new Identifier(modid,modid), storageCabinet);
        Registry.register(Registry.ITEM, new Identifier(modid, modid), new BlockItem(storageCabinet, new Item.Settings().group(ItemGroup.MISC)));
    }
}
