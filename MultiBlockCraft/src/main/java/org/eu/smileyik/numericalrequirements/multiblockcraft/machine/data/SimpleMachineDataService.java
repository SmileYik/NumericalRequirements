package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.MachineService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleMachineDataService implements MachineDataService {
    private final MachineService  machineService;
    private final Map<String, MachineData> machineDataMap = new HashMap<>();
    private final Map<String, MachineDataUpdatable> machineDataUpdatableMap = new HashMap<>();
    private final File folder;
    private final File metadataFile;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public SimpleMachineDataService(MultiBlockCraftExtension extension, MachineService machineService) {
        this.machineService = machineService;
        folder = new File(extension.getDataFolder(), "machine-data");
        metadataFile = new File(folder, "metadata.yml");
        YamlConfiguration metadata = YamlConfiguration.loadConfiguration(metadataFile);
        metadata.getStringList("running").forEach(this::loadMachineData);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            machineDataUpdatableMap.forEach((k, v) -> {
                try {
                    v.update();
                } catch (Exception e) {
                    DebugLogger.debug(e);
                }
            });
        }, 40, 40, TimeUnit.MILLISECONDS);
    }

    private MachineData loadMachineData(ConfigurationSection section) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String type = section.getString("type");
        String machineId = section.getString("machine");
        MachineData data = (MachineData) Class.forName(type).getDeclaredConstructor(Machine.class).newInstance(machineService.getMachine(machineId));
        data.load(section);
        return data;
    }

    @Override
    public synchronized MachineData loadMachineData(String identifier) {
        if (!machineDataMap.containsKey(identifier)) {
            File file = new File(folder, identifier + ".yml");
            if (!file.exists()) return null;
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            try {
                MachineData machineData = loadMachineData(config);
                if (machineData instanceof MachineDataUpdatable) {
                    machineDataUpdatableMap.put(identifier, (MachineDataUpdatable) machineData);
                }
                machineDataMap.put(identifier, machineData);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return machineDataMap.get(identifier);
    }

    @Override
    public synchronized void storeMachineData(MachineData machineData) {
        if (machineData instanceof MachineDataUpdatable) {
            machineDataUpdatableMap.put(machineData.getIdentifier(), (MachineDataUpdatable) machineData);
        }
        machineDataMap.put(machineData.getIdentifier(), machineData);
    }

    @Override
    public void stop() {
        scheduledExecutorService.shutdown();
        save();
    }

    @Override
    public synchronized void save() {
        Set<String> needDelete = new HashSet<>();
        machineDataUpdatableMap.forEach((k, v) -> {
            if (!v.isRunning()) {
                needDelete.add(k);
            }
        });
        needDelete.forEach(machineDataUpdatableMap::remove);

        YamlConfiguration metadata = new YamlConfiguration();
        metadata.set("running", new ArrayList<String>(machineDataUpdatableMap.keySet()));
        try {
            metadata.save(metadataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        machineDataMap.forEach((k, v) -> {
            File file = new File(folder, k + ".yml");
            YamlConfiguration config = new YamlConfiguration();
            v.store(config);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        needDelete.forEach(machineDataMap::remove);
    }

    @Override
    public synchronized void removeMachineData(String identifier) {
        File file = new File(folder, identifier + ".yml");
        if (file.exists()) file.delete();
        machineDataMap.remove(identifier);
        machineDataUpdatableMap.remove(identifier);
    }
}
