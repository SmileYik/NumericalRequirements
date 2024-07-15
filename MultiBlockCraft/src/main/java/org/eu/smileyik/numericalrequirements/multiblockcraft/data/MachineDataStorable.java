package org.eu.smileyik.numericalrequirements.multiblockcraft.data;

import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public interface MachineDataStorable extends MachineData {
    /**
     * 遍历现有物品
     * @param consumer
     */
    void forEach(BiConsumer<Integer, ItemStack> consumer);

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
