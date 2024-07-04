package org.eu.smileyik.numericalrequirements.nms.nbtitem;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * 使用NBTItemHelper将Bukkit物品转换为NBTItem.
 *
 * 使用过程中，仅有调用 getItemStack() 后才会将修改保存。
 * 并且需要注意的是，如果仅使用Bukkit物品创建实例并且不调用
 * 任何其他方法，则 getItemStack() 所返回的物品为原来物品，
 * 若调用其他方法，则将返回的为原来的副本物品，或者在修改失败时，
 * 将仅返回null。
 * <p>使用例子1：</p>
 * <code><pre>
 *     ItemStack item = new ItemStack(Material.APPLE);
 *     NBTItem nbtItem = NBTItemHelper.cast(item);
 *     nbtItem.put("Key1", "Value");
 *     nbtItem.put("Key2", 2);
 *     ItemStack newItem = nbtItem.getItemStack();
 * </pre></code>
 * <p>使用例子2：</p>
 * <code><pre>
 *     ItemStack item = new ItemStack(Material.APPLE);
 *     ItemStack newItem = NBTItemHelper.cast(item)
 *                                      .append("Key1", "Value")
 *                                      .append("Key2", 2)
 *                                      .getItemStack()
 * </pre></code>
 */
public interface NBTItem {

    static String getBukkitVersion() {
        return Bukkit.getServer().getClass()
                .getPackage().getName().replace(".", ",").split(",")[3];
    }

    /**
     * 是否存在指定Key
     * @param key 需要查询的Key
     * @return 如果存在则返回True, 反之则为False
     */
    boolean containsKey(String key);

    /**
     * 放置NBT
     * @param key Key
     * @param value 需要放置的值，get方法支持什么类型，此放置方法就支持什么类型。
     */
    void put(String key, Object value);

    /**
     * 放置NBT并返回当前实例。
     * @param key Key
     * @param value 需要放置的值，get方法支持什么类型，此放置方法就支持什么类型。
     * @return 当前实例。
     */
    default NBTItem append(String key, Object value) {
        put(key, value);
        return this;
    }

    /**
     * 根据提供的Key获取字符串。
     * @param key
     * @return Key若不存在则返回null。
     */
    String getString(String key);

    /**
     * 根据提供的Key获取字节值
     * @param key
     * @return
     */
    Byte getByte(String key);

    /**
     * 根据提供的Key获取整形数值。
     * @param key
     * @return
     */
    Integer getInt(String key);

    /**
     * 根据提供的Key获取短整型值。
     * @param key
     * @return
     */
    Short getShort(String key);

    /**
     * 根据提供的Key获取长整型值。
     * @param key
     * @return
     */
    Long getLong(String key);

    /**
     * 根据提供的Key获取双精度浮点数值。
     * @param key
     * @return
     */
    Double getDouble(String key);

    /**
     * 根据提供的Key获取单精度浮点数值。
     * @param key
     * @return
     */
    Float getFloat(String key);

    /**
     * 根据提供的Key获取布尔值。
     * @param key
     * @return
     */
    boolean getBoolean(String key);

    /**
     * 根据提供的Key获取字节数组。
     * @param key
     * @return
     */
    byte[] getByteArray(String key);

    /**
     * 根据提供的Key获取整形数组。
     * @param key
     * @return
     */
    int[] getIntArray(String key);

    /**
     * 根据提供的Key获取UUID。
     * @param key
     * @return
     */
    UUID getUUID(String key);

    /**
     * 移除一个Key.
     * @param key
     */
    void remove(String key);

    /**
     * 移除一个Key并返回当前正在操作的NBT物品实例。
     * @param key Key
     * @return 当前实例
     */
    default NBTItem erase(String key) {
        remove(key);
        return this;
    }

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
