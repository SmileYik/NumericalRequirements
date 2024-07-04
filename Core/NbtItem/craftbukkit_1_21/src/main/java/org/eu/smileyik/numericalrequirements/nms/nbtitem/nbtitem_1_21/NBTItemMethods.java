package org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_21;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.item.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectField;
import org.eu.smileyik.numericalrequirements.reflect.ReflectMethod;

import java.lang.reflect.Method;

public class NBTItemMethods {
    // ----------------
    // DataComponents
    // ----------------
    public static final ReflectField getCustomDataKey;

    // ----------------
    //     ItemStack
    // ----------------
    public static final ReflectMethod<Object> getCustomData;
    public static final ReflectMethod<Void> setCustomData;

    // ----------------
    //    CustomData
    // ----------------
    public static final ReflectMethod<NBTTagCompound> getNBTTagCompound;
    public static final ReflectMethod<Object> newCustomData;

    static {
        try {
            getCustomDataKey = MySimpleReflect.get("net.minecraft.core.component.DataComponents@b");

            getCustomData = MySimpleReflect.get("net.minecraft.world.item.ItemStack#c(net.minecraft.core.component.DataComponentType)");
            setCustomData = MySimpleReflect.get("net.minecraft.world.item.ItemStack#b(net.minecraft.core.component.DataComponentType, java.lang.Object)");

            getNBTTagCompound = MySimpleReflect.get("net.minecraft.world.item.component.CustomData#c()");
            newCustomData = MySimpleReflect.get("net.minecraft.world.item.component.CustomData#a(net.minecraft.nbt.NBTTagCompound)");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
