package org.eu.smileyik.numericalrequirements.nms.nbtitem;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class AbstractNBTItem implements NBTItem {
    private ItemStack item;
    private Object nmsCopy;
    private Object tags;

    private final Object nmsCopyLock = new Object();
    private final Object tagsLock = new Object();

    public AbstractNBTItem(ItemStack item) {
        this.item = item;
    }

    private Object getNMSCopy() {
        synchronized (nmsCopyLock) {
            if (nmsCopy == null) {
                nmsCopy = doGetNMSCopy(item);
            }
        }
        return nmsCopy;
    }

    private Object getTags() {
        synchronized (tagsLock) {
            if (tags == null) {
                tags = doGetTags(getNMSCopy());
            }
        }
        return tags;
    }

    /**
     * 是否存在指定Key
     *
     * @param key 需要查询的Key
     * @return 如果存在则返回True, 反之则为False
     */
    @Override
    public boolean containsKey(String key) {
        Object tags = getTags();
        return tags != null && doContainsKey(tags, key);
    }

    /**
     * 放置NBT
     *
     * @param key   Key
     * @param value 需要放置的值，get方法支持什么类型，此放置方法就支持什么类型。
     */
    @Override
    public void put(String key, Object value) {
        Object tags = getTags();
        if (tags != null) {
            if (value == null) {
                remove(key);
            } else {
                doPut(tags, key, value);
            }
        }
    }

    /**
     * 根据提供的Key获取字符串。
     *
     * @param key
     * @return Key若不存在则返回Null。
     */
    @Override
    public String getString(String key) {
        Object tags = getTags();
        if (tags == null) {
            return null;
        }
        return doGetString(tags, key);
    }

    /**
     * 根据提供的Key获取字节值
     *
     * @param key
     * @return
     */
    @Override
    public Byte getByte(String key) {
        Object tags = getTags();
        if (tags == null) {
            return null;
        }
        return doGetByte(tags, key);
    }

    /**
     * 根据提供的Key获取整形数值。
     *
     * @param key
     * @return
     */
    @Override
    public Integer getInt(String key) {
        Object tags = getTags();
        if (tags == null) {
            return null;
        }
        return doGetInt(tags, key);
    }

    /**
     * 根据提供的Key获取短整型值。
     *
     * @param key
     * @return
     */
    @Override
    public Short getShort(String key) {
        Object tags = getTags();
        if (tags == null) {
            return null;
        }
        return doGetShort(tags, key);
    }

    /**
     * 根据提供的Key获取长整型值。
     *
     * @param key
     * @return
     */
    @Override
    public Long getLong(String key) {
        Object tags = getTags();
        if (tags == null) {
            return null;
        }
        return doGetLong(tags, key);
    }

    /**
     * 根据提供的Key获取双精度浮点数值。
     *
     * @param key
     * @return
     */
    @Override
    public Double getDouble(String key) {
        Object tags = getTags();
        if (tags == null) {
            return null;
        }
        return doGetDouble(tags, key);
    }

    /**
     * 根据提供的Key获取单精度浮点数值。
     *
     * @param key
     * @return
     */
    @Override
    public Float getFloat(String key) {
        Object tags = getTags();
        if (tags == null) {
            return null;
        }
        return doGetFloat(tags, key);
    }

    /**
     * 根据提供的Key获取布尔值。
     *
     * @param key
     * @return
     */
    @Override
    public boolean getBoolean(String key) {
        Object tags = getTags();
        if (tags == null) {
            return false;
        }
        return doGetBoolean(tags, key);
    }

    /**
     * 根据提供的Key获取字节数组。
     *
     * @param key
     * @return
     */
    @Override
    public byte[] getByteArray(String key) {
        Object tags = getTags();
        if (tags == null) {
            return null;
        }
        return doGetByteArray(tags, key);
    }

    /**
     * 根据提供的Key获取整形数组。
     *
     * @param key
     * @return
     */
    @Override
    public int[] getIntArray(String key) {
        Object tags = getTags();
        if (tags == null) {
            return null;
        }
        return doGetIntArray(tags, key);
    }

    @Override
    public UUID getUUID(String key) {
        Object tags = getTags();
        if (tags == null) {
            return null;
        }
        return doGetUUID(tags, key);
    }

    @Override
    public void remove(String key) {
        Object tags = getTags();
        if (tags != null) doRemove(tags, key);
    }

    /**
     * 将该物品的NBT标签保存为字符串。
     *
     * @return
     */
    @Override
    public String saveToString() {
        Object copy = getNMSCopy();
        if (copy == null) {
            return null;
        }
        return doSaveToString(copy);
    }

    /**
     * 保存标签并获取物品。
     * 在正常返回物品时，会自动更新NBTItem内的物品数据，使得下一次操作能够免去
     * 构建NBTItem实例的步骤。
     *
     * @return 若本实例未调用其他方法则返回的物品与传入构造器时的物品为同一个物品。
     * 若调用了其他方法则大概率返回一个新的物品，若在转换过程中发生意外，则可能返回
     * null。
     */
    @Override
    public ItemStack getItemStack() {
        if (nmsCopy == null || tags == null) {
            return item;
        }

        synchronized (nmsCopyLock) {
            synchronized (tagsLock) {
                item = applyNBT(nmsCopy, tags);

            }
        }
        return item;
    }

    /**
     * 将NBT的修改应用到物品上并返回Bukkit版本物品。
     * @param nmsCopy NMS版本物品实例,不可能为null
     * @param tags NBT组件,不可能为null
     * @return 失败返回null
     */
    protected abstract ItemStack applyNBT(Object nmsCopy, Object tags);

    /**
     * 将物品的NBT组件内容保存为字符串。
     * @param copy NMS版本物品实例, 不可能为null
     * @return 失败返回null
     */
    protected abstract String doSaveToString(Object copy);

    /**
     * 获取字节数组。
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @return 失败返回null
     */
    protected abstract byte[] doGetByteArray(Object tags, String key);

    /**
     * 获取布尔值。
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @return
     */
    protected abstract boolean doGetBoolean(Object tags, String key);

    /**
     * 获取单精度浮点数。
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @return 失败返回null
     */
    protected abstract Float doGetFloat(Object tags, String key);

    /**
     * 获取双精度浮点数。
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @return 失败返回null
     */
    protected abstract Double doGetDouble(Object tags, String key);

    /**
     * 获取长整型数。
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @return 失败返回null
     */
    protected abstract Long doGetLong(Object tags, String key);

    /**
     * 获取短整型数
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @return 失败返回null
     */
    protected abstract Short doGetShort(Object tags, String key);

    /**
     * 获取整形数
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @return 失败返回null
     */
    protected abstract Integer doGetInt(Object tags, String key);

    /**
     * 获取字节类型数值。
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @return 失败返回null
     */
    protected abstract Byte doGetByte(Object tags, String key);

    /**
     * 获取字符串值。
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @return 失败返回null
     */
    protected abstract String doGetString(Object tags, String key);

    /**
     * 根据Bukkit版本物品获取NMS版本物品。
     * @param item Bukkit版本物品。 可能为null
     * @return 获取失败返回null。
     */
    protected abstract Object doGetNMSCopy(ItemStack item);

    /**
     * 根据NMS物品获取NBT组件。
     * @param nmsCopy NMS物品，可能为null
     * @return 获取失败返回null
     */
    protected abstract Object doGetTags(Object nmsCopy);

    /**
     * 检测是否包含某条NBT
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @return 如果包含返回true,反之返回false
     */
    protected abstract boolean doContainsKey(Object tags, String key);

    /**
     * 放置一条NBT至NBT组件中。
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @param value 值
     */
    protected abstract void doPut(Object tags, String key, Object value);

    /**
     * 获取整形数组。
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @return 失败返回null
     */
    protected abstract int[] doGetIntArray(Object tags, String key);

    /**
     * 获取UUID。
     * @param tags NBT组件，不可能为null
     * @param key Key
     * @return 失败返回null
     */
    protected abstract UUID doGetUUID(Object tags, String key);

    /**
     * 移除指定键值对。
     * @param tags NBT组件，不可能为null
     * @param key Key
     */
    protected abstract void doRemove(Object tags, String key);
}
