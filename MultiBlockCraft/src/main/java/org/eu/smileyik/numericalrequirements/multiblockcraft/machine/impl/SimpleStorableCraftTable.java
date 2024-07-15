package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class SimpleStorableCraftTable extends SimpleCraftTable {

    public SimpleStorableCraftTable() {
    }

    public SimpleStorableCraftTable(ConfigurationSection section) {
        super(section);
    }

    @Override
    public void open(Player player, String identifier) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }
}
