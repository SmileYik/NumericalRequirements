package org.eu.smileyik.numericalrequirements.core.api.effect;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.api.AbstractUpdatable;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerDataValueUpdatable;

public class AbstractEffectData extends AbstractUpdatable implements EffectData, PlayerDataValueUpdatable {
    private double duration;

    @Override
    public double getDuration() {
        return duration;
    }

    @Override
    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public void store(ConfigurationSection section) {
        section.set("duration", duration);
    }

    @Override
    public void load(ConfigurationSection section) {
        duration = section.getDouble("duration");
    }

    @Override
    protected boolean doUpdate(double second) {
        duration -= second;
        return true;
    }

    @Override
    public boolean isTimeout() {
        return duration <= 0;
    }

    @Override
    public boolean canDelete() {
        return duration <= 0;
    }

    @Override
    public long period() {
        return 0;
    }
}
