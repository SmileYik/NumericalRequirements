package org.eu.smileyik.numericalrequirements.core.item.serialization;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.util.YamlUtil;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

public class YamlItemSerializer extends AbstractItemSerializer {

    @Override
    public String serialize(ItemStack itemStack) {
        if (itemStack == null) {
            return new YamlConfiguration().saveToString();
        }

        return YamlUtil.saveToString(YamlUtil.fromMap(serializeToConfigurationHashMap(itemStack)));
    }

    @Override
    public ItemStack deserialize(String string) {
        ConfigurationSection configurationSection;
        try {
            configurationSection = YamlUtil.loadFromString(string);
        } catch (InvalidConfigurationException e) {
            DebugLogger.debug(e);
            return null;
        }

        return deserialize(YamlUtil.toMap(configurationSection));
    }
}
