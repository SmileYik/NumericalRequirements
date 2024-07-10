package org.eu.smileyik.numericalrequirements.core.api.extension;

import org.bukkit.command.CommandSender;

public interface ExtensionTask {
    String getId();

    String getName();

    String getDescription();

    Extension getExtension();

    void run(CommandSender sender, String[] args);
}
