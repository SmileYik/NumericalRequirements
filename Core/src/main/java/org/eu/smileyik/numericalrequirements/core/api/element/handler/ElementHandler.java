package org.eu.smileyik.numericalrequirements.core.api.element.handler;

import org.eu.smileyik.numericalrequirements.core.api.element.data.singlenumber.DoubleElementBar;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerDataValue;

public interface ElementHandler {
    default void handlePlayer(NumericalPlayer player, PlayerDataValue value) {

    }

    default void handlePlayer(NumericalPlayer player, DoubleElementBar value) {

    }
}
