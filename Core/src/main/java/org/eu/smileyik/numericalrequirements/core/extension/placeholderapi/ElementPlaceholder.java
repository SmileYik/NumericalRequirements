package org.eu.smileyik.numericalrequirements.core.extension.placeholderapi;

import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.api.element.data.Element;
import org.eu.smileyik.numericalrequirements.core.api.extension.placeholder.PlaceholderRequestCallback;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;

public interface ElementPlaceholder extends PlaceholderRequestCallback {
    static final String PREFIX = "element_";
    static final int PREFIX_LENGTH = PREFIX.length();

    @Override
    default String onRequest(NumericalPlayer player, String identifier) {
        if (!identifier.startsWith(PREFIX)) {
            return null;
        }
        String str = identifier.substring(PREFIX_LENGTH);
        int i = str.indexOf("_");
        String id = i == -1 ? str : str.substring(0, i);
        String args = i == -1 ? "" : str.substring(i + 1);
        org.eu.smileyik.numericalrequirements.core.api.element.Element element = NumericalRequirements
                .getInstance()
                .getElementService()
                .findElementById(id);
        Element elementData = ElementPlayer.getElementData(
                player, element
        );
        if (elementData == null) {
            return null;
        }

        return onRequest(element, elementData, args);
    }

    String onRequest(org.eu.smileyik.numericalrequirements.core.api.element.Element element, Element elementData, String args);
}
