package org.eu.smileyik.numericalrequirements.core.event.network;

import org.bukkit.entity.Player;

public class PlayerNetworkEvent extends NetworkEvent {
    private final Player player;

    public PlayerNetworkEvent(Player player) {
        super();
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
