package org.eu.smileyik.numericalrequirements.core.command.tabsuggests;

import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.command.TabSuggest;

import java.util.List;

public class ElementTagSuggest implements TabSuggest {
    @Override
    public String getKeyword() {
        return "tag";
    }

    @Override
    public List<String> suggest() {
        return NumericalRequirements.getInstance().getItemService().getTagIds(ItemService.TAG_TYPE_LORE);
    }
}
