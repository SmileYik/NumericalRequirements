package org.eu.smileyik.numericalrequirements.core.effect.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.effect.*;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerValue;

import java.util.*;

public class EffectBundle extends AbstractEffect {
    private final EffectService effectService;

    public EffectBundle(EffectService effectService) {
        super(
                "EffectBundle",
                I18N.tr("effect.effect-bundle.name"),
                "SmileYik",
                "1.0.0",
                I18N.tr("effect.effect-bundle.description")
        );
        this.effectService = effectService;
    }

    @Override
    protected void register() {

    }

    @Override
    protected void unregister() {

    }

    @Override
    public EffectData newEffectData() {
        return new EffectBundleData(effectService);
    }

    @Override
    public EffectData newEffectData(String[] args) {
        if (args.length != 2 || effectService.getBundleConfigById(args[0]) == null) {
            return null;
        }
        EffectBundleData effectBundleData = new EffectBundleData(effectService);
        effectBundleData.setBundleId(args[0]);
        effectBundleData.setDuration(Double.parseDouble(args[1]));
        return effectBundleData;
    }

    @Override
    public void handlePlayer(NumericalPlayer player, PlayerValue value) {
        ((EffectBundleData) value).handlePlayer(player);
    }

    @Override
    public void onRegisterToPlayerData(NumericalPlayer player, PlayerValue value) {
        ((EffectBundleData) value).onRegisterToPlayerData(player);
    }

    @Override
    public void onUnregisterFromPlayerData(NumericalPlayer player, PlayerValue value) {
        ((EffectBundleData) value).onUnregisterFromPlayerData(player);
    }

    public static class EffectBundleData extends AbstractEffectData {
        private final EffectService effectService;
        private Map<Effect, List<EffectDataWrapper>> map;

        private String bundleId;
        private String bundleType;

        public String getBundleType() {
            return bundleType;
        }

        public EffectBundleData(EffectService effectService) {
            this.effectService = effectService;
        }

        public String getBundleId() {
            return bundleId;
        }

        public void setBundleId(String bundleId) {
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
                        map.get(effect).add(new EffectDataWrapper(
                                effect.newEffectData(
                                        new String[] {c.getConfigurationSection(key).getString("bundle-id"), "0"})
                        ));
                    } else {
                        map.get(effect).add(new EffectDataWrapper(
                                (EffectData) effect.loadDataValue(c.getConfigurationSection(key))
                        ));
                    }
                }
            }
        }

        public void onRegisterToPlayerData(NumericalPlayer player) {
            if (map != null) {
                map.forEach((effect, data) -> {
                    data.forEach(it -> {
                        effect.onRegisterToPlayerData(player, it.data);
                    });
                });
            }
        }

        public void onUnregisterFromPlayerData(NumericalPlayer player) {
            if (map != null) {
                map.forEach((effect, data) -> {
                    data.forEach(it -> {
                        effect.onUnregisterFromPlayerData(player, it.data);
                    });
                });
            }
        }

        public void handlePlayer(NumericalPlayer player) {
            if (map != null) {
                map.forEach((effect, data) -> {
                    data.forEach(it -> {
                        if (it.updateResult) effect.handlePlayer(player, it.data);
                    });
                });
            }
        }

        @Override
        public void load(ConfigurationSection section) {
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
                        map.get(effect).add(new EffectDataWrapper(
                                (EffectData) effect.loadDataValue(c.getConfigurationSection(String.valueOf(idx)))
                        ));
                        idx++;
                    } else {
                        break;
                    }
                }
            }
        }

        @Override
        public void store(ConfigurationSection section) {
            super.store(section);
            section.set("bundle-id", bundleId);
            section.set("type", bundleType);
            if (map != null) {
                map.forEach((effect, data) -> {
                    ConfigurationSection config = section.createSection(effect.getId());
                    int idx = 0;
                    for (EffectDataWrapper v : data) {
                        effect.storeDataValue(config.createSection(String.valueOf(idx)), v.data);
                        ++idx;
                    }
                });
            }
        }

        @Override
        protected boolean doUpdate(double second) {
            super.doUpdate(second);
            if (map != null) {
                map.values().forEach(list -> list.forEach(EffectDataWrapper::update));
            }
            return true;
        }

        @Override
        public boolean isSimilar(EffectData other) {
            return (other instanceof EffectBundleData) &&
                    Objects.equals(((EffectBundleData) other).bundleId, this.bundleId);
        }

        private static class EffectDataWrapper {
            private final EffectData data;
            private boolean updateResult = false;

            private EffectDataWrapper(EffectData data) {
                this.data = data;
            }

            public void update() {
                updateResult = data.update();
            }
        }
    }

}
