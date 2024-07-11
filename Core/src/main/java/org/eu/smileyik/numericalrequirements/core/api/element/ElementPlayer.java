package org.eu.smileyik.numericalrequirements.core.api.element;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.element.data.Element;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ElementPlayer {

    static Pair<org.eu.smileyik.numericalrequirements.core.api.element.Element, Element> getElementData(@NotNull NumericalPlayer player, String elementID) {
        org.eu.smileyik.numericalrequirements.core.api.element.Element element = NumericalRequirements.getInstance().getElementService().findElementById(elementID);
        if (element == null) {
            return null;
        }
        return Pair.newUnchangablePair(
                element,
                getElementData(player, element)
        );
    }

    static Element getElementData(@NotNull NumericalPlayer player, org.eu.smileyik.numericalrequirements.core.api.element.Element element) {
        List<Element> registeredValues = player.getRegisteredValues(element, Element.class);
        if (registeredValues != null && !registeredValues.isEmpty()) {
            return registeredValues.get(0);
        }
        return null;
    }

    static void registerElement(@NotNull NumericalPlayer player, org.eu.smileyik.numericalrequirements.core.api.element.Element element, Element elementData) {
        player.registerData(org.eu.smileyik.numericalrequirements.core.api.element.Element.class, element, elementData);
    }

    static void unregisterElement(@NotNull NumericalPlayer player, org.eu.smileyik.numericalrequirements.core.api.element.Element element) {
        player.unregisterData(element);
    }

    static Map<org.eu.smileyik.numericalrequirements.core.api.element.Element, Element> getDisabledElementMap(@NotNull NumericalPlayer player) {
        Map<org.eu.smileyik.numericalrequirements.core.api.element.Element, Element> map = new HashMap<>();
        Map<org.eu.smileyik.numericalrequirements.core.api.element.Element, List<Element>> disabledDataMap = player.getDisabledDataMap(org.eu.smileyik.numericalrequirements.core.api.element.Element.class, Element.class);
        disabledDataMap.forEach((k, v) -> {
            if (v != null && !v.isEmpty()) {
                map.put(k, v.get(0));
            }
        });
        return map;
    }

    static void load(@NotNull NumericalPlayer player, ConfigurationSection section, org.eu.smileyik.numericalrequirements.core.api.element.Element element) {
        load(player, section, element, null);
    }

    static void load(@NotNull NumericalPlayer player,
                     ConfigurationSection section, org.eu.smileyik.numericalrequirements.core.api.element.Element element, Element defaultData) {
        player.load(section, org.eu.smileyik.numericalrequirements.core.api.element.Element.class, element, defaultData);
    }
}
