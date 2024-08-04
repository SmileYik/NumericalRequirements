package org.eu.smileyik.numericalrequirements.core.item;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializer;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;

import java.util.Collection;

public interface ItemKeeper {

    /**
     * 从json读取一个物品.
     * @param json
     * @return
     */
    ItemStack loadItemFromJson(String json);

    /**
     * 从json读取物品.
     * @param json
     * @param amount
     * @return
     */
    ItemStack loadItemFromJson(String json, int amount);

    /**
     * 从yaml读取1个物品
     * @param section
     * @return
     */
    ItemStack loadItemFromYaml(ConfigurationSection section);

    /**
     * 从yaml读取物品
     * @param section
     * @param amount
     * @return
     */
    ItemStack loadItemFromYaml(ConfigurationSection section, int amount);

    /**
     * 从ConfigurationHashMap中读取1个物品
     * @param map
     * @return
     */
    ItemStack loadItem(ConfigurationHashMap map);

    /**
     * 从ConfigurationHashMap中读取物品
     * @param map
     * @param amount
     * @return
     */
    ItemStack loadItem(ConfigurationHashMap map, int amount);

    /**
     * 根据物品ID读取物品. 将会返回读取到的物品的副本.
     * @param itemId
     * @return
     */
    ItemStack loadItem(String itemId);

    /**
     * 保存物品到指定物品id上.
     * 如果是新物品, 则会优先保存在拥有 main-file 配置字段的文件中,
     * 否则则优先保存在 items.json 文件中.
     * @param itemId
     * @param itemStack
     */
    void storeItem(String itemId, ItemStack itemStack);

    /**
     * 保存物品到ConfigurationHashMap
     * @param itemStack
     * @return
     */
    ConfigurationHashMap storeItem(ItemStack itemStack);

    /**
     * 获取物品序列化器.
     * @return
     */
    ItemSerializer getSerializer();

    /**
     * 从物品中获取物品ID
     * @param itemStack
     * @return
     */
    String getItemId(ItemStack itemStack);

    /**
     * 该物品是否启用物品同步.
     * @param itemStack
     * @return
     */
    boolean isSyncItem(ItemStack itemStack);

    /**
     * 保存现有物品到文件中.
     */
    void saveItems();

    /**
     * 重载物品.
     */
    void reloadItems();

    /**
     * 获取物品ID
     * @return
     */
    Collection<String> getItemIds();

    /**
     * 清理所有物品
     */
    void clear();
}
