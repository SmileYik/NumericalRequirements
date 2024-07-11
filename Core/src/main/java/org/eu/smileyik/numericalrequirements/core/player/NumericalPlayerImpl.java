package org.eu.smileyik.numericalrequirements.core.player;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.api.AbstractUpdatable;
import org.eu.smileyik.numericalrequirements.core.api.player.*;
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
    private final ConcurrentHashMap<PlayerKey, List<PlayerValue>> registeredMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerKey, List<PlayerValueUpdatable>> updatableMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends PlayerKey>, List<PlayerKey>> basedClassInstanceListMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerKey, Class<? extends PlayerKey>> instanceBaseClassMap = new ConcurrentHashMap<>();

    public NumericalPlayerImpl(PlayerUpdater playerUpdater, Player player) {
        this.playerUpdater = playerUpdater;
        this.player = player;
    }

    @Override
    protected boolean doUpdate(double second) {
        if (player.isDead()) return true;
        List<Pair<PlayerKey, PlayerValueUpdatable>> timeoutList = new ArrayList<>();

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
    public void registerData(Class<? extends PlayerKey> basedClass,
                             PlayerKey key, PlayerValue value) {
        registerData(basedClass, key, value, true);
    }

    public void registerData(Class<? extends PlayerKey> basedClass,
                             PlayerKey key, PlayerValue value, boolean first) {
        if (key == null || value == null) return;
        boolean flag = updateLock.isWriteLockedByCurrentThread();
        if (!flag) lock.writeLock().lock();
        try {
            if (value instanceof PlayerValueOneShot) {
                key.handlePlayer(this, value);
                return;
            }

            List<PlayerValue> playerValues = registeredMap.putIfAbsent(key, new ArrayList<>());
            registeredMap.get(key).add(value);
            if (playerValues == null) {
                List<PlayerKey> playerKeys = basedClassInstanceListMap.putIfAbsent(basedClass, new ArrayList<>());
                if (playerKeys != null && !playerKeys.contains(key)) {
                    playerKeys.add(key);
                }
            }
            if (value instanceof PlayerValueUpdatable) {
                if (!updatableMap.containsKey(key)) {
                    updatableMap.put(key, new ArrayList<>());
                }
                updatableMap.get(key).add((PlayerValueUpdatable) value);
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
    public void unregisterData(PlayerKey key) {
        boolean flag = updateLock.isWriteLockedByCurrentThread();
        if (!flag) lock.writeLock().lock();
        try {
            Class<? extends PlayerKey> basedClass = instanceBaseClassMap.remove(key);
            basedClassInstanceListMap.get(basedClass).remove(key);
            registeredMap.remove(key).forEach(it -> key.onUnregisterFromPlayerData(this, it));
            updatableMap.remove(key);
        } finally {
            if (!flag) lock.writeLock().unlock();
        }
    }

    @Override
    public void unregisterData(PlayerKey key, SingleOperator<Boolean, PlayerValue> operator) {
        boolean flag = updateLock.isWriteLockedByCurrentThread();
        if (!flag) lock.writeLock().lock();
        try {
            for (PlayerValue playerValue : registeredMap.getOrDefault(key, Collections.emptyList())) {
                if (operator.apply(playerValue)) {
                    if (playerValue instanceof PlayerValueUpdatable) {
                        List<PlayerValueUpdatable> updatableValues = updatableMap.getOrDefault(key, Collections.emptyList());
                        updatableValues.remove(playerValue);
                        if (updatableValues.isEmpty()) {
                            updatableMap.remove(key);
                        }
                    }
                    List<PlayerValue> registeredValues = registeredMap.getOrDefault(key, Collections.emptyList());
                    registeredValues.remove(playerValue);
                    if (registeredValues.isEmpty()) {
                        basedClassInstanceListMap.get(instanceBaseClassMap.remove(key)).remove(key);
                        registeredMap.remove(key);
                    }
                    key.onUnregisterFromPlayerData(this, playerValue);
                    return;
                }
            }
        } finally {
            if (!flag) lock.writeLock().unlock();
        }
    }

    @Override
    public List<PlayerValue> getRegisteredValues(PlayerKey key) {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(registeredMap.get(key));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T extends PlayerValue> List<T> getRegisteredValues(PlayerKey key, Class<T> tClass) {
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
            Map<PlayerKey, List<PlayerValue>> disabledDataMap = getDisabledDataMap();
            disabledDataMap.keySet().forEach(this::unregisterData);
        } finally {
            if (!flag) lock.writeLock().unlock();
        }
    }

    @Override
    public Map<PlayerKey, List<PlayerValue>> getDisabledDataMap() {
        Map<PlayerKey, List<PlayerValue>> map = new HashMap<>();
        registeredMap.forEach(registeredMap.mappingCount(), (key, value) -> {
            if (key.isDisable()) {
                map.put(key, Collections.unmodifiableList(value));
            }
        });
        return map;
    }

    @Override
    public <T extends PlayerKey> Map<T, List<PlayerValue>> getDisabledDataMap(Class<T> tClass) {
        List<PlayerKey> keys = basedClassInstanceListMap.getOrDefault(tClass, new ArrayList<>());
        Map<T, List<PlayerValue>> map = new HashMap<>();
        for (PlayerKey key : keys) {
            if (key.isDisable()) {
                map.put(tClass.cast(key), Collections.unmodifiableList(registeredMap.get(key)));
            }
        }
        return map;
    }

    @Override
    public <K extends PlayerKey, V extends PlayerValue> Map<K, List<V>> getDisabledDataMap(Class<K> kClass, Class<V> vClass) {
        List<PlayerKey> keys = basedClassInstanceListMap.getOrDefault(kClass, new ArrayList<>());
        Map<K, List<V>> map = new HashMap<>();
        for (PlayerKey key : keys) {
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
                for (PlayerValue v : value) {
                    key.storeDataValue(valueSection.createSection(String.valueOf(idx)), v);
                    idx++;
                }
            });
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void load(ConfigurationSection section, Class<? extends PlayerKey> basedClass, PlayerKey key) {
        load(section, basedClass, key, null);
    }

    @Override
    public void load(ConfigurationSection section, Class<? extends PlayerKey> basedClass, PlayerKey key, PlayerValue defaultValue) {
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
