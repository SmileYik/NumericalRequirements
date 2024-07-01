package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRecipe implements Recipe {
    protected final String name;
    protected ItemStack[] products;

    protected AbstractRecipe(String name) {
        this.name = name;
    }

    public void setProducts(ItemStack[] products) {
        this.products = products;
    }

    @Override
    public ItemStack[] getProducts() {
        return products;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void load(ConfigurationSection section) {
        ConfigurationSection productsSection = section.getConfigurationSection("products");
        List<ItemStack> itemStacks = new ArrayList<>();
        for (String key : productsSection.getKeys(false)) {
            itemStacks.add(productsSection.getItemStack(key));
        }
        setProducts(itemStacks.toArray(new ItemStack[0]));
    }

    @Override
    public void store(ConfigurationSection section) {
        ConfigurationSection productSection = section.createSection("products");
        for (int i = 0; i < products.length; ++i) {
            productSection.set(String.valueOf(i), products[i]);
        }
    }
}
