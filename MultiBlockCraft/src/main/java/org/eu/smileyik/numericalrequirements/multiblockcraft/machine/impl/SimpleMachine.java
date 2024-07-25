package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineData;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event.FindRecipeEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event.PreFindRecipeEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.InvItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.ItemButton;
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
        {
            PreFindRecipeEvent event = new PreFindRecipeEvent(this, inputs);
            MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
            if (event.getRecipe() != null) {
                return event.getRecipe();
            }
        }

        if (!shapeRecipes.isEmpty()) {
            Pair<Byte[], ItemStack[]> pair = OrderedRecipe.spawnShape(inputs);
            Set<String> set = shapeRecipes.getOrDefault(HexUtil.bytesToHex(pair.getFirst()), Collections.emptySet());
            for (String id : set) {
                Recipe recipe = recipes.get(id);
                if (((OrderedRecipe) recipe).isMatch(pair)) {
                    FindRecipeEvent event = new FindRecipeEvent(this, recipe);
                    MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) return recipe;
                }
            }
        }

        for (String id : normalRecipes) {
            Recipe recipe = recipes.get(id);
            if (recipe.isMatch(inputs)) {
                FindRecipeEvent event = new FindRecipeEvent(this, recipe);
                MultiBlockCraftExtension.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) return recipe;
            }
        }
        return null;
    }

    public boolean isClickedButton(int slot, Inventory inv, MachineData data) {
        if (slot < 0 || inv.getSize() <= slot) return false;
        ItemStack clickedItem = inv.getItem(slot);
        if (data == null || clickedItem == null || clickedItem.getType() == Material.AIR) return false;
        InvItem invItem = funcItems.get(slot);
        if (invItem instanceof ItemButton) {
            inv.setItem(slot, ((ItemButton) invItem).click(clickedItem, data));
            return true;
        }
        return false;
    }
}
