package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe;

import org.eu.smileyik.numericalrequirements.core.extension.Extension;

import java.util.HashMap;
import java.util.Map;

public abstract class RecipeService {
    protected final Map<String, Recipe> idRecipeMap = new HashMap<>();
    protected final Extension extension;

    public RecipeService(Extension extension) {
        this.extension = extension;
    }

    public synchronized void addRecipe(Recipe recipe) {
        idRecipeMap.put(recipe.getName(), recipe);
    }

    public synchronized void createRecipe(Recipe recipe) {
        idRecipeMap.put(recipe.getName(), recipe);
        storeRecipe(recipe);
    }

    public synchronized Recipe getRecipe(String id) {
        return idRecipeMap.get(id);
    }

    public abstract void loadRecipes();

    protected abstract void storeRecipe(Recipe recipe);
}
