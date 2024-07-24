package org.eu.smileyik.numericalrequirements.multiblockcraft;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.api.extension.Extension;
import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionTask;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.MachineService;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.SimpleMachineService;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.listener.MachineListener;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.tag.MachineLoreTag;
import org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.MultiBlockListener;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.listener.RecipeListener;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag.DurabilityLore;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.tag.NotConsumableInputLore;

public class MultiBlockCraftExtension extends Extension {
    private static MultiBlockCraftExtension instance;

    private MachineService machineService;
    private MachineLoreTag machineLoreTag;
    private DurabilityLore durabilityLore;
    private NotConsumableInputLore notConsumableInputLore;

    @Override
    public void onEnable() {
        instance = this;
        machineLoreTag = new MachineLoreTag();
        durabilityLore = new DurabilityLore();
        notConsumableInputLore = new NotConsumableInputLore();
        getApi().getItemService().registerItemTag(machineLoreTag);
        getApi().getItemService().registerItemTag(durabilityLore);
        getApi().getItemService().registerItemTag(notConsumableInputLore);
        getPlugin().getServer().getPluginManager().registerEvents(new RecipeListener(
                durabilityLore, notConsumableInputLore
        ), getPlugin());

        getApi().getExtensionService().registerTask(new ExtensionTask() {
            @Override
            public String getId() {
                return "create-recipe";
            }

            @Override
            public String getName() {
                return "create-recipe";
            }

            @Override
            public String getDescription() {
                return "create-simple-craft-recipe";
            }

            @Override
            public Extension getExtension() {
                return instance;
            }

            @Override
            public void run(CommandSender sender, String[] args) {
                machineService.getMachine(args[0]).createRecipe((Player) sender);
            }
        });

        machineService = new SimpleMachineService(this);
        getPlugin().getServer().getPluginManager().registerEvents(new MachineListener(machineLoreTag), getPlugin());
        getPlugin().getServer().getPluginManager().registerEvents(new MultiBlockListener(), getPlugin());
    }

    @Override
    public void onDisable() {
        machineService.stop();
    }

    public static MultiBlockCraftExtension getInstance() {
        return instance;
    }

    public MachineService getMachineService() {
        return machineService;
    }
}
