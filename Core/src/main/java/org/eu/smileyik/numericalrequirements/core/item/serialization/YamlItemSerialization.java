package org.eu.smileyik.numericalrequirements.core.item.serialization;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.item.serialization.yaml.*;
import org.eu.smileyik.numericalrequirements.core.util.YamlUtil;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.util.*;

public class YamlItemSerialization implements ItemSerialization {
    private final List<YamlItemEntry> entries;

    public YamlItemSerialization() {
        List<YamlItemEntry> entries = new ArrayList<>();
        entries.add(new AttributeEntry.Attribute1Entry());
        entries.add(new AttributeEntry.Attribute2Entry());
        entries.add(new AttributeEntry.Attribute3Entry());
        entries.add(new AttributeEntry.Attribute4Entry());
        entries.add(new FoodEntry());
        entries.add(new HideTooltipEntry());
        entries.add(new ItemRarityEntry());
        entries.add(new UnbreakableEntry());
        entries.add(new FireResistantEntry());
        entries.add(new CustomModelDataEntry());
        entries.add(new EnchantmentNewEntry());
        entries.add(new EnchantmentOldEntry());
        entries.add(new LoreEntry());
        entries.add(new ItemFlagEntry());
        entries.add(new NBTEntry());
        entries.add(new NBTSyncEntry());
        entries.add(new PotionEntry.Potion1Entry());

        DebugLogger.debug((d) -> {
            entries.forEach(it -> {
                DebugLogger.debug("Entry Init:\nAvailable: %s\nClass: %s\ninstance: %s", it.isAvailable(), it.getClass().getName(), it);
            });
        });

        this.entries = entries.stream().filter(YamlItemEntry::isAvailable).sorted(Comparator.comparingInt(YamlItemEntry::getPriority)).toList();

        DebugLogger.debug((d) -> {
            this.entries.forEach(it -> {
                DebugLogger.debug("YamlItemSerialization Entry Enabled:\nAvailable: %s\nClass: %s\ninstance: %s", it.isAvailable(), it.getClass().getName(), it);
            });
        });
        entries.clear();
    }

    @Override
    public void configure(ConfigurationSection section) {
        if (section == null) return;
        entries.forEach(entry -> {
            if (section.isConfigurationSection(entry.getId())) {
                entry.configure(section.getConfigurationSection(entry.getId()));
            }
        });
    }

    @Override
    public String serialize(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        if (itemStack == null) {
            return config.saveToString();
        }

        ItemMeta meta = itemStack.getItemMeta();
        storeCommons(config, itemStack, meta);

        Set<String> ids = new HashSet<>();
        entries.forEach(entry -> {
            if (!entry.isAvailable() || ids.contains(entry.getId())) return;
            ConfigurationSection section = config;
            if (entry.getKey() != null) {
                section = section.createSection(entry.getKey());
            }
            SimpleHandler handler = new SimpleHandler();
            entry.serialize(handler, section, itemStack, meta);
            if (handler.isDeny()) {
                section.set(entry.getKey(), null);
            } else {
                ids.add(entry.getId());
            }
        });
        return config.saveToString();
    }

    @Override
    public ItemStack deserialize(String string) {
        ConfigurationSection config;
        try {
            config = YamlUtil.loadFromString(string);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        ItemStack itemStack = loadCommons(config);
        if (itemStack == null) {
            return null;
        }
        ItemMeta meta = itemStack.getItemMeta();

        Set<String> ids = new HashSet<>();
        for (YamlItemEntry entry : entries) {
            if (!entry.isAvailable() || ids.contains(entry.getId())) continue;

            ConfigurationSection section = config;
            if (entry.getKey() != null) {
                section = section.getConfigurationSection(entry.getKey());
            }
            if (section == null) {
                continue;
            }

            SimpleHandler handler = new SimpleHandler();
            DebugLogger.debug("Before: \n item: %s\nmeta: %s", itemStack, meta);
            DebugLogger.debug("Serialization class: %s", entry.getClass().getName());
            ItemStack deserialize = entry.deserialize(handler, section, itemStack, meta);
            if (!handler.isDeny()) {
                itemStack.setItemMeta(meta);
                meta = itemStack.getItemMeta();
                DebugLogger.debug("After: \n item: %s\nmeta: %s", itemStack, meta);
                if (deserialize != null) {
                    itemStack = deserialize;
                    meta = itemStack.getItemMeta();
                }
                ids.add(entry.getId());
            }
        }
        return itemStack;
    }

    private ItemStack loadCommons(ConfigurationSection section) {
        if (!section.contains("material")) {
            return null;
        }
        ItemStack stack = new ItemStack(Material.matchMaterial(section.getString("material")));
        ItemMeta meta = stack.getItemMeta();

        if (section.contains("name")) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name")));
        }
        if (section.contains("durability")) {
            stack.setDurability((short) section.getInt("durability"));
        }
        stack.setItemMeta(meta);
        return stack;
    }

    private void storeCommons(ConfigurationSection section, ItemStack itemStack, ItemMeta meta) {
        section.set("material", itemStack.getType().name());
        section.set("durability", itemStack.getDurability());
        if (meta != null) {
            if (meta.hasDisplayName()) {
                section.set("name", meta.getDisplayName());
            }
        }
    }

    private static class SimpleHandler implements YamlItemEntry.Handler {
        private boolean flag = false;

        @Override
        public void deny() {
            flag = true;
        }

        @Override
        public boolean isDeny() {
            return flag;
        }
    }
}
