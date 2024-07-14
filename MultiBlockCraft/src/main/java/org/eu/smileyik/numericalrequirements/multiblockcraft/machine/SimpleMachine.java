package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl.YamlMachine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.OrderedRecipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.util.HexUtil;

import java.util.*;

public abstract class SimpleMachine extends YamlMachine {
    protected Map<String, Recipe> recipes = new HashMap<>();
    protected Map<String, Set<String>> shapeRecipes = new HashMap<>();
    protected List<String> normalRecipes = new ArrayList<>();


    public SimpleMachine() {

    }

    public SimpleMachine(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void addRecipe(Recipe recipe) {
        recipes.put(recipe.getId(), recipe);
        if (recipe instanceof OrderedRecipe) {
            String shapeString = ((OrderedRecipe) recipe).getShapeString();
            shapeRecipes.putIfAbsent(shapeString, new HashSet<>());
            shapeRecipes.get(shapeString).add(recipe.getId());
        } else {
            normalRecipes.add(recipe.getId());
        }
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
        if (!shapeRecipes.isEmpty()) {
            Pair<Byte[], ItemStack[]> pair = OrderedRecipe.spawnShape(inputs);
            Set<String> set = shapeRecipes.getOrDefault(HexUtil.bytesToHex(pair.getFirst()), Collections.emptySet());
            for (String id : set) {
                Recipe recipe = recipes.get(id);
                if (((OrderedRecipe) recipe).isMatch(pair)) {
                    return recipe;
                }
            }
        }

        for (String id : normalRecipes) {
            Recipe recipe = recipes.get(id);
            if (recipe.isMatch(inputs)) {
                return recipe;
            }
        }

        return null;
    }
}
