package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegistry {

    public static StorageCabinetBlock WOOD_CABINET;
    public static StorageCabinetBlock IRON_CABINET;
    public static StorageCabinetBlock GOLD_CABINET;
    public static StorageCabinetBlock DIAMOND_CABINET;
    public static StorageCabinetBlock EMERALD_CABINET;

    public static void init() {
        WOOD_CABINET = addCabinet(0, Block.Settings.of(Material.WOOD, MaterialColor.WOOD), "wood");
        IRON_CABINET = addCabinet(1, Block.Settings.of(Material.METAL, MaterialColor.IRON), "iron");
        GOLD_CABINET = addCabinet(2, Block.Settings.of(Material.METAL, MaterialColor.GOLD), "gold");
        DIAMOND_CABINET = addCabinet(3, Block.Settings.of(Material.METAL, MaterialColor.DIAMOND), "diamond");
        EMERALD_CABINET = addCabinet(4, Block.Settings.of(Material.METAL, MaterialColor.EMERALD), "emerald");
    }

    public static StorageCabinetBlock addCabinet(int tier, Block.Settings settings, String suffix) {
        StorageCabinetBlock storageCabinetBlock = new StorageCabinetBlock(settings.strength(5.0F, 6.0F), tier);
        Registry.register(Registry.BLOCK, new Identifier(StorageCabinet.modid, StorageCabinet.modid + "_" + suffix), storageCabinetBlock);
        Registry.register(Registry.ITEM, new Identifier(StorageCabinet.modid, StorageCabinet.modid + "_" + suffix), new BlockItem(storageCabinetBlock, new Item.Settings().group(StorageCabinet.CABINET_GROUP)));
        return storageCabinetBlock;
    }

}
