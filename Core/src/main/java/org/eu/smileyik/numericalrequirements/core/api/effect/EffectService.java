package org.eu.smileyik.numericalrequirements.core.api.effect;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.List;

public interface EffectService {
    Effect findEffectById(String id);

    List<Effect> getRegisteredEffects();

    void registerEffect(Effect effect);

    void unregisterEffect(Effect effect);

    Collection<String> getEffectBundleIds();

    ConfigurationSection getBundleConfigById(String id);

    void addBundleConfigSection(ConfigurationSection section);

    void shutdown();
}
