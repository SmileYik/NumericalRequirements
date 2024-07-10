package org.eu.smileyik.numericalrequirements.core.api.event.player;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;

public class NumericalPlayerLoadEvent extends NumericalPlayerEvent {
    private final ConfigurationSection section;

    public NumericalPlayerLoadEvent(boolean isAsync, NumericalPlayer player, ConfigurationSection section) {
        super(isAsync, player);
        this.section = section;
    }

    public ConfigurationSection getSection() {
        return section;
    }
}
