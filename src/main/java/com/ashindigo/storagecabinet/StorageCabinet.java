package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.container.CabinetManagerContainer;
import com.ashindigo.storagecabinet.container.StorageCabinetContainer;
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
    public static BlockEntityType<?> storageCabinetEntity;
    public static BlockEntityType<CabinetManagerEntity> cabinetManagerEntity;
    public static ItemGroup CABINET_GROUP;
    public static ExtendedScreenHandlerType<StorageCabinetContainer> cabinetScreenHandler;
    public static ExtendedScreenHandlerType<? extends CabinetManagerContainer> managerScreenHandler;

    // TODO
    // Crash when shift clicking - Only when on SP? - Spinnery Issue, fixed on my end with a Mixin
    // Texture/Model for cabinet manager, and recipe
    // Also make it give available items from all cabinets, to bad fabric doesn't have an ae2 or rs like mod
    // Random idea: Cabinet's display their item on the front


    @Override
    public void onInitialize() {
        CABINET_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, MODID), () -> new ItemStack(BlockRegistry.IRON_CABINET));
        BlockRegistry.init();
        ItemRegistry.init();
        cabinetScreenHandler = (ExtendedScreenHandlerType<StorageCabinetContainer>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, MODID), (syncId, inventory, buf) -> new StorageCabinetContainer(syncId, inventory, buf.readBlockPos()));
        managerScreenHandler = (ExtendedScreenHandlerType<CabinetManagerContainer>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "cabinet_manager"), (syncId, inventory, buf) -> new CabinetManagerContainer(syncId, inventory, buf.readBlockPos()));
        storageCabinetEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, MODID + ":" + MODID, BlockEntityType.Builder.create(StorageCabinetEntity::new, BlockRegistry.WOOD_CABINET).build(null));
        cabinetManagerEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, MODID + ":" + "cabinet_manager", BlockEntityType.Builder.create(CabinetManagerEntity::new, BlockRegistry.CABINET_MANAGER).build(null));
    }
}
