package org.eu.smileyik.numericalrequirements.core.api.item.tag;

import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;

import java.util.List;

public interface ConsumableTag <T> {

    void onConsume(NumericalPlayer player, T value);

    default void onConsume(NumericalPlayer player, List<T> valueList) {
        valueList.forEach(it -> {
            onConsume(player, it);;
        });
    }
}
