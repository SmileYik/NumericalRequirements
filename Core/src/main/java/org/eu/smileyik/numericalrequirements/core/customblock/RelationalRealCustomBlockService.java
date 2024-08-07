package org.eu.smileyik.numericalrequirements.core.customblock;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.core.DataSource;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.customblock.CustomBlock;
import org.eu.smileyik.numericalrequirements.core.api.customblock.Pos;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;

/**
 * 关系型数据库版的 RealCustomBlockService.
 * 与 RealCustomBlockService 的区别仅在与从数据库中读取自定义方块配置以及自定义方块的位置信息.
 */
public class RelationalRealCustomBlockService extends AbstractRealCustomBlockService {
    private final Plugin plugin;

    /**
     * 因为使用的是真实的实体和真实的方块, 需要记录下 CustomBlock 以保持实体的控制权.
     */
    private final Map<String, CustomBlock> customBlocks = new HashMap<>();
    private final Map<String, String> itemId2CustomBlock = new HashMap<>();

    private final Dao<CustomBlockData, UUID> customBlockDataDao;
    private final Dao<CustomBlockConfiguration, String> customBlockConfigurationDao;

    public RelationalRealCustomBlockService(NumericalRequirements plugin, ConfigurationSection config) throws SQLException {
        this.plugin = plugin;
        DataSourceConnectionSource connectionSource = DataSource.getConnectionSource(config.getString("datasource"));

        customBlockDataDao = DaoManager.createDao(connectionSource, CustomBlockData.class);
        TableUtils.createTableIfNotExists(connectionSource, CustomBlockData.class);

        customBlockConfigurationDao = DaoManager.createDao(connectionSource, CustomBlockConfiguration.class);
        TableUtils.createTableIfNotExists(connectionSource, CustomBlockConfiguration.class);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    protected synchronized CustomBlock getCustomBlock(@NotNull String blockId) {
        if (customBlocks.containsKey(blockId)) {
            return customBlocks.get(blockId);
        }
        try {
            CustomBlock customBlock = customBlockConfigurationDao.queryForId(blockId).toCustomBlock();
            if (customBlock != null) {
                customBlocks.put(blockId, customBlock);
                itemId2CustomBlock.put(customBlock.getBlockItemId(), blockId);
            }
            return customBlock;
        } catch (Throwable e) {
            DebugLogger.debug("get custom block from id failed, block id is %s", blockId);
            DebugLogger.debug(e);
            return null;
        }
    }

    @Override
    protected synchronized CustomBlock getCustomBlockByItemId(@NotNull String itemId) {
        if (itemId2CustomBlock.containsKey(itemId)) {
            return customBlocks.get(itemId2CustomBlock.get(itemId));
        }
        try {
            List<CustomBlockConfiguration> result = customBlockConfigurationDao.queryForEq("item-id", itemId);
            if (result.isEmpty()) return null;
            CustomBlock customBlock = result.get(0).toCustomBlock();
            if (customBlock != null) {
                customBlocks.put(customBlock.getId(), customBlock);
                itemId2CustomBlock.put(itemId, customBlock.getId());
            }
            return customBlock;
        } catch (Throwable e) {
            DebugLogger.debug("get custom block from item-id failed, item id is %s", itemId);
            DebugLogger.debug(e);
            return null;
        }
    }

    @Override
    protected synchronized CustomBlock getCustomBlockByPos(@NotNull Pos pos) {
        CustomBlockData data = new CustomBlockData(pos);
        try {
            List<CustomBlockData> list = customBlockDataDao.queryForMatching(data);
            String blockId = list.isEmpty() ? null : list.get(0).getCustomBlockId();
            return blockId == null ? null : getCustomBlock(blockId);
        } catch (Throwable e) {
            DebugLogger.debug("get custom block data from pos failed, item pos is %s", pos);
            DebugLogger.debug(e);
            return null;
        }
    }

    @Override
    protected synchronized boolean registerCustomBlockData(@NotNull CustomBlockData customBlockData) {
        DebugLogger.debug("registering custom block data, the data is %s", customBlockData);
        try {
            long l = customBlockDataDao.queryBuilder()
                    .where()
                    .eq("world", customBlockData.getWorld()).and()
                    .eq("block-x", customBlockData.getBlockX()).and()
                    .eq("block-y", customBlockData.getBlockY()).and()
                    .eq("block-z", customBlockData.getBlockZ())
                    .countOf();

            return l == 0 && customBlockDataDao.create(customBlockData) != 0;
        } catch (Throwable e) {
            DebugLogger.debug("failed to register custom block data, the data is %s", customBlockData);
            DebugLogger.debug(e);
            return false;
        }
    }

    @Override
    protected synchronized boolean unregisterCustomBlockData(@NotNull CustomBlockData customBlockData) {
        DebugLogger.debug("unregistering custom block data, the data is %s", customBlockData);
        try {
            DeleteBuilder<CustomBlockData, UUID> builder = customBlockDataDao.deleteBuilder();
            builder.setWhere(builder.where()
                            .eq("world", customBlockData.getWorld()).and()
                            .eq("block-x", customBlockData.getBlockX()).and()
                            .eq("block-y", customBlockData.getBlockY()).and()
                            .eq("block-z", customBlockData.getBlockZ()));
            int delete = builder.delete();
            return delete != 0;
        } catch (SQLException e) {
            DebugLogger.debug("failed to unregister custom block data, the data is %s", customBlockData);
            DebugLogger.debug(e);
            return false;
        }
    }

    private synchronized List<CustomBlockData> queryForMatchingChunk(CustomBlockData customBlockData) {
        try {
            return customBlockDataDao.queryBuilder().where()
                    .eq("world", customBlockData.getWorld()).and()
                    .eq("chunk-x", customBlockData.getChunkX()).and()
                    .eq("chunk-z", customBlockData.getChunkZ())
                    .query();
        } catch (SQLException e) {
            DebugLogger.debug("failed to query custom block data, the data is %s", customBlockData);
            DebugLogger.debug(e);
            return Collections.emptyList();
        }
    }

    @Override
    protected synchronized void loadChunkBlocks(Chunk chunk) {
        List<CustomBlockData> list = queryForMatchingChunk(new CustomBlockData(chunk));
        for (CustomBlockData data : list) {
            CustomBlock customBlock = getCustomBlock(data.getCustomBlockId());
            if (customBlock != null) {
                DebugLogger.debug("load custom block data, the data is %s", data);
                customBlock.place(data.toPos());
            }
        }
    }

    @Override
    protected synchronized void unloadChunkBlocks(Chunk chunk) {
        List<CustomBlockData> list = queryForMatchingChunk(new CustomBlockData(chunk));
        for (CustomBlockData data : list) {
            CustomBlock customBlock = getCustomBlock(data.getCustomBlockId());
            if (customBlock != null) {
                DebugLogger.debug("unload custom block data, the data is %s", data);
                customBlock.remove(data.toPos(), true);
            }
        }
    }

    @Override
    public synchronized void initialize() {
        for (World world : plugin.getServer().getWorlds()) {
            for (Chunk loadedChunk : world.getLoadedChunks()) {
                loadChunkBlocks(loadedChunk);
            }
        }
    }

    @Override
    public synchronized void shutdown() {
        for (World world : plugin.getServer().getWorlds()) {
            for (Chunk loadedChunk : world.getLoadedChunks()) {
                unloadChunkBlocks(loadedChunk);
            }
        }

        customBlocks.clear();
        itemId2CustomBlock.clear();
    }
}
