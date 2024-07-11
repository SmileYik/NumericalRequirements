package org.eu.smileyik.numericalrequirements.core.api.effect;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerKey;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerValue;

public interface Effect extends PlayerKey {
    EffectData newEffectData();
    EffectData newEffectData(String[] args);
    default EffectData newEffectData(ConfigurationSection config) {
        return (EffectData) loadDataValue(config);
    }

    default void storeDataValue(ConfigurationSection section, PlayerValue value) {
        EffectData effectData = (EffectData) value;
        effectData.store(section);
    }

    default PlayerValue loadDataValue(ConfigurationSection section) {
        EffectData effectData = newEffectData();
        effectData.load(section);
        return effectData;
    }
}
