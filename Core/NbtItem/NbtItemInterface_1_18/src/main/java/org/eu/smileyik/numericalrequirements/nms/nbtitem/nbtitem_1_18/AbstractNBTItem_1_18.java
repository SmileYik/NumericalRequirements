package org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_18;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.AbstractNBTItem;

import java.util.UUID;

import static org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_18.NBTItemMethods.*;

public abstract class AbstractNBTItem_1_18 extends AbstractNBTItem {

    public AbstractNBTItem_1_18(org.bukkit.inventory.ItemStack item) {
        super(item);
    }

    @Override
    protected Object doGetNMSCopy(org.bukkit.inventory.ItemStack item) {
        try {
            return NBTItemMethods.asNMSCopy.invoke(null, item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Object doGetTags(Object o) {
        ItemStack nmsCopy = (ItemStack) o;
        if (nmsCopy == null) return null;
        return nmsCopy.r() ? nmsCopy.t() : new NBTTagCompound();
    }

    @Override
    protected boolean doContainsKey(Object tagsO, String key) {
        NBTTagCompound tags = (NBTTagCompound) tagsO;
        return tags.e(key);
    }

    @Override
    protected void doPut(Object tagsO, String key, Object value) {
        NBTTagCompound tags = (NBTTagCompound) tagsO;
        if (value instanceof UUID) {
            tags.a(key, (UUID) value);
        } else if (value instanceof String) {
            tags.a(key, (String) value);
        } else if (value instanceof Integer) {
            tags.a(key, (int) value);
        } else if (value instanceof Double) {
            tags.a(key, (double) value);
        } else if (value instanceof Boolean) {
            tags.a(key, (boolean) value);
        } else if (value instanceof Float) {
            tags.a(key, (float) value);
        } else if (value instanceof Long) {
            tags.a(key, (long) value);
        } else if (value instanceof Short) {
            tags.a(key, (short) value);
        } else if (value instanceof Byte) {
            tags.a(key, (byte) value);
        } else if (value instanceof int[]) {
            tags.a(key, (int[]) value);
        } else if (value instanceof byte[]) {
            tags.a(key, (byte[]) value);
        }
    }

    @Override
    protected String doGetString(Object tags, String key) {
        return ((NBTTagCompound) tags).l(key);
    }

    @Override
    protected Byte doGetByte(Object tags, String key) {
        return ((NBTTagCompound) tags).f(key);
    }

    @Override
    protected Integer doGetInt(Object tags, String key) {
        return ((NBTTagCompound) tags).h(key);
    }

    @Override
    protected Short doGetShort(Object tags, String key) {
        return ((NBTTagCompound) tags).g(key);
    }

    @Override
    protected Long doGetLong(Object tags, String key) {
        return ((NBTTagCompound) tags).i(key);
    }

    @Override
    protected Double doGetDouble(Object tags, String key) {
        return ((NBTTagCompound) tags).k(key);
    }

    @Override
    protected Float doGetFloat(Object tags, String key) {
        return ((NBTTagCompound) tags).j(key);
    }

    @Override
    protected boolean doGetBoolean(Object tags, String key) {
        return ((NBTTagCompound) tags).q(key);
    }

    @Override
    protected byte[] doGetByteArray(Object tags, String key) {
        return ((NBTTagCompound) tags).m(key);
    }

    @Override
    protected int[] doGetIntArray(Object tags, String key) {
        return ((NBTTagCompound) tags).n(key);
    }

    @Override
    protected UUID doGetUUID(Object tags, String key) {
        return ((NBTTagCompound) tags).a(key);
    }

    @Override
    protected void doRemove(Object tagsO, String key) {
        NBTTagCompound tags = (NBTTagCompound) tagsO;
        tags.r(key);
    }

    @Override
    protected String doSaveToString(Object copyO) {
        if (copyO == null) return null;
        ItemStack copy = (ItemStack) copyO;
        return copy.b(new NBTTagCompound()).toString();
    }

    @Override
    protected org.bukkit.inventory.ItemStack applyNBT(Object nmsCopy, Object tags) {
        ((ItemStack) nmsCopy).c((NBTTagCompound) tags);
        try {
            return (org.bukkit.inventory.ItemStack) asBukkitCopy.invoke(null, nmsCopy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
