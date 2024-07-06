package org.eu.smileyik.numericalrequirements.core.item.serialization.yaml;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.item.ItemTag;
import org.eu.smileyik.numericalrequirements.core.item.serialization.YamlItemEntry;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagTypeValue;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagValue;
import org.eu.smileyik.numericalrequirements.core.util.Pair;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LoreEntry implements YamlItemEntry {
    @Override
    public String getId() {
        return "lore";
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void serialize(Handler handler, ConfigurationSection section, ItemStack itemStack, ItemMeta itemMeta) {
        if (!itemMeta.hasLore()) return;
        ItemService itemService = NumericalRequirements.getInstance().getItemService();

        List<String> lore = new LinkedList<>();
        for (String s : itemMeta.getLore()) {
            Pair<ItemTag, LoreTagValue> pair = itemService.analyzeLore(s, ItemService.TAG_ALL);
            if (pair == null) {
                lore.add(s);
                continue;
            }

            // ${tagid: value1;value2}
            StringBuilder sb = new StringBuilder();
            sb.append(pair.getFirst().getTagId()).append(": ");
            LoreTagValue values = pair.getSecond();
            for (LoreTagTypeValue value : values) {
                sb.append(value.getValueString().replace(";", ";;"))
                        .append(";");
            }
            String str = sb.toString();
            if (str.endsWith(";")) {
                str = str.substring(0, str.length() - 1);
            }
            lore.add(String.format("${%s}", str));
        }

        section.set("lore", lore);
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationSection section, ItemStack itemStack, ItemMeta itemMeta) {
        if (!section.contains("lore")) return null;

        ItemService itemService = NumericalRequirements.getInstance().getItemService();
        List<String> lore = new ArrayList<>();
        for (String s : section.getStringList("lore")) {
            final String old = s;
            s = ChatColor.translateAlternateColorCodes('&', s);
            if (!(s.startsWith("${") && s.endsWith("}"))) {
                lore.add(s);
                continue;
            }

            DebugLogger.debug("反序列化Tag： %s", old);
            s = s.substring(2, s.length() - 1);
            int i = s.indexOf(": ");
            String id = s.substring(0, i);
            ItemTag tag = itemService.getItemTagById(id);
            DebugLogger.debug("Tag %s, id %s", tag.getName(), tag.getTagId());
            List<String> values = Arrays.stream(s.substring(i + 2)
                    .replace(";;", "${&1}")
                    .split(";"))
                    .filter(it -> !it.isEmpty())
                    .map(it -> it.replace("${&1}", ";"))
                    .toList();
            DebugLogger.debug("values: %s", values);
            if (!tag.isValidValues(values)) {
                I18N.warning("item.serialization.lore.invalid-tag-value", old, tag.getTagId(), tag.getName());
                continue;
            }
            String s1 = tag.getPattern().buildLoreByStringList(values);
            DebugLogger.debug("build tag: %s", s1);
            if (s1 == null) {
                I18N.warning("item.serialization.lore.invalid-tag-value", old, tag.getTagId(), tag.getName());
                continue;
            }
            lore.add(s1);
        }
        itemMeta.setLore(lore);
        return null;
    }
}
