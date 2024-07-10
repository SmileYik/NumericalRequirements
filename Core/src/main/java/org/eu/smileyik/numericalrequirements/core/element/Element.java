package org.eu.smileyik.numericalrequirements.core.element;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.RegisterInfo;
import org.eu.smileyik.numericalrequirements.core.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.element.data.singlenumber.DoubleElementBar;
import org.eu.smileyik.numericalrequirements.core.element.data.singlenumber.DoubleElementValue;
import org.eu.smileyik.numericalrequirements.core.element.formatter.ElementFormatter;
import org.eu.smileyik.numericalrequirements.core.extension.placeholderapi.PlaceholderRequestCallback;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataKey;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataValue;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

public interface Element extends RegisterInfo, PlayerDataKey, PlaceholderRequestCallback {
    ElementData newElementData();

    default void storeDataValue(ConfigurationSection section, PlayerDataValue value) {
        ElementData elementData = (ElementData) value;
        elementData.store(section);
    }

    default PlayerDataValue loadDataValue(ConfigurationSection section) {
        ElementData elementData = newElementData();
        elementData.load(section);
        return elementData;
    }

    @Override
    default String onRequest(NumericalPlayer player, String identifier) {
        String id = getId();
        if (!identifier.equalsIgnoreCase(id) && !identifier.startsWith(id + "_")) {
            return null;
        }
        ElementData elementData = ElementPlayer.getElementData(
                player, this
        );
        if (elementData == null) {
            return null;
        }
        String str = identifier.substring(id.length());
        if (str.isEmpty()) {
            return onRequestPlaceholder(elementData);
        } else {
            return onRequestPlaceholder(elementData, str.substring(1));
        }
    }

    boolean isPlaceholder();

    default String onRequestPlaceholder(ElementData data) {
        if (data instanceof DoubleElementValue) {
            return String.format("%.2f", ((DoubleElementValue) data).getValue());
        }
        return null;
    }

    default String onRequestPlaceholder(ElementData data, String args) {
        if (data instanceof DoubleElementValue) {
            DoubleElementValue d = (DoubleElementValue) data;
            if (args.startsWith("format_")) {
                return String.format("%" + args.substring(7) + "f", d.getValue());
            } else if (args.equalsIgnoreCase("rate")) {
                return String.valueOf(d.getRate());
            } else if (args.equalsIgnoreCase("previous")) {
                return String.format("%.2f", d.getPreviousValue());
            } else if (args.startsWith("previous_format_")) {
                return String.format("%" + args.substring(16) + "f", d.getValue());
            } else if (args.equalsIgnoreCase("natural_depletion")) {
                return String.valueOf(d.getNaturalDepletionValue());
            }
        }
        if (data instanceof DoubleElementBar) {
            DoubleElementBar d = (DoubleElementBar) data;
            if (args.equalsIgnoreCase("lower_bound")) {
                return String.valueOf(d.getLowerBound());
            } else if (args.equalsIgnoreCase("upper_bound")) {
                return String.valueOf(d.getUpperBound());
            } else if (args.equalsIgnoreCase("percentage")) {
                return String.format("%.2f", d.getValueOfUpperBound() * 100);
            }
        }
        return null;
    }

    default <K extends Element, V extends ElementData> String toString(ElementFormatter<K, V> formatter, ElementData data) {
        try {
            K k = (K) this;
            V v = (V) data;
            return formatter.format((K) this, (V) data);
        } catch (ClassCastException e) {
            DebugLogger.debug(e);
            return I18N.tr("element.format.formatter-error", formatter.getId());
        }
    }

    default String toString(ElementData data) {
        return toString(ElementFormatter.ELEMENT_FORMATTERS.get("simple"), data);
    }
}
