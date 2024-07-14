package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

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
    Collection<ItemStack> getInputs();

    /**
     * 获取输出物品
     * @return
     */
    Collection<ItemStack> getOutputs();

    void takeInputs(ItemStack[] inputs);

    /**
     * 检测是否符合输入
     * @param inputs
     * @return
     */
    boolean isMatch(Collection<ItemStack> inputs);

    /**
     * 检测是否符合输入
     * @param inputs
     * @return
     */
    boolean isMatch(ItemStack[] inputs);

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
}
