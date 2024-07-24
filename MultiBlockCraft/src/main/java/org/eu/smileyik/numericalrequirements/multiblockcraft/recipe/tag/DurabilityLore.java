package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag;

import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreTag;

public class DurabilityLore extends LoreTag {
    @Override
    protected String getModeString() {
        return "耐久: <%:int>";
    }

    @Override
    public String getId() {
        return "recipe-durability";
    }

    @Override
    public String getName() {
        return "耐久度";
    }

    @Override
    public String getDescription() {
        return "耐久度";
    }
}
