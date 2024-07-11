package org.eu.smileyik.numericalrequirements.core.api.player;

import org.bukkit.entity.Player;

public interface PlayerService {
    void loadOnlinePlayers();

    NumericalPlayer getNumericalPlayer(Player player);

    boolean isPlayerJoin(Player player);

    NumericalPlayer joinPlayer(Player p);

    NumericalPlayer quitPlayer(Player p);

    void savePlayerData();

    void removeDisabledKey();

    void shutdown();
}
