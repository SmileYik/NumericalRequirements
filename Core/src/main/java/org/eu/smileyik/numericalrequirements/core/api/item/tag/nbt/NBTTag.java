package org.eu.smileyik.numericalrequirements.core.api.item.tag.nbt;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.ItemTag;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

import java.util.List;

public abstract class NBTTag <V> implements ItemTag<V> {
    /**
     * 获取NBTTag的key
     * @return
     */
    public String getTagKey() {
        return getId();
    }

    /**
     * 获取值
     * @param tagCompound
     * @return
     */
    public abstract V getValue(NBTTagCompound tagCompound);

    public abstract boolean isValidValue(List<String> value);

    public abstract void setValue(ItemStack itemStack, List<String> value);

    public void setValue(ItemStack itemStack, V value) {
        throw new RuntimeException("not implemented yet");
    }
    /**
     * 从物品中获取值。
     * @param itemStack
     * @return
     */
    public V getValue(ItemStack itemStack) {
        NBTItem cast = NBTItemHelper.cast(itemStack);
        if (cast == null) return null;
        NBTTagCompound tag = cast.getTag();
        return tag == null ? null : getValue(tag);
    }
}
