package org.eu.smileyik.numericalrequirements.core.command.tabsuggests;

import org.bukkit.entity.HumanEntity;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.command.TabSuggest;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerNameSuggest implements TabSuggest {
    private final NumericalRequirements plugin;

    public PlayerNameSuggest(NumericalRequirements plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getKeyword() {
        return "player";
    }

    @Override
    public List<String> suggest() {
        return plugin.getServer()
                .getOnlinePlayers()
                .stream()
                .map(HumanEntity::getName)
                .collect(Collectors.toList());
    }
}
