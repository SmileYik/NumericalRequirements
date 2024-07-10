package org.eu.smileyik.numericalrequirements.core.element.formatter;

import org.eu.smileyik.numericalrequirements.core.api.element.Element;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementFormatter;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.api.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.api.extension.placeholder.PlaceholderRequestCallback;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;

public class ElementFormatterPlaceholderCallback implements PlaceholderRequestCallback {
    @Override
    public String onRequest(NumericalPlayer player, String identifier) {
        String[] split = identifier.split("_");
        if (split.length != 2) return null;
        ElementFormatter<?, ?> elementFormatter = ElementFormatter.ELEMENT_FORMATTERS.get(split[0]);
        if (elementFormatter == null) return null;
        Pair<Element, ElementData> elementData = ElementPlayer.getElementData(player, split[1]);
        if (elementData == null) return null;
        return elementData.getFirst().toString(elementFormatter, elementData.getSecond());
    }
}
