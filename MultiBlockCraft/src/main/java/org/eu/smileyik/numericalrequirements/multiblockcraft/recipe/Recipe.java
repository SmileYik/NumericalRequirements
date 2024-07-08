package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.item.tag.lore.LorePattern;

import java.util.Collection;

public interface Recipe {
    LorePattern UNCONSUME_TAG = LorePattern.compile("不在合成时消耗");

    /**
     * 获取配方名.
     * @return
     */
    String getName();

    /**
     * 配方所要求的材料，每个相同材料仅占一个元素长。
     * 包含所需个数。
     * @return
     */
    Collection<ItemStack> getMaterials();

    /**
     * 配方所给予的产物，每个相同材料仅占一个元素长。
     * @return
     */
    ItemStack[] getProducts();

    boolean isMatch(ItemStack[] items);
    boolean takeMaterials(ItemStack[] items);

    void store(ConfigurationSection section);
    void load(ConfigurationSection section);
}
