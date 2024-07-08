package org.eu.smileyik.numericalrequirements.core.item.tag.lore.translator;

public class NumberF1Translator extends NumberTranslator {

    @Override
    public String getName() {
        return "numf1";
    }

    @Override
    public String asString(Number value) {
        if (value.doubleValue() == value.intValue()) {
            return String.valueOf(value.intValue());
        }
        return String.format("%.1f", value.doubleValue());
    }
}
