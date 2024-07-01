package org.eu.smileyik.numericalrequirements.multiblockcraft.inventory.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;

public class SimpleCraftInventoryListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        MultiBlockCraftExtension.getExtension().getSimpleCraftInventory().handleInventoryOpen(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        MultiBlockCraftExtension.getExtension().getSimpleCraftInventory().handleInventoryClick(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        MultiBlockCraftExtension.getExtension().getSimpleCraftInventory().handleInventoryClose(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        MultiBlockCraftExtension.getExtension().getSimpleCraftInventory().handleInventoryDrag(event);
    }
}
