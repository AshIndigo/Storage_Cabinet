package com.ashindigo.storagecabinet.fabric.test;

import com.ashindigo.storagecabinet.StorageCabinet;
import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Method;

public class TestStorageCabinet implements FabricGameTest {

    final BlockPos cabinetPos = new BlockPos(0, 2, 0);
    final BlockPos hopperPos = new BlockPos(0, 3, 0);

    /**
     * By overriding invokeTestMethod you can wrap the method call.
     * This can be used as shown to run code before and after each test.
     */
    @Override
    public void invokeTestMethod(GameTestHelper context, Method method) {
        beforeEach(context);
        FabricGameTest.super.invokeTestMethod(context, method);
        afterEach(context);
    }

    private void beforeEach(GameTestHelper context) { // Set up a cabinet each test?
    }

    private void afterEach(GameTestHelper context) {
    }

    // Upgrade wood cabinet to iron one
    @GameTest(template = EMPTY_STRUCTURE)
    public void upgradeWoodCabinet(GameTestHelper context) {
        context.setBlock(cabinetPos, StorageCabinet.WOOD_CABINET.get());
        context.assertBlock(cabinetPos, (block) -> block == StorageCabinet.WOOD_CABINET.get(), "Expected block to be wood cabinet");
        Player player = context.makeMockPlayer();
        new ItemStack(StorageCabinet.IRON_CABINET_UPGRADE.get()).useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(context.absoluteVec(new Vec3(0, 2, 0)), Direction.NORTH, context.absolutePos(cabinetPos), false)));
        context.succeedWhen(() ->
                context.assertBlock(cabinetPos, (block) -> block == StorageCabinet.IRON_CABINET.get(), "Expected block to be iron cabinet")
        );
    }

    // Cabinets cannot downgrade
    @GameTest(template = EMPTY_STRUCTURE)
    public void attemptDowngrade(GameTestHelper context) {
        context.setBlock(cabinetPos, StorageCabinet.GOLD_CABINET.get());
        context.assertBlock(cabinetPos, (block) -> block == StorageCabinet.GOLD_CABINET.get(), "Expected block to be gold cabinet");
        Player player = context.makeMockPlayer();
        new ItemStack(StorageCabinet.IRON_CABINET_UPGRADE.get()).useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(context.absoluteVec(new Vec3(0, 2, 0)), Direction.NORTH, context.absolutePos(cabinetPos), false)));
        context.succeedWhen(() ->
                context.assertBlock(cabinetPos, (block) -> block == StorageCabinet.GOLD_CABINET.get(), "Expected block to be gold cabinet")
        );
    }

    // Make sure Cabinet takes items
    @GameTest(template = EMPTY_STRUCTURE)
    public void attemptInsertion(GameTestHelper context) {
        context.setBlock(cabinetPos, StorageCabinet.WOOD_CABINET.get());
        context.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN).setValue(HopperBlock.ENABLED, true));
        context.assertBlock(cabinetPos, (block) -> block == StorageCabinet.WOOD_CABINET.get(), "Expected block to be wood cabinet");
        ((HopperBlockEntity) context.getBlockEntity(hopperPos)).setItem(0, new ItemStack(Items.APPLE));
        context.succeedWhen(() -> {
                    if (((StorageCabinetEntity) context.getBlockEntity(cabinetPos)).getItem(0).getItem() != Items.APPLE) {
                        throw new GameTestAssertPosException("Item did not insert properly", context.absolutePos(cabinetPos), cabinetPos, context.getTick());
                    }
                }
        );
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void attemptInsertionOfSimilarTagItem(GameTestHelper context) {
        context.setBlock(cabinetPos, StorageCabinet.WOOD_CABINET.get());
        context.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN).setValue(HopperBlock.ENABLED, true));
        context.assertBlock(cabinetPos, (block) -> block == StorageCabinet.WOOD_CABINET.get(), "Expected block to be wood cabinet");
        ((HopperBlockEntity) context.getBlockEntity(hopperPos)).setItem(0, new ItemStack(Items.BLUE_WOOL));
        ((HopperBlockEntity) context.getBlockEntity(hopperPos)).setItem(1, new ItemStack(Items.RED_WOOL));
        context.succeedWhen(() -> {
                    if (((StorageCabinetEntity) context.getBlockEntity(cabinetPos)).getItem(0).getItem() != Items.BLUE_WOOL || ((StorageCabinetEntity) context.getBlockEntity(cabinetPos)).getItem(1).getItem() != Items.RED_WOOL) {
                        throw new GameTestAssertPosException("Item did not insert properly", context.absolutePos(cabinetPos), cabinetPos, context.getTick());
                    }
                }
        );
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void attemptInsertionOfSimilarNBTItem(GameTestHelper context) {
        context.setBlock(cabinetPos, StorageCabinet.WOOD_CABINET.get());
        context.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN).setValue(HopperBlock.ENABLED, true));
        context.assertBlock(cabinetPos, (block) -> block == StorageCabinet.WOOD_CABINET.get(), "Expected block to be wood cabinet");
        ItemStack sharpBook = new ItemStack(Items.ENCHANTED_BOOK);
        sharpBook.enchant(Enchantments.SHARPNESS, 2);
        ItemStack efficBook = new ItemStack(Items.ENCHANTED_BOOK);
        efficBook.enchant(Enchantments.BLOCK_EFFICIENCY, 2);
        ((HopperBlockEntity) context.getBlockEntity(hopperPos)).setItem(0, sharpBook);
        ((HopperBlockEntity) context.getBlockEntity(hopperPos)).setItem(1, efficBook);
        context.succeedWhen(() -> {
                    if (((StorageCabinetEntity) context.getBlockEntity(cabinetPos)).countItem(Items.ENCHANTED_BOOK) != 2) {
                        throw new GameTestAssertPosException("Item did not insert properly", context.absolutePos(cabinetPos), cabinetPos, context.getTick());
                    }
                }
        );
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void attemptInsertionOfNonSimilarItem(GameTestHelper context) {
        context.setBlock(cabinetPos, StorageCabinet.WOOD_CABINET.get());
        context.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN).setValue(HopperBlock.ENABLED, true));
        context.assertBlock(cabinetPos, (block) -> block == StorageCabinet.WOOD_CABINET.get(), "Expected block to be wood cabinet");
        ((HopperBlockEntity) context.getBlockEntity(hopperPos)).setItem(0, new ItemStack(Items.BLUE_WOOL));
        ((HopperBlockEntity) context.getBlockEntity(hopperPos)).setItem(1, new ItemStack(Items.DIAMOND));
        context.succeedWhen(() -> {
                    if (((StorageCabinetEntity) context.getBlockEntity(cabinetPos)).getItem(0).getItem() == Items.BLUE_WOOL && ((StorageCabinetEntity) context.getBlockEntity(cabinetPos)).getItem(1).getItem() == Items.DIAMOND) {
                        throw new GameTestAssertPosException("Insertion was not prevented", context.absolutePos(cabinetPos), cabinetPos, context.getTick());
                    }
                }
        );
    }

    @GameTest(template = EMPTY_STRUCTURE)
    public void attemptInsertionIntoCabinetManager(GameTestHelper context) {
        context.setBlock(cabinetPos, StorageCabinet.WOOD_CABINET.get());
        context.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN).setValue(HopperBlock.ENABLED, true));
        context.assertBlock(cabinetPos, (block) -> block == StorageCabinet.WOOD_CABINET.get(), "Expected block to be wood cabinet");
        ((HopperBlockEntity) context.getBlockEntity(hopperPos)).setItem(0, new ItemStack(Items.BLUE_WOOL));
        ((HopperBlockEntity) context.getBlockEntity(hopperPos)).setItem(1, new ItemStack(Items.DIAMOND));
        context.succeedWhen(() -> {
                    if (((StorageCabinetEntity) context.getBlockEntity(cabinetPos)).getItem(0).getItem() == Items.BLUE_WOOL && ((StorageCabinetEntity) context.getBlockEntity(cabinetPos)).getItem(1).getItem() == Items.DIAMOND) {
                        throw new GameTestAssertPosException("Insertion was not prevented", context.absolutePos(cabinetPos), cabinetPos, context.getTick());
                    }
                }
        );
    }
}