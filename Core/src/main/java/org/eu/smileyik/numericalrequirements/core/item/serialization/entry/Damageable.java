package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializerEntry;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;

/**
 * ItemMeta: org.bukkit.inventory.meta.Damageable
 */
public class Damageable implements ItemSerializerEntry {

    private final Entry damage = new Entry() {
        @Override
        protected String getFieldName() {
            return "Damage";
        }

        @Override
        protected String getFieldType() {
            return "int";
        }

        @Override
        public String getId() {
            return "damage";
        }
    };
    private final Entry maxDamage = new Entry() {
        @Override
        protected String getFieldType() {
            return "java.lang.Integer";
        }

        @Override
        protected String getFieldName() {
            return "MaxDamage";
        }

        @Override
        public String getId() {
            return "max-damage";
        }
    };

    @Override
    public String getId() {
        return "damageable";
    }

    private static abstract class Entry extends AbstractSimpleReflectItemSerializerEntry {
        @Override
        protected String getClassName() {
            return "org.bukkit.inventory.meta.Damageable";
        }

        @Override
        protected Object getValue(ConfigurationHashMap section) {
            if (section.containsKey(getId())) {
                return section.getInt(getId());
            }
            return null;
        }

        @Override
        protected void setValue(ConfigurationHashMap section, Object value) {
            section.put(getId(), value);
        }
    }

    @Override
    public boolean isAvailable() {
        return maxDamage.isAvailable() || damage.isAvailable();
    }

    @Override
    public void serialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        if (maxDamage.isAvailable()) maxDamage.serialize(handler, section, itemStack, itemMeta);
        if (damage.isAvailable()) damage.serialize(handler, section, itemStack, itemMeta);
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        if (maxDamage.isAvailable()) maxDamage.deserialize(handler, section, itemStack, itemMeta);
        if (damage.isAvailable()) damage.deserialize(handler, section, itemStack, itemMeta);
        return null;
    }
}
