package org.eu.smileyik.numericalrequirements.core.item.serialization.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.item.serialization.YamlItemEntry;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClassPathBuilder;

public class CustomModelDataEntry implements YamlItemEntry {
    final boolean flag;
    private ReflectClass itemMetaClass;

    public CustomModelDataEntry() {
        boolean flag1 = true;
        try {
            itemMetaClass = MySimpleReflect.getReflectClass(new ReflectClassPathBuilder()
                            .newGroup("org.bukkit.inventory.meta.ItemMeta#")
                            .append("getCustomModelData()")
                            .append("setCustomModelData(java.lang.Integer)")
                            .append("hasCustomModelData()")
                            .endGroup()
                            .finish());
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
            DebugLogger.debug(e);
            flag1 = false;
        }
        flag = flag1;
    }

    @Override
    public String getId() {
        return "custom-model-data";
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return flag;
    }

    @Override
    public void serialize(Handler handler, ConfigurationSection section, ItemStack itemStack, ItemMeta itemMeta) {
        if ((boolean) itemMetaClass.execute("hasCustomModelData", itemMeta)) {
            section.set(getId(), itemMetaClass.execute("getCustomModelData", itemMeta));
        }
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationSection section, ItemStack itemStack, ItemMeta itemMeta) {
        if (section.contains(getId())) {
            itemMetaClass.execute("setCustomModelData", itemMeta, section.getInt(getId()));
        }
        return null;
    }
}
