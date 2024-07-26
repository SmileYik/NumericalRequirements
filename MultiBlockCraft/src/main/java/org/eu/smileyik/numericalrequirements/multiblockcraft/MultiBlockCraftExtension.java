package org.eu.smileyik.numericalrequirements.multiblockcraft;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.extension.Extension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.MachineService;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.SimpleMachineService;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.listener.MachineListener;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.tag.MachineLoreTag;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.tag.MachineNBTTag;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.listener.MinecraftRecipeListener;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.listener.RecipeToolListener;
import org.eu.smileyik.numericalrequirements.multiblockcraft.task.ConvertRecipeTask;
import org.eu.smileyik.numericalrequirements.multiblockcraft.task.CreateMachineTask;
import org.eu.smileyik.numericalrequirements.multiblockcraft.task.CreateRecipeTask;

import java.io.File;

public class MultiBlockCraftExtension extends Extension {
    private static MultiBlockCraftExtension instance;
    private static ConfigurationSection config;
    private MachineService machineService;

    @Override
    public void onEnable() {
        loadConfiguration();
        if (!config.getBoolean("enable", true)) {
            getApi().getExtensionService().unregister(this);
            return;
        }

        instance = this;
        setupMinecraftRecipe();
        setupMachineService();
        setupRecipeTool();
        registerTasks();

        I18N.info("multi-block-craft.enabled");
    }

    @Override
    public void onDisable() {
        if (instance == null || machineService == null) return;
        machineService.stop();
    }

    private void loadConfiguration() {
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(file);
    }

    private void registerTasks() {
        CreateRecipeTask createRecipeTask = new CreateRecipeTask();
        getApi().getExtensionService().registerTask(createRecipeTask);
        getApi().getCommandService().registerTabSuggest(createRecipeTask);

        CreateMachineTask createMachineTask = new CreateMachineTask();
        getApi().getExtensionService().registerTask(createMachineTask);
        getApi().getCommandService().registerTabSuggest(createMachineTask);

        getApi().getExtensionService().registerTask(new ConvertRecipeTask());

    }

    private void setupRecipeTool() {
        if (config.getBoolean("recipe-tool.enable", true)) {
            new RecipeToolListener(this);
        }
    }

    private void setupMachineService() {
        machineService = new SimpleMachineService();
        MachineLoreTag machineLoreTag = new MachineLoreTag();
        MachineNBTTag machineNBTTag = new MachineNBTTag();
        getApi().getItemService().registerItemTag(machineNBTTag);
        getApi().getItemService().registerItemTag(machineLoreTag);
        getPlugin().getServer().getPluginManager().registerEvents(new MachineListener(machineLoreTag, machineNBTTag), getPlugin());
        getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(
                getPlugin(), () -> {
                    machineService.load();
                    I18N.info("multi-block-craft.machine-service-enabled");
                }, 60
        );
    }

    private void setupMinecraftRecipe() {
        if (config.getBoolean("enable-minecraft-recipe", true)) {
            new MinecraftRecipeListener(this);
        }
    }

    public static ConfigurationSection getConfig() {
        return config;
    }

    public static MultiBlockCraftExtension getInstance() {
        return instance;
    }

    public MachineService getMachineService() {
        return machineService;
    }
}
