package org.eu.smileyik.numericalrequirements.core.element.formatter;

import org.eu.smileyik.numericalrequirements.core.element.Element;
import org.eu.smileyik.numericalrequirements.core.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.extension.placeholderapi.PlaceholderRequestCallback;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.util.Pair;

public class ElementFormatterPlaceholderCallback implements PlaceholderRequestCallback {
    @Override
    public String onRequest(NumericalPlayer player, String identifier) {
        String[] split = identifier.split("_");
        if (split.length != 2) return "";
        ElementFormatter<?, ?> elementFormatter = ElementFormatter.ELEMENT_FORMATTERS.get(split[0]);
        if (elementFormatter == null) return "";
        Pair<Element, ElementData> elementData = ElementPlayer.getElementData(player, split[1]);
        if (elementData == null) return "";
        return elementData.getFirst().toString(elementFormatter, elementData.getSecond());
    }
}
