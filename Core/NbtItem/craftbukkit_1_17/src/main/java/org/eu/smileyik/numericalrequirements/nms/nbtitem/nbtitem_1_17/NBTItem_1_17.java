package org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_17;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.AbstractNBTItem;

import static org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_17.NBTItemMethods.*;

public class NBTItem_1_17 extends AbstractNBTItem {

    public NBTItem_1_17(org.bukkit.inventory.ItemStack item) {
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
        return nmsCopy.hasTag() ? nmsCopy.getTag() : new NBTTagCompound();
    }

    @Override
    protected boolean doContainsKey(Object tagsO, String key) {
        NBTTagCompound tags = (NBTTagCompound) tagsO;
        return tags.hasKey(key);
    }

    @Override
    protected void doPut(Object tagsO, String key, Object value) {
        NBTTagCompound tags = (NBTTagCompound) tagsO;
        if (value instanceof String) {
            tags.setString(key, (String) value);
        } else if (value instanceof Integer) {
            tags.setInt(key, (int) value);
        } else if (value instanceof Double) {
            tags.setDouble(key, (double) value);
        } else if (value instanceof Boolean) {
            tags.setBoolean(key, (boolean) value);
        } else if (value instanceof Float) {
            tags.setFloat(key, (float) value);
        } else if (value instanceof Long) {
            tags.setLong(key, (long) value);
        } else if (value instanceof Short) {
            tags.setShort(key, (short) value);
        } else if (value instanceof Byte) {
            tags.setByte(key, (byte) value);
        } else if (value instanceof int[]) {
            tags.setIntArray(key, (int[]) value);
        } else if (value instanceof byte[]) {
            tags.setByteArray(key, (byte[]) value);
        }
    }

    @Override
    protected String doGetString(Object tags, String key) {
        return ((NBTTagCompound) tags).getString(key);
    }

    @Override
    protected Byte doGetByte(Object tags, String key) {
        return ((NBTTagCompound) tags).getByte(key);
    }

    @Override
    protected Integer doGetInt(Object tags, String key) {
        return ((NBTTagCompound) tags).getInt(key);
    }

    @Override
    protected Short doGetShort(Object tags, String key) {
        return ((NBTTagCompound) tags).getShort(key);
    }

    @Override
    protected Long doGetLong(Object tags, String key) {
        return ((NBTTagCompound) tags).getLong(key);
    }

    @Override
    protected Double doGetDouble(Object tags, String key) {
        return ((NBTTagCompound) tags).getDouble(key);
    }

    @Override
    protected Float doGetFloat(Object tags, String key) {
        return ((NBTTagCompound) tags).getFloat(key);
    }

    @Override
    protected boolean doGetBoolean(Object tags, String key) {
        return ((NBTTagCompound) tags).getBoolean(key);
    }

    @Override
    protected byte[] doGetByteArray(Object tags, String key) {
        return ((NBTTagCompound) tags).getByteArray(key);
    }

    @Override
    protected int[] doGetIntArray(Object tags, String key) {
        return ((NBTTagCompound) tags).getIntArray(key);
    }

    @Override
    protected String doSaveToString(Object copyO) {
        if (copyO == null) return null;
        ItemStack copy = (ItemStack) copyO;
        return copy.save(new NBTTagCompound()).toString();
    }

    @Override
    protected org.bukkit.inventory.ItemStack applyNBT(Object nmsCopy, Object tags) {
        ((ItemStack) nmsCopy).setTag((NBTTagCompound) tags);
        try {
            return (org.bukkit.inventory.ItemStack) asBukkitCopy.invoke(null, nmsCopy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
