package org.eu.smileyik.numericalrequirements.nms.nbtitem.impl;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.CraftItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NMSItemStack;

import java.util.Objects;

public class NBTItemImpl implements NBTItem {
    private ItemStack item;
    private final NMSItemStack nmsItem;
    private NBTTagCompound tag;

    public NBTItemImpl(ItemStack item) {
        this.item = item;
        nmsItem = CraftItemStack.asNMSCopy(item);
    }

    @Override
    public boolean hasTag() {
        return nmsItem.hasTag();
    }

    @Override
    public NBTTagCompound getTag() {
        if (nmsItem == null) return null;
        if (tag == null) {
            tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        }
        return tag;
    }

    @Override
    public void setTag(NBTTagCompound tag) {
        this.tag = tag;
        nmsItem.setTag(tag);
    }

    @Override
    public String saveToString() {
        if (tag == null) {
            tag = getTag();
        }
        return Objects.toString(tag);
    }

    @Override
    public ItemStack getItemStack() {
        nmsItem.setTag(tag);
        item = CraftItemStack.asBukkitCopy(nmsItem);
        return item;
    }

    @Override
    public NMSItemStack getNMSItemStack() {
        return nmsItem;
    }
}
