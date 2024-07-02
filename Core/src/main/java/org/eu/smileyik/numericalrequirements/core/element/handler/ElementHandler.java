package org.eu.smileyik.numericalrequirements.core.element.handler;

import org.eu.smileyik.numericalrequirements.core.element.data.singlenumber.DoubleElementBar;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataValue;

public interface ElementHandler {
    default void handlePlayer(NumericalPlayer player, PlayerDataValue value) {

    }

    default void handlePlayer(NumericalPlayer player, DoubleElementBar value) {

    }
}
