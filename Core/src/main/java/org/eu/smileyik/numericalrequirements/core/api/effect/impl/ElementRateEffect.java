package org.eu.smileyik.numericalrequirements.core.api.effect.impl;

import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.effect.AbstractEffect;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectData;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.api.element.data.Element;
import org.eu.smileyik.numericalrequirements.core.api.element.data.RatableData;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerValue;

public class ElementRateEffect extends AbstractEffect {
    private final org.eu.smileyik.numericalrequirements.core.api.element.Element element;

    public ElementRateEffect(org.eu.smileyik.numericalrequirements.core.api.element.Element element) {
        super(
                String.format("%s-RateEffect", element.getId()),
                I18N.tr("effect.rate.normal-name", element.getName()),
                "SmileYik",
                "1.0.0",
                I18N.tr("effect.rate.normal-description", element.getName())
        );
        this.element = element;
        if (!(element.newElementData() instanceof RatableData)) {
            throw new RuntimeException("This element data not implement RatableData!" + element);
        }
    }

    @Override
    protected void register() {

    }

    @Override
    protected void unregister() {

    }

    @Override
    public EffectData newEffectData() {
        return new DoubleEffectData();
    }

    @Override
    public EffectData newEffectData(String[] args) {
        if (args.length == 2) {
            DoubleEffectData doubleEffectData = new DoubleEffectData();
            doubleEffectData.setDuration(Double.parseDouble(args[0]));
            doubleEffectData.setData(Double.parseDouble(args[1]));
            return doubleEffectData;
        }
        return null;
    }

    @Override
    public void handlePlayer(NumericalPlayer player, PlayerValue value) {

    }

    @Override
    public void onRegisterToPlayerData(NumericalPlayer player, PlayerValue value) {
        Element elementData = ElementPlayer.getElementData(player, element);
        DoubleEffectData data = (DoubleEffectData) value;
        if (elementData != null) {
            RatableData ratableData = (RatableData) elementData;
            ratableData.calculateRateAndGet(data.getData(), Double::sum);
        } else {
            data.recovery();
        }
    }

    @Override
    public void onUnregisterFromPlayerData(NumericalPlayer player, PlayerValue value) {
        Element elementData = ElementPlayer.getElementData(player, element);
        DoubleEffectData data = (DoubleEffectData) value;
        if (elementData != null) {
            RatableData ratableData = (RatableData) elementData;
            ratableData.calculateRateAndGet(data.getData(), (a, b) -> a - b);
            data.recovery();
        }
    }
}
