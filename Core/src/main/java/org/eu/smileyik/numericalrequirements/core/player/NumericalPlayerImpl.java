package org.eu.smileyik.numericalrequirements.core.player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.api.AbstractUpdatable;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerDataKey;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerDataValue;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerDataValueUpdatable;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.core.api.util.SingleOperator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NumericalPlayerImpl extends AbstractUpdatable implements NumericalPlayer {
    private final PlayerUpdater playerUpdater;
    private final Player player;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final ReentrantReadWriteLock updateLock = new ReentrantReadWriteLock(true);
    private final ReentrantLock playerUpdaterLock = new ReentrantLock(true);
    private final ConcurrentHashMap<PlayerDataKey, List<PlayerDataValue>> registeredMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerDataKey, List<PlayerDataValueUpdatable>> updatableMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends PlayerDataKey>, List<PlayerDataKey>> basedClassInstanceListMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerDataKey, Class<? extends PlayerDataKey>> instanceBaseClassMap = new ConcurrentHashMap<>();

    public NumericalPlayerImpl(PlayerUpdater playerUpdater, Player player) {
        this.playerUpdater = playerUpdater;
        this.player = player;
    }

    @Override
    protected boolean doUpdate(double second) {
        if (player.isDead()) return true;
        List<Pair<PlayerDataKey, PlayerDataValueUpdatable>> timeoutList = new ArrayList<>();

        updatableMap.forEach(updatableMap.mappingCount(), (key, value) -> {
            lock.writeLock().lock();
            updateLock.writeLock().lock();
            try {
                if (key.isDisable()) return;
                value.forEach(it -> {
                    playerUpdater.submit(() -> {
                        playerUpdaterLock.lock();
                        try {
                            if (it.update()) key.handlePlayer(this, it);
                        } finally {
                            playerUpdaterLock.unlock();
                        }
                    });
                    if (it.isTimeout()) timeoutList.add(Pair.newUnchangablePair(key, it));
                });
            } finally {
                lock.writeLock().unlock();
                updateLock.writeLock().unlock();
            }
        });

        timeoutList.forEach(it -> {
            lock.writeLock().lock();
            updateLock.writeLock().lock();
            try {
                if (it.getSecond().canDelete()) {
                    it.getFirst().onUnregisterFromPlayerData(this, it.getSecond());
                    updatableMap.getOrDefault(it.getFirst(), Collections.emptyList()).remove(it.getSecond());
                    registeredMap.getOrDefault(it.getFirst(), Collections.emptyList()).remove(it.getSecond());
                }
            } finally {
                lock.writeLock().unlock();
                updateLock.writeLock().unlock();
            }
        });

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
        boolean flag = updateLock.isWriteLockedByCurrentThread();
        if (!flag) lock.writeLock().lock();
        try {
            if (value == null) return;
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
        } finally {
            if (!flag) lock.writeLock().unlock();
        }
    }

    @Override
    public void unregisterData(PlayerDataKey key) {
        boolean flag = updateLock.isWriteLockedByCurrentThread();
        if (!flag) lock.writeLock().lock();
        try {
            Class<? extends PlayerDataKey> basedClass = instanceBaseClassMap.remove(key);
            basedClassInstanceListMap.get(basedClass).remove(key);
            registeredMap.remove(key).forEach(it -> key.onUnregisterFromPlayerData(this, it));
            updatableMap.remove(key);
        } finally {
            if (!flag) lock.writeLock().unlock();
        }
    }

    @Override
    public void unregisterData(PlayerDataKey key, SingleOperator<Boolean, PlayerDataValue> operator) {
        boolean flag = updateLock.isWriteLockedByCurrentThread();
        if (!flag) lock.writeLock().lock();
        try {
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
        } finally {
            if (!flag) lock.writeLock().unlock();
        }
    }

    @Override
    public List<PlayerDataValue> getRegisteredValues(PlayerDataKey key) {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(registeredMap.get(key));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T extends PlayerDataValue> List<T> getRegisteredValues(PlayerDataKey key, Class<T> tClass) {
        lock.readLock().lock();
        try {
            List<T> ts = (List<T>) (registeredMap.get(key));
            return ts == null ? null : Collections.unmodifiableList(ts);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void removeDisabledKey() {
        boolean flag = updateLock.isWriteLockedByCurrentThread();
        if (!flag) lock.writeLock().lock();
        try {
            Map<PlayerDataKey, List<PlayerDataValue>> disabledDataMap = getDisabledDataMap();
            disabledDataMap.keySet().forEach(this::unregisterData);
        } finally {
            if (!flag) lock.writeLock().unlock();
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
        lock.readLock().lock();
        try {
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
        } finally {
            lock.readLock().unlock();
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
