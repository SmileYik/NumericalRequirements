package org.eu.smileyik.numericalrequirements.core.element.formatter;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.element.Element;
import org.eu.smileyik.numericalrequirements.core.element.data.singlenumber.DoubleElementBar;

public class PercentageFormatter implements ElementFormatter <Element, DoubleElementBar> {
    @Override
    public String format(Element element, DoubleElementBar data) {
        double rate = data.getValue() / data.getUpperBound();
        return String.format("%.2f", rate * 100);
    }

    @Override
    public String getId() {
        return "percent";
    }

    @Override
    public void configure(ConfigurationSection section) {

    }
}
