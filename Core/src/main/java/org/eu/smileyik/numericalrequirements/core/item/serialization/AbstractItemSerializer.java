package org.eu.smileyik.numericalrequirements.core.item.serialization;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializer;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializerEntry;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.core.item.serialization.entry.*;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.util.*;

public abstract class AbstractItemSerializer implements ItemSerializer {
    protected static final List<ItemSerializerEntry> ENTRIES;

    static {
        List<ItemSerializerEntry> entries = new ArrayList<>();
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
        entries.add(new PotionEntry.Potion1Entry());
        entries.add(new PotionEntry.Potion2Entry());
        entries.add(new AxolotlBucketEntry());
        entries.add(new Damageable());
        entries.add(new RepairableEntry());
        entries.add(new OminousBottleMetaEntry());
        entries.add(new CrossbowMetaEntry());
        entries.add(new BundleMetaEntry());

        DebugLogger.debug((d) -> {
            entries.forEach(it -> {
                DebugLogger.debug("Entry Init:\nAvailable: %s\nClass: %s\ninstance: %s", it.isAvailable(), it.getClass().getName(), it);
            });
        });

        entries.removeIf(it -> !it.isAvailable());
        entries.sort(Comparator.comparingInt(ItemSerializerEntry::getPriority));
        ENTRIES = entries;

        DebugLogger.debug((d) -> {
            entries.forEach(it -> {
                DebugLogger.debug("YamlItemSerialization Entry Enabled:\nAvailable: %s\nClass: %s\ninstance: %s", it.isAvailable(), it.getClass().getName(), it);
            });
        });
    }

    @Override
    public void configure(ConfigurationSection section) {
        if (section == null) return;
        ENTRIES.forEach(entry -> {
            if (section.isConfigurationSection(entry.getId())) {
                entry.configure(section.getConfigurationSection(entry.getId()));
            }
        });
    }

    @Override
    public ConfigurationHashMap serializeToConfigurationHashMap(ItemStack itemStack) {
        ConfigurationHashMap config = new ConfigurationHashMap();
        ItemMeta meta = itemStack.getItemMeta();
        storeCommons(config, itemStack, meta);

        Set<String> ids = new HashSet<>();
        ENTRIES.forEach(entry -> {
            if (ids.contains(entry.getId())) return;

            ConfigurationHashMap section = config;
            if (entry.getKey() != null) {
                section = section.createMap(entry.getKey());
            }
            SimpleHandler handler = new SimpleHandler();
            entry.serialize(this, handler, section, itemStack, meta);
            if (section.isEmpty()) config.remove(entry.getKey());
            if (handler.isDeny()) {
                config.remove(entry.getKey());
            } else {
                ids.add(entry.getId());
            }
        });
        return config;
    }

    @Override
    public ItemStack deserialize(ConfigurationHashMap config) {
        ItemStack itemStack = loadCommons(config);
        if (itemStack == null) {
            return null;
        }
        ItemMeta meta = itemStack.getItemMeta();

        Set<String> ids = new HashSet<>();
        for (ItemSerializerEntry entry : ENTRIES) {
            if (ids.contains(entry.getId())) continue;

            ConfigurationHashMap section = config;
            if (entry.getKey() != null) {
                section = section.getMap(entry.getKey());
            }
            if (section == null) {
                continue;
            }

            SimpleHandler handler = new SimpleHandler();
            // DebugLogger.debug("Before: \n item: %s\nmeta: %s", itemStack, meta);
            // DebugLogger.debug("Serialization class: %s", entry.getClass().getName());
            ItemStack deserialize = entry.deserialize(this, handler, section, itemStack, meta);
            if (!handler.isDeny()) {
                itemStack.setItemMeta(meta);
                meta = itemStack.getItemMeta();
                // DebugLogger.debug("After: \n item: %s\nmeta: %s", itemStack, meta);
                if (deserialize != null) {
                    itemStack = deserialize;
                    meta = itemStack.getItemMeta();
                }
                ids.add(entry.getId());
            }
        }
        return itemStack;
    }

    protected ItemStack loadCommons(ConfigurationHashMap section) {
        if (!section.contains("material")) {
            return null;
        }
        ItemStack stack = new ItemStack(Material.matchMaterial(section.getString("material")));
        ItemMeta meta = stack.getItemMeta();

        if (section.contains("name")) {
            meta.setDisplayName(section.getColorString("name"));
        }
        if (section.contains("durability")) {
            stack.setDurability((short) section.getInt("durability"));
        }
        stack.setItemMeta(meta);
        return stack;
    }

    protected void storeCommons(ConfigurationHashMap section, ItemStack itemStack, ItemMeta meta) {
        section.put("material", itemStack.getType().name());
        if (itemStack.getDurability() != 0) {
            section.put("durability", itemStack.getDurability());
        }
        if (meta != null) {
            if (meta.hasDisplayName()) {
                section.put("name", meta.getDisplayName());
            }
        }
    }

    protected static class SimpleHandler implements ItemSerializerEntry.Handler {
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
