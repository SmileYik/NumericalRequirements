package org.eu.smileyik.numericalrequirements.nms.nbtitem;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
/**
 * 使用NBTItemHelper将Bukkit物品转换为NBTItem.
 */
public interface NBTItem {

    static String getBukkitVersion() {
        return Bukkit.getServer().getClass()
                .getPackage().getName().replace(".", ",").split(",")[3];
    }

    /**
     * 获取或者创建一个NBT.
     * @return
     */
    NBTTagCompound getTag();

    /**
     * 应用NBT到物品上.
     * @param tag
     */
    void setTag(NBTTagCompound tag);

    /**
     * 将该物品的NBT标签保存为字符串。
     * @return
     */
    String saveToString();

    /**
     * 保存标签并获取物品。
     * @return
     */
    ItemStack getItemStack();
}
