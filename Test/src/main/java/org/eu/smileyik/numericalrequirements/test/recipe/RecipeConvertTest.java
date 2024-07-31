package org.eu.smileyik.numericalrequirements.test.recipe;

import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.RecipeConvertor;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.SimpleRecipe;
import org.eu.smileyik.numericalrequirements.test.NeedTest;

import java.util.Iterator;

@NeedTest
public class RecipeConvertTest {
    @NeedTest
    public void convert() {
        Iterator<Recipe> recipeIterator = NumericalRequirements.getPlugin().getServer().recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            if (recipe instanceof ShapelessRecipe) {
                SimpleRecipe simpleRecipe = RecipeConvertor.convertRecipe((ShapelessRecipe) recipe);
            } else if (recipe instanceof ShapedRecipe) {
                RecipeConvertor.convertRecipe((ShapedRecipe) recipe);
            }
        }
    }
}
