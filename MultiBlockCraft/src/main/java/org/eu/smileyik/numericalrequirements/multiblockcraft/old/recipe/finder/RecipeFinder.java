package org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.finder;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.Recipe;

import java.util.List;

public interface RecipeFinder {
    List<String> getRecipeIds();
    List<Recipe> getRecipe();

    Recipe findRecipe(ItemStack[] itemStacks);
}
