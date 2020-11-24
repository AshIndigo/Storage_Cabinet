package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.description.CabinetManagerDescription;
import com.ashindigo.storagecabinet.description.StorageCabinetDescription;
import com.ashindigo.storagecabinet.entity.CabinetManagerEntity;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StorageCabinet implements ModInitializer {

    public static final String MODID = "storagecabinet";
    public static BlockEntityType<StorageCabinetEntity> storageCabinetEntity;
    public static BlockEntityType<CabinetManagerEntity> cabinetManagerEntity;
    public static ItemGroup CABINET_GROUP;
    public static ExtendedScreenHandlerType<StorageCabinetDescription> cabinetScreenHandler;
    public static ExtendedScreenHandlerType<? extends CabinetManagerDescription> managerScreenHandler;

    @Override
    public void onInitialize() {
        CABINET_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, MODID), () -> new ItemStack(BlockRegistry.IRON_CABINET));
        BlockRegistry.init();
        ItemRegistry.init();
        cabinetScreenHandler = (ExtendedScreenHandlerType<StorageCabinetDescription>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, MODID), (syncId, inventory, buf) -> new StorageCabinetDescription(syncId, inventory, ScreenHandlerContext.create(inventory.player.getEntityWorld(), buf.readBlockPos())));
        managerScreenHandler = (ExtendedScreenHandlerType<CabinetManagerDescription>) ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "cabinet_manager"), (syncId, inventory, buf) -> new CabinetManagerDescription(syncId, inventory, ScreenHandlerContext.create(inventory.player.getEntityWorld(), buf.readBlockPos())));
        storageCabinetEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, MODID), BlockEntityType.Builder.create(StorageCabinetEntity::new, BlockRegistry.WOOD_CABINET, BlockRegistry.IRON_CABINET, BlockRegistry.GOLD_CABINET, BlockRegistry.DIAMOND_CABINET, BlockRegistry.EMERALD_CABINET).build(null));
        cabinetManagerEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "cabinet_manager"), BlockEntityType.Builder.create(CabinetManagerEntity::new, BlockRegistry.CABINET_MANAGER).build(null));
    }
}
