package org.eu.smileyik.numericalrequirements.core.item.tagold.type.impl;

import org.eu.smileyik.numericalrequirements.core.item.tagold.type.AbstractLoreTagType;

public class IntTag extends AbstractLoreTagType<Integer> {
    public IntTag() {
        super("int", "([+-]?[0-9]+)");
    }

    @Override
    public Integer castValue(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public String castString(Integer value) {
        return value.toString();
    }
}
