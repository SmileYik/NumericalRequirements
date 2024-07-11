package org.eu.smileyik.numericalrequirements.core.api.element;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.RegisterInfo;
import org.eu.smileyik.numericalrequirements.core.api.element.data.singlenumber.DoubleElementBar;
import org.eu.smileyik.numericalrequirements.core.api.element.data.singlenumber.DoubleElementValue;
import org.eu.smileyik.numericalrequirements.core.api.extension.placeholder.PlaceholderRequestCallback;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerKey;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerValue;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

public interface Element extends RegisterInfo, PlayerKey, PlaceholderRequestCallback {
    org.eu.smileyik.numericalrequirements.core.api.element.data.Element newElementData();

    default void storeDataValue(ConfigurationSection section, PlayerValue value) {
        org.eu.smileyik.numericalrequirements.core.api.element.data.Element elementData = (org.eu.smileyik.numericalrequirements.core.api.element.data.Element) value;
        elementData.store(section);
    }

    default PlayerValue loadDataValue(ConfigurationSection section) {
        org.eu.smileyik.numericalrequirements.core.api.element.data.Element elementData = newElementData();
        elementData.load(section);
        return elementData;
    }

    @Override
    default String onRequest(NumericalPlayer player, String identifier) {
        String id = getId();
        if (!identifier.equalsIgnoreCase(id) && !identifier.startsWith(id + "_")) {
            return null;
        }
        org.eu.smileyik.numericalrequirements.core.api.element.data.Element elementData = ElementPlayer.getElementData(
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

    default String onRequestPlaceholder(org.eu.smileyik.numericalrequirements.core.api.element.data.Element data) {
        if (data instanceof DoubleElementValue) {
            return String.format("%.2f", ((DoubleElementValue) data).getValue());
        }
        return null;
    }

    default String onRequestPlaceholder(org.eu.smileyik.numericalrequirements.core.api.element.data.Element data, String args) {
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

    default <K extends Element, V extends org.eu.smileyik.numericalrequirements.core.api.element.data.Element> String toString(ElementFormatter<K, V> formatter, org.eu.smileyik.numericalrequirements.core.api.element.data.Element data) {
        try {
            K k = (K) this;
            V v = (V) data;
            return formatter.format((K) this, (V) data);
        } catch (ClassCastException e) {
            DebugLogger.debug(e);
            return I18N.tr("element.format.formatter-error", formatter.getId(), getName(), getId());
        }
    }

    default String toString(org.eu.smileyik.numericalrequirements.core.api.element.data.Element data) {
        return toString(ElementFormatter.ELEMENT_FORMATTERS.get("simple"), data);
    }
}
