package org.eu.smileyik.numericalrequirements.core.player.event;

import org.eu.smileyik.numericalrequirements.core.event.Event;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;

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
