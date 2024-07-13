package org.eu.smileyik.numericalrequirements.multiblockcraft.old.inventory;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.inventory.holder.CraftInventoryHolder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.finder.RecipeFinder;
import org.eu.smileyik.numericalrequirements.multiblockcraft.old.recipe.finder.SimpleRecipeFinder;

import java.util.List;

public abstract class AbstractCraftInventory implements CraftInventory {
    private final String id;
    private List<String> recipeIds;
    protected Class<? extends CraftInventoryHolder> holderClass;
    private RecipeFinder recipeFinder;


    public AbstractCraftInventory(String id) {
        this.id = id;
    }

    @Override
    public List<String> getAvailableRecipeIds() {
        return recipeIds;
    }

    public void setRecipeIds(List<String> recipeIds) {
        this.recipeIds = recipeIds;
        recipeFinder = new SimpleRecipeFinder(recipeIds);
    }

    @Override
    public Class<? extends CraftInventoryHolder> getHolderClass() {
        return holderClass;
    }

    public void setHolderClass(Class<? extends CraftInventoryHolder> holderClass) {
        this.holderClass = holderClass;
    }

    @Override
    public String getCraftId() {
        return id;
    }

    @Override
    public void store(ConfigurationSection section) {
        section.set("recipe-id", recipeIds);
        section.set("holder", holderClass.getName());
    }

    @Override
    public RecipeFinder getRecipeFinder() {
        return recipeFinder;
    }

    @Override
    public void load(ConfigurationSection section) {
        setRecipeIds(section.getStringList("recipe-id"));
        try {
            holderClass = (Class<? extends CraftInventoryHolder>) Class.forName(section.getString("holder"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
