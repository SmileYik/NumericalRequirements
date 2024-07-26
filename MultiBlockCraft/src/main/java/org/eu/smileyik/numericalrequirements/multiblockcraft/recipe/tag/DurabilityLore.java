package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag;

import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreTag;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;

public class DurabilityLore extends LoreTag {
    @Override
    protected String getModeString() {
        return MultiBlockCraftExtension.getConfig().getString("recipe-tool.tag.lore-tool-durability");
    }

    @Override
    public String getId() {
        return "lore-tool-durability";
    }

    @Override
    public String getName() {
        return I18N.tr("extension.multi-block-craft.tag.tool-durability.name");
    }

    @Override
    public String getDescription() {
        return I18N.tr("extension.multi-block-craft.tag.tool-durability.description");
    }
}
