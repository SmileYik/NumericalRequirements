package org.eu.smileyik.numericalrequirements.core.effect.impl;

import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.effect.AbstractEffect;
import org.eu.smileyik.numericalrequirements.core.effect.EffectData;
import org.eu.smileyik.numericalrequirements.core.effect.service.EffectService;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataValue;

public class EffectBundle extends AbstractEffect {
    private final EffectService effectService;

    public EffectBundle(EffectService effectService) {
        super(
                "EffectBundle",
                I18N.tr("effect.effect-bundle.name"),
                "SmileYik",
                "1.0.0",
                I18N.tr("effect.effect-bundle.description")
        );
        this.effectService = effectService;
    }

    @Override
    protected void register() {

    }

    @Override
    protected void unregister() {

    }

    @Override
    public EffectData newEffectData() {
        return new EffectBundleData(effectService);
    }

    @Override
    public EffectData newEffectData(String[] args) {
        if (args.length != 2 || effectService.getBundleConfigById(args[0]) == null) {
            return null;
        }
        EffectBundleData effectBundleData = new EffectBundleData(effectService);
        effectBundleData.setBundleId(args[0]);
        effectBundleData.setDuration(Double.parseDouble(args[1]));
        return effectBundleData;
    }

    @Override
    public void handlePlayer(NumericalPlayer player, PlayerDataValue value) {
        ((EffectBundleData) value).handlePlayer(player);
    }

    @Override
    public void onRegisterToPlayerData(NumericalPlayer player, PlayerDataValue value) {
        ((EffectBundleData) value).onRegisterToPlayerData(player);
    }

    @Override
    public void onUnregisterFromPlayerData(NumericalPlayer player, PlayerDataValue value) {
        ((EffectBundleData) value).onUnregisterFromPlayerData(player);
    }
}
