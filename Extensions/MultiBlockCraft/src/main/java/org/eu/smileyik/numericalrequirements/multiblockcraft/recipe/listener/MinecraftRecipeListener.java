package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.listener;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event.MachineLoadEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.RecipeConvertor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MinecraftRecipeListener implements Listener {
    private final List<Recipe> recipes = new LinkedList<>();

    public MinecraftRecipeListener(MultiBlockCraftExtension extension) {
        File file = new File(extension.getDataFolder(), "minecraft-recipe.yml");
        if (!file.exists()) RecipeConvertor.convert(extension);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.getKeys(false).forEach(it -> {
            ConfigurationSection section = config.getConfigurationSection(it);
            String type = section.getString("type");
            if (type == null) return;
            try {
                Recipe recipe = (Recipe) Class.forName(type).getDeclaredConstructor().newInstance();
                recipe.load(section);
                recipes.add(recipe);
            } catch (Exception e) {
                DebugLogger.debug(e);
            }
        });
        extension.getPlugin().getServer().getPluginManager().registerEvents(this, extension.getPlugin());
    }

    @EventHandler
    public void onMachineLoaded(MachineLoadEvent event) {
        if (event.getConfiguration().getBoolean("attribute.enable-minecraft-recipe", false)) {
            Machine machine = event.getMachine();
            recipes.forEach(machine::addRecipe);
            DebugLogger.debug("enable minecraft recipe for %s", machine.getId());
        }
    }
}
