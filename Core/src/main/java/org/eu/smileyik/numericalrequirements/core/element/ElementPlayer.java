package org.eu.smileyik.numericalrequirements.core.element;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ElementPlayer {
    static ElementData getElementData(@NotNull NumericalPlayer player, Element element) {
        List<ElementData> registeredValues = player.getRegisteredValues(element, ElementData.class);
        if (registeredValues != null && !registeredValues.isEmpty()) {
            return registeredValues.get(0);
        }
        return null;
    }

    static void registerElement(@NotNull NumericalPlayer player, Element element, ElementData elementData) {
        player.registerData(Element.class, element, elementData);
    }

    static void unregisterElement(@NotNull NumericalPlayer player, Element element) {
        player.unregisterData(element);
    }

    static Map<Element, ElementData> getDisabledElementMap(@NotNull NumericalPlayer player) {
        Map<Element, ElementData> map = new HashMap<>();
        Map<Element, List<ElementData>> disabledDataMap = player.getDisabledDataMap(Element.class, ElementData.class);
        disabledDataMap.forEach((k, v) -> {
            if (v != null && !v.isEmpty()) {
                map.put(k, v.get(0));
            }
        });
        return map;
    }

    static void load(@NotNull NumericalPlayer player, ConfigurationSection section, Element element) {
        load(player, section, element, null);
    }

    static void load(@NotNull NumericalPlayer player,
                     ConfigurationSection section, Element element, ElementData defaultData) {
        player.load(section, Element.class, element, defaultData);
    }
}
