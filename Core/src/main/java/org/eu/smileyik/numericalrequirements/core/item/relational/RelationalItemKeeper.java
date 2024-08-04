package org.eu.smileyik.numericalrequirements.core.item.relational;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.core.DataSource;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemKeeper;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializer;
import org.eu.smileyik.numericalrequirements.core.item.serialization.JsonItemSerializer;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

public class RelationalItemKeeper implements ItemKeeper {

    private final JsonItemSerializer serializer = new JsonItemSerializer();
    private final boolean syncItem;
    private final Dao<ItemEntity, String> itemDao;
    private final Map<String, ItemStack> itemStackCache = Collections.synchronizedMap(new WeakHashMap<>());
    private final Map<String, Boolean> syncFlagCache;

    public RelationalItemKeeper(Plugin plugin, ConfigurationSection section) {
        String sync = section.getString("sync", "enable").toLowerCase();
        this.syncItem = sync.equals("enable") || sync.equals("true");
        this.serializer.configure(section.getConfigurationSection("serialization"));
        String datasource = section.getString("datasource");
        if (datasource == null) {
            throw new RuntimeException("No datasource specified");
        }
        DataSourceConnectionSource connectionSource = DataSource.getConnectionSource(datasource);
        if (connectionSource == null) {
            throw new RuntimeException("No datasource specified");
        }
        try {
            itemDao = DaoManager.createDao(connectionSource, ItemEntity.class);
        } catch (SQLException e) {
            I18N.severe("item.initialize-relational-item-keeper-failed");
            throw new RuntimeException(e);
        }
        try {
            TableUtils.createTableIfNotExists(connectionSource, ItemEntity.class);
        } catch (SQLException e) {
            I18N.severe("item.create-relational-item-table-failed");
            throw new RuntimeException(e);
        }

        if (syncItem) {
            syncFlagCache = Collections.synchronizedMap(new WeakHashMap<>());
        } else {
            syncFlagCache = null;
        }
    }

    @Override
    public synchronized ItemStack loadItem(String itemId) {
        if (itemStackCache.containsKey(itemId)) {
            return itemStackCache.get(itemId).clone();
        }
        ItemEntity itemEntity = getItem(itemId);
        if (itemEntity == null || itemEntity.getItem() == null) return null;
        ItemStack deserialize = serializer.deserialize(itemEntity.getItem());
        if (deserialize == null) return null;
        ItemKeeper.setItemId(deserialize, itemId);
        itemStackCache.put(itemId, deserialize);
        if (syncFlagCache != null) syncFlagCache.put(itemId, itemEntity.isSync());
        return deserialize.clone();
    }

    @Override
    public ItemStack loadItem(String itemId, int amount) {
        ItemStack itemStack = loadItem(itemId);
        if (itemStack != null) itemStack.setAmount(amount);
        return itemStack;
    }

    @Override
    public synchronized void storeItem(String itemId, ItemStack itemStack) {
        itemStackCache.remove(itemId);
        if (syncFlagCache != null) syncFlagCache.remove(itemId);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(itemId);
        itemEntity.setItem(serializer.serialize(itemStack, false));
        try {
            if (itemDao.createOrUpdate(itemEntity).getNumLinesChanged() == 0) {
                DebugLogger.debug("There is no change after create or-update item %s: %s", itemId, itemEntity.getItem());
            }
        } catch (SQLException e) {
            DebugLogger.debug("failed to create or-update item %s: %s", itemId, itemEntity.getItem());
            DebugLogger.debug(e);
        }
    }

    @Override
    public ItemSerializer getSerializer() {
        return serializer;
    }

    @Override
    public boolean isSyncItem(ItemStack itemStack) {
        if (!syncItem) return false;
        String itemId = getItemId(itemStack);
        if (itemId == null) return false;
        if (syncFlagCache.containsKey(itemId)) {
            return syncFlagCache.get(itemId);
        }
        ItemEntity itemEntity = getItem(itemId);
        return itemEntity != null && itemEntity.isSync();
    }

    @Override
    public void saveItems() {

    }

    @Override
    public void reloadItems() {
        itemStackCache.clear();
        if (syncFlagCache != null) syncFlagCache.clear();
    }

    @Override
    public Collection<String> getItemIds() {
        try (GenericRawResults<String[]> results = itemDao.queryBuilder().selectColumns("id").queryRaw()) {
            return results
                    .getResults()
                    .stream()
                    .map(it -> it[0])
                    .collect(Collectors.toList());
        } catch (Exception e) {
            DebugLogger.debug("failed to get ids from database: %s", e.getMessage());
            DebugLogger.debug(e);
        }
        return Collections.emptyList();
    }

    @Override
    public void clear() {
        itemStackCache.clear();
        if (syncFlagCache != null) syncFlagCache.clear();
    }

    private ItemEntity getItem(String itemId) {
        try {
            return itemDao.queryForId(itemId);
        } catch (SQLException e) {
            DebugLogger.debug("failed to get item %s: %s", itemId, e.getMessage());
            DebugLogger.debug(e);
        }
        return null;
    }
}
