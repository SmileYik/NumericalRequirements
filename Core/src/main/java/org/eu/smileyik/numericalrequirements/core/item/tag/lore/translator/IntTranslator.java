package org.eu.smileyik.numericalrequirements.core.item.tag.lore.translator;

import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.ValueTranslator;

public class IntTranslator implements ValueTranslator<Integer> {

    @Override
    public String getName() {
        return "int";
    }

    @Override
    public String getRegex() {
        return "([+-]?[0-9]+)";
    }

    @Override
    public Integer cast(String str) {
        return Integer.parseInt(str);
    }

    @Override
    public Class<Integer> getTargetClass() {
        return Integer.class;
    }
}
