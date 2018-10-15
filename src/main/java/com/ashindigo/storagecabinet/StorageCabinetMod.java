package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import com.ashindigo.storagecabinet.tileentities.TileEntityStorageCabinet;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Objects;

// TODO Make/Clean comments
@Mod(modid = StorageCabinetMod.MODID, name = StorageCabinetMod.NAME, version = StorageCabinetMod.VERSION)
public class StorageCabinetMod {

    @Mod.Instance
    public static StorageCabinetMod instance;

    static final String MODID = "storagecabinet";
    static final String NAME = "Storage Cabinet";
    static final String VERSION = "1.0";

    static Block storageCabinetBlock;

    @SidedProxy(serverSide = "com.ashindigo.storagecabinet.CommonProxy", clientSide = "com.ashindigo.storagecabinet.ClientProxy")
    private static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        storageCabinetBlock = new StorageCabinetBlock(Material.IRON);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new RegistrationHandler());
        GameRegistry.registerTileEntity(TileEntityStorageCabinet.class, new ResourceLocation(MODID, "storagecabinet"));

    }

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public void registerBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(storageCabinetBlock.setRegistryName(MODID, "storagecabinet"));
        }

        @SubscribeEvent
        public void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(new ItemBlock(storageCabinetBlock).setRegistryName(Objects.requireNonNull(storageCabinetBlock.getRegistryName())));
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            proxy.registerItemRenderer(Item.getItemFromBlock(storageCabinetBlock), 0, "storagecabinet");
        }
    }


    @EventHandler
    public void init(FMLInitializationEvent event) {
    }
}
