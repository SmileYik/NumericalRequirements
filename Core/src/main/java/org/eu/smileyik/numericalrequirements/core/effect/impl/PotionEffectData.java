package org.eu.smileyik.numericalrequirements.core.effect.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.eu.smileyik.numericalrequirements.core.AbstractUpdatable;
import org.eu.smileyik.numericalrequirements.core.effect.AbstractEffectData;
import org.eu.smileyik.numericalrequirements.core.effect.EffectData;

import java.util.Objects;

public class PotionEffectData extends AbstractEffectData {
    private String potionType;
    private int amplifier;
    private PotionEffect potionEffect;
    private int times = 0;

    public synchronized int getAmplifier() {
        return amplifier;
    }

    public synchronized void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    public synchronized String getPotionType() {
        return potionType;
    }

    public synchronized void setPotionType(String potionType) {
        this.potionType = potionType;
    }

    @Override
    public synchronized void store(ConfigurationSection section) {
        super.store(section);
        section.set("potion-type", potionType);
        section.set("amplifier", amplifier);
    }

    @Override
    public synchronized void load(ConfigurationSection section) {
        super.load(section);
        potionType = section.getString("potion-type");
        amplifier = section.getInt("amplifier");
    }

    public synchronized PotionEffect getPotionEffect() {
        if (potionEffect == null) {
            if (potionType != null) {
                PotionEffectType type = PotionEffectType.getByName(potionType);
                if (type != null) {
                    potionEffect = type.createEffect(100, amplifier);
                }
            }
        }
        return potionEffect;
    }

    @Override
    protected boolean doUpdate(double second) {
        super.doUpdate(second);
        return times++ % 4 == 0;
    }

    @Override
    public long period() {
        return 1000;
    }

    @Override
    public boolean isSimilar(EffectData other) {
        return (other instanceof PotionEffectData) &&
                Objects.equals(((PotionEffectData) other).potionType, this.potionType);
    }
}
