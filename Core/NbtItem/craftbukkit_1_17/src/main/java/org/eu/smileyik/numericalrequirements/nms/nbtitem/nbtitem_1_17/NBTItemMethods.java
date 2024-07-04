package org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_17;

import net.minecraft.world.item.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;

import java.lang.reflect.Method;

public class NBTItemMethods {
    // -------------------
    //   CraftItemStack
    // -------------------
    public static final Method asNMSCopy;
    public static final Method asBukkitCopy;

    static {
        String ver = NBTItem.getBukkitVersion();
        try {
            // ----------------
            //      CLASS
            // ----------------
            Class<?> CraftItemStackClass = Class.forName(String.format("org.bukkit.craftbukkit.%s.inventory.CraftItemStack", ver));
            // ----------------
            //  CraftItemStack
            // ----------------
            asBukkitCopy = CraftItemStackClass.getDeclaredMethod("asBukkitCopy", ItemStack.class);
            asNMSCopy = CraftItemStackClass.getDeclaredMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
