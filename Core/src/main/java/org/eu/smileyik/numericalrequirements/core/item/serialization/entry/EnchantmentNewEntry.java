package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializationEntry;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClassPathBuilder;

public class EnchantmentNewEntry implements ItemSerializationEntry {

    final boolean flag;

    ReflectClass namespaceKey;
    ReflectClass registry;
    ReflectClass keyed;

    public EnchantmentNewEntry() {
        boolean flag0 = true;
        try {
            namespaceKey = MySimpleReflect.getReflectClass(new ReflectClassPathBuilder()
                            .newGroup("org.bukkit.NamespacedKey")
                            .newGroup("#")
                            .append("fromString(java.lang.String)")
                            .append("getKey()")
                            .endGroup()
                            .endGroup()
                            .finish());
            registry = MySimpleReflect.getReflectClass(new ReflectClassPathBuilder()
                            .newGroup("org.bukkit.Registry")
                            .newGroup("@")
                            .append("ENCHANTMENT")
                            .endGroup()
                            .newGroup("#")
                            .append("get(org.bukkit.NamespacedKey)")
                            .endGroup()
                            .endGroup()
                            .finish());
            keyed = MySimpleReflect.getReflectClass(new ReflectClassPathBuilder()
                            .newGroup("org.bukkit.Keyed")
                            .newGroup("#")
                            .append("getKey()")
                            .endGroup()
                            .endGroup()
                            .finish());
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
            flag0 = false;
            DebugLogger.debug(e);
        }
        flag = flag0;
    }


    @Override
    public String getId() {
        return "enchantment";
    }

    @Override
    public boolean isAvailable() {
        return flag;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void serialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        if (itemMeta.hasEnchants()) {
            itemMeta.getEnchants().forEach((k, v) -> {
                section.put((String) namespaceKey.execute("getKey", keyed.execute("getKey", k)), v);
            });
        }
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        for (String key : section.keySet()) {
            Object nk = namespaceKey.execute("fromString", null, key);
            if (nk == null) {
                I18N.warning("item.serialization.enchantment.unknown", key);
                continue;
            }
            Enchantment enchantment = (Enchantment) registry.execute("get", registry.get("ENCHANTMENT", null), nk);
            if (enchantment == null) {
                I18N.warning("item.serialization.enchantment.unknown", key);
                continue;
            }
            itemMeta.addEnchant(enchantment, section.getInt(key), true);
        }
        return null;
    }
}
