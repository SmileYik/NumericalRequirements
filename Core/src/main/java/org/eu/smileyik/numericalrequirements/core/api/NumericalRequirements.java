package org.eu.smileyik.numericalrequirements.core.api;

import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.core.command.CommandService;
import org.eu.smileyik.numericalrequirements.core.effect.service.EffectService;
import org.eu.smileyik.numericalrequirements.core.element.service.ElementService;
import org.eu.smileyik.numericalrequirements.core.extension.ExtensionService;
import org.eu.smileyik.numericalrequirements.core.extension.placeholderapi.PlaceholderApiExtension;
import org.eu.smileyik.numericalrequirements.core.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.player.service.PlayerService;

public interface NumericalRequirements {
    static NumericalRequirements getInstance() {
        return org.eu.smileyik.numericalrequirements.core.NumericalRequirements.getInstance();
    }

    static Plugin getPlugin() {
        return org.eu.smileyik.numericalrequirements.core.NumericalRequirements.getInstance();
    }

    ElementService getElementService();

    EffectService getEffectService();

    PlayerService getPlayerService();

    ExtensionService getExtensionService();

    ItemService getItemService();

    PlaceholderApiExtension getPlaceholderApiExtension();

    CommandService getCommandService();

    void runTask(Runnable task);
}
