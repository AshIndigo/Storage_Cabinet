package com.ashindigo.filingcabinet;

import com.ashindigo.filingcabinet.blocks.FilingCabinetBlock;
import com.ashindigo.filingcabinet.tileentities.TileEntityFilingCabinet;
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
@Mod(modid = FilingCabinetMod.MODID, name = FilingCabinetMod.NAME, version = FilingCabinetMod.VERSION)
public class FilingCabinetMod {

    @Mod.Instance
    public static FilingCabinetMod instance;

    static final String MODID = "filingcabinet";
    static final String NAME = "Filing Cabinet";
    static final String VERSION = "1.0";

    static Block filingCabinetBlock;

    @SidedProxy(serverSide = "com.ashindigo.filingcabinet.CommonProxy", clientSide = "com.ashindigo.filingcabinet.ClientProxy")
    private static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        filingCabinetBlock = new FilingCabinetBlock(Material.IRON);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new RegistrationHandler());
        GameRegistry.registerTileEntity(TileEntityFilingCabinet.class, new ResourceLocation(MODID, "filingcabinet"));

    }

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public void registerBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(filingCabinetBlock.setRegistryName(MODID, "filingcabinet"));
        }

        @SubscribeEvent
        public void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(new ItemBlock(filingCabinetBlock).setRegistryName(Objects.requireNonNull(filingCabinetBlock.getRegistryName())));
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            proxy.registerItemRenderer(Item.getItemFromBlock(filingCabinetBlock), 0, "filingcabinet");
        }
    }


    @EventHandler
    public void init(FMLInitializationEvent event) {
    }
}
