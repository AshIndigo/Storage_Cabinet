package com.ashindigo.storagecabinet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Objects;

import static com.ashindigo.storagecabinet.StorageCabinetMod.MODID;

@Mod(MODID)
public class StorageCabinetMod {

    static final String MODID = "storagecabinet";
    static final String NAME = "Storage Cabinet";

    @ObjectHolder("storagecabinet:storagecabinet")
    static ContainerType<ContainerStorageCabinet> cabinetType = null;

    @ObjectHolder("storagecabinet:storagecabinet")
    static Block storageCabinetBlock ;

    @ObjectHolder(MODID + ":" + MODID)
    static TileEntityType<?> storageCabinetTileEntity;

    private static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public StorageCabinetMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, StorageCabinetMod::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, StorageCabinetMod::registerContainers);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, StorageCabinetMod::registerTileEntity);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, StorageCabinetMod::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::config);
        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public static void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.func_223042_a(TileEntityStorageCabinet::new, storageCabinetBlock).build(null).setRegistryName(new ResourceLocation(MODID, MODID)));
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        Block.Properties properties = Block.Properties.create(Material.IRON, MaterialColor.GRAY);
        properties.hardnessAndResistance(3.0F);
        event.getRegistry().registerAll(new StorageCabinetBlock(properties));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(new BlockItem(storageCabinetBlock, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(Objects.requireNonNull(storageCabinetBlock.getRegistryName())));
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(IForgeContainerType.create(ContainerStorageCabinet::new).setRegistryName(MODID, MODID));
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        proxy.registerGui();
    }

    @SubscribeEvent
    public void config(ModConfig.ModConfigEvent event) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StorageCabinetConfig.spec);
    }
}
