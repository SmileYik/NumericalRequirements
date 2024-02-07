package org.eu.smileyik.numericalrequirements.core.effect.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.Updatable;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.effect.AbstractEffectData;
import org.eu.smileyik.numericalrequirements.core.effect.Effect;
import org.eu.smileyik.numericalrequirements.core.effect.EffectData;
import org.eu.smileyik.numericalrequirements.core.effect.service.EffectService;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectBundleData extends AbstractEffectData {
    private final EffectService effectService;
    private Map<Effect, List<EffectData>> map;

    private String bundleId;
    private String bundleType;

    public String getBundleType() {
        return bundleType;
    }

    public EffectBundleData(EffectService effectService) {
        this.effectService = effectService;
    }

    public synchronized String getBundleId() {
        return bundleId;
    }

    public synchronized void setBundleId(String bundleId) {
        this.bundleId = bundleId;
        ConfigurationSection config = effectService.getBundleConfigById(bundleId);
        if (config == null) {
            return;
        }
        map = new HashMap<>();
        bundleType = config.getString("type");
        for (String id : config.getKeys(false)) {
            Effect effect = effectService.findEffectById(id);
            if (effect == null || !config.isConfigurationSection(id)) continue;
            ConfigurationSection c = config.getConfigurationSection(id);
            for (String key : c.getKeys(false)) {
                if (!map.containsKey(effect)) {
                    map.put(effect, new ArrayList<>());
                }
                if (effect instanceof EffectBundle) {
                    map.get(effect).add(effect.newEffectData(
                            new String[] {c.getConfigurationSection(key).getString("bundle-id"), "0"}));
                } else {
                    map.get(effect).add((EffectData) effect.loadDataValue(c.getConfigurationSection(key)));
                }
            }
        }
    }

    public synchronized void onRegisterToPlayerData(NumericalPlayer player) {
        if (map != null) {
            map.forEach((effect, data) -> {
                data.forEach(it -> {
                    effect.onRegisterToPlayerData(player, it);
                });
            });
        }
    }

    public synchronized void onUnregisterFromPlayerData(NumericalPlayer player) {
        if (map != null) {
            map.forEach((effect, data) -> {
                data.forEach(it -> {
                    effect.onUnregisterFromPlayerData(player, it);
                });
            });
        }
    }

    public synchronized void handlePlayer(NumericalPlayer player) {
        if (map != null) {
            map.forEach((effect, data) -> {
                data.forEach(it -> {
                    effect.handlePlayer(player, it);
                });
            });
        }
    }

    @Override
    public synchronized void load(ConfigurationSection section) {
        super.load(section);
        bundleId = section.getString("bundle-id");
        bundleType = section.getString("type");
        if (map == null) {
            map = new HashMap<>();
        }
        for (String id : section.getKeys(false)) {
            Effect effect = effectService.findEffectById(id);
            if (effect == null || !section.isConfigurationSection(id)) continue;
            ConfigurationSection c = section.getConfigurationSection(id);
            int idx = 0;
            while (true) {
                if (c.isConfigurationSection(String.valueOf(idx))) {
                    if (!map.containsKey(effect)) {
                        map.put(effect, new ArrayList<>());
                    }
                    map.get(effect).add((EffectData) effect.loadDataValue(c.getConfigurationSection(String.valueOf(idx))));
                    idx++;
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public synchronized void store(ConfigurationSection section) {
        super.store(section);
        section.set("bundle-id", bundleId);
        section.set("type", bundleType);
        if (map != null) {
            map.forEach((effect, data) -> {
                ConfigurationSection config = section.createSection(effect.getId());
                int idx = 0;
                for (EffectData v : data) {
                    effect.storeDataValue(config.createSection(String.valueOf(idx)), v);
                    ++idx;
                }
            });
        }
    }

    @Override
    protected synchronized boolean doUpdate(double second) {
        super.doUpdate(second);
        if (map != null) {
            map.values().forEach(list -> list.forEach(Updatable::update));
        }
        return true;
    }
}
