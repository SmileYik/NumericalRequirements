package org.eu.smileyik.numericalrequirements.core.item.tag.lore.translator;

import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.ValueTranslator;

public class NumberTranslator implements ValueTranslator<Number> {

    @Override
    public String getName() {
        return "num";
    }

    @Override
    public String getRegex() {
        return "(([+-]?[0-9]+[.]?[0-9]+)|([+-]?[.]?[0-9]+)|([+-]?[0-9]+[.]?))";
    }

    @Override
    public Number cast(String str) {
        return Double.parseDouble(str);
    }

    @Override
    public Class<Number> getTargetClass() {
        return Number.class;
    }
}
