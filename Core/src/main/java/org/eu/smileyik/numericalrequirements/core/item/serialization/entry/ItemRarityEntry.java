package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializerEntry;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClassPathBuilder;

public class ItemRarityEntry implements ItemSerializerEntry {
    final boolean flag;
    private ReflectClass itemMetaClass;
    private ReflectClass itemRarity;

    public ItemRarityEntry() {
        boolean flag1 = true;
        try {
            itemMetaClass = MySimpleReflect.getReflectClass(new ReflectClassPathBuilder()
                            .newGroup("org.bukkit.inventory.meta.ItemMeta#")
                            .append("getRarity()")
                            .append("setRarity(org.bukkit.inventory.ItemRarity)")
                            .append("hasRarity()")
                            .endGroup()
                            .finish());
            itemRarity = MySimpleReflect.getReflectClass(new ReflectClassPathBuilder()
                            .newGroup("")
                            .append("java.lang.Enum#name()")
                            .append("org.bukkit.inventory.ItemRarity#valueOf(java.lang.String)")
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
        return "rarity";
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
        if ((boolean) itemMetaClass.execute("hasRarity", itemMeta)) {
            section.put(getId(), itemRarity.execute("name", itemMetaClass.execute("getRarity", itemMeta)));
        }
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        if (section.contains(getId())) {
            itemMetaClass.execute("setRarity", itemMeta, itemRarity.execute("valueOf", null, section.getString(getId())));
        }
        return null;
    }
}
