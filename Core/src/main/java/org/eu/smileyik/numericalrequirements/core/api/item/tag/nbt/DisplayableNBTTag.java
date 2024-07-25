package org.eu.smileyik.numericalrequirements.core.api.item.tag.nbt;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LorePattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DisplayableNBTTag <V> extends NBTTag<V> {
    private final LorePattern pattern;

    protected DisplayableNBTTag(String modeString) {
        this.pattern = LorePattern.compile(modeString);
    }

    public abstract void setValue(ItemStack itemStack, V value);

    public void clearDisplayLore(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) return;
        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasLore()) return;
        List<String> lore = meta.getLore();
        for (int i = lore.size() - 1; i >= 0; i--) {
            String line = lore.get(i);
            if (pattern.matches(line)) {
                lore.remove(i);
                break;
            }
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }

    public void refreshDisplayLore(ItemStack itemStack) {
        if (itemStack == null) return;
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        boolean notFound = true;
        for (int i = lore.size() - 1; i >= 0; i--) {
            String line = lore.get(i);
            if (pattern.matches(line)) {
                notFound = false;
                lore.set(i, pattern.buildLoreByStringList(Collections.singletonList(getValue(itemStack).toString())));
                break;
            }
        }
        if (notFound) {
            lore.add(pattern.buildLoreByStringList(Collections.singletonList(getValue(itemStack).toString())));
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }
}
