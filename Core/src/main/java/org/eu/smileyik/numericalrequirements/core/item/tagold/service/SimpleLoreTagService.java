package org.eu.smileyik.numericalrequirements.core.item.tagold.service;

import org.eu.smileyik.numericalrequirements.core.item.tagold.type.LoreTagType;
import org.eu.smileyik.numericalrequirements.core.item.tagold.type.impl.IntTag;
import org.eu.smileyik.numericalrequirements.core.item.tagold.type.impl.NumberF1Type;
import org.eu.smileyik.numericalrequirements.core.item.tagold.type.impl.NumberTag;
import org.eu.smileyik.numericalrequirements.core.item.tagold.type.impl.StringTag;

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
    public void registerLoreTagType(LoreTagType<?> type) {
        tagTypeMap.put(type.getTypeName(), type);
    }

    @Override
    public LoreTagType<?> getLoreTagType(String typeName) {
        return tagTypeMap.get(typeName);
    }

    @Override
    public void shutdown() {
        tagTypeMap.clear();
    }
}
