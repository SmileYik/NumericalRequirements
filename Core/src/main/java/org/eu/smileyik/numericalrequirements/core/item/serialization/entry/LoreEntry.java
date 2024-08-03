package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializationEntry;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreTag;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreValue;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.ValueTranslator;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LoreEntry implements ItemSerializationEntry {
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
    public void serialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
        if (!itemMeta.hasLore()) return;
        ItemService itemService = NumericalRequirements.getInstance().getItemService();

        List<String> lore = new LinkedList<>();
        for (String s : itemMeta.getLore()) {
            Pair<LoreTag, LoreValue> pair = itemService.analyzeLore(s, (byte) (ItemService.TAG_TYPE_MASK | ItemService.TAG_TYPE_LORE));
            if (pair == null) {
                lore.add(s);
                continue;
            }

            // ${tagid: value1;value2}
            StringBuilder sb = new StringBuilder();
            sb.append(pair.getFirst().getId()).append(": ");
            LoreValue values = pair.getSecond();
            for (Pair<ValueTranslator<?>, String> value : values) {
                sb.append(value.getSecond().replace(";", ";;")).append(";");
            }
            String str = sb.toString();
            if (str.endsWith(";")) {
                str = str.substring(0, str.length() - 1);
            }
            lore.add(String.format("${%s}", str));
        }

        if (!lore.isEmpty()) section.put("lore", lore);
    }

    @Override
    public ItemStack deserialize(Handler handler, ConfigurationHashMap section, ItemStack itemStack, ItemMeta itemMeta) {
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
            LoreTag tag = (LoreTag) itemService.getItemTagById(id);
            if (tag == null) {
                lore.add(s);
                I18N.warning("item.serialization.lore.not-find-tag", id, s);
                continue;
            }
            DebugLogger.debug("Tag %s, id %s", tag.getName(), tag.getId());
            List<String> values = Arrays.stream(s.substring(i + 2)
                    .replace(";;", "${&1}")
                    .split(";"))
                    .filter(it -> !it.isEmpty())
                    .map(it -> it.replace("${&1}", ";"))
                    .collect(Collectors.toList());
            DebugLogger.debug("values: %s", values);
            if (!tag.isValidValues(values)) {
                I18N.warning("item.serialization.lore.invalid-tag-value", old, tag.getId(), tag.getName());
                continue;
            }
            String s1 = tag.buildLore(values);
            DebugLogger.debug("build tag: %s", s1);
            if (s1 == null) {
                I18N.warning("item.serialization.lore.invalid-tag-value", old, tag.getId(), tag.getName());
                continue;
            }
            lore.add(s1);
        }
        itemMeta.setLore(lore);
        return null;
    }
}
