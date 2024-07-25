package org.eu.smileyik.numericalrequirements.multiblockcraft.task;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.Msg;
import org.eu.smileyik.numericalrequirements.core.api.extension.Extension;
import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionTask;
import org.eu.smileyik.numericalrequirements.core.command.DefaultTabSuggest;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.creater.MachineCreator;

import java.util.Collections;
import java.util.List;

public class CreateMachineTask implements ExtensionTask, DefaultTabSuggest {
    @Override
    public String getId() {
        return "create-machine";
    }

    @Override
    public String getName() {
        return I18N.tr("extension.multi-block-craft.task.create-machine.name");
    }

    @Override
    public String getDescription() {
        return I18N.tr("extension.multi-block-craft.task.create-machine.description");
    }

    @Override
    public Extension getExtension() {
        return MultiBlockCraftExtension.getInstance();
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Msg.trMsg(sender, "extension.multi-block-craft.task.create-machine.not-player");
            return;
        }
        if (args.length != 1) {
            Msg.trMsg(sender, "extension.multi-block-craft.task.create-machine.not-valid-args");
            return;
        }
        int size = 0;
        try {
            size = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            Msg.trMsg(sender, "extension.multi-block-craft.task.create-machine.not-number", args[0]);
            return;
        }
        if (size <= 0 || size > 9 * 6 || size % 9 != 0) {
            Msg.trMsg(sender, "extension.multi-block-craft.task.create-machine.not-valid-number", size);
            return;
        }
        new MachineCreator(MultiBlockCraftExtension.getInstance(), (Player) sender, size).start();
    }

    @Override
    public boolean matches(String[] args, int commandIdx) {
        return args.length >= 2 && args[args.length - 2].equalsIgnoreCase(getId());
    }

    @Override
    public List<String> suggest() {
        return Collections.singletonList(I18N.tr("extension.multi-block-craft.task.create-machine.tab-suggest"));
    }
}
