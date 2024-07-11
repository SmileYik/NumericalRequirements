package org.eu.smileyik.numericalrequirements.core.api.item.tag.nbt;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.ItemTag;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;

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
}
