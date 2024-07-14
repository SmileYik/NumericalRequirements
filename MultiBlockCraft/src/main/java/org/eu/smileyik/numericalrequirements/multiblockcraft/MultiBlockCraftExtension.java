package org.eu.smileyik.numericalrequirements.multiblockcraft;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.api.extension.Extension;
import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionTask;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.MachineService;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.SimpleMachineService;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.listener.MachineListener;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.tag.MachineLoreTag;

public class MultiBlockCraftExtension extends Extension {
    private static MultiBlockCraftExtension instance;

    private MachineService machineService;
    private MachineLoreTag machineLoreTag;

    @Override
    public void onEnable() {
        instance = this;
        machineLoreTag = new MachineLoreTag();
        getApi().getItemService().registerItemTag(machineLoreTag);
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
                return "create-simple-craft-recip";
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
    }

    public static MultiBlockCraftExtension getInstance() {
        return instance;
    }

    public MachineService getMachineService() {
        return machineService;
    }
}
