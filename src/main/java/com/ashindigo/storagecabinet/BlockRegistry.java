package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.blocks.CabinetManagerBlock;
import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
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
    public static CabinetManagerBlock CABINET_MANAGER;

    public static void init() {
        WOOD_CABINET = addCabinet(0, FabricBlockSettings.of(Material.WOOD, MapColor.OAK_TAN).breakByTool(FabricToolTags.AXES), "wood");
        IRON_CABINET = addCabinet(1, FabricBlockSettings.of(Material.METAL, MapColor.IRON_GRAY).breakByTool(FabricToolTags.PICKAXES, 2), "iron");
        GOLD_CABINET = addCabinet(2, FabricBlockSettings.of(Material.METAL, MapColor.GOLD).breakByTool(FabricToolTags.PICKAXES, 2), "gold");
        DIAMOND_CABINET = addCabinet(3, FabricBlockSettings.of(Material.METAL, MapColor.DIAMOND_BLUE).breakByTool(FabricToolTags.PICKAXES, 2), "diamond");
        EMERALD_CABINET = addCabinet(4, FabricBlockSettings.of(Material.METAL, MapColor.EMERALD_GREEN).breakByTool(FabricToolTags.PICKAXES, 2), "emerald");
        CABINET_MANAGER = new CabinetManagerBlock(FabricBlockSettings.of(Material.METAL, MapColor.IRON_GRAY).requiresTool().breakByTool(FabricToolTags.PICKAXES, 2).breakByHand(false).strength(3, 5));
        Registry.register(Registry.BLOCK, new Identifier(StorageCabinet.MODID, "cabinet_manager"), CABINET_MANAGER);
        Registry.register(Registry.ITEM, new Identifier(StorageCabinet.MODID, "cabinet_manager"), new BlockItem(CABINET_MANAGER, new Item.Settings().group(StorageCabinet.CABINET_GROUP)));

    }

    public static StorageCabinetBlock addCabinet(int tier, FabricBlockSettings settings, String suffix) {
        StorageCabinetBlock storageCabinetBlock = new StorageCabinetBlock(settings.strength(3.0F, 5.0F).breakByHand(false).requiresTool(), tier);
        Registry.register(Registry.BLOCK, new Identifier(StorageCabinet.MODID, StorageCabinet.MODID + "_" + suffix), storageCabinetBlock);
        Registry.register(Registry.ITEM, new Identifier(StorageCabinet.MODID, StorageCabinet.MODID + "_" + suffix), new BlockItem(storageCabinetBlock, new Item.Settings().group(StorageCabinet.CABINET_GROUP)));
        GrassBlock grassBlock = (GrassBlock) new Block(FabricBlockSettings.of(Material.GLASS));
        return storageCabinetBlock;
    }

    public static StorageCabinetBlock getByTier(int tier) {
        return switch (tier) {
            case 1 -> IRON_CABINET;
            case 2 -> GOLD_CABINET;
            case 3 -> DIAMOND_CABINET;
            case 4 -> EMERALD_CABINET;
            default -> WOOD_CABINET;
        };
    }
}
