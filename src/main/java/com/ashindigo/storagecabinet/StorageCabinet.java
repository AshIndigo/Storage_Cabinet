package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.handler.CabinetManagerHandler;
import com.ashindigo.storagecabinet.handler.StorageCabinetHandler;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StorageCabinet implements ModInitializer {

    public static final String MODID = "storagecabinet";
    public static BlockEntityType<StorageCabinetEntity> storageCabinetEntity;
    public static BlockEntityType<CabinetManagerEntity> cabinetManagerEntity;
    public static ItemGroup CABINET_GROUP;
    public static ExtendedScreenHandlerType<StorageCabinetHandler> cabinetScreenHandler;
    public static ExtendedScreenHandlerType<? extends CabinetManagerHandler> managerScreenHandler;

    @Override
    public void onInitialize() {
        CABINET_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, MODID), () -> new ItemStack(BlockRegistry.IRON_CABINET));
        BlockRegistry.init();
        ItemRegistry.init();
        cabinetScreenHandler = (ExtendedScreenHandlerType<StorageCabinetHandler>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, MODID), (syncId, inventory, buf) -> new StorageCabinetHandler(syncId, inventory, buf.readBlockPos()));
        managerScreenHandler = (ExtendedScreenHandlerType<CabinetManagerHandler>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "cabinet_manager"), (syncId, inventory, buf) -> new CabinetManagerHandler(syncId, inventory, buf.readBlockPos()));
        storageCabinetEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, MODID), BlockEntityType.Builder.create(StorageCabinetEntity::new, BlockRegistry.WOOD_CABINET, BlockRegistry.IRON_CABINET, BlockRegistry.GOLD_CABINET, BlockRegistry.DIAMOND_CABINET, BlockRegistry.EMERALD_CABINET).build(null));
        cabinetManagerEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "cabinet_manager"), BlockEntityType.Builder.create(CabinetManagerEntity::new, BlockRegistry.CABINET_MANAGER).build(null));
    }
}
