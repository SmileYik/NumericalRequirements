package org.eu.smileyik.numericalrequirements.nms.nbtitem;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_17.NBTItem_1_17;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_5_to_1_16.NBTItem_1_5_to_1_16;

import java.lang.reflect.Constructor;

/**
 * 辅助创建NBTItem实例。
 * @see NBTItem
 */
public class NBTItemHelper {
    private static final Constructor<? extends NBTItem> newNBTItem;

    static {
        String version = NBTItem.getBukkitVersion();
        String[] versions = version.split("_");
        int i = Integer.parseInt(versions[1]);
        if (i >= 5 && i <= 16) {
            try {
                newNBTItem = NBTItem_1_5_to_1_16.class.getDeclaredConstructor(ItemStack.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (i == 17) {
            try {
                newNBTItem = NBTItem_1_17.class.getDeclaredConstructor(ItemStack.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            newNBTItem = null;
        }
    }

    /**
     * 将Bukkit的物品转换为NBT物品。
     * @param itemStack 要转换的物品。
     * @return 如果转换成功返回对应NBT物品，否则则返回null。
     */
    public static NBTItem cast(ItemStack itemStack) {
        if (newNBTItem == null) {
            return null;
        }
        try {
            return newNBTItem.newInstance(itemStack);
        } catch (Exception e) {
            return null;
        }
    }
}
