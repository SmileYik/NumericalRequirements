package org.eu.smileyik.numericalrequirements.core.api.item;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public interface ItemSerialization {
    /**
     * 初始化配置。
     * @param section 配置片段。
     */
    void configure(ConfigurationSection section);

    /**
     * 序列化物品为字符串。
     * @param itemStack 物品
     * @return 字符串。
     */
    String serialize(ItemStack itemStack);

    /**
     * 将字符串反序列化为物品。
     * @param string 序列化后的物品
     * @return 物品
     */
    ItemStack deserialize(String string);
}
