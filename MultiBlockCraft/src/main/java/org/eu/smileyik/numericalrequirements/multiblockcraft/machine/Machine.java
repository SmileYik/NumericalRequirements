package org.eu.smileyik.numericalrequirements.multiblockcraft.machine;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.item.InvItem;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Machine {
    String getId();
    String getName();
    String getTitle();

    default void open(Player player, String identifier) {

    }

    default void createRecipe(Player player) {

    }

    default Inventory createGui() {
        return null;
    }

    List<Integer> getInputSlots();
    List<Integer> getOutputSlots();
    List<Integer> getEmptySlots();
    ItemStack getMachineItem();

    Collection<Recipe> getRecipes();
    void addRecipe(Recipe recipe);
    Recipe findRecipe(String id);
    Recipe findRecipe(ItemStack[] inputs);

    default void onOpen(InventoryOpenEvent event) {

    }

    default void onClick(InventoryClickEvent event) {

    }

    default void onDrag(InventoryDragEvent event) {

    }

    default void onClose(InventoryCloseEvent event) {

    }

    static String getIdentifier(Location location) {
        return String.format(
                "%s;%d;%d;%d",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    static Location fromIdentifier(String identifier) {
        String[] split = identifier.split(";");
        return new Location(
                MultiBlockCraftExtension.getInstance().getPlugin().getServer().getWorld(split[0]),
                Integer.parseInt(split[1]),
                Integer.parseInt(split[2]),
                Integer.parseInt(split[3])
        );
    }

    Map<Integer, InvItem> getFuncItems();
}
