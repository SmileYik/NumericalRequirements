package org.eu.smileyik.numericalrequirements.core.item.serialization.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.item.serialization.YamlItemEntry;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClassPathBuilder;

public class EnchantmentNewEntry implements YamlItemEntry {

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
    public void serialize(Handler handler, ConfigurationSection section, ItemStack itemStack, ItemMeta itemMeta) {
        if (itemMeta.hasEnchants()) {
            itemMeta.getEnchants().forEach((k, v) -> {
                section.set((String) namespaceKey.execute("getKey", keyed.execute("getKey", k)), v);
            });
        }
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationSection section, ItemStack itemStack, ItemMeta itemMeta) {
        for (String key : section.getKeys(false)) {
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
