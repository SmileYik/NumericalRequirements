package org.eu.smileyik.numericalrequirements.core.api.extension.placeholder;

import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;

public interface PlaceholderRequestCallback {
    String onRequest(NumericalPlayer player, String identifier);

}
