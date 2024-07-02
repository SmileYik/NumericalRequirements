package org.eu.smileyik.numericalrequirements.core.effect.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.effect.AbstractEffect;
import org.eu.smileyik.numericalrequirements.core.effect.AbstractEffectData;
import org.eu.smileyik.numericalrequirements.core.effect.EffectData;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataValue;

public class ChatMessageEffect extends AbstractEffect {

    public ChatMessageEffect() {
        super(
                "ChatMessage",
                I18N.tr("effect.chat-message.name"),
                "SmileYik",
                "1.0.0",
                I18N.tr("effect.chat-message.description")
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
    public void handlePlayer(NumericalPlayer player, PlayerDataValue value) {

    }

    @Override
    public void onRegisterToPlayerData(NumericalPlayer player, PlayerDataValue value) {
        player.getPlayer().sendMessage(((Data) value).message);
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
        public synchronized boolean isTimeout() {
            return true;
        }

        @Override
        public synchronized void load(ConfigurationSection section) {
            super.load(section);
            message = section.getString("message");
        }

        @Override
        public synchronized void store(ConfigurationSection section) {
            super.store(section);
            section.set("message", message);
        }
    }
}
