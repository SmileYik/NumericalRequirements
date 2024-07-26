package org.eu.smileyik.numericalrequirements.multiblockcraft.task;

import org.bukkit.command.CommandSender;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.extension.Extension;
import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionTask;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.RecipeConvertor;

public class ConvertRecipeTask implements ExtensionTask {
    @Override
    public String getId() {
        return "convert-recipe";
    }

    @Override
    public String getName() {
        return I18N.tr("extension.multi-block-craft.task.convert-recipe.name");
    }

    @Override
    public String getDescription() {
        return I18N.tr("extension.multi-block-craft.task.convert-recipe.description");
    }

    @Override
    public Extension getExtension() {
        return MultiBlockCraftExtension.getInstance();
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        RecipeConvertor.convert((MultiBlockCraftExtension) getExtension());
    }
}
