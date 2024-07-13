package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.holder;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.YamlMachine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class SimpleMachine extends YamlMachine {
    protected Map<String, Recipe> recipes = new HashMap<>();

    public SimpleMachine() {

    }

    public SimpleMachine(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void addRecipe(Recipe recipe) {
        recipes.put(recipe.getId(), recipe);
    }

    @Override
    public Recipe findRecipe(String id) {
        return recipes.get(id);
    }

    @Override
    public Collection<Recipe> getRecipes() {
        return recipes.values();
    }

    @Override
    public Recipe findRecipe(ItemStack[] inputs) {
        for (Map.Entry<String, Recipe> entry : recipes.entrySet()) {
            if (entry.getValue().isMatch(inputs)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
