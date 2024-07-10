package org.eu.smileyik.numericalrequirements.thirst.listener;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectPlayer;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.thirst.ThirstData;
import org.eu.smileyik.numericalrequirements.thirst.ThirstElement;

public class DeathPunishment implements Listener {
    private final ConfigurationSection config;
    private final ThirstElement thirstElement;

    public DeathPunishment(ConfigurationSection config, ThirstElement thirstElement) {
        this.config = config;
        this.thirstElement = thirstElement;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
//        String worldName = e.getRespawnLocation().getWorld().getName();
//        if (!NumericalRequirements.getInstance().isAvailableWorld(worldName)) {
//            return;
//        }

        NumericalPlayer numericalPlayer =
                NumericalRequirements.getInstance().getPlayerService().getNumericalPlayer(e.getPlayer());

        if (numericalPlayer == null) return;
        ThirstData elementData = (ThirstData) ElementPlayer.getElementData(numericalPlayer, thirstElement);
        assert elementData != null;
        elementData.setValue(config.getDouble("value") / 100D * elementData.getUpperBound());

        ConfigurationSection ec = config.getConfigurationSection("effect");
        for (String key : ec.getKeys(false)) {
            ConfigurationSection effect = ec.getConfigurationSection(key);
            EffectPlayer.registerEffectBundle(
                    numericalPlayer, effect.getString("bundle"),
                    effect.getDouble("duration"), EffectPlayer.MERGE_NONE);
        }
    }
}
