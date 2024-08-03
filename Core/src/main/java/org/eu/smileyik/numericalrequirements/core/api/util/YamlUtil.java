package org.eu.smileyik.numericalrequirements.core.api.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class YamlUtil {
    public static String saveToString(ConfigurationSection section) {
        Yaml yaml = new Yaml(new YamlConstructor(), new YamlRepresenter(), new DumperOptions());
        return yaml.dumpAsMap(section.getValues(false));
    }

    public static ConfigurationSection loadFromString(String str) throws InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString(str);
        return configuration;
    }

    public static ConfigurationSection fromMap(Map<String, Object> map) {
        ConfigurationSection section = new YamlConfiguration();
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                section.set(key, fromMap((Map<String, Object>) value));
            } else {
                section.set(key, value);
            }
        });
        return section;
    }

    public static ConfigurationHashMap toMap(ConfigurationSection section) {
        ConfigurationHashMap map = new ConfigurationHashMap();
        section.getValues(false).forEach((k, v) -> {
            if (v instanceof ConfigurationSection) {
                map.put(k, toMap((ConfigurationSection) v));
            } else {
                map.put(k, v);
            }
        });
        return map;
    }
}
