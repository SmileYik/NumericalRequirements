package org.eu.smileyik.numericalrequirements.core.item.serialization;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;

public class JsonItemSerializer extends AbstractItemSerializer {
    private boolean prettyPrint = true;

    @Override
    public String serialize(ItemStack itemStack) {
        if (itemStack == null) return "{}";
        return serializeToConfigurationHashMap(itemStack).toJson(prettyPrint);
    }

    @Override
    public ItemStack deserialize(String string) {
        return deserialize(ConfigurationHashMap.fromJson(string));
    }

    @Override
    public void configure(ConfigurationSection section) {
        if (section == null) return;
        prettyPrint = section.getBoolean("pretty-print", true);
        super.configure(section);
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }
}
