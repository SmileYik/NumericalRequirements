package org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_19;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_18.AbstractNBTItem_1_18;

public class NBTItem_1_19 extends AbstractNBTItem_1_18 {

    public NBTItem_1_19(org.bukkit.inventory.ItemStack item) {
        super(item);
    }

    @Override
    protected Object doGetTags(Object o) {
        ItemStack nmsCopy = (ItemStack) o;
        if (nmsCopy == null) return null;
        return nmsCopy.t() ? nmsCopy.u() : new NBTTagCompound();
    }
}
