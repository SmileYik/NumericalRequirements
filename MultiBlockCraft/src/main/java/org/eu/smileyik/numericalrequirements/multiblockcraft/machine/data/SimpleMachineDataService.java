package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data;

import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.MachineService;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event.MachineDataLoadEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event.MachineDataRemoveEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event.MachineDataUnloadEvent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;

public class SimpleMachineDataService implements MachineDataService, Listener {
    private final MachineService  machineService;
    private final ConcurrentMap<String, MachineData> machineDataMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, MachineDataUpdatable> machineDataUpdatableMap = new ConcurrentHashMap<>();
    private final File folder;
    private final File metadataFile;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, Set<String>> chunkMachineMap = new HashMap<>();

    public SimpleMachineDataService(MultiBlockCraftExtension extension, MachineService machineService) {
        this.machineService = machineService;
        folder = new File(extension.getDataFolder(), "machine-data");
        metadataFile = new File(folder, "metadata.yml");
        YamlConfiguration metadata = YamlConfiguration.loadConfiguration(metadataFile);
        ConfigurationSection chunk = metadata.getConfigurationSection("chunk");
        if (chunk != null) {
            chunk.getKeys(false).forEach(it -> {
                chunkMachineMap.put(it, new HashSet<>(chunk.getStringList(it)));
            });
        }
        loadLoadedChunks(extension.getPlugin());

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            machineDataUpdatableMap.forEach((k, v) -> {
                try {
                    v.update();
                } catch (Exception e) {
                    DebugLogger.debug(e);
                }
            });
        }, 40, 40, TimeUnit.MILLISECONDS);
        extension.getPlugin().getServer().getPluginManager().registerEvents(this, extension.getPlugin());
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
            DebugLogger.debug("loading machine data: %s", identifier);
            File file = new File(folder, identifier + ".yml");
            if (!file.exists()) return null;
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            try {
                MachineData machineData = loadMachineData(config);
                String id = getChunkId(machineData.getLocation().getChunk());

                MachineDataLoadEvent event = new MachineDataLoadEvent(machineData.getMachine(), machineData.getIdentifier(), machineData, config, id);
                MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) return null;

                if (machineData instanceof MachineDataUpdatable) {
                    machineDataUpdatableMap.put(identifier, (MachineDataUpdatable) machineData);
                }
                machineDataMap.put(identifier, machineData);
                if (!chunkMachineMap.containsKey(id)) chunkMachineMap.put(id, new HashSet<>());
                chunkMachineMap.get(id).add(machineData.getIdentifier());
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
        String id = getChunkId(machineData.getLocation().getChunk());
        if (!chunkMachineMap.containsKey(id)) chunkMachineMap.put(id, new HashSet<>());
        chunkMachineMap.get(id).add(machineData.getIdentifier());
    }

    @Override
    public void stop() {
        scheduledExecutorService.shutdown();
        save();
    }

    @Override
    public synchronized void save() {
        YamlConfiguration metadata = new YamlConfiguration();
        ConfigurationSection chunk = metadata.createSection("chunk");
        chunkMachineMap.forEach((k, v) -> {
            chunk.set(k, new ArrayList<>(v));
        });
        try {
            metadata.save(metadataFile);
        } catch (IOException e) {
            DebugLogger.debug(e);
        }

        machineDataMap.forEach((k, v) -> {
            YamlConfiguration config = new YamlConfiguration();
            v.store(config);
            try {
                config.save(new File(folder, k + ".yml"));
            } catch (IOException e) {
                DebugLogger.debug(e);
            }
        });
    }

    @Override
    public synchronized void removeMachineData(String identifier) {
        MachineData machineData = machineDataMap.get(identifier);
        if (machineData != null) {
            MachineDataRemoveEvent event = new MachineDataRemoveEvent(machineData.getMachine(), identifier, machineData);
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
        }

        File file = new File(folder, identifier + ".yml");
        if (file.exists()) file.delete();
        machineDataMap.remove(identifier);
        machineDataUpdatableMap.remove(identifier);
        chunkMachineMap.getOrDefault(identifier, Collections.emptySet()).remove(identifier);
    }

    private synchronized void loadLoadedChunks(Plugin plugin) {
        plugin.getServer().getWorlds().forEach(world -> {
            for (Chunk loadedChunk : world.getLoadedChunks()) {
                onChunkLoad(loadedChunk);
            }
        });
    }

    private synchronized void onChunkLoad(Chunk chunk) {
        String chunkId = getChunkId(chunk);
        DebugLogger.debug("load chunk: %s", chunkId);
        chunkMachineMap.getOrDefault(chunkId, Collections.emptySet()).forEach(this::loadMachineData);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        onChunkLoad(event.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        String chunkId = getChunkId(event.getChunk());
        DebugLogger.debug("unload chunk: %s", chunkId);
        chunkMachineMap.getOrDefault(chunkId, Collections.emptySet()).forEach(identifier -> {
            DebugLogger.debug("unload machine data: %s", identifier);
            MachineData machineData = machineDataMap.get(identifier);
            if (machineData == null) return;
            YamlConfiguration config = new YamlConfiguration();

            MachineDataUnloadEvent unloadEvent = new MachineDataUnloadEvent(machineData.getMachine(), machineData.getIdentifier(), machineData, config, chunkId);
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(unloadEvent);
            if (unloadEvent.isCancelled()) return;

            machineData.store(config);
            try {
                config.save(new File(folder, identifier + ".yml"));
            } catch (IOException e) {
                DebugLogger.debug(e);
            }
            machineDataMap.remove(identifier);
            machineDataUpdatableMap.remove(identifier);
        });
    }

    private String getChunkId(Chunk chunk) {
        return String.format("%s;%d;%d", chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }
}
