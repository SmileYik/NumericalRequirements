package org.eu.smileyik.numericalrequirements.core.item.tag.service;

import org.eu.smileyik.numericalrequirements.core.item.tag.type.impl.IntTag;
import org.eu.smileyik.numericalrequirements.core.item.tag.type.impl.NumberF1Type;
import org.eu.smileyik.numericalrequirements.core.item.tag.type.impl.NumberTag;
import org.eu.smileyik.numericalrequirements.core.item.tag.type.impl.StringTag;
import org.eu.smileyik.numericalrequirements.core.item.tag.type.LoreTagType;

import java.util.HashMap;
import java.util.Map;

public class SimpleLoreTagService implements LoreTagService {
    private final Map<String, LoreTagType<?>> tagTypeMap = new HashMap<>();

    public SimpleLoreTagService() {
        registerLoreTagType(new IntTag());
        registerLoreTagType(new NumberTag());
        registerLoreTagType(new NumberF1Type());
        registerLoreTagType(new StringTag());
    }

    @Override
    public synchronized void registerLoreTagType(LoreTagType<?> type) {
        tagTypeMap.put(type.getTypeName(), type);
    }

    @Override
    public synchronized LoreTagType<?> getLoreTagType(String typeName) {
        return tagTypeMap.get(typeName);
    }

    @Override
    public void shutdown() {
        tagTypeMap.clear();
    }
}
