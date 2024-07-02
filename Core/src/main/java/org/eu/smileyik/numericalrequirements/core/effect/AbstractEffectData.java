package org.eu.smileyik.numericalrequirements.core.effect;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.AbstractUpdatable;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataValueUpdatable;

public class AbstractEffectData extends AbstractUpdatable implements EffectData, PlayerDataValueUpdatable {
    private double duration;

    @Override
    public synchronized double getDuration() {
        return duration;
    }

    @Override
    public synchronized void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public synchronized void store(ConfigurationSection section) {
        section.set("duration", duration);
    }

    @Override
    public synchronized void load(ConfigurationSection section) {
        duration = section.getDouble("duration");
    }

    @Override
    protected boolean doUpdate(double second) {
        duration -= second;
        return true;
    }

    @Override
    public synchronized boolean isTimeout() {
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
