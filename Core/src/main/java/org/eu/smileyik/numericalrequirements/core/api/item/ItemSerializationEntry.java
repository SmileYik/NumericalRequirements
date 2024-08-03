package org.eu.smileyik.numericalrequirements.core.api.item;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;

public interface ItemSerializationEntry {

    interface Handler {
        void deny();
        boolean isDeny();
    }

    /**
     * 初始化配置。
     * @param section
     */
    default void configure(ConfigurationSection section) {

    }

    /**
     * 获取配置的片段的Key, 若能直接读出来，不需要分新片段则返回null。
     * @return
     */
    default String getKey() {
        return getId();
    }

    /**
     * ID是识别序列化程序类型的一种手段，若某一个方法有多个版本，可以设置同一个ID,不同的优先级来确保仅有一个相同类型的程序被运行。
     * @return
     */
    String getId();

    /**
     * 设定优先级，优先级越高越先执行。
     * @return
     */
    default int getPriority() {
        return 0;
    }

    /**
     * 是否能够使用。
     * @return
     */
    boolean isAvailable();

    /**
     * 将物品序列化。
     * @param section 配置片段
     * @param itemStack
     * @param itemMeta
     */
    void serialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta);

    /**
     * 将配置反序列化为物品，若在反序列化时需要产出新的物品，则将该新物品返回即可。
     * @param section 配置片段
     * @param itemStack
     * @param itemMeta
     * @return
     */
    ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta);
}
