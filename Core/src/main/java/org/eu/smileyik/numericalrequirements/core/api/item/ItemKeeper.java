package org.eu.smileyik.numericalrequirements.core.api.item;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagCompound;
import org.eu.smileyik.numericalrequirements.nms.nbt.NBTTagTypeId;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItem;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.NBTItemHelper;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public interface ItemKeeper {
    /**
     * 根据物品ID读取物品. 将会返回读取到的物品的副本.
     * @param itemId
     * @return
     */
    ItemStack loadItem(String itemId);

    ItemStack loadItem(String itemId, int amount);

    /**
     * 保存物品到指定物品id上.
     * 如果是新物品, 则会优先保存在拥有 main-file 配置字段的文件中,
     * 否则则优先保存在 items.json 文件中.
     * @param itemId
     * @param itemStack
     */
    void storeItem(String itemId, ItemStack itemStack);

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
    default String getItemId(ItemStack itemStack) {
        if (itemStack == null) return null;
        NBTItem cast = NBTItemHelper.cast(itemStack);
        if (cast == null) return null;
        NBTTagCompound tag = cast.getTag();
        if (tag == null) return null;
        if (tag.hasKeyOfType(ItemService.NBT_KEY_ID, NBTTagTypeId.STRING)) {
            return tag.getString(ItemService.NBT_KEY_ID);
        }
        return null;
    }

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

    static void setItemId(ItemStack itemStack, String itemId) {
        if (itemStack == null) return;
        NBTItem cast = NBTItemHelper.cast(itemStack);
        if (cast != null) {
            NBTTagCompound tag = cast.getTag();
            if (tag != null) {
                tag.setString(ItemService.NBT_KEY_ID, itemId);
                ItemStack copy = cast.getItemStack();
                if (copy != null) {
                    itemStack.setItemMeta(copy.getItemMeta());
                }
            }
        }
    }

    static ItemKeeper newKeeper(Plugin plugin, ConfigurationSection config) {
        String string = config.getString("type");
        try {
            return (ItemKeeper) Class.forName(string).getDeclaredConstructor(Plugin.class, ConfigurationSection.class).newInstance(plugin, config);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException | ClassCastException e) {
            DebugLogger.debug(e);
        }
        return null;
    }
}
