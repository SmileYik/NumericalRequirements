package org.eu.smileyik.numericalrequirements.core.customblock;

import org.bukkit.Chunk;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.customblock.CustomBlock;
import org.eu.smileyik.numericalrequirements.core.api.customblock.Pos;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 真实的自定义方块服务主要控制 ArmorStandCustomBlock 及其子类的自定义方块的放置与拆除.
 *
 */
public class RealCustomBlockService extends AbstractRealCustomBlockService {

    /**
     * 自定义方块ID表
     */
    private final Map<String, CustomBlock> customBlocks = new HashMap<>();

    /**
     * 自定义方块所要求的物品ID与自定义方块ID的映射表.
     */
    private final Map<String, String> itemId2CustomId = new HashMap<>();

    /**
     * 位置与自定义方块ID之间的映射表.
     * 用于存放已经放置了的方块的位置和方块类型.
     */
    private final Map<Pos, String> pos2CustomBlockId = new HashMap<>();

    /**
     * 区块id 与 所放置的方块位置之间的映射表.
     * 用于跟随区块的卸载来快速找到对应的已放置方块, 并进行移除.
     */
    private final Map<String, Set<Pos>> chunkId2Pos = new HashMap<>();

    /**
     * 未加载的区块ID 与 自定义方块及其所在位置 之间的映射表.
     * 用与在区块加载时, 放置方块.
     */
    private final Map<String, Map<Pos, String>> unloadCustomBlocks = new HashMap<>();

    private final NumericalRequirements plugin;

    public RealCustomBlockService(NumericalRequirements plugin, ConfigurationSection config) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        loadCustomBlocks();
        loadCustomBlocksPos();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        I18N.info("real-custom-block-service-enable");
    }

    private void loadCustomBlocks() {
        File dataFolder = plugin.getDataFolder();
        File customBlocksFile = new File(dataFolder, "custom-blocks.yml");
        if (!customBlocksFile.exists()) {
            plugin.saveResource("custom-blocks.yml", false);
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(customBlocksFile);
        for (String key : yamlConfiguration.getKeys(false)) {
            ConfigurationSection section = yamlConfiguration.getConfigurationSection(key);
            CustomBlock customBlock = null;
            try {
                customBlock = CustomBlock.loadFromConfigurationSection(section);
            } catch (Throwable e) {
                e.printStackTrace();
                DebugLogger.debug(e);
            }
            if (customBlock == null) {
                I18N.warning("load-custom-block-failed", key);
                continue;
            }
            customBlock.setId(key);
            customBlocks.put(key, customBlock);
            itemId2CustomId.put(customBlock.getBlockItemId(), key);
        }
    }

    private synchronized void loadCustomBlocksPos() {
        File dataFolder = plugin.getDataFolder();
        File customBlocksFile = new File(dataFolder, "custom-block-pos.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(customBlocksFile);
        for (String key : yamlConfiguration.getKeys(false)) {
            CustomBlock customBlock = getCustomBlock(key);
            if (customBlock == null) continue;
            Object object = this;
            yamlConfiguration.getStringList(key).forEach(it -> {
                String[] split = it.split(":");
                Pos pos = new Pos(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
                plugin.getManager().runTask(() -> {
                    synchronized (object) {
                        Chunk chunk = pos.toLocation().getChunk();
                        if (chunk.isLoaded()) {
                            placeCustomBlock(customBlock, pos);
                            return;
                        }

                        String chunkId = getChunkId(chunk);
                        if (!unloadCustomBlocks.containsKey(chunkId)) {
                            unloadCustomBlocks.put(chunkId, new HashMap<>());
                        }
                        unloadCustomBlocks.get(chunkId).put(pos, key);
                    }
                });

            });
        }
    }

    private synchronized void saveCustomBlocks() {
        File dataFolder = plugin.getDataFolder();
        File customBlocksFile = new File(dataFolder, "custom-block-pos.yml");
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        Map<String, List<String>> posList = new HashMap<>();
        pos2CustomBlockId.forEach((pos, id) -> {
            if (!posList.containsKey(id)) posList.put(id, new ArrayList<>());
            posList.get(id).add(String.format("%s:%d:%d:%d", pos.getWorld(), pos.getX(), pos.getY(), pos.getZ()));
        });
        unloadCustomBlocks.forEach((key, map) -> {
            map.forEach((pos, id) -> {
                if (!posList.containsKey(id)) posList.put(id, new ArrayList<>());
                posList.get(id).add(String.format("%s:%d:%d:%d", pos.getWorld(), pos.getX(), pos.getY(), pos.getZ()));
            });
        });
        posList.forEach(yamlConfiguration::set);
        try {
            yamlConfiguration.save(customBlocksFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        saveCustomBlocks();

        // 移除所有方块
        pos2CustomBlockId.forEach((pos, id) -> {
            CustomBlock customBlock = getCustomBlock(id);
            if (customBlock == null) return;
            customBlock.remove(pos);
        });
    }

    @Override
    protected synchronized CustomBlock getCustomBlock(@NotNull String blockId) {
        return customBlocks.get(blockId);
    }

    @Override
    protected synchronized CustomBlock getCustomBlockByItemId(@NotNull String itemId) {
        String s = itemId2CustomId.get(itemId);
        if (s == null) return null;
        return getCustomBlock(s);
    }

    @Override
    protected synchronized CustomBlock getCustomBlockByPos(@NotNull Pos pos) {
        String s = pos2CustomBlockId.get(pos);
        if (s == null) return null;
        return getCustomBlock(s);
    }

    @Override
    protected synchronized boolean registerCustomBlockData(@NotNull CustomBlockData data) {
        Pos pos = data.toPos();
        pos2CustomBlockId.put(pos, data.getCustomBlockId());
        String chunkId = getChunkId(pos.toLocation().getChunk());
        if (!chunkId2Pos.containsKey(chunkId)) {
            chunkId2Pos.put(chunkId, new HashSet<>());
        }
        chunkId2Pos.get(chunkId).add(pos);
        return true;
    }

    @Override
    protected synchronized boolean unregisterCustomBlockData(@NotNull CustomBlockData data) {
        Pos pos = data.toPos();
        String chunkId = getChunkId(pos.toLocation().getChunk());
        chunkId2Pos.getOrDefault(chunkId, Collections.emptySet()).remove(pos);
        return pos2CustomBlockId.remove(pos) != null;
    }

    @Override
    protected synchronized void loadChunkBlocks(Chunk chunk) {
        String chunkId = getChunkId(chunk);
        Map<Pos, String> remove = unloadCustomBlocks.remove(chunkId);
        if (remove != null) {
            remove.forEach((pos, id) -> {
                placeCustomBlock(getCustomBlock(id), pos);
            });
        }
    }

    @Override
    protected synchronized void unloadChunkBlocks(Chunk chunk) {
        String chunkId = getChunkId(chunk);
        Set<Pos> remove = chunkId2Pos.remove(chunkId);
        if (remove != null) {
            if (!unloadCustomBlocks.containsKey(chunkId)) {
                unloadCustomBlocks.put(chunkId, new HashMap<>());
            }
            Map<Pos, String> posStringMap = unloadCustomBlocks.get(chunkId);
            remove.forEach(pos -> {
                CustomBlock customBlockByPos = getCustomBlockByPos(pos);
                if (unregisterCustomBlockData(new CustomBlockData(pos, customBlockByPos.getId()))) {
                    DebugLogger.debug("break custom block %s, %s", customBlockByPos.getId(), pos);
                    customBlockByPos.remove(pos, true);
                }
                posStringMap.put(pos, customBlockByPos.getId());
            });
        }
    }

    private String getChunkId(Chunk chunk) {
        return String.format("%s:%d:%d", chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }
}
