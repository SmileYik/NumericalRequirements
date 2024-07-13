package org.eu.smileyik.numericalrequirements.multiblockcraft.old.inventory.holder;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.inventory.CraftInventory;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.Recipe;

public interface CraftInventoryHolder extends InventoryHolder {
    CraftInventory getCraftInventory();
    boolean isCrafted();
    ItemStack[] getMaterials();
    Recipe getRecipe();

    boolean takeItems();

    void reset(Player player);

    boolean findRecipe(int slot, ItemStack itemStack);

    void setCrafted(boolean crafted);

    void setMaterials(ItemStack[] itemStacks);
}
