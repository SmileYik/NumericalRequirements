package org.eu.smileyik.numericalrequirements.multiblockcraft.task;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.Msg;
import org.eu.smileyik.numericalrequirements.core.api.extension.Extension;
import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionTask;
import org.eu.smileyik.numericalrequirements.core.command.DefaultTabSuggest;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.creater.RecipeCreator;

import java.util.ArrayList;
import java.util.List;

public class CreateRecipeTask implements ExtensionTask, DefaultTabSuggest {
    @Override
    public String getId() {
        return "create-machine-recipe";
    }

    @Override
    public String getName() {
        return I18N.tr("extension.multi-block-craft.task.create-recipe.name");
    }

    @Override
    public String getDescription() {
        return I18N.tr("extension.multi-block-craft.task.create-recipe.description");
    }

    @Override
    public Extension getExtension() {
        return MultiBlockCraftExtension.getInstance();
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Msg.trMsg(sender, "extension.multi-block-craft.task.create-recipe.not-player");
            return;
        }
        if (args.length != 1) {
            Msg.trMsg(sender, "extension.multi-block-craft.task.create-recipe.not-valid-args");
            return;
        }
        Machine machine = MultiBlockCraftExtension.getInstance().getMachineService().getMachine(args[0]);
        if (machine == null) {
            Msg.trMsg(sender, "extension.multi-block-craft.task.create-recipe.not-valid-machine-id", args[0]);
            return;
        }

        new RecipeCreator(
                MultiBlockCraftExtension.getInstance(),
                machine, (Player) sender
        ).open();
    }

    @Override
    public boolean matches(String[] args, int commandIdx) {
        return args.length >= 2 && args[args.length - 2].equalsIgnoreCase(getId());
    }

    @Override
    public List<String> suggest() {
        return new ArrayList<>(MultiBlockCraftExtension.getInstance().getMachineService().getMachineIds());
    }
}
