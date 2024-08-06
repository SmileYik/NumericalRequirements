package org.eu.smileyik.numericalrequirements.core.api;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectService;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementService;
import org.eu.smileyik.numericalrequirements.core.api.extension.ExtensionService;
import org.eu.smileyik.numericalrequirements.core.api.item.ItemService;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerService;
import org.eu.smileyik.numericalrequirements.core.command.CommandService;
import org.eu.smileyik.numericalrequirements.core.customblock.CustomBlockService;
import org.eu.smileyik.numericalrequirements.core.extension.placeholderapi.PlaceholderApiExtension;
import org.eu.smileyik.numericalrequirements.core.network.NetworkService;

public interface NumericalRequirements {
    static NumericalRequirements getInstance() {
        return org.eu.smileyik.numericalrequirements.core.NumericalRequirements.getInstance().getManager();
    }

    static Plugin getPlugin() {
        return org.eu.smileyik.numericalrequirements.core.NumericalRequirements.getInstance();
    }

    static boolean isAvailableWorld(Player player) {
        return getInstance().isAvailableWorld(player.getWorld().getName());
    }

    ElementService getElementService();

    EffectService getEffectService();

    PlayerService getPlayerService();

    ExtensionService getExtensionService();

    ItemService getItemService();

    PlaceholderApiExtension getPlaceholderApiExtension();

    CommandService getCommandService();

    NetworkService getNetworkService();

    CustomBlockService getCustomBlockService();

    void runTask(Runnable task);

    boolean isAvailableWorld(String worldName);
}
