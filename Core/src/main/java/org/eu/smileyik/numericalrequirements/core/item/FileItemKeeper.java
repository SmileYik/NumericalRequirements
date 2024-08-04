package org.eu.smileyik.numericalrequirements.core.item;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemKeeper;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializer;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.core.api.util.YamlUtil;
import org.eu.smileyik.numericalrequirements.core.item.serialization.JsonItemSerializer;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class FileItemKeeper implements ItemKeeper {
    /**
     * 序列化器字段
     */
    private static final String KEY_ITEM_SERIALIZER = "serializer";
    /**
     * 所在文件字段
     */
    private static final String KEY_FILE = "file";
    /**
     * 物品字段
     */
    private static final String KEY_ITEMS = "items";
    /**
     * 导入其他物品文件字段, 值为字符串列表.
     */
    private static final String KEY_IMPORT = "import";
    /**
     * 是否为主要存储文件.
     */
    private static final String KEY_MAIN_FILE = "main-file";
    private static final String KEY_ITEM_SYNC = "sync";

    private static final byte VALUE_ITEM_SERIALIZER_YAML = 0;
    private static final byte VALUE_ITEM_SERIALIZER_JSON = 1;

    private static final String DEFAULT_JSON_ITEM_FILE = "items.json";
    private static final String DEFAULT_YAML_ITEM_FILE = "items.yml";

    private final ItemSerializer serializer = new JsonItemSerializer();
    private final List<ConfigurationHashMap> itemSources = new LinkedList<>();
    private final Map<String, ItemStack> itemStackCache = Collections.synchronizedMap(new WeakHashMap<>());
    private final Plugin plugin;
    private final File dataFolder;
    private final int mainItemsIdx;
    private final boolean sync;

    private Set<String> idCache;

    public FileItemKeeper(Plugin plugin, ConfigurationSection config) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        this.serializer.configure(config.getConfigurationSection("serialization"));
        reloadItems();
        int mainItemsIndex = 0;
        if (!itemSources.isEmpty()) {
            for (int i = itemSources.size() - 1; i >= 1; i--) {
                ConfigurationHashMap itemSource = itemSources.get(i);
                if (itemSource.getBoolean(KEY_MAIN_FILE, false)) {
                    mainItemsIndex = i;
                    break;
                }
            }
        }
        this.mainItemsIdx = mainItemsIndex;
        String sync = config.getString("sync", "enable").toLowerCase();
        this.sync = sync.equals("enable") || sync.equals("true");
        DebugLogger.debug("sync item: %s", sync);
    }

    private File storeDefaultIfNotExists(File file) {
        if (!file.exists()) {
            plugin.saveResource(file.getName(), false);
        }
        return file;
    }

    private synchronized void loadJsonItems(File jsonFile) {
        if (!jsonFile.exists() || !jsonFile.isFile()) return;
        ConfigurationHashMap configurationHashMap = null;
        try {
            configurationHashMap = ConfigurationHashMap.GSON.fromJson(
                    Files.newBufferedReader(jsonFile.toPath()),
                    ConfigurationHashMap.class
            );
        } catch (IOException e) {
            DebugLogger.debug(e);
            return;
        }
        if (configurationHashMap == null) configurationHashMap = new ConfigurationHashMap();

        ConfigurationHashMap map = new ConfigurationHashMap();
        map.put(KEY_FILE, jsonFile.toString());
        map.put(KEY_ITEMS, configurationHashMap);
        map.put(KEY_ITEM_SERIALIZER, VALUE_ITEM_SERIALIZER_JSON);
        itemSources.add(map);

        // 从 import 字段中读取导入的物品文件的路径, 该路径相对于现在正在处理的 json 文件所在目录路径.
        if (configurationHashMap.contains(KEY_IMPORT)) {
            for (String s : configurationHashMap.getStringList(KEY_IMPORT)) {
                loadJsonItems(new File(jsonFile.getParent(), s));
            }
        }
    }

    private synchronized void loadYamlItems(File yamlFile) {
        if (!yamlFile.exists() || !yamlFile.isFile()) return;
        ConfigurationHashMap items = YamlUtil.toMap(YamlConfiguration.loadConfiguration(yamlFile));
        ConfigurationHashMap map = new ConfigurationHashMap();
        map.put(KEY_FILE, yamlFile.toString());
        map.put(KEY_ITEMS, items);
        map.put(KEY_ITEM_SERIALIZER, VALUE_ITEM_SERIALIZER_YAML);
        itemSources.add(map);

        // 从 import 字段中读取导入的物品文件的路径, 该路径相对于现在正在处理的 yaml 文件所在目录路径.
        if (items.contains(KEY_IMPORT)) {
            for (String s : items.getStringList(KEY_IMPORT)) {
                loadYamlItems(new File(yamlFile.getParent(), s));
            }
        }
    }

    private synchronized ConfigurationHashMap findItemById(String itemId) {
        for (ConfigurationHashMap map : itemSources) {
            ConfigurationHashMap items = map.getMap(KEY_ITEMS);
            if (items.isMap(itemId)) return items.getMap(itemId);
        }
        return null;
    }

    @Override
    public synchronized ItemStack loadItem(String itemId) {
        ItemStack itemStack = itemStackCache.get(itemId);
        if (itemStack != null) return itemStack.clone();
        ConfigurationHashMap config = findItemById(itemId);
        if (config == null) return null;
        ItemStack deserialize = serializer.deserialize(config);
        if (deserialize == null) return null;

        // set item id
        ItemKeeper.setItemId(deserialize, itemId);

        itemStackCache.put(itemId, deserialize);
        return deserialize.clone();
    }

    @Override
    public ItemStack loadItem(String itemId, int amount) {
        ItemStack itemStack = loadItem(itemId);
        if (itemStack == null) return null;
        itemStack.setAmount(amount);
        return itemStack;
    }

    @Override
    public synchronized void storeItem(String itemId, ItemStack itemStack) {
        if (itemStack == null) return;
        ConfigurationHashMap config = findItemById(itemId);
        if (config == null) {
            config = itemSources.get(mainItemsIdx).getMap(KEY_ITEMS);
        } else {
            config = config.getParent();
        }
        idCache.add(itemId);
        config.put(itemId, serializer.serializeToConfigurationHashMap(itemStack));
        itemStackCache.remove(itemId);
        saveItems(config.getParent());
    }

    @Override
    public ItemSerializer getSerializer() {
        return serializer;
    }

    @Override
    public boolean isSyncItem(ItemStack itemStack) {
        if (!sync) return false;
        String itemId = getItemId(itemStack);
        if (itemId == null) return false;
        ConfigurationHashMap itemById = findItemById(itemId);
        if (itemById == null) return false;
        return itemById.getBoolean(KEY_ITEM_SYNC, true);
    }

    private synchronized void saveItems(ConfigurationHashMap itemSource) {
        byte aByte = itemSource.getByte(KEY_ITEM_SERIALIZER);
        if (aByte == VALUE_ITEM_SERIALIZER_YAML) {
            saveYamlItems(
                    new File(itemSource.getString(KEY_FILE)),
                    itemSource.getMap(KEY_ITEMS)
            );
        } else {
            saveJsonItems(
                    new File(itemSource.getString(KEY_FILE)),
                    itemSource.getMap(KEY_ITEMS)
            );
        }
    }

    @Override
    public synchronized void saveItems() {
        for (ConfigurationHashMap itemSource : itemSources) {
            saveItems(itemSource);
        }
    }

    @Override
    public synchronized void reloadItems() {
        itemStackCache.clear();
        itemSources.clear();
        idCache = null;
        loadJsonItems(storeDefaultIfNotExists(new File(dataFolder, DEFAULT_JSON_ITEM_FILE)));
        loadYamlItems(storeDefaultIfNotExists(new File(dataFolder, DEFAULT_YAML_ITEM_FILE)));
    }

    @Override
    public synchronized Collection<String> getItemIds() {
        if (idCache == null) {
            idCache = new HashSet<>();
            itemSources.forEach(it -> idCache.addAll(it.getMap(KEY_ITEMS).keySet()));
            idCache.remove(KEY_IMPORT);
            idCache.remove(KEY_MAIN_FILE);
        }
        return idCache;
    }

    @Override
    public void clear() {
        itemSources.clear();
        itemStackCache.clear();
    }

    private synchronized void saveJsonItems(File jsonFile, ConfigurationHashMap items) {
        try {
            Files.write(
                    jsonFile.toPath(),
                    items.toJson(true).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE
            );
        } catch (IOException e) {
            I18N.severe("item.can-not-save-items", jsonFile);
            DebugLogger.debug(e);
        }
    }

    private synchronized void saveYamlItems(File yamlFile, ConfigurationHashMap items) {
        YamlConfiguration configurationSection = (YamlConfiguration) YamlUtil.fromMap(items);
        try {
            configurationSection.save(yamlFile);
        } catch (IOException e) {
            I18N.severe("item.can-not-save-items", yamlFile);
            DebugLogger.debug(e);
        }
    }
}
