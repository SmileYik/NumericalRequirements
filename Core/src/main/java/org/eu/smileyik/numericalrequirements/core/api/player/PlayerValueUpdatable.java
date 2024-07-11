package org.eu.smileyik.numericalrequirements.core.api.player;

import org.eu.smileyik.numericalrequirements.core.api.Updatable;

public interface PlayerValueUpdatable extends PlayerValue, Updatable {
    default boolean isTimeout() {
        return false;
    }
    default boolean canDelete() {
        return isTimeout();
    }
}
