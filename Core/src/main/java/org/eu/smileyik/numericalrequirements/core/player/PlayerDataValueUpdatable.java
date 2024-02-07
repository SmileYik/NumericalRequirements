package org.eu.smileyik.numericalrequirements.core.player;

import org.eu.smileyik.numericalrequirements.core.Updatable;

public interface PlayerDataValueUpdatable extends PlayerDataValue, Updatable {
    default boolean isTimeout() {
        return false;
    }
    default boolean canDelete() {
        return isTimeout();
    }
}
