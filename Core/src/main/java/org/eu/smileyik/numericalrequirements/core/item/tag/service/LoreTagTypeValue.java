package org.eu.smileyik.numericalrequirements.core.item.tag.service;

import org.eu.smileyik.numericalrequirements.core.item.tag.type.LoreTagType;

import java.util.function.BiFunction;

public class LoreTagTypeValue {
    private final LoreTagType<?> type;
    private final String valueString;

    protected LoreTagTypeValue(LoreTagType<?> type, String valueString) {
        this.type = type;
        this.valueString = type.castString(valueString);
    }

    public String getValueString() {
        return valueString;
    }

    public Object getValue() {
        return type.castValue(valueString);
    }

    public LoreTagTypeValue merge(LoreTagTypeValue second, BiFunction<LoreTagTypeValue, LoreTagTypeValue, String> operator) {
        if (type != second.type) {
            return null;
        }
        String apply = operator.apply(this, second);
        return new LoreTagTypeValue(type, apply);
    }

    public LoreTagType<?> getType() {
        return type;
    }

    @Override
    public String toString() {
        return "LoreTagValue{" +
                "type=" + type +
                ", valueString='" + valueString + '\'' +
                '}';
    }
}
