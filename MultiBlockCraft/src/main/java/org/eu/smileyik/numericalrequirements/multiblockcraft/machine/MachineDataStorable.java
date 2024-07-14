package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.bukkit.inventory.ItemStack;

public interface MachineDataStorable extends MachineData {
    /**
     * 设置物品
     * @param slot
     * @param item
     */
    void setItem(int slot, ItemStack item);

    /**
     * 获取物品
     * @param slot
     */
    ItemStack getItem(int slot);

    /**
     * 移除物品
     * @param slot
     */
    void removeItem(int slot);
}
