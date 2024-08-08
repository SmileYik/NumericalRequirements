package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag;

import org.bukkit.ChatColor;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.item.tag.lore.LoreTag;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;

public class NotConsumableInputLore extends LoreTag {
    @Override
    public String getModeString() {
        return ChatColor.translateAlternateColorCodes('&', MultiBlockCraftExtension.getConfig().getString("recipe-tool.tag.lore-recipe-not-consumable"));
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
