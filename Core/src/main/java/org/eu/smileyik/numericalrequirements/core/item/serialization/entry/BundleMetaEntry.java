package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializer;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * ItemMeta: org.bukkit.inventory.meta.BundleMeta
 */
public class BundleMetaEntry extends AbstractSimpleReflectItemSerializerEntry {
    @Override
    protected String getFieldName() {
        return "Items";
    }

    @Override
    protected String getClassName() {
        return "org.bukkit.inventory.meta.BundleMeta";
    }

    @Override
    protected String getFieldType() {
        return "java.util.List";
    }

    @Override
    protected Object getValue(ItemSerializer context, ConfigurationHashMap section) {
        List<ConfigurationHashMap> list = section.getList(getId(), ConfigurationHashMap.class);
        List<ItemStack> itemStacks = new LinkedList<>();
        for (ConfigurationHashMap configurationHashMap : list) {
            itemStacks.add(context.deserialize(configurationHashMap));
        }
        return itemStacks;
    }

    @Override
    protected void setValue(ItemSerializer context, ConfigurationHashMap section, Object value) {
        List<ItemStack> itemStacks = (List<ItemStack>) value;
        List<ConfigurationHashMap> list = new ArrayList<>();
        itemStacks.forEach(itemStack -> {
            list.add(context.serializeToConfigurationHashMap(itemStack));
        });
        section.put(getId(), list);
    }

    @Override
    protected Object getValue(ConfigurationHashMap section) {
        return null;
    }

    @Override
    protected void setValue(ConfigurationHashMap section, Object value) {

    }

    @Override
    public String getId() {
        return "bundle-meta";
    }

}
