package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.block.CabinetManagerBlock;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.container.CabinetManagerContainer;
import com.ashindigo.storagecabinet.container.StorageCabinetContainer;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.ashindigo.storagecabinet.item.StorageCabinetDolly;
import com.ashindigo.storagecabinet.item.StorageCabinetKey;
import com.ashindigo.storagecabinet.item.StorageCabinetUpgrade;
import com.ashindigo.storagecabinet.networking.SizeChangeMessage;
import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import java.util.function.Consumer;

public class StorageCabinet {

    public static final SimpleNetworkManager NETWORK_MANAGER = SimpleNetworkManager.create(Constants.MODID);

    // Items
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Constants.MODID, Registries.ITEM);
    public static final RegistrySupplier<Item> WOOD_CABINET_UPGRADE = ITEMS.register("storagecabinet_wood_upgrade", () -> new StorageCabinetUpgrade(0));
    public static final RegistrySupplier<Item> IRON_CABINET_UPGRADE = ITEMS.register("storagecabinet_iron_upgrade", () -> new StorageCabinetUpgrade(1));
    public static final RegistrySupplier<Item> GOLD_CABINET_UPGRADE = ITEMS.register("storagecabinet_gold_upgrade", () -> new StorageCabinetUpgrade(2));
    public static final RegistrySupplier<Item> DIAMOND_CABINET_UPGRADE = ITEMS.register("storagecabinet_diamond_upgrade", () -> new StorageCabinetUpgrade(3));
    public static final RegistrySupplier<Item> EMERALD_CABINET_UPGRADE = ITEMS.register("storagecabinet_emerald_upgrade", () -> new StorageCabinetUpgrade(4));
    public static final RegistrySupplier<Item> KEY = ITEMS.register("key", StorageCabinetKey::new);
    public static final RegistrySupplier<Item> DOLLY = ITEMS.register("dolly", StorageCabinetDolly::new);
    //public static final RegistrySupplier<Item> DEBUG = ITEMS.register("debug", CabinetDebug::new);

    // Blocks
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Constants.MODID, Registries.BLOCK);
    public static final RegistrySupplier<Block> WOOD_CABINET = BLOCKS.register("storagecabinet_wood", () -> new StorageCabinetBlock(BlockBehaviour.Properties.of(Material.WOOD), 0));
    public static final RegistrySupplier<Block> IRON_CABINET = BLOCKS.register("storagecabinet_iron", () -> new StorageCabinetBlock(BlockBehaviour.Properties.of(Material.METAL), 1));
    public static final RegistrySupplier<Block> GOLD_CABINET = BLOCKS.register("storagecabinet_gold", () -> new StorageCabinetBlock(BlockBehaviour.Properties.of(Material.METAL), 2));
    public static final RegistrySupplier<Block> DIAMOND_CABINET = BLOCKS.register("storagecabinet_diamond", () -> new StorageCabinetBlock(BlockBehaviour.Properties.of(Material.METAL), 3));
    public static final RegistrySupplier<Block> EMERALD_CABINET = BLOCKS.register("storagecabinet_emerald", () -> new StorageCabinetBlock(BlockBehaviour.Properties.of(Material.METAL), 4));
    public static final RegistrySupplier<Block> CABINET_MANAGER = BLOCKS.register("cabinet_manager", () -> new CabinetManagerBlock(BlockBehaviour.Properties.of(Material.METAL)));

    // Tile Entities
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(Constants.MODID, Registries.BLOCK_ENTITY_TYPE);
    public static RegistrySupplier<BlockEntityType<? extends StorageCabinetEntity>> CABINET_ENTITY;
    public static RegistrySupplier<BlockEntityType<? extends CabinetManagerEntity>> CABINET_MANAGER_ENTITY;

    // Containers
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Constants.MODID, Registries.MENU);
    public static final RegistrySupplier<MenuType<StorageCabinetContainer>> CABINET_CONTAINER = CONTAINERS.register(Constants.MODID, () -> MenuRegistry.ofExtended(StorageCabinetContainer::new));
    public static final RegistrySupplier<MenuType<CabinetManagerContainer>> MANAGER_CONTAINER = CONTAINERS.register("cabinet_manager", () -> MenuRegistry.ofExtended(CabinetManagerContainer::new));

    // Registering a new creative tab
    public static final CreativeTabRegistry.TabSupplier CABINET_GROUP = CreativeTabRegistry.create(new ResourceLocation(Constants.MODID, Constants.MODID), () -> new ItemStack(IRON_CABINET.get()));

    // Item Blocks
    private static final Item.Properties DEF_PROPS = new Item.Properties().arch$tab(CABINET_GROUP);
    public static final RegistrySupplier<Item> WOOD_CABINET_ITEM = ITEMS.register("storagecabinet_wood", () -> new BlockItem(WOOD_CABINET.get(), DEF_PROPS));
    public static final RegistrySupplier<Item> IRON_CABINET_ITEM = ITEMS.register("storagecabinet_iron", () -> new BlockItem(IRON_CABINET.get(), DEF_PROPS));
    public static final RegistrySupplier<Item> GOLD_CABINET_ITEM = ITEMS.register("storagecabinet_gold", () -> new BlockItem(GOLD_CABINET.get(), DEF_PROPS));
    public static final RegistrySupplier<Item> DIAMOND_CABINET_ITEM = ITEMS.register("storagecabinet_diamond", () -> new BlockItem(DIAMOND_CABINET.get(), DEF_PROPS));
    public static final RegistrySupplier<Item> EMERALD_CABINET_ITEM = ITEMS.register("storagecabinet_emerald", () -> new BlockItem(EMERALD_CABINET.get(), DEF_PROPS));
    public static final RegistrySupplier<Item> CABINET_MANAGER_ITEM = ITEMS.register("cabinet_manager", () -> new BlockItem(CABINET_MANAGER.get(), DEF_PROPS));

    // Packets
    public static final MessageType CHANGE_SIZE = NETWORK_MANAGER.registerC2S("change_size", SizeChangeMessage::new);

    public static void init() {
        BLOCKS.register();
        ITEMS.register();
        CONTAINERS.register();
    }

    public static Block getByTier(int tier) {
        return switch (tier) {
            case 1 -> IRON_CABINET.get();
            case 2 -> GOLD_CABINET.get();
            case 3 -> DIAMOND_CABINET.get();
            case 4 -> EMERALD_CABINET.get();
            default -> WOOD_CABINET.get();
        };
    }
}
