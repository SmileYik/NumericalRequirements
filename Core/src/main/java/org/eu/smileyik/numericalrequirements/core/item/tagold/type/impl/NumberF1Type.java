package org.eu.smileyik.numericalrequirements.core.item.tagold.type.impl;

import org.eu.smileyik.numericalrequirements.core.item.tagold.type.AbstractLoreTagType;

public class NumberF1Type extends AbstractLoreTagType<Number> {
    public NumberF1Type() {
        super("numf1", "(([+-]?[0-9]+[.]?[0-9]+)|([+-]?[.]?[0-9]+)|([+-]?[0-9]+[.]?))");
    }

    @Override
    public Number castValue(String value) {
        return Double.parseDouble(value);
    }

    @Override
    public String castString(Number value) {
        if (value.doubleValue() == value.intValue()) {
            return String.valueOf(value.intValue());
        }
        return String.format("%.1f", value.doubleValue());
    }
}
