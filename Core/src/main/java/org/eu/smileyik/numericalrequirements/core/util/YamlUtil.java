package org.eu.smileyik.numericalrequirements.core.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

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
}
