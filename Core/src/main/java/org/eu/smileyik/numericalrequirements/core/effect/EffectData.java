package org.eu.smileyik.numericalrequirements.core.effect;

import org.eu.smileyik.numericalrequirements.core.player.PlayerDataKey;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataValue;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataValueUpdatable;

public interface EffectData extends PlayerDataValueUpdatable {
    double getDuration();

    void setDuration(double duration);

}
