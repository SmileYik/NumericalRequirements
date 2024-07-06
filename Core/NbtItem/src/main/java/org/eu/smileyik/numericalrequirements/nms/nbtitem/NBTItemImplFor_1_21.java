package org.eu.smileyik.numericalrequirements.nms.nbtitem;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;

import java.util.Objects;

public class NBTItemImplFor_1_21 implements NBTItem {

    private ItemStack item;
    private final NMSItemStack nmsItem;
    private NBTTagCompound tag;

    public NBTItemImplFor_1_21(ItemStack item) {
        this.item = item;
        nmsItem = CraftItemStack.asNMSCopy(item);
    }

    @Override
    public NBTTagCompound getTag() {
        if (tag == null) {
            CustomData customData = nmsItem.getCustomData();
            tag = customData == null ? new NBTTagCompound() : customData.getNBTTagCompound();
        }
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        return tag;
    }

    @Override
    public void setTag(NBTTagCompound tag) {
        this.tag = tag;
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
        nmsItem.setCustomData(CustomData.newCustomData(tag));
        item = CraftItemStack.asBukkitCopy(nmsItem);
        return item;
    }
}
