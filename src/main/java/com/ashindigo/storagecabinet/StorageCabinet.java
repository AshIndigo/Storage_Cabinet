package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.block.CabinetManagerBlock;
import com.ashindigo.storagecabinet.block.StorageCabinetBlock;
import com.ashindigo.storagecabinet.client.StorageCabinetRenderer;
import com.ashindigo.storagecabinet.client.screen.CabinetManagerScreen;
import com.ashindigo.storagecabinet.client.screen.StorageCabinetScreen;
import com.ashindigo.storagecabinet.container.CabinetManagerContainer;
import com.ashindigo.storagecabinet.container.StorageCabinetContainer;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.ashindigo.storagecabinet.item.StorageCabinetDolly;
import com.ashindigo.storagecabinet.item.StorageCabinetKey;
import com.ashindigo.storagecabinet.item.StorageCabinetUpgrade;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(StorageCabinet.MODID)
public class StorageCabinet {

    public static final String MODID = "storagecabinet";
    // Blocks
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, StorageCabinet.MODID);
    public static final RegistryObject<Block> WOOD_CABINET = BLOCKS.register("storagecabinet_wood", () -> new StorageCabinetBlock(AbstractBlock.Properties.of(Material.WOOD).harvestTool(ToolType.AXE), 0));
    public static final RegistryObject<Block> IRON_CABINET = BLOCKS.register("storagecabinet_iron", () -> new StorageCabinetBlock(AbstractBlock.Properties.of(Material.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(2), 1));
    public static final RegistryObject<Block> GOLD_CABINET = BLOCKS.register("storagecabinet_gold", () -> new StorageCabinetBlock(AbstractBlock.Properties.of(Material.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(2), 2));
    public static final RegistryObject<Block> DIAMOND_CABINET = BLOCKS.register("storagecabinet_diamond", () -> new StorageCabinetBlock(AbstractBlock.Properties.of(Material.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(2), 3));
    public static final RegistryObject<Block> EMERALD_CABINET = BLOCKS.register("storagecabinet_emerald", () -> new StorageCabinetBlock(AbstractBlock.Properties.of(Material.METAL).harvestTool(ToolType.PICKAXE).harvestLevel(2), 4));
    public static final RegistryObject<Block> CABINET_MANAGER = BLOCKS.register("cabinet_manager", () -> new CabinetManagerBlock(AbstractBlock.Properties.of(Material.METAL).harvestTool(ToolType.PICKAXE)));
    // Items
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, StorageCabinet.MODID);
    public static final RegistryObject<Item> WOOD_CABINET_UPGRADE = ITEMS.register("storagecabinet_wood_upgrade", () -> new StorageCabinetUpgrade(0));
    public static final RegistryObject<Item> IRON_CABINET_UPGRADE = ITEMS.register("storagecabinet_iron_upgrade", () -> new StorageCabinetUpgrade(1));
    public static final RegistryObject<Item> GOLD_CABINET_UPGRADE = ITEMS.register("storagecabinet_gold_upgrade", () -> new StorageCabinetUpgrade(2));
    public static final RegistryObject<Item> DIAMOND_CABINET_UPGRADE = ITEMS.register("storagecabinet_diamond_upgrade", () -> new StorageCabinetUpgrade(3));
    public static final RegistryObject<Item> EMERALD_CABINET_UPGRADE = ITEMS.register("storagecabinet_emerald_upgrade", () -> new StorageCabinetUpgrade(4));
    public static final RegistryObject<Item> KEY = ITEMS.register("key", StorageCabinetKey::new);
    public static final RegistryObject<Item> DOLLY = ITEMS.register("dolly", StorageCabinetDolly::new);
    public static final ItemGroup CABINET_GROUP = new ItemGroup(MODID + "." + MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(IRON_CABINET.get());
        }
    };
    // Tile Entities
    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, StorageCabinet.MODID);
    public static final RegistryObject<TileEntityType<StorageCabinetEntity>> CABINET_ENTITY = TILE_ENTITIES.register(MODID, () -> TileEntityType.Builder.of(StorageCabinetEntity::new, WOOD_CABINET.get(), IRON_CABINET.get(), GOLD_CABINET.get(), DIAMOND_CABINET.get(), EMERALD_CABINET.get()).build(null));
    public static final RegistryObject<TileEntityType<CabinetManagerEntity>> CABINET_MANAGER_ENTITY = TILE_ENTITIES.register("cabinet_manager", () -> TileEntityType.Builder.of(CabinetManagerEntity::new, CABINET_MANAGER.get()).build(null));
    // Containers
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, StorageCabinet.MODID);
    public static final RegistryObject<ContainerType<StorageCabinetContainer>> CABINET_CONTAINER = CONTAINERS.register(MODID, () -> IForgeContainerType.create(StorageCabinetContainer::new));
    public static final RegistryObject<ContainerType<CabinetManagerContainer>> MANAGER_CONTAINER = CONTAINERS.register("cabinet_manager", () -> IForgeContainerType.create(CabinetManagerContainer::new));

    public StorageCabinet() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public void registerClient(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(CABINET_ENTITY.get(), StorageCabinetRenderer::new);
        ScreenManager.register(CABINET_CONTAINER.get(), StorageCabinetScreen::new);
        ScreenManager.register(MANAGER_CONTAINER.get(), CabinetManagerScreen::new);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        Item.Properties prop = new Item.Properties().tab(StorageCabinet.CABINET_GROUP);
        event.getRegistry().registerAll(
                new BlockItem(WOOD_CABINET.get(), prop).setRegistryName(MODID, "storagecabinet_wood"),
                new BlockItem(IRON_CABINET.get(), prop).setRegistryName(MODID, "storagecabinet_iron"),
                new BlockItem(GOLD_CABINET.get(), prop).setRegistryName(MODID, "storagecabinet_gold"),
                new BlockItem(DIAMOND_CABINET.get(), prop).setRegistryName(MODID, "storagecabinet_diamond"),
                new BlockItem(EMERALD_CABINET.get(), prop).setRegistryName(MODID, "storagecabinet_emerald"),
                new BlockItem(CABINET_MANAGER.get(), prop).setRegistryName(MODID, "cabinet_manager"));
    }

    public static Block getByTier(int tier) {
        switch (tier) {
            case 1:
                return IRON_CABINET.get();
            case 2:
                return GOLD_CABINET.get();
            case 3:
                return DIAMOND_CABINET.get();
            case 4:
                return EMERALD_CABINET.get();
            case 0:
            default:
                return WOOD_CABINET.get();
        }
    }
}
