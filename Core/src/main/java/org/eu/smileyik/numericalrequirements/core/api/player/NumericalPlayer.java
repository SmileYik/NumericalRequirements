package org.eu.smileyik.numericalrequirements.core.api.player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.api.Updatable;
import org.eu.smileyik.numericalrequirements.core.api.util.SingleOperator;

import java.util.List;
import java.util.Map;

public interface NumericalPlayer extends Updatable {
    Player getPlayer();

    void registerData(Class<? extends PlayerKey> basedClass,
                      PlayerKey key, PlayerValue value);

    void unregisterData(PlayerKey key);

    void unregisterData(PlayerKey key, SingleOperator<Boolean, PlayerValue> operator);

    List<PlayerValue> getRegisteredValues(PlayerKey key);

    <T extends PlayerValue> List<T> getRegisteredValues(PlayerKey key, Class<T> tClass);

    void removeDisabledKey();

    Map<PlayerKey, List<PlayerValue>> getDisabledDataMap();

    <T extends PlayerKey> Map<T, List<PlayerValue>> getDisabledDataMap(Class<T> tClass);

    <K extends PlayerKey, V extends PlayerValue> Map<K, List<V>> getDisabledDataMap(Class<K> kClass, Class<V> vClass);

    void store(ConfigurationSection section);

    void load(ConfigurationSection section, Class<? extends PlayerKey> basedClass, PlayerKey key);

    /**
     * 从配置片段中读取指定数据并注册至玩家数据中。
     * @param section 配置片段
     * @param basedClass 键的基类
     * @param key 键实例
     * @param defaultValue 默认值.
     */
    void load(ConfigurationSection section, Class<? extends PlayerKey> basedClass, PlayerKey key, PlayerValue defaultValue);
}
