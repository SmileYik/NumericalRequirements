package org.eu.smileyik.numericalrequirements.core.effect.service;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.effect.Effect;
import org.eu.smileyik.numericalrequirements.core.effect.impl.ChatMessageEffect;
import org.eu.smileyik.numericalrequirements.core.effect.impl.EffectBundle;
import org.eu.smileyik.numericalrequirements.core.effect.EffectPlayer;
import org.eu.smileyik.numericalrequirements.core.effect.impl.PotionEffect;
import org.eu.smileyik.numericalrequirements.core.player.event.NumericalPlayerLoadEvent;

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
    public synchronized Effect findEffectById(String id) {
        return idEffectMap.get(id.toLowerCase());
    }

    @Override
    public synchronized List<Effect> getRegisteredEffects() {
        return Collections.unmodifiableList(
                idEffectMap.values()
                        .stream()
                        .filter(it -> !it.isDisable())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public synchronized void registerEffect(Effect effect) {
        String id = effect.getId().toLowerCase();
        if (idEffectMap.containsKey(id)) {
            return;
        }
        effect.onRegister();
        idEffectMap.put(id, effect);
    }

    @Override
    public synchronized void unregisterEffect(Effect effect) {
        Effect removed = idEffectMap.remove(effect.getId().toLowerCase());
        effect.onUnregister();
        if (removed != null) {
            plugin.getPlayerService().removeDisabledKey();
        }
    }

    @Override
    public synchronized Collection<String> getEffectBundleIds() {
        return effectBundleConfig.getKeys(false);
    }

    @Override
    public synchronized ConfigurationSection getBundleConfigById(String id) {
        if (effectBundleConfig == null || !effectBundleConfig.isConfigurationSection(id)) {
            return null;
        }
        return effectBundleConfig.getConfigurationSection(id);
    }

    @Override
    public synchronized void addBundleConfigSection(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            effectBundleConfig.set(key, section.getConfigurationSection(key));
        }
    }

    @Override
    public void shutdown() {
        idEffectMap.clear();
    }

    @EventHandler
    public synchronized void onLoadPlayerData(NumericalPlayerLoadEvent event) {
        synchronized (this) {
            idEffectMap.values().forEach(effect -> {
                EffectPlayer.load(event.getPlayer(), event.getSection(), effect);
            });
        }
    }
}
