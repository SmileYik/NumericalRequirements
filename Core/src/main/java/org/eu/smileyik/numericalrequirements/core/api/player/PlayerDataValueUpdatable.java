package org.eu.smileyik.numericalrequirements.core.api.player;

import org.eu.smileyik.numericalrequirements.core.api.Updatable;

public interface PlayerDataValueUpdatable extends PlayerDataValue, Updatable {
    default boolean isTimeout() {
        return false;
    }
    default boolean canDelete() {
        return isTimeout();
    }
}
