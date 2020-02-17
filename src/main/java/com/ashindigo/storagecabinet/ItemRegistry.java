package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.items.StorageCabinetUpgrade;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistry {

    public static StorageCabinetUpgrade WOOD_CABINET_UPGRADE;
    public static StorageCabinetUpgrade IRON_CABINET_UPGRADE;
    public static StorageCabinetUpgrade GOLD_CABINET_UPGRADE;
    public static StorageCabinetUpgrade DIAMOND_CABINET_UPGRADE;
    public static StorageCabinetUpgrade EMERALD_CABINET_UPGRADE;

    public static void init() {
        WOOD_CABINET_UPGRADE = addCabinetUpgrade(0,"wood");
        IRON_CABINET_UPGRADE = addCabinetUpgrade(1, "iron");
        GOLD_CABINET_UPGRADE = addCabinetUpgrade(2, "gold");
        DIAMOND_CABINET_UPGRADE = addCabinetUpgrade(3, "diamond");
        EMERALD_CABINET_UPGRADE = addCabinetUpgrade(4,"emerald");
    }

    public static StorageCabinetUpgrade addCabinetUpgrade(int tier, String suffix) {
        StorageCabinetUpgrade upgradeItem = new StorageCabinetUpgrade(new Item.Settings().group(StorageCabinet.CABINET_GROUP), tier);
        Registry.register(Registry.ITEM, new Identifier(StorageCabinet.modid, StorageCabinet.modid + "_" + suffix + "_upgrade"), upgradeItem);
        return upgradeItem;
    }
}
