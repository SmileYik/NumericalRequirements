package org.eu.smileyik.numericalrequirements.core.api.item.tag;

import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;

public interface FunctionalTag<T> {
    /**
     * 当物品开始生效时将会被调用。
     * @param player
     * @param value
     */
    void onRegister(NumericalPlayer player, T value);

    /**
     * 当物品失效时将会被调用。
     * @param player
     * @param value
     */
    void onUnregister(NumericalPlayer player, T value);
}
