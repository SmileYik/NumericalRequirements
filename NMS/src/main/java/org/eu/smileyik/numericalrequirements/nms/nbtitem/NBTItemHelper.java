package org.eu.smileyik.numericalrequirements.nms.nbtitem;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.impl.NBTItemImpl;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.impl.NBTItemImplFor_1_21;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

/**
 * 辅助创建NBTItem实例。
 * @see NBTItem
 */
public class NBTItemHelper {

    public static String[] VERSIONS = VersionScript.VERSION.split("_");
    public static int MIDDLE_VERSION = Integer.parseInt(VERSIONS[1]);

    /**
     * 将Bukkit的物品转换为NBT物品。
     * @param itemStack 要转换的物品。
     * @return 如果转换成功返回对应NBT物品，否则则返回null。
     */
    public static NBTItem cast(ItemStack itemStack) {
        if (MIDDLE_VERSION >= 21) {
            return new NBTItemImplFor_1_21(itemStack);
        } else if (MIDDLE_VERSION >= 5) {
            return new NBTItemImpl(itemStack);
        }
        return null;
    }
}
