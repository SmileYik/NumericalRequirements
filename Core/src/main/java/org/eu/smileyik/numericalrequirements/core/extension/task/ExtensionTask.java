package org.eu.smileyik.numericalrequirements.core.extension.task;

import org.bukkit.command.CommandSender;
import org.eu.smileyik.numericalrequirements.core.extension.Extension;

public interface ExtensionTask {
    String getId();

    String getName();

    String getDescription();

    Extension getExtension();

    void run(CommandSender sender, String[] args);
}
