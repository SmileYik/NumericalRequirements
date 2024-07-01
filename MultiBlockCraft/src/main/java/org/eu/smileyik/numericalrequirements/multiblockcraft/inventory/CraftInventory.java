package org.eu.smileyik.numericalrequirements.multiblockcraft.inventory;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.eu.smileyik.numericalrequirements.multiblockcraft.inventory.holder.CraftInventoryHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.finder.RecipeFinder;

import java.util.List;

public interface CraftInventory {
    String getCraftId();
    Class<? extends CraftInventoryHolder> getHolderClass();
    String getInventoryTitle();
    List<String> getAvailableRecipeIds();
    RecipeFinder getRecipeFinder();
    Inventory createInventory();
    Inventory createInventory(Recipe recipe);

    List<Integer> getMaterialSlots();

    List<Integer> getProductSlots();

    void handleInventoryOpen(InventoryOpenEvent event);
    void handleInventoryClose(InventoryCloseEvent event);
    void handleInventoryClick(InventoryClickEvent event);

    void handleInventoryDrag(InventoryDragEvent event);

    void store(ConfigurationSection section);
    void load(ConfigurationSection section);
}
