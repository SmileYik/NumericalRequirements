package org.eu.smileyik.numericalrequirements.core;

import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.util.HashMap;
import java.util.Map;

public class DataSource {
    private static final int DATA_SOURCE_MAP_CAPACITY = 8;
    private static final Map<String, HikariDataSource> DATA_SOURCE_MAP = new HashMap<>(DATA_SOURCE_MAP_CAPACITY);
    private static final Map<String, DataSourceConnectionSource> CONNECTION_SOURCE_MAP = new HashMap<>(DATA_SOURCE_MAP_CAPACITY);

    protected synchronized static void init(Plugin plugin, ConfigurationSection config) {
        if (config == null) return;
        for (String key : config.getKeys(false)) {
            if (!config.isConfigurationSection(key)) continue;
            ConfigurationSection section = config.getConfigurationSection(key);
            if (!section.contains("jdbc-url")) continue;
            if (!section.contains("driver")) continue;
            HikariDataSource hikariDataSource = null;
            try {
                HikariConfig hikariConfig = new HikariConfig();
                hikariConfig.setJdbcUrl(section.getString("jdbc-url").replace("${data_folder}", plugin.getDataFolder().toString()));
                if (section.contains("username")) hikariConfig.setUsername(section.getString("username"));
                if (section.contains("password")) hikariConfig.setPassword(section.getString("password"));
                if (section.isConfigurationSection("properties")) {
                    ConfigurationSection properties = section.getConfigurationSection("properties");
                    for (String propertiesKey : properties.getKeys(false)) {
                        hikariConfig.addDataSourceProperty(propertiesKey, properties.getString(propertiesKey));
                    }
                }
                hikariConfig.setDriverClassName(section.getString("driver"));
                hikariDataSource = new HikariDataSource(hikariConfig);
                DATA_SOURCE_MAP.put(key, hikariDataSource);
                CONNECTION_SOURCE_MAP.put(key, new DataSourceConnectionSource(hikariDataSource, hikariDataSource.getJdbcUrl()));
            } catch (Exception e) {
                I18N.severe("initialization-database-failed", key);
                DebugLogger.debug(e);
                CONNECTION_SOURCE_MAP.remove(key);
                DATA_SOURCE_MAP.remove(key);
                try {
                    if (hikariDataSource != null) hikariDataSource.close();
                } catch (Exception ex) {
                    DebugLogger.debug(ex);
                }
            }
        }
    }

    public static HikariDataSource getDataSource(String key) {
        return DATA_SOURCE_MAP.get(key);
    }

    public static DataSourceConnectionSource getConnectionSource(String key) {
        return CONNECTION_SOURCE_MAP.get(key);
    }

    protected static synchronized void close() {
        DATA_SOURCE_MAP.forEach((key, value) -> {
            try {
                value.close();
            } catch (Exception e) {

            }
        });
    }
}
