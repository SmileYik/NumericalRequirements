package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializerEntry;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClassPathBuilder;

public class FireResistantEntry implements ItemSerializerEntry {
    final boolean flag;
    private ReflectClass itemMetaClass;

    public FireResistantEntry() {
        boolean flag1 = true;
        try {
            itemMetaClass = MySimpleReflect.getReflectClass(new ReflectClassPathBuilder()
                            .newGroup("org.bukkit.inventory.meta.ItemMeta#")
                            .append("isFireResistant()")
                            .append("setFireResistant(boolean)")
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
        return "fire-resistant";
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
    public void serialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        boolean flag = (boolean) itemMetaClass.execute("isFireResistant", itemMeta);
        if (flag) section.put(getId(), true);
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        if (section.contains(getId())) {
            itemMetaClass.execute("setFireResistant", itemMeta, section.getBoolean(getId()));
        }
        return null;
    }
}
