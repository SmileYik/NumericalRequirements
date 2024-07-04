package org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_21;

import net.minecraft.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_18.AbstractNBTItem_1_18;

import static org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_18.NBTItemMethods.*;
        import static org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_21.NBTItemMethods.*;

public class NBTItem_1_21 extends AbstractNBTItem_1_18 {

    public NBTItem_1_21(org.bukkit.inventory.ItemStack item) {
        super(item);
    }

    @Override
    protected Object doGetTags(Object nmsi) {
        if (nmsi == null) return null;
        Object customData = getCustomData.execute(nmsi, getCustomDataKey.execute(null));
        return customData == null ? new NBTTagCompound() : getNBTTagCompound.execute(customData);
    }

    @Override
    protected String doSaveToString(Object copyO) {
        if (copyO == null) return null;
        return doGetTags(copyO).toString();
    }

    @Override
    protected org.bukkit.inventory.ItemStack applyNBT(Object nmsCopy, Object tags) {
        setCustomData.execute(nmsCopy,
                getCustomDataKey.execute(null),
                newCustomData.execute(null, tags));
        try {
            return (org.bukkit.inventory.ItemStack) asBukkitCopy.invoke(null, nmsCopy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
