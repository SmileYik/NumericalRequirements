package org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.finder;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.RecipeService;

import java.util.ArrayList;
import java.util.List;

public class SimpleRecipeFinder implements RecipeFinder {
    private final RecipeService recipeService = MultiBlockCraftExtension.getExtension().getRecipeService();
    private final List<String> recipeIds;
    private final List<Recipe> recipes = new ArrayList<>();

    public SimpleRecipeFinder(List<String> recipeIds) {
        this.recipeIds = recipeIds;

        recipeIds.forEach(it -> {
            Recipe recipe = recipeService.getRecipe(it);
            recipes.add(recipe);
        });
    }

    @Override
    public List<String> getRecipeIds() {
        return recipeIds;
    }

    @Override
    public List<Recipe> getRecipe() {
        return recipes;
    }

    @Override
    public Recipe findRecipe(ItemStack[] itemStacks) {
        for (Recipe recipe : recipes) {
            if (recipe.isMatch(itemStacks)) {
                return recipe;
            }
        }
        return null;
    }
}
