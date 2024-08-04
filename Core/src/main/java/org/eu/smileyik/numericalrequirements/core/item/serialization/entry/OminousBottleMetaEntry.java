package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;

public class OminousBottleMetaEntry extends AbstractSimpleReflectItemSerializerEntry {
    @Override
    protected String getClassName() {
        return "org.bukkit.inventory.meta.OminousBottleMeta";
    }

    @Override
    protected String getFieldType() {
        return "int";
    }

    @Override
    protected String getFieldName() {
        return "Amplifier";
    }

    @Override
    public String getId() {
        return "ominous-bottle-amplifier";
    }

    @Override
    protected Object getValue(ConfigurationHashMap section) {
        return section.getInt(getId());
    }

    @Override
    protected void setValue(ConfigurationHashMap section, Object value) {
        section.put(getId(), value);
    }
}
