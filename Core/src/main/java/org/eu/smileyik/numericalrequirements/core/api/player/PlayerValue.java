package org.eu.smileyik.numericalrequirements.core.api.player;

import org.bukkit.configuration.ConfigurationSection;

public interface PlayerValue {
    /**
     * 保存数据至指定配置片段. 实现时需要先调用父方法，防止配置保存逻辑丢失.
     * @param section 配置片段.
     */
    void store(ConfigurationSection section);

    /**
     * 从指定配置片段中读取数据。实现时需要先调用父方法，防止配置读取逻辑丢失.
     * @param section
     */
    void load(ConfigurationSection section);
}
