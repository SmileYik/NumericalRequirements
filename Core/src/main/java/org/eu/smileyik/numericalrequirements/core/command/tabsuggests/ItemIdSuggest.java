package org.eu.smileyik.numericalrequirements.core.command.tabsuggests;

import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.command.TabSuggest;

import java.util.ArrayList;
import java.util.List;

public class ItemIdSuggest implements TabSuggest {
    private final NumericalRequirements plugin;

    public ItemIdSuggest(NumericalRequirements plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getKeyword() {
        return "item-id";
    }

    @Override
    public List<String> suggest() {
        return new ArrayList<>(plugin.getItemService().getItemKeeper().getItemIds());
    }
}
