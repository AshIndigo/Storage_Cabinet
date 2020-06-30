package com.ashindigo.storagecabinet;

import com.ashindigo.storagecabinet.blocks.StorageCabinetBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import spinnery.common.container.BaseContainer;
import spinnery.widget.WInterface;
import spinnery.widget.WSlot;

public class StorageCabinetContainer extends BaseContainer {

    public static final int INVENTORY = 1;

    StorageCabinetEntity cabinetEntity;
    int arrayHeight;
    int arrayWidth; // Isn't it always 9.

    public StorageCabinetContainer(int synchronizationID, PlayerInventory playerInventory, BlockPos pos) {
        super(synchronizationID, playerInventory);
        cabinetEntity = ((StorageCabinetEntity) getWorld().getBlockEntity(pos));
        WInterface mainInterface = getInterface();
        getInventories().put(INVENTORY, cabinetEntity);
        cabinetEntity.addListener(this::onContentChanged);
        arrayHeight = StorageCabinetBlock.Manager.getHeight(cabinetEntity.tier);
        arrayWidth = StorageCabinetBlock.Manager.getWidth();
        for (int y = 0; y < arrayHeight; ++y) {
            for (int x = 0; x < arrayWidth; ++x) {
                mainInterface.createChild(WSlotCabinet::new).setSlotNumber(y * arrayWidth + x).setInventoryNumber(INVENTORY).setWhitelist();
            }
        }
        WSlot.addHeadlessPlayerInventory(mainInterface);
    }

    @Override
    public ScreenHandlerType<?> getType() {
        return StorageCabinet.cabinetScreenHandler;
    }

    //    @Override
//    // TODO Remove once spinnery update comes out
//    public void onSlotAction(int slotNumber, int inventoryNumber, int button, Action action, PlayerEntity player) {
//        WSlot slotA = null;
//
//        for (WAbstractWidget widget : serverInterface.getAllWidgets()) {
//            if (widget instanceof WSlot && ((WSlot) widget).getSlotNumber() == slotNumber && ((WSlot) widget).getInventoryNumber() == inventoryNumber) {
//                slotA = (WSlot) widget;
//            }
//        }
//
//        if (slotA == null) {
//            return;
//        }
//
//        ItemStack stackA = slotA.getStack().copy();
//        ItemStack stackB = player.inventory.getCursorStack().copy();
//
//        switch (action) {
//            case PICKUP: {
//                if (!StackUtilities.equalItemAndTag(stackA, stackB)) {
//                    if (button == 0) { // Swap with existing // LMB
//                        if (slotA.isOverrideMaximumCount()) {
//                            if (stackA.isEmpty()) {
//                                if (slotA.refuses(stackB)) return;
//
//                                ItemStack stackC = stackA.copy();
//                                stackA = stackB.copy();
//                                stackB = stackC.copy();
//                            } else if (stackB.isEmpty()) {
//                                if (slotA.refuses(stackB)) return;
//
//                                int maxA = slotA.getMaxCount();
//                                int maxB = stackB.getMaxCount();
//
//                                int countA = stackA.getCount();
//                                int countB = stackB.getCount();
//
//                                int availableA = maxA - countA;
//                                int availableB = maxB - countB;
//
//                                ItemStack stackC = stackA.copy();
//                                stackC.setCount(Math.min(countA, availableB));
//                                stackB = stackC.copy();
//                                stackA.decrement(Math.min(countA, availableB));
//                            }
//                        } else {
//                            if (!stackB.isEmpty() && slotA.refuses(stackB)) return;
//
//                            ItemStack stackC = stackA.copy();
//                            stackA = stackB.copy();
//                            stackB = stackC.copy();
//                        }
//                    } else if (button == 1 && !stackB.isEmpty()) { // Add to existing // RMB
//                        if (stackA.isEmpty()) { // If existing is empty, initialize it // RMB
//                            stackA = new ItemStack(stackB.getItem(), 1);
//                            stackA.setTag(stackB.getTag());
//                            stackB.decrement(1);
//                        }
//                    } else if (button == 1) { // Split existing // RMB
//                        if (slotA.isOverrideMaximumCount()) {
//                            ItemStack stackC = stackA.split(Math.min(stackA.getCount(), stackA.getMaxCount()) / 2);
//                            stackB = stackC.copy();
//                        } else {
//                            ItemStack stackC = stackA.split(Math.max(1, stackA.getCount() / 2));
//                            stackB = stackC.copy();
//                        }
//                    }
//                } else {
//                    if (button == 0) {
//                        StackUtilities.clamp(stackB, stackA, stackB.getMaxCount(), slotA.getMaxCount()); // Add to existing // LMB
//                    } else {
//                        boolean canStackTransfer = stackB.getCount() >= 1 && stackA.getCount() < slotA.getMaxCount();
//                        if (canStackTransfer) { // Add to existing // RMB
//                            stackA.increment(1);
//                            stackB.decrement(1);
//                        }
//                    }
//                }
//                break;
//            }
//            case CLONE: {
//                if (player.isCreative()) {
//                    stackB = new ItemStack(stackA.getItem(), stackA.getMaxCount()); // Clone existing // MMB
//                    stackB.setTag(stackA.getTag());
//                }
//                break;
//            }
//            case QUICK_MOVE: {
//                for (WAbstractWidget widget : serverInterface.getAllWidgets()) {
//                    if (widget instanceof WSlot && ((WSlot) widget).getLinkedInventory() != slotA.getLinkedInventory()) {
//                        WSlot slotB = ((WSlot) widget);
//                        ItemStack stackC = slotB.getStack();
//
//                        if (slotB.refuses(stackA)) continue;
//
//                        if (!stackA.isEmpty() && (stackC.getCount() < slotB.getMaxCount() || stackC.getCount() < stackA.getMaxCount())) {
//                            if (stackC.isEmpty() || (stackA.getItem() == stackC.getItem() && stackA.getTag() == stackC.getTag())) {
//                                Pair<ItemStack, ItemStack> result = StackUtilities.clamp(stackA, stackC, slotA.getMaxCount(), slotB.getMaxCount());
//                                stackA = result.getFirst();
//                                slotB.setStack(result.getSecond());
//                                break;
//
//                            }
//                        }
//                    }
//                }
//                break;
//            }
//            case PICKUP_ALL: {
//                ItemStack stackC = getInterface().getContainer().getPlayerInventory().getCursorStack();
//
//                for (WAbstractWidget widget : getInterface().getAllWidgets()) {
//                    if (widget instanceof WSlot && StackUtilities.equalItemAndTag(((WSlot) widget).getStack(), stackC)) {
//                        if (((WSlot) widget).isLocked()) continue;
//
//                        StackUtilities.clamp(((WSlot) widget).getStack(), stackC, ((WSlot) widget).getMaxCount(), stackC.getMaxCount());
//                    }
//                }
//
//                return;
//            }
//        }
//
//        slotA.setStack(stackA);
//        ((PlayerInventory) inventories.get(PLAYER_INVENTORY)).setCursorStack(stackB);
//    }
}
