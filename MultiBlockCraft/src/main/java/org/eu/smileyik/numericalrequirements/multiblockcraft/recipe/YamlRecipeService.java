package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.core.api.extension.Extension;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class YamlRecipeService extends RecipeService {
    public YamlRecipeService(Extension extension) {
        super(extension);
    }

    @Override
    public void loadRecipes() {
        File dataFolder = extension.getDataFolder();
        File recipeFile = new File(dataFolder, "recipe.yml");
        if (!recipeFile.exists()) {
            return;
        }
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(recipeFile);
        for (String key : configuration.getKeys(false)) {
            try {
                ConfigurationSection recipeConfig = configuration.getConfigurationSection(key);
                Class<?> aClass = Class.forName(recipeConfig.getString("class"));
                Constructor<?> declaredConstructor = aClass.getDeclaredConstructor(String.class);
                Recipe recipe = (Recipe) declaredConstructor.newInstance(key);
                recipe.load(recipeConfig);
                addRecipe(recipe);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void storeRecipe(Recipe recipe) {
        File dataFolder = extension.getDataFolder();
        File recipeFile = new File(dataFolder, "recipe.yml");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(recipeFile);
        ConfigurationSection section = configuration.createSection(recipe.getName());
        section.set("class", recipe.getClass().getName());
        recipe.store(section);
        try {
            configuration.save(recipeFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
