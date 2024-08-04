package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

/**
 * ItemMeta: org.bukkit.inventory.meta.CrossbowMeta
 */
public class CrossbowMetaEntry extends BundleMetaEntry {
    @Override
    protected String getFieldName() {
        return "ChargedProjectiles";
    }

    @Override
    protected String getClassName() {
        return "org.bukkit.inventory.meta.CrossbowMeta";
    }

    @Override
    public String getId() {
        return "crossbow-meta";
    }
}
