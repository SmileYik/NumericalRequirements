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

public class ActionBarEffect extends AbstractEffect implements PlayerValueOneShot {

    public ActionBarEffect() {
        super(
                "ActionBar",
                I18N.tr("effect.action-bar.name"),
                "SmileYik",
                "1.0.0",
                I18N.tr("effect.action-bar.description")
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
        return new Data("");
    }

    @Override
    public EffectData newEffectData(String[] args) {
        return args.length == 0 ? newEffectData() : new Data(args[0]);
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
        Msg.actionBar(player, ((Data) value).message);
    }

    public static final class Data extends AbstractEffectData {
        private String message;

        public Data(String message) {
            this.message = message;
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
            message = section.getString("message");
        }

        @Override
        public void store(ConfigurationSection section) {
            super.store(section);
            section.set("message", message);
        }
    }
}
