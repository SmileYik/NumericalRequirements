package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SimpleMachineService implements MachineService {
    private Map<String, Machine> machines = new HashMap<>();
    private Map<String, File> machinesFiles = new HashMap<>();

    private final ReentrantReadWriteLock machineBlockMetadataMapLock = new ReentrantReadWriteLock();
    private final Map<String, Map<String, String>> machineBlockMetadataMap = new HashMap<>();
    private final String machineBlockMetadataFilePath;

    public SimpleMachineService(MultiBlockCraftExtension extension) {
        File dataFolder = extension.getDataFolder();
        machineBlockMetadataFilePath = new File(dataFolder, "machine-metadata.yml").toString();
        loadMachineBlockMetadata(machineBlockMetadataFilePath);
        loadMachines(dataFolder);
    }

    @Override
    public void setMachineMetadata(Block block, String key, String value) {
        machineBlockMetadataMapLock.writeLock().lock();
        try {
            String identifier = Machine.getIdentifier(block.getLocation());
            if (!machineBlockMetadataMap.containsKey(identifier)) {
                machineBlockMetadataMap.put(identifier, new HashMap<>());
            }
            machineBlockMetadataMap.get(identifier).put(key, value);
        } finally {
            machineBlockMetadataMapLock.writeLock().unlock();
        }
    }

    @Override
    public String delMachineMetadata(Block block, String key) {
        machineBlockMetadataMapLock.writeLock().lock();
        try {
            String identifier = Machine.getIdentifier(block.getLocation());
            if (machineBlockMetadataMap.containsKey(identifier)) {
                return machineBlockMetadataMap.get(identifier).remove(key);
            }
        } finally {
            machineBlockMetadataMapLock.writeLock().unlock();
        }
        return null;
    }

    @Override
    public String getMachineMetadata(Block block, String key) {
        machineBlockMetadataMapLock.readLock().lock();
        try {
            String identifier = Machine.getIdentifier(block.getLocation());
            if (machineBlockMetadataMap.containsKey(identifier)) {
                return machineBlockMetadataMap.get(identifier).get(key);
            }
        } finally {
            machineBlockMetadataMapLock.readLock().unlock();
        }
        return null;
    }

    @Override
    public Machine getMachine(String id) {
        return machines.get(id);
    }

    @Override
    public File createRecipe(String machineId, Recipe recipe) {
        File file = machinesFiles.get(machineId);
        file = new File(new File(file, "recipes"), recipe.getId() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        recipe.store(config);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    @Override
    public void save() {
        saveMachineBlockMetadata();
    }

    private void loadMachineBlockMetadata(String path) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(path));
        machineBlockMetadataMapLock.writeLock().lock();
        try {
            config.getKeys(false).forEach(key -> {
                    ConfigurationSection section = config.getConfigurationSection(key);
                section.getKeys(false).forEach(it -> {
                    machineBlockMetadataMap.putIfAbsent(key, new HashMap<>());
                    machineBlockMetadataMap.get(key).put(it, section.getString(it));
                });
            });
        } finally {
            machineBlockMetadataMapLock.writeLock().unlock();
        }
    }

    private void saveMachineBlockMetadata() {
        YamlConfiguration metadata = new YamlConfiguration();
        machineBlockMetadataMapLock.readLock().lock();
        try {
            machineBlockMetadataMap.forEach((identifier, metadataMap) -> {
                if (!metadataMap.isEmpty()) {
                    metadata.set(identifier, metadataMap);
                }
            });
        } finally {
            machineBlockMetadataMapLock.readLock().unlock();
        }
        try {
            metadata.save(machineBlockMetadataFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMachines(File folder) {
        for (File dir : Objects.requireNonNull(folder.listFiles())) {
            if (!dir.isDirectory()) continue;
            File file = new File(dir, "machine.yml");
            if (!file.exists()) continue;
            ConfigurationSection machine = YamlConfiguration.loadConfiguration(file);

            String type = machine.getString("type");
            if (type == null) continue;
            Machine m = null;
            try {
                m = (Machine) Class.forName(type).getDeclaredConstructor(ConfigurationSection.class).newInstance(machine);
            } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException |
                     InvocationTargetException | IllegalAccessException | ClassCastException e) {
                e.printStackTrace();
            }
            if (m == null) continue;

            System.out.println("loaded machine " + m.getId());
            machines.put(m.getId(), m);
            machinesFiles.put(m.getId(), dir);

            file = new File(dir, "recipes");
            if (!file.exists()) continue;

            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                if (listFile.isFile() && listFile.getName().toLowerCase().endsWith(".yml")) {
                    ConfigurationSection r = YamlConfiguration.loadConfiguration(listFile);
                    type = r.getString("type");
                    if (type == null) continue;
                    Recipe recipe = null;
                    try {
                        recipe = (Recipe) Class.forName(type).getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException |
                             InvocationTargetException | IllegalAccessException | ClassCastException e) {
                        e.printStackTrace();
                    }
                    if (recipe == null) continue;
                    recipe.load(r);
                    m.addRecipe(recipe);
                }
            }
        }
    }
}
