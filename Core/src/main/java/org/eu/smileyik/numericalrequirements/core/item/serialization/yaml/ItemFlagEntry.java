package org.eu.smileyik.numericalrequirements.core.item.serialization.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.item.serialization.YamlItemEntry;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectMethod;

import java.util.List;
import java.util.Set;

public class ItemFlagEntry implements YamlItemEntry {
    final boolean flag;
    ReflectMethod<Object> valueOf;
    ReflectMethod<String> name;
    ReflectMethod<Void> addItemFlags;
    ReflectMethod<Set<?>> getItemFlags;

    public ItemFlagEntry() {
        boolean flag0 = true;
        try {
            valueOf = MySimpleReflect.get("org.bukkit.inventory.ItemFlag#valueOf(java.lang.String)");
            name = MySimpleReflect.getForce("java.lang.Enum#name()");
            addItemFlags = MySimpleReflect.get("org.bukkit.inventory.meta.ItemMeta#addItemFlags(org.bukkit.inventory.ItemFlag)");
            getItemFlags = MySimpleReflect.getForce("org.bukkit.inventory.meta.ItemMeta#getItemFlags()");
        } catch (Exception e) {
            DebugLogger.debug(e);
            flag0 = false;
        }
        flag = flag0;
    }


    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getId() {
        return "flags";
    }

    @Override
    public boolean isAvailable() {
        return flag;
    }

    @Override
    public void serialize(Handler handler, ConfigurationSection section, ItemStack itemStack, ItemMeta itemMeta) {
        Set<?> flags = getItemFlags.execute(itemMeta);
        if (flags == null || flags.isEmpty()) return;
        section.set(getId(), flags.stream().map(it -> (String) name.execute(it)).toList());
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationSection section, ItemStack itemStack, ItemMeta itemMeta) {
        if (!section.contains(getId())) return null;
        List<String> stringList = section.getStringList(getId());
        if (stringList == null || stringList.isEmpty()) return null;
        for (String flag : stringList) {
            addItemFlags.execute(itemMeta, valueOf.execute(null, flag.toUpperCase()));
        }
        return null;
    }
}
