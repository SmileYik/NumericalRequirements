package org.eu.smileyik.numericalrequirements.core.item.tagold.type.impl;

import org.eu.smileyik.numericalrequirements.core.item.tagold.type.AbstractLoreTagType;

public class NumberTag extends AbstractLoreTagType<Number> {
    public NumberTag() {
        super("num", "(([+-]?[0-9]+[.]?[0-9]+)|([+-]?[.]?[0-9]+)|([+-]?[0-9]+[.]?))");
    }

    @Override
    public Number castValue(String value) {
        return Double.parseDouble(value);
    }

    @Override
    public String castString(Number value) {
        return value.toString();
    }
}
