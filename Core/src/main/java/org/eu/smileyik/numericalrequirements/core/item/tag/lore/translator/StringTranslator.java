package org.eu.smileyik.numericalrequirements.core.item.tag.lore.translator;

import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.ValueTranslator;

public class StringTranslator implements ValueTranslator<String> {

    @Override
    public String getName() {
        return "str";
    }

    @Override
    public String getRegex() {
        return "(.+)";
    }

    @Override
    public String cast(String str) {
        return str;
    }

    @Override
    public Class<String> getTargetClass() {
        return String.class;
    }
}
