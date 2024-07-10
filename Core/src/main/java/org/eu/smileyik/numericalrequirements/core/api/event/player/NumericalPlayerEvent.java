package org.eu.smileyik.numericalrequirements.core.api.event.player;

import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.event.Event;

public class NumericalPlayerEvent extends Event {

    private final NumericalPlayer player;

    public NumericalPlayerEvent(NumericalPlayer player) {
        this.player = player;
    }

    public NumericalPlayerEvent(boolean isAsync, NumericalPlayer player) {
        super(isAsync);
        this.player = player;
    }

    public NumericalPlayer getPlayer() {
        return player;
    }
}
