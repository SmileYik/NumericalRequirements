package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag;

import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreTag;

public class NotConsumableInputLore extends LoreTag {
    @Override
    public String getModeString() {
        return "在合成中不被消耗";
    }

    @Override
    public String getId() {
        return "recipe-not-consumable-lore";
    }

    @Override
    public String getName() {
        return "不被消耗";
    }

    @Override
    public String getDescription() {
        return "不被消耗";
    }
}
