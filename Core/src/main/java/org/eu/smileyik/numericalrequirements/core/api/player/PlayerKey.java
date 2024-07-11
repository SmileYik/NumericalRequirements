package org.eu.smileyik.numericalrequirements.core.api.player;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.api.RegisterInfo;

public interface PlayerKey extends RegisterInfo {

    /**
     * 存储数据信息至配置片段。
     * @param section 要保存至的数据片段。
     * @param value 要存储的数据信息。
     */
    void storeDataValue(ConfigurationSection section, PlayerValue value);

    /**
     * 从配置片段中读取数据值。
     * @param section 配置片段。
     * @return
     */
    PlayerValue loadDataValue(ConfigurationSection section);

    /**
     * 根据元素值处理玩家。
     * @param player
     * @param value
     */
    void handlePlayer(NumericalPlayer player, final PlayerValue value);

    default void onRegisterToPlayerData(NumericalPlayer player, final PlayerValue value) {

    }

    default void onUnregisterFromPlayerData(NumericalPlayer player, final PlayerValue value) {

    }
}
