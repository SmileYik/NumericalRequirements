package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.eu.smileyik.numericalrequirements.core.api.util.YamlUtil;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.SimpleItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.SimpleOrderedRecipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl.SimpleRecipe;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public interface RecipeConvertor {

    static void convert(MultiBlockCraftExtension extension) {
        YamlConfiguration config = new YamlConfiguration();
        int id = 0;
        Iterator<org.bukkit.inventory.Recipe> recipeIterator = extension.getPlugin().getServer().recipeIterator();
        while (recipeIterator.hasNext()) {
            org.bukkit.inventory.Recipe recipe = recipeIterator.next();
            if (recipe instanceof ShapedRecipe) {
                SimpleOrderedRecipe simpleOrderedRecipe = convertRecipe((ShapedRecipe) recipe, 2);
                if (simpleOrderedRecipe != null) simpleOrderedRecipe.store(config.createSection(String.format("recipe-%d", id++)));
                convertRecipe((ShapedRecipe) recipe, 3).store(config.createSection(String.format("recipe-%d", id++)));
                convertRecipe((ShapedRecipe) recipe, 4).store(config.createSection(String.format("recipe-%d", id++)));
                convertRecipe((ShapedRecipe) recipe, 5).store(config.createSection(String.format("recipe-%d", id++)));
                convertRecipe((ShapedRecipe) recipe, 6).store(config.createSection(String.format("recipe-%d", id++)));
            }  else if (recipe instanceof ShapelessRecipe) {
                convertRecipe((ShapelessRecipe) recipe).store(config.createSection(String.format("recipe-%d", id++)));
            }
        }
        try {
            config.save(new File(extension.getDataFolder(), "minecraft-recipe.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    static SimpleRecipe convertRecipe(ShapelessRecipe recipe) {
        ItemStack[] inputs = recipe.getIngredientList().toArray(new ItemStack[0]);
        ItemStack[] outputs = new ItemStack[] { recipe.getResult() };
        ConfigurationSection configurationSection = convertRecipe(inputs, outputs, recipe.getKey().toString(), recipe.getKey().toString());
        DebugLogger.debug((e) -> {
            DebugLogger.debug(e, "convert simple recipe from %s: \n%s", recipe.getKey(), YamlUtil.saveToString(configurationSection));
        });
        SimpleRecipe simpleRecipe = new SimpleRecipe();
        simpleRecipe.load(configurationSection);
        return simpleRecipe;
    }

    static SimpleOrderedRecipe convertRecipe(ShapedRecipe recipe) {
        return convertRecipe(recipe, 3);
    }

    static SimpleOrderedRecipe convertRecipe(ShapedRecipe recipe, int length) {
        String[] shape = recipe.getShape();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < shape.length; i++) {
            if (shape[i].length() > length) return null;
            while (shape[i].length() != length) {
                shape[i] += " ";
            }
            sb.append(shape[i]);
        }
        char[] charArray = sb.toString().toCharArray();
        Map<Character, ItemStack> ingredientMap = recipe.getIngredientMap();
        ItemStack[] inputs = new ItemStack[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == ' ') continue;
            inputs[i] = ingredientMap.get(charArray[i]);
        }

        String name = String.format("%s:%dx%d", recipe.getKey(), length, length);
        ConfigurationSection configurationSection = convertRecipe(inputs, new ItemStack[]{recipe.getResult()}, name, name);
        DebugLogger.debug((e) -> {
            DebugLogger.debug(e, "convert simple ordered recipe from %s: \n%s", recipe.getKey(), YamlUtil.saveToString(configurationSection));
        });
        SimpleOrderedRecipe simpleRecipe = new SimpleOrderedRecipe();
        simpleRecipe.load(configurationSection);
        return simpleRecipe;
    }

    static ConfigurationSection convertRecipe(ItemStack[] inputs, ItemStack[] outputs, String recipeId, String recipeName) {
        SimpleItem[] simpleItemInputs = new SimpleItem[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            simpleItemInputs[i] = new SimpleItem(SimpleItem.TYPE_BUKKIT, inputs[i]);
        }
        SimpleItem[] simpleItemOutputs = new SimpleItem[outputs.length];
        for (int i = 0; i < outputs.length; i++) {
            simpleItemOutputs[i] = new SimpleItem(SimpleItem.TYPE_BUKKIT, outputs[i]);
        }

        Recipe recipe = new SimpleRecipe() {
            {
                this.rawInputs = simpleItemInputs;
                this.rawOutputs = simpleItemOutputs;
                this.id = recipeId;
                this.name = recipeName;
            }
        };
        YamlConfiguration configuration = new YamlConfiguration();
        recipe.store(configuration);
        return configuration;
    }
}
