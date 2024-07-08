package org.eu.smileyik.numericalrequirements.core.item;

import org.eu.smileyik.numericalrequirements.core.item.tagold.service.LoreTagPattern;
import org.eu.smileyik.numericalrequirements.core.item.tagold.service.LoreTagTypeValue;

import java.util.List;
import java.util.function.BiFunction;

public abstract class ItemTag implements BiFunction<LoreTagTypeValue, LoreTagTypeValue, String> {
    private final String tagId;
    private final String name;
    private final String description;
    private final LoreTagPattern pattern;

    protected ItemTag(String tagId, String name, String description, LoreTagPattern pattern) {
        this.tagId = tagId;
        this.name = name;
        this.description = description;
        this.pattern = pattern;
    }

    public LoreTagPattern getPattern() {
        return pattern;
    }

    public String getTagId() {
        return tagId;
    }

    public abstract boolean canMerge();

    public boolean isValidValues(List<String> values) {
        int size = pattern.getValueSize();
        if (values.size() != size) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (!pattern.getValueTypes().get(i).matches(values.get(i))) {
                return false;
            }
        }
        return true;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
