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
import dev.architectury.hooks.block.BlockEntityHooks;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
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

public class StorageCabinet {

    public static final String MODID = "storagecabinet";
    
    // Items
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MODID, Registry.ITEM_REGISTRY);
    public static final RegistrySupplier<Item> WOOD_CABINET_UPGRADE = ITEMS.register("storagecabinet_wood_upgrade", () -> new StorageCabinetUpgrade(0));
    public static final RegistrySupplier<Item> IRON_CABINET_UPGRADE = ITEMS.register("storagecabinet_iron_upgrade", () -> new StorageCabinetUpgrade(1));
    public static final RegistrySupplier<Item> GOLD_CABINET_UPGRADE = ITEMS.register("storagecabinet_gold_upgrade", () -> new StorageCabinetUpgrade(2));
    public static final RegistrySupplier<Item> DIAMOND_CABINET_UPGRADE = ITEMS.register("storagecabinet_diamond_upgrade", () -> new StorageCabinetUpgrade(3));
    public static final RegistrySupplier<Item> EMERALD_CABINET_UPGRADE = ITEMS.register("storagecabinet_emerald_upgrade", () -> new StorageCabinetUpgrade(4));
    public static final RegistrySupplier<Item> KEY = ITEMS.register("key", StorageCabinetKey::new);
    public static final RegistrySupplier<Item> DOLLY = ITEMS.register("dolly", StorageCabinetDolly::new);

    // Blocks
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MODID, Registry.BLOCK_REGISTRY);
    public static final RegistrySupplier<Block> WOOD_CABINET = BLOCKS.register("storagecabinet_wood", () -> new StorageCabinetBlock(BlockBehaviour.Properties.of(Material.WOOD), 0));
    public static final RegistrySupplier<Block> IRON_CABINET = BLOCKS.register("storagecabinet_iron", () -> new StorageCabinetBlock(BlockBehaviour.Properties.of(Material.METAL), 1));
    public static final RegistrySupplier<Block> GOLD_CABINET = BLOCKS.register("storagecabinet_gold", () -> new StorageCabinetBlock(BlockBehaviour.Properties.of(Material.METAL), 2));
    public static final RegistrySupplier<Block> DIAMOND_CABINET = BLOCKS.register("storagecabinet_diamond", () -> new StorageCabinetBlock(BlockBehaviour.Properties.of(Material.METAL), 3));
    public static final RegistrySupplier<Block> EMERALD_CABINET = BLOCKS.register("storagecabinet_emerald", () -> new StorageCabinetBlock(BlockBehaviour.Properties.of(Material.METAL), 4));
    public static final RegistrySupplier<Block> CABINET_MANAGER = BLOCKS.register("cabinet_manager", () -> new CabinetManagerBlock(BlockBehaviour.Properties.of(Material.METAL)));
    
    // Tile Entities
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(MODID, Registry.BLOCK_ENTITY_TYPE_REGISTRY);
    public static final RegistrySupplier<BlockEntityType<StorageCabinetEntity>> CABINET_ENTITY = TILE_ENTITIES.register(MODID, () -> BlockEntityHooks.builder(StorageCabinetEntity::new, WOOD_CABINET.get(), IRON_CABINET.get(), GOLD_CABINET.get(), DIAMOND_CABINET.get(), EMERALD_CABINET.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<CabinetManagerEntity>> CABINET_MANAGER_ENTITY = TILE_ENTITIES.register("cabinet_manager", () -> BlockEntityHooks.builder(CabinetManagerEntity::new, CABINET_MANAGER.get()).build(null));

    // Containers
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(MODID, Registry.MENU_REGISTRY);
    public static final RegistrySupplier<MenuType<StorageCabinetContainer>> CABINET_CONTAINER = CONTAINERS.register(MODID, () -> MenuRegistry.ofExtended(StorageCabinetContainer::new));
    public static final RegistrySupplier<MenuType<CabinetManagerContainer>> MANAGER_CONTAINER = CONTAINERS.register("cabinet_manager", () -> MenuRegistry.ofExtended(CabinetManagerContainer::new));

    // Registering a new creative tab
    public static final CreativeModeTab CABINET_GROUP = CreativeTabRegistry.create(new ResourceLocation(MODID, MODID), () -> new ItemStack(IRON_CABINET.get()));

    // Item Blocks
    private static final Item.Properties DEF_PROPS = new Item.Properties().tab(CABINET_GROUP);
    public static final RegistrySupplier<Item> WOOD_CABINET_ITEM = ITEMS.register("storagecabinet_wood", () -> new BlockItem(WOOD_CABINET.get(), DEF_PROPS));
    public static final RegistrySupplier<Item> IRON_CABINET_ITEM = ITEMS.register("storagecabinet_iron", () -> new BlockItem(IRON_CABINET.get(), DEF_PROPS));
    public static final RegistrySupplier<Item> GOLD_CABINET_ITEM = ITEMS.register("storagecabinet_gold", () -> new BlockItem(GOLD_CABINET.get(), DEF_PROPS));
    public static final RegistrySupplier<Item> DIAMOND_CABINET_ITEM = ITEMS.register("storagecabinet_diamond", () -> new BlockItem(DIAMOND_CABINET.get(), DEF_PROPS));
    public static final RegistrySupplier<Item> EMERALD_CABINET_ITEM = ITEMS.register("storagecabinet_emerald", () -> new BlockItem(EMERALD_CABINET.get(), DEF_PROPS));
    public static final RegistrySupplier<Item> CABINET_MANAGER_ITEM = ITEMS.register("cabinet_manager", () -> new BlockItem(CABINET_MANAGER.get(), DEF_PROPS));

    public static void init() {
        ITEMS.register();
        BLOCKS.register();
        TILE_ENTITIES.register();
        CONTAINERS.register();
    }



}