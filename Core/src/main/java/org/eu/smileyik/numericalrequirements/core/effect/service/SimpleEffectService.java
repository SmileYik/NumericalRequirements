package org.eu.smileyik.numericalrequirements.core.effect.service;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.effect.Effect;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectPlayer;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectService;
import org.eu.smileyik.numericalrequirements.core.api.event.player.NumericalPlayerLoadEvent;
import org.eu.smileyik.numericalrequirements.core.effect.impl.ChatMessageEffect;
import org.eu.smileyik.numericalrequirements.core.effect.impl.EffectBundle;
import org.eu.smileyik.numericalrequirements.core.effect.impl.PotionEffect;

import java.util.*;
import java.util.stream.Collectors;

public class SimpleEffectService implements Listener, EffectService {
    private final NumericalRequirements plugin;
    private final Map<String, Effect> idEffectMap = new HashMap<>();
    private final ConfigurationSection effectBundleConfig;

    public SimpleEffectService(NumericalRequirements plugin) {
        this.plugin = plugin;
        this.effectBundleConfig = plugin.getConfig().getConfigurationSection("effect.bundle");
        registerEffect(new PotionEffect());
        registerEffect(new EffectBundle(this));
        registerEffect(new ChatMessageEffect());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public Effect findEffectById(String id) {
        return idEffectMap.get(id.toLowerCase());
    }

    @Override
    public List<Effect> getRegisteredEffects() {
        return Collections.unmodifiableList(
                idEffectMap.values()
                        .stream()
                        .filter(it -> !it.isDisable())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void registerEffect(Effect effect) {
        String id = effect.getId().toLowerCase();
        if (idEffectMap.containsKey(id)) {
            return;
        }
        effect.onRegister();
        idEffectMap.put(id, effect);
    }

    @Override
    public void unregisterEffect(Effect effect) {
        Effect removed = idEffectMap.remove(effect.getId().toLowerCase());
        effect.onUnregister();
        if (removed != null) {
            plugin.getPlayerService().removeDisabledKey();
        }
    }

    @Override
    public Collection<String> getEffectBundleIds() {
        return effectBundleConfig.getKeys(false);
    }

    @Override
    public ConfigurationSection getBundleConfigById(String id) {
        if (effectBundleConfig == null || !effectBundleConfig.isConfigurationSection(id)) {
            return null;
        }
        return effectBundleConfig.getConfigurationSection(id);
    }

    @Override
    public void addBundleConfigSection(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            effectBundleConfig.set(key, section.getConfigurationSection(key));
        }
    }

    @Override
    public void shutdown() {
        idEffectMap.clear();
    }

    @EventHandler
    public void onLoadPlayerData(NumericalPlayerLoadEvent event) {
        {
            idEffectMap.values().forEach(effect -> {
                EffectPlayer.load(event.getPlayer(), event.getSection(), effect);
            });
        }
    }
}
