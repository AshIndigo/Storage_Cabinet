package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.items.StorageCabinetUpgrade;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StorageCabinet implements ModInitializer {

    public static final String modid = "storagecabinet";
    public static BlockEntityType<?> storageCabinetEntity;
    // Texture and add upgrade items and itemgroup

    @Override
    public void onInitialize() {
        ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier(modid, modid), (syncId, id, player, buffer) -> new StorageCabinetContainer(syncId, player.inventory, buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readInt()));
        storageCabinetEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, modid + ":" + modid, BlockEntityType.Builder.create(StorageCabinetEntity::new, addCabinet(0, Block.Settings.of(Material.WOOD, MaterialColor.WOOD), "wood")).build(null));
        addCabinet(1, Block.Settings.of(Material.METAL, MaterialColor.IRON), "iron");
        addCabinet(2, Block.Settings.of(Material.METAL, MaterialColor.GOLD), "gold");
        addCabinet(3, Block.Settings.of(Material.METAL, MaterialColor.DIAMOND), "diamond");
        addCabinet(4, Block.Settings.of(Material.METAL, MaterialColor.EMERALD), "emerald");
    }

    public StorageCabinetBlock addCabinet(int tier, Block.Settings settings, String suffix) {
        StorageCabinetBlock storageCabinetBlock = new StorageCabinetBlock(settings.strength(5.0F, 6.0F), tier);
        Registry.register(Registry.BLOCK, new Identifier(modid,modid + "_" + suffix), storageCabinetBlock);
        Registry.register(Registry.ITEM, new Identifier(modid, modid + "_" + suffix), new BlockItem(storageCabinetBlock, new Item.Settings().group(ItemGroup.MISC)));
        Registry.register(Registry.ITEM, new Identifier(modid, modid + "_" + suffix + "_upgrade"), new StorageCabinetUpgrade(new Item.Settings().group(ItemGroup.MISC), tier));
        return storageCabinetBlock;
    }
}
