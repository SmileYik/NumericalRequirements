package org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_5_to_1_16;

import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class NBTItemMethods {
    // -------------------
    //   CraftItemStack
    // -------------------
    public static final Method asNMSCopy;
    public static final Method asBukkitCopy;

    // -------------------
    //     ItemStack
    // -------------------
    public static final Method hasTag;
    public static final Method getTag;
    public static final Method setTag;
    public static final Method save;

    // -------------------
    //   NBTTagCompound
    // -------------------
    public static final Constructor<?> newNBTTagCompound;
    public static final Method hasKey;
    public static final Method setInt;
    public static final Method getInt;
    public static final Method setDouble;
    public static final Method getDouble;
    public static final Method setBoolean;
    public static final Method getBoolean;
    public static final Method setIntArray;
    public static final Method getIntArray;
    public static final Method setByte;
    public static final Method getByte;
    public static final Method setShort;
    public static final Method getShort;
    public static final Method setLong;
    public static final Method getLong;
    public static final Method setFloat;
    public static final Method getFloat;
    public static final Method setString;
    public static final Method getString;
    public static final Method setByteArray;
    public static final Method getByteArray;

    static {
        String ver = NBTItem.getBukkitVersion();
        try {
            // ----------------
            //      CLASS
            // ----------------
            Class<?> ItemStackClass = Class.forName(String.format("net.minecraft.server.%s.ItemStack", ver));
            Class<?> NBTTagCompoundClass = Class.forName(String.format("net.minecraft.server.%s.NBTTagCompound", ver));
            Class<?> CraftItemStackClass = Class.forName(String.format("org.bukkit.craftbukkit.%s.inventory.CraftItemStack", ver));
            // ----------------
            //   ItemStack
            // ----------------
            hasTag = ItemStackClass.getDeclaredMethod("hasTag");
            getTag = ItemStackClass.getDeclaredMethod("getTag");
            setTag = ItemStackClass.getDeclaredMethod("setTag", NBTTagCompoundClass);
            save = ItemStackClass.getDeclaredMethod("save", NBTTagCompoundClass);
            // ----------------
            //  CraftItemStack
            // ----------------
            asBukkitCopy = CraftItemStackClass.getDeclaredMethod("asBukkitCopy", ItemStackClass);
            asNMSCopy = CraftItemStackClass.getDeclaredMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
            // ----------------
            //  NBTTagCompound
            // ----------------
            newNBTTagCompound = NBTTagCompoundClass.getDeclaredConstructor();
            hasKey = NBTTagCompoundClass.getDeclaredMethod("hasKey", String.class);
            getInt = NBTTagCompoundClass.getDeclaredMethod("getInt", String.class);
            getDouble = NBTTagCompoundClass.getDeclaredMethod("getDouble", String.class);
            getBoolean = NBTTagCompoundClass.getDeclaredMethod("getBoolean", String.class);
            getIntArray = NBTTagCompoundClass.getDeclaredMethod("getIntArray", String.class);
            getByte = NBTTagCompoundClass.getDeclaredMethod("getByte", String.class);
            getShort = NBTTagCompoundClass.getDeclaredMethod("getShort", String.class);
            getLong = NBTTagCompoundClass.getDeclaredMethod("getLong", String.class);
            getFloat = NBTTagCompoundClass.getDeclaredMethod("getFloat", String.class);
            getString = NBTTagCompoundClass.getDeclaredMethod("getString", String.class);
            getByteArray = NBTTagCompoundClass.getDeclaredMethod("getByteArray", String.class);
            setInt = NBTTagCompoundClass.getDeclaredMethod("setInt", String.class, int.class);
            setDouble = NBTTagCompoundClass.getDeclaredMethod("setDouble", String.class, double.class);
            setBoolean = NBTTagCompoundClass.getDeclaredMethod("setBoolean", String.class, boolean.class);
            setIntArray = NBTTagCompoundClass.getDeclaredMethod("setIntArray", String.class, int[].class);
            setByte = NBTTagCompoundClass.getDeclaredMethod("setByte", String.class, byte.class);
            setByteArray = NBTTagCompoundClass.getDeclaredMethod("setByteArray", String.class, byte[].class);
            setFloat = NBTTagCompoundClass.getDeclaredMethod("setFloat", String.class, float.class);
            setLong = NBTTagCompoundClass.getDeclaredMethod("setLong", String.class, long.class);
            setString = NBTTagCompoundClass.getDeclaredMethod("setString", String.class, String.class);
            setShort = NBTTagCompoundClass.getDeclaredMethod("setShort", String.class, short.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
