package org.eu.smileyik.numericalrequirements.core.item.serialization;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;

public class JsonItemSerializer extends AbstractItemSerializer {
    @Override
    public String serialize(ItemStack itemStack) {
        if (itemStack == null) return "{}";
        return doSerialize(itemStack).toJson();
    }

    @Override
    public ItemStack deserialize(String string) {
        return doDeserialize(ConfigurationHashMap.fromJson(string));
    }
}
