package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

public interface Recipe {
    /**
     * 获取配方ID
     * @return
     */
    String getId();

    /**
     * 获取配方名字
     * @return
     */
    String getName();

    /**
     * 获取输入物品
     * @return
     */
    default Collection<ItemStack> getInputs() {
        return Collections.EMPTY_LIST;
    }

    /**
     * 获取输出物品
     * @return
     */
    default Collection<ItemStack> getOutputs() {
        return Collections.EMPTY_LIST;
    }

    void takeInputs(ItemStack[] inputs);

    /**
     * 检测是否符合输入
     * @param inputs
     * @return
     */
    default boolean isMatch(Collection<ItemStack> inputs) {
        return false;
    }

    /**
     * 检测是否符合输入
     * @param inputs
     * @return
     */
    default boolean isMatch(ItemStack[] inputs) {
        return false;
    }

    /**
     * 配方存储
     * @param section
     */
    void store(ConfigurationSection section);

    /**
     * 配方读取
     * @param section
     */
    void load(ConfigurationSection section);

    ItemStack[] getDisplayedOutput();

    ConfigurationSection getAttribute();
}
