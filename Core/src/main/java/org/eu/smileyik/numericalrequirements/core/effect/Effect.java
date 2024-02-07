package org.eu.smileyik.numericalrequirements.core.effect;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataKey;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataValue;

public interface Effect extends PlayerDataKey {
    EffectData newEffectData();
    EffectData newEffectData(String[] args);
    default EffectData newEffectData(ConfigurationSection config) {
        return (EffectData) loadDataValue(config);
    }

    default void storeDataValue(ConfigurationSection section, PlayerDataValue value) {
        EffectData effectData = (EffectData) value;
        effectData.store(section);
    }

    default PlayerDataValue loadDataValue(ConfigurationSection section) {
        EffectData effectData = newEffectData();
        effectData.load(section);
        return effectData;
    }
}
