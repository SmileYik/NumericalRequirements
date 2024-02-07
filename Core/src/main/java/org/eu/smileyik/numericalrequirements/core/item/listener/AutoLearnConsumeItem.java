package org.eu.smileyik.numericalrequirements.core.item.listener;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AutoLearnConsumeItem implements Listener {
    private final NumericalRequirements plugin;
    private final Set<Material> consumableItems = new HashSet<>();

    public AutoLearnConsumeItem(NumericalRequirements plugin) {
        this.plugin = plugin;
    }

    public void startLearn() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void load(ConfigurationSection section) {
        List<String> stringList = section.getStringList("consumable-items");
        for (String str : stringList) {
            Material material = Material.getMaterial(str);
            consumableItems.add(material);
        }
    }

    public void store(ConfigurationSection section) {
        List<String> list = new ArrayList<>();
        consumableItems.forEach(it -> list.add(it.name()));
        section.set("consumable-items", list);
    }

    public boolean isConsumable(Material material) {
        return consumableItems.contains(material);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemConsuming(PlayerItemConsumeEvent event) {
        consumableItems.add(event.getItem().getType());
    }
}
