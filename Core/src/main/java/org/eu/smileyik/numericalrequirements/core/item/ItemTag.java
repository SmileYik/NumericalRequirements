package org.eu.smileyik.numericalrequirements.core.item;

import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagPattern;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagTypeValue;
import org.eu.smileyik.numericalrequirements.core.item.tag.service.LoreTagValue;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;

import java.util.ArrayList;
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

    public abstract void handlePlayer(NumericalPlayer player, LoreTagValue value);

    public void handlePlayer(NumericalPlayer player, List<LoreTagValue> valueList) {
        valueList.forEach(it -> {
            handlePlayer(player, it);;
        });
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
