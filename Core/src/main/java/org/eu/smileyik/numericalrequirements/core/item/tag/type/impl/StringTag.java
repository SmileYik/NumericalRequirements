package org.eu.smileyik.numericalrequirements.core.item.tag.type.impl;

import org.eu.smileyik.numericalrequirements.core.item.tag.type.AbstractLoreTagType;

public class StringTag extends AbstractLoreTagType<String> {
    public StringTag() {
        super("str", "(.+)");
    }

    @Override
    public String castValue(String value) {
        return value;
    }

    @Override
    public String castString(String value) {
        return value;
    }
}
