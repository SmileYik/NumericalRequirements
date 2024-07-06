package org.eu.smileyik.numericalrequirements.core.item.serialization.yaml;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.item.serialization.YamlItemEntry;

public class EnchantmentOldEntry implements YamlItemEntry {

    @Override
    public String getId() {
        return "enchantment";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public void serialize(Handler handler, ConfigurationSection section, ItemStack itemStack, ItemMeta itemMeta) {
        if (itemMeta.hasEnchants()) {
            itemMeta.getEnchants().forEach((k, v) -> {
                section.set(k.getName(), v);
            });
        }
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationSection section, ItemStack itemStack, ItemMeta itemMeta) {
        for (String key : section.getKeys(false)) {
            Enchantment enchantment = Enchantment.getByName(key.toUpperCase());
            if (enchantment == null) {
                I18N.warning("item.serialization.enchantment.unknown", key);
                continue;
            } else if (!enchantment.getName().equals(key)) {
                I18N.warning("item.serialization.enchantment.not-equal", key, enchantment.getName());
                continue;
            }
            itemMeta.addEnchant(enchantment, section.getInt(key), true);
        }
        return null;
    }
}
