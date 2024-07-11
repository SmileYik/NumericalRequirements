package org.eu.smileyik.numericalrequirements.core.effect.impl.message;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.Msg;
import org.eu.smileyik.numericalrequirements.core.api.effect.AbstractEffect;
import org.eu.smileyik.numericalrequirements.core.api.effect.AbstractEffectData;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectData;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerValue;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerValueOneShot;

public class TitleMessageEffect extends AbstractEffect implements PlayerValueOneShot {

    public TitleMessageEffect() {
        super(
                "TitleMessage",
                I18N.tr("effect.title-message.name"),
                "SmileYik",
                "1.0.0",
                I18N.tr("effect.title-message.description")
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
        return new Data(null, null, 0, 0, 0);
    }

    @Override
    public EffectData newEffectData(String[] args) {
        if (args.length != 5) {
            return new Data(null, null, 0, 0, 0);
        }
        return new Data(
                args[0], args[1],
                Integer.parseInt(args[2]),
                Integer.parseInt(args[3]),
                Integer.parseInt(args[4])
        );
    }

    /**
     * 根据元素值处理玩家。
     *
     * @param player
     * @param value
     */
    @Override
    public void handlePlayer(NumericalPlayer player, PlayerValue value) {

    }

    @Override
    public void onRegisterToPlayerData(NumericalPlayer player, PlayerValue value) {
        Data data = (Data) value;
        Msg.title(player, data.main, data.sub, data.fadeIn, data.stay, data.fadeOut);
    }

    public static final class Data extends AbstractEffectData {
        private String main, sub;
        private int fadeIn, stay, fadeOut;

        public Data(String main, String sub, int fadeIn, int stay, int fadeOut) {
            this.main = main;
            this.sub = sub;
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }

        @Override
        public boolean canDelete() {
            return true;
        }

        @Override
        public boolean isTimeout() {
            return true;
        }

        @Override
        public void load(ConfigurationSection section) {
            super.load(section);
            main = section.getString("main");
            sub = section.getString("sub");
            fadeIn = section.getInt("fade-in", 0);
            stay = section.getInt("stay", 0);
            fadeOut = section.getInt("fade-out", 0);
        }

        @Override
        public void store(ConfigurationSection section) {
            super.store(section);
            section.set("main", main);
            section.set("sub", sub);
            section.set("fade-in", fadeIn);
            section.set("stay", stay);
            section.set("fade-out", fadeOut);
        }
    }
}
