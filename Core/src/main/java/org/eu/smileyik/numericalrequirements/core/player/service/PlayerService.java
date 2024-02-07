package org.eu.smileyik.numericalrequirements.core.player.service;

import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.Updatable;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataKey;

public interface PlayerService extends Updatable {
    void loadOnlinePlayers();

    NumericalPlayer getNumericalPlayer(Player player);

    boolean isPlayerJoin(Player player);

    NumericalPlayer joinPlayer(Player p);

    NumericalPlayer quitPlayer(Player p);

    void savePlayerData();

    void removeDisabledKey();

    void shutdown();
}
