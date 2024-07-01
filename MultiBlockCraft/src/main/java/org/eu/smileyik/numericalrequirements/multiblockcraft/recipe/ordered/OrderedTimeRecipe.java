package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.ordered;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.TimeRecipe;

import java.util.Map;

public class OrderedTimeRecipe extends OrderedRecipe implements TimeRecipe {
    private double craftTime;

    protected OrderedTimeRecipe(String name) {
        super(name);
    }

    public void setCraftTime(double craftTime) {
        this.craftTime = craftTime;
    }

    @Override
    public double getCraftTime() {
        return craftTime;
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
        craftTime = section.getDouble("craft-time");
    }

    @Override
    public void store(ConfigurationSection section) {
        super.store(section);
        section.set("craft-time", craftTime);
    }
}
