package org.eu.smileyik.numericalrequirements.core.effect.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.AbstractRegisterInfo;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.effect.AbstractEffectData;
import org.eu.smileyik.numericalrequirements.core.api.effect.Effect;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectData;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerValue;

import java.util.Objects;

public class PotionEffect extends AbstractRegisterInfo implements Effect {

    public PotionEffect() {
        super(
                "PotionEffect",
                I18N.tr("effect.potion-effect.name"),
                "SmileYik",
                "1.0.0",
                I18N.tr("effect.potion-effect.description")
        );
    }

    @Override
    protected void register() {

    }

    @Override
    protected void unregister() {

    }

    @Override
    public EffectData newEffectData() {
        return new PotionEffectData();
    }

    @Override
    public EffectData newEffectData(String[] args) {
        if (args.length != 3) return null;
        if (PotionEffectType.getByName(args[0]) != null) {
            PotionEffectData potionEffectData = new PotionEffectData();
            potionEffectData.setPotionType(args[0]);
            potionEffectData.setDuration(Double.parseDouble(args[1]));
            potionEffectData.setAmplifier(Integer.parseInt(args[2]));
            return potionEffectData;
        }
        return null;
    }

    @Override
    public EffectData newEffectData(ConfigurationSection config) {
        PotionEffectData potionEffectData = new PotionEffectData();
        potionEffectData.load(config);
        return potionEffectData;
    }

    public EffectData newEffectData(String potionType, int duration, int amplifier) {
        PotionEffectData potionEffectData = new PotionEffectData();
        potionEffectData.setPotionType(potionType);
        potionEffectData.setDuration(duration);
        potionEffectData.setAmplifier(amplifier);
        return potionEffectData;
    }

    @Override
    public void handlePlayer(NumericalPlayer player, PlayerValue value) {
        org.bukkit.potion.PotionEffect potionEffect = ((PotionEffectData) value).getPotionEffect();
        if (potionEffect != null) {
            // if (!player.getPlayer().hasPotionEffect(potionEffect.getType())) {
                NumericalRequirements.getInstance().runTask(() -> {
                    player.getPlayer().addPotionEffect(potionEffect, true);
                });
            //}
        }
    }

    public static class PotionEffectData extends AbstractEffectData {
        private String potionType;
        private int amplifier;
        private org.bukkit.potion.PotionEffect potionEffect;

        public int getAmplifier() {
            return amplifier;
        }

        public void setAmplifier(int amplifier) {
            this.amplifier = amplifier;
        }

        public String getPotionType() {
            return potionType;
        }

        public void setPotionType(String potionType) {
            this.potionType = potionType;
        }

        @Override
        public void store(ConfigurationSection section) {
            super.store(section);
            section.set("potion-type", potionType);
            section.set("amplifier", amplifier);
        }

        @Override
        public void load(ConfigurationSection section) {
            super.load(section);
            potionType = section.getString("potion-type");
            amplifier = section.getInt("amplifier");
        }

        public org.bukkit.potion.PotionEffect getPotionEffect() {
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
        public long period() {
            return 980;
        }

        @Override
        public boolean isSimilar(EffectData other) {
            return (other instanceof PotionEffectData) &&
                    Objects.equals(((PotionEffectData) other).potionType, this.potionType);
        }
    }
}
