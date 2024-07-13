package org.eu.smileyik.numericalrequirements.multiblockcraft.old.inventory.holder;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.inventory.CraftInventory;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.Recipe;

import java.util.Objects;

public class SimpleCraftHolder implements CraftInventoryHolder {
    private Inventory inventory;
    private CraftInventory craftInventory;

    private ItemStack[] materials;
    private Recipe recipe;
    private boolean crafted = false;

    public void setCraftInventory(CraftInventory craftInventory) {
        this.craftInventory = craftInventory;
        materials = new ItemStack[this.craftInventory.getMaterialSlots().size()];
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public CraftInventory getCraftInventory() {
        return craftInventory;
    }

    @Override
    public boolean isCrafted() {
        return crafted;
    }

    @Override
    public void setCrafted(boolean crafted) {
        this.crafted = crafted;
        if (isCrafted()) {
            recipe.takeMaterials(materials);
        }
    }

    @Override
    public void setMaterials(ItemStack[] materials) {
        this.materials = materials;
    }

    @Override
    public ItemStack[] getMaterials() {
        return materials;
    }

    @Override
    public Recipe getRecipe() {
        return recipe;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public boolean takeItems() {
        if (recipe == null)  return false;
        ItemStack[] materials = new ItemStack[getCraftInventory().getMaterialSlots().size()];
        for (int i = 0; i < materials.length; ++i) {
            materials[i] = inventory.getItem(getCraftInventory().getMaterialSlots().get(i));
        }
        return recipe.takeMaterials(materials);
    }

    @Override
    public void reset(Player player) {
        this.materials = new ItemStack[this.craftInventory.getMaterialSlots().size()];
        recipe = null;
        crafted = false;
    }

    @Override
    public boolean findRecipe(int clickingSlot, ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() == Material.AIR) {
            itemStack = null;
        }
        boolean replace = false;
        int idx = 0;
        for (Integer slot : craftInventory.getMaterialSlots()) {
            ItemStack item = inventory.getItem(slot);
            if (clickingSlot == slot) {
                if (item != null && itemStack != null && item.isSimilar(itemStack)) {
                    item = item.clone();
                    item.setAmount(item.getAmount() + itemStack.getAmount());
                } else {
                    item = itemStack;
                }
            }

            if (!Objects.equals(materials[idx], item)) {
                replace = true;
            }
            if (replace) {
                materials[idx] = item;
            }
            idx++;
        }
        if (!replace) {
            return this.recipe != null;
        }

        Recipe recipe = craftInventory.getRecipeFinder().findRecipe(materials);
        if (recipe == null) {
            return false;
        }
        this.recipe = recipe;
        return true;
    }
}
