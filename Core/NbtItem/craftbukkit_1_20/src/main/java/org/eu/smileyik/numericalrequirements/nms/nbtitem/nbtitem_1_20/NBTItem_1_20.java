package org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_20;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_18.AbstractNBTItem_1_18;

public class NBTItem_1_20 extends AbstractNBTItem_1_18 {

    public NBTItem_1_20(ItemStack item) {
        super(item);
    }

    @Override
    protected Object doGetTags(Object o) {
        net.minecraft.world.item.ItemStack nmsCopy = (net.minecraft.world.item.ItemStack) o;
        if (nmsCopy == null) return null;
        return nmsCopy.u() ? nmsCopy.v() : new NBTTagCompound();
    }
}
