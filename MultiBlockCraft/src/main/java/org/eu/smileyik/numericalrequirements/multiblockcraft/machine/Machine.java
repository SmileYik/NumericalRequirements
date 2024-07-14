package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.util.Collection;
import java.util.List;

public interface Machine {
    String getId();
    String getName();
    String getTitle();

    void open(Player player, String identifier);
    void createRecipe(Player player);

    List<Integer> getInputSlots();
    List<Integer> getOutputSlots();
    List<Integer> getEmptySlots();
    ItemStack getMachineItem();

    Collection<Recipe> getRecipes();
    void addRecipe(Recipe recipe);
    Recipe findRecipe(String id);
    Recipe findRecipe(ItemStack[] inputs);

    void onClick(InventoryClickEvent event);
    void onDrag(InventoryDragEvent event);
    void onClose(InventoryCloseEvent event);

    static String getIdentifier(Location location) {
        return String.format(
                "%s;%d;%d;%d",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }
}
