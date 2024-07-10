package org.eu.smileyik.numericalrequirements.core.api.player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.api.Updatable;
import org.eu.smileyik.numericalrequirements.core.api.util.SingleOperator;

import java.util.List;
import java.util.Map;

public interface NumericalPlayer extends Updatable {
    Player getPlayer();

    void registerData(Class<? extends PlayerDataKey> basedClass,
                      PlayerDataKey key, PlayerDataValue value);

    void unregisterData(PlayerDataKey key);

    void unregisterData(PlayerDataKey key, SingleOperator<Boolean, PlayerDataValue> operator);

    List<PlayerDataValue> getRegisteredValues(PlayerDataKey key);

    <T extends PlayerDataValue> List<T> getRegisteredValues(PlayerDataKey key, Class<T> tClass);

    void removeDisabledKey();

    Map<PlayerDataKey, List<PlayerDataValue>> getDisabledDataMap();

    <T extends PlayerDataKey> Map<T, List<PlayerDataValue>> getDisabledDataMap(Class<T> tClass);

    <K extends PlayerDataKey, V extends PlayerDataValue> Map<K, List<V>> getDisabledDataMap(Class<K> kClass, Class<V> vClass);

    void store(ConfigurationSection section);

    void load(ConfigurationSection section, Class<? extends PlayerDataKey> basedClass, PlayerDataKey key);

    /**
     * 从配置片段中读取指定数据并注册至玩家数据中。
     * @param section 配置片段
     * @param basedClass 键的基类
     * @param key 键实例
     * @param defaultValue 默认值.
     */
    void load(ConfigurationSection section, Class<? extends PlayerDataKey> basedClass, PlayerDataKey key, PlayerDataValue defaultValue);
}
