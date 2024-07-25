package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag;

import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreTag;

public class NotConsumableInputLore extends LoreTag {
    @Override
    public String getModeString() {
        return "在合成中不被消耗";
    }

    @Override
    public String getId() {
        return "lore-recipe-not-consumable";
    }

    @Override
    public String getName() {
        return I18N.tr("extension.multi-block-craft.tag.recipe-not-consumable.name");
    }

    @Override
    public String getDescription() {
        return I18N.tr("extension.multi-block-craft.tag.recipe-not-consumable.description");
    }
}
