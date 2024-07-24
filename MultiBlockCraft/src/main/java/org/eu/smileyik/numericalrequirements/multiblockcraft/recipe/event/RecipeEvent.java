package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.event;

import org.eu.smileyik.numericalrequirements.core.event.Event;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

public class RecipeEvent extends Event {
    private Recipe recipe;

    public RecipeEvent(Recipe recipe) {
        this.recipe = recipe;
    }

    public RecipeEvent(boolean isAsync, Recipe recipe) {
        super(isAsync);
        this.recipe = recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
