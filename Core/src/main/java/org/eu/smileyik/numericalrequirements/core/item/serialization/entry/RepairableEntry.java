package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;

/**
 * ItemMeta: org.bukkit.inventory.meta.Repairable
 */
public class RepairableEntry extends AbstractSimpleReflectItemSerializerEntry {
    @Override
    public String getId() {
        return "repair-cost";
    }

    @Override
    protected String getFieldName() {
        return "RepairCost";
    }

    @Override
    protected String getClassName() {
        return "org.bukkit.inventory.meta.Repairable";
    }

    @Override
    protected String getFieldType() {
        return "int";
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