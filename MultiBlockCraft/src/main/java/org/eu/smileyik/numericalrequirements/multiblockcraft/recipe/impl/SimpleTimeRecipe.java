package org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.TimeRecipe;

public class SimpleTimeRecipe extends SimpleRecipe implements TimeRecipe {
    private double time;

    @Override
    public double getTime() {
        return time;
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
        this.time = section.getDouble("time");
    }

    @Override
    public void store(ConfigurationSection section) {
        super.store(section);
        section.set("time", time);
    }
}
