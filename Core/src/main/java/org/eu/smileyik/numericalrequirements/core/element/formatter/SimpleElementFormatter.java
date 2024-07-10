package org.eu.smileyik.numericalrequirements.core.element.formatter;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.api.element.Element;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementFormatter;
import org.eu.smileyik.numericalrequirements.core.api.element.data.BoundedData;
import org.eu.smileyik.numericalrequirements.core.api.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.api.element.data.SingleElementData;

import java.util.Objects;

public class SimpleElementFormatter implements ElementFormatter<Element, ElementData> {
    @Override
    public String format(Element element, ElementData elementData) {
        if (elementData == null) {
            return "";
        }
        if (elementData instanceof SingleElementData) {
            if (elementData instanceof BoundedData) {
                return String.format(
                        "%s / %s",
                        ((SingleElementData<?>) elementData).getValue(),
                        ((BoundedData<?>) elementData).getUpperBound()
                );
            }
            return Objects.toString(((SingleElementData<?>) elementData).getValue());
        }
        return elementData.toString();
    }

    @Override
    public String getId() {
        return "simple";
    }

    @Override
    public void configure(ConfigurationSection section) {

    }
}
