package org.eu.smileyik.numericalrequirements.core.element.formatter;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.element.Element;
import org.eu.smileyik.numericalrequirements.core.element.data.singlenumber.DoubleElementBar;

public class ProcessBarFormatter implements ElementFormatter<Element, DoubleElementBar> {
    private String fill = "§3-";
    private String empty = "§e-";
    private int length = 10;


    @Override
    public String format(Element element, DoubleElementBar data) {
        double rate = data.getValue() / data.getUpperBound();
        int normalLength = (int) Math.round(length * rate);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < normalLength; i++) {
            sb.append(fill);
        }
        for (int i = length - normalLength; i > 0; i--) {
            sb.append(empty);
        }
        return sb.toString();
    }

    @Override
    public String getId() {
        return "process-bar";
    }

    @Override
    public void configure(ConfigurationSection section) {
        fill = ChatColor.translateAlternateColorCodes('&', section.getString("fill"));
        empty = ChatColor.translateAlternateColorCodes('&', section.getString("empty"));
        length = section.getInt("length", length);
    }
}
