package org.eu.smileyik.numericalrequirements.core.player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.AbstractUpdatable;
import org.eu.smileyik.numericalrequirements.core.util.Pair;
import org.eu.smileyik.numericalrequirements.core.util.SingleOperator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NumericalPlayerImpl extends AbstractUpdatable implements NumericalPlayer {
    private final Player player;
    private final ConcurrentHashMap<PlayerDataKey, List<PlayerDataValue>> registeredMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerDataKey, List<PlayerDataValueUpdatable>> updatableMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends PlayerDataKey>, List<PlayerDataKey>> basedClassInstanceListMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerDataKey, Class<? extends PlayerDataKey>> instanceBaseClassMap = new ConcurrentHashMap<>();
    private final Object lock = new Object();


    public NumericalPlayerImpl(Player player) {
        this.player = player;
    }

    @Override
    protected boolean doUpdate(double second) {
        synchronized (lock) {
            if (player.isDead()) return true;
            List<Pair<PlayerDataKey, PlayerDataValueUpdatable>> timeoutList = new ArrayList<>();
            updatableMap.forEach(updatableMap.mappingCount(), (key, value) -> {
                if (key.isDisable()) return;
                value.forEach(it -> {
                    try {
                        if (it.update()) {
                            key.handlePlayer(this, it);
                        }
                        if (it.isTimeout()) {
                            timeoutList.add(Pair.newUnchangablePair(key, it));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
            timeoutList.forEach(it -> {
                it.getFirst().onUnregisterFromPlayerData(this, it.getSecond());
                if (it.getSecond().canDelete()) {
                    updatableMap.getOrDefault(it.getFirst(), Collections.emptyList()).remove(it.getSecond());
                    registeredMap.getOrDefault(it.getFirst(), Collections.emptyList()).remove(it.getSecond());
                }
            });
        }
        return true;
    }

    @Override
    public long period() {
        return 0;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void registerData(Class<? extends PlayerDataKey> basedClass,
                             PlayerDataKey key, PlayerDataValue value) {
        registerData(basedClass, key, value, true);
    }

    public void registerData(Class<? extends PlayerDataKey> basedClass,
                                          PlayerDataKey key, PlayerDataValue value, boolean first) {
        synchronized (lock) {
            List<PlayerDataValue> playerDataValues = registeredMap.putIfAbsent(key, new ArrayList<>());
            registeredMap.get(key).add(value);
            if (playerDataValues == null) {
                List<PlayerDataKey> playerDataKeys = basedClassInstanceListMap.putIfAbsent(basedClass, new ArrayList<>());
                if (playerDataKeys != null && !playerDataKeys.contains(key)) {
                    playerDataKeys.add(key);
                }
            }
            if (value instanceof PlayerDataValueUpdatable) {
                if (!updatableMap.containsKey(key)) {
                    updatableMap.put(key, new ArrayList<>());
                }
                updatableMap.get(key).add((PlayerDataValueUpdatable) value);
            }
            instanceBaseClassMap.putIfAbsent(key, basedClass);
            if (first) {
                key.onRegisterToPlayerData(this, value);
            }
        }
    }

    @Override
    public void unregisterData(PlayerDataKey key) {
        synchronized (lock) {
            Class<? extends PlayerDataKey> basedClass = instanceBaseClassMap.remove(key);
            basedClassInstanceListMap.get(basedClass).remove(key);
            registeredMap.remove(key).forEach(it -> key.onUnregisterFromPlayerData(this, it));
            updatableMap.remove(key);
        }
    }

    @Override
    public void unregisterData(PlayerDataKey key, SingleOperator<Boolean, PlayerDataValue> operator) {
        synchronized (lock) {
            for (PlayerDataValue playerDataValue : registeredMap.getOrDefault(key, Collections.emptyList())) {
                if (operator.apply(playerDataValue)) {
                    if (playerDataValue instanceof PlayerDataValueUpdatable) {
                        List<PlayerDataValueUpdatable> updatableValues = updatableMap.getOrDefault(key, Collections.emptyList());
                        updatableValues.remove(playerDataValue);
                        if (updatableValues.isEmpty()) {
                            updatableMap.remove(key);
                        }
                    }
                    List<PlayerDataValue> registeredValues = registeredMap.getOrDefault(key, Collections.emptyList());
                    registeredValues.remove(playerDataValue);
                    if (registeredValues.isEmpty()) {
                        basedClassInstanceListMap.get(instanceBaseClassMap.remove(key)).remove(key);
                        registeredMap.remove(key);
                    }
                    key.onUnregisterFromPlayerData(this, playerDataValue);
                    return;
                }
            }
        }
    }

    @Override
    public List<PlayerDataValue> getRegisteredValues(PlayerDataKey key) {
        return Collections.unmodifiableList(registeredMap.get(key));
    }

    @Override
    public <T extends PlayerDataValue> List<T> getRegisteredValues(PlayerDataKey key, Class<T> tClass) {
        List<T> ts = (List<T>) (registeredMap.get(key));
        return ts == null ? null : Collections.unmodifiableList(ts);
    }

    @Override
    public void removeDisabledKey() {
        synchronized (lock) {
            Map<PlayerDataKey, List<PlayerDataValue>> disabledDataMap = getDisabledDataMap();
            disabledDataMap.keySet().forEach(this::unregisterData);
        }
    }

    @Override
    public Map<PlayerDataKey, List<PlayerDataValue>> getDisabledDataMap() {
        Map<PlayerDataKey, List<PlayerDataValue>> map = new HashMap<>();
        registeredMap.forEach(registeredMap.mappingCount(), (key, value) -> {
            if (key.isDisable()) {
                map.put(key, Collections.unmodifiableList(value));
            }
        });
        return map;
    }

    @Override
    public <T extends PlayerDataKey> Map<T, List<PlayerDataValue>> getDisabledDataMap(Class<T> tClass) {
        List<PlayerDataKey> keys = basedClassInstanceListMap.getOrDefault(tClass, new ArrayList<>());
        Map<T, List<PlayerDataValue>> map = new HashMap<>();
        for (PlayerDataKey key : keys) {
            if (key.isDisable()) {
                map.put(tClass.cast(key), Collections.unmodifiableList(registeredMap.get(key)));
            }
        }
        return map;
    }

    @Override
    public <K extends PlayerDataKey, V extends PlayerDataValue> Map<K, List<V>> getDisabledDataMap(Class<K> kClass, Class<V> vClass) {
        List<PlayerDataKey> keys = basedClassInstanceListMap.getOrDefault(kClass, new ArrayList<>());
        Map<K, List<V>> map = new HashMap<>();
        for (PlayerDataKey key : keys) {
            if (key.isDisable()) {
                map.put(kClass.cast(key), (List<V>) (registeredMap.get(key)));
            }
        }
        return map;
    }

    @Override
    public void store(ConfigurationSection section) {
        synchronized (lock) {
            registeredMap.forEach((key, value) -> {
                if (key.isDisable()) return;
                String id = key.getId();
                String path = String.format("%s.%s", key.getClass().getName(), id);
                ConfigurationSection valueSection = section.createSection(path);
                int idx = 0;
                for (PlayerDataValue v : value) {
                    key.storeDataValue(valueSection.createSection(String.valueOf(idx)), v);
                    idx++;
                }
            });
        }
    }

    @Override
    public void load(ConfigurationSection section, Class<? extends PlayerDataKey> basedClass, PlayerDataKey key) {
        load(section, basedClass, key, null);
    }

    @Override
    public void load(ConfigurationSection section, Class<? extends PlayerDataKey> basedClass, PlayerDataKey key, PlayerDataValue defaultValue) {
        if (key.isDisable()) return;
        String id = key.getId();
        String path = String.format("%s.%s", key.getClass().getName(), id);
        boolean added = false;
        if (section.isConfigurationSection(path)) {
            ConfigurationSection config = section.getConfigurationSection(path);
            int idx = 0;
            while (true) {
                if (config.isConfigurationSection(String.valueOf(idx))) {
                    registerData(basedClass, key, key.loadDataValue(config.getConfigurationSection(String.valueOf(idx))));
                    idx++;
                    added = true;
                } else {
                    break;
                }
            }
        }
        if (!added && defaultValue != null) {
            registerData(basedClass, key, defaultValue, false);
        }
    }
}
