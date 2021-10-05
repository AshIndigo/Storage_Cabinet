package com.ashindigo.storagecabinet.container;

import com.ashindigo.storagecabinet.entity.StorageCabinetEntity;
import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// This is a horrible patch work solution please ban me.
class ManagerInvList<E> extends NonNullList<E> {

    private final List<E> trueList = Lists.newArrayList();
    private final E defaultValue;

    private ManagerInvList(List<E> list, @Nullable E object) {
        super(list, object);
        defaultValue = object;
    }

    public static ManagerInvList<ItemStack> create() {
        return new ManagerInvList<>(Lists.newArrayList(), ItemStack.EMPTY);
    }

    @NotNull
    @Override
    public E get(int i) {
        return trueList.get(i);
    }

    @Override
    public E set(int i, E object) {
        Validate.notNull(object);
        return trueList.set(i, object);
    }

    @Override
    public void add(int i, E object) {
        Validate.notNull(object);
        trueList.add(i, object);
    }

    @Override
    public E remove(int i) {
        return trueList.remove(i);
    }

    @Override
    public int size() {
        return trueList.size();
    }

    @Override
    public void clear() {
        if (this.defaultValue == null) {
            super.clear();
        } else {
            for(int i = 0; i < this.size(); ++i) {
                this.set(i, this.defaultValue);
            }
        }
    }

    public ManagerInvList<E> setCabinetList(ArrayList<StorageCabinetEntity> cabinetList, Inventory inv) {
        for (StorageCabinetEntity cabinet : cabinetList) {
            trueList.addAll((Collection<? extends E>) cabinet.getItems());
        }
        trueList.addAll((Collection<? extends E>) inv.items);
        return this;
    }
}