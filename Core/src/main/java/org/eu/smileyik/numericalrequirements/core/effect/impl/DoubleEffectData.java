package org.eu.smileyik.numericalrequirements.core.effect.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.effect.AbstractEffectData;

public class DoubleEffectData extends AbstractEffectData {
    private double data;
    private boolean recovered = false;

    public synchronized double getData() {
        return data;
    }

    public synchronized void setData(double data) {
        this.data = data;
    }

    public synchronized void recovery() {
        recovered = true;
    }

    @Override
    public synchronized void load(ConfigurationSection section) {
        super.load(section);
        data = section.getDouble("data");
        recovered = section.getBoolean("recovered", false);
    }

    @Override
    public synchronized void store(ConfigurationSection section) {
        super.store(section);
        section.set("data", data);
        section.set("recovered", recovered);
    }

    @Override
    public boolean canDelete() {
        return recovered;
    }
}
