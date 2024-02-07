package org.eu.smileyik.numericalrequirements.core.item;

import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagService;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagValue;
import org.eu.smileyik.numericalrequirements.core.util.Pair;

import java.util.List;
import java.util.Map;

public interface ItemService {
    byte TAG_ALL = 0;
    byte TAG_CONSUME = 1;
    byte TAG_UNCONSUME = 2;

    LoreTagService getLoreTagService();

    void registerItemTag(ItemTag tag);
    ItemTag getItemTagById(String id);

    Map<ItemTag, List<LoreTagValue>> analyzeLoreList(List<String> loreList, byte tagType);
    Pair<ItemTag, LoreTagValue> analyzeLore(String lore, byte tagType);
    boolean matches(ItemTag tag, String lore);

    void shutdown();
}
