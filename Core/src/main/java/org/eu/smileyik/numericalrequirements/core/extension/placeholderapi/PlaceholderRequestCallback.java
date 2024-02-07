package org.eu.smileyik.numericalrequirements.core.extension.placeholderapi;

import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;

public interface PlaceholderRequestCallback {
    String onRequest(NumericalPlayer player, String identifier);

}
