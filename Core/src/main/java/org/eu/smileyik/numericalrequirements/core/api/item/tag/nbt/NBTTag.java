package org.eu.smileyik.numericalrequirements.core.api.item.tag.nbt;

import org.eu.smileyik.numericalrequirements.core.api.item.tag.ItemTag;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;

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
}
