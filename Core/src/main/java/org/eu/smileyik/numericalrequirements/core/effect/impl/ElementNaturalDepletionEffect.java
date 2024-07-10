package org.eu.smileyik.numericalrequirements.core.effect.impl;

import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.effect.AbstractEffect;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectData;
import org.eu.smileyik.numericalrequirements.core.api.element.Element;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.api.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.api.element.data.singlenumber.DoubleElementValue;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerDataValue;

public class ElementNaturalDepletionEffect extends AbstractEffect {
    private final Element element;

    public ElementNaturalDepletionEffect(Element element) {
        super(
                String.format("%s-NaturalDepletionEffect", element.getId()),
                I18N.tr("effect.natural-depletion.normal-name", element.getName()),
                "SmileYik",
                "1.0.0",
                I18N.tr("effect.natural-depletion.normal-description", element.getName())
        );
        this.element = element;

        if (!(element.newElementData() instanceof DoubleElementValue)) {
            throw new RuntimeException("This element data not extends DoubleElementValue!" + element);
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
        if (args.length != 2) return null;
        DoubleEffectData doubleEffectData = new DoubleEffectData();
        doubleEffectData.setDuration(Double.parseDouble(args[0]));
        doubleEffectData.setData(Double.parseDouble(args[1]));
        return doubleEffectData;
    }

    @Override
    public void handlePlayer(NumericalPlayer player, PlayerDataValue value) {

    }

    @Override
    public void onRegisterToPlayerData(NumericalPlayer player, PlayerDataValue value) {
        ElementData elementData = ElementPlayer.getElementData(player, element);
        DoubleEffectData data = (DoubleEffectData) value;
        if (elementData != null) {
            DoubleElementValue elementValue = (DoubleElementValue) elementData;
            elementValue.calculateNaturalDepletionAndGet(data.getData(), Double::sum);
        } else {
            data.recovery();
        }
    }

    @Override
    public void onUnregisterFromPlayerData(NumericalPlayer player, PlayerDataValue value) {
        ElementData elementData = ElementPlayer.getElementData(player, element);
        DoubleEffectData data = (DoubleEffectData) value;
        if (elementData != null) {
            DoubleElementValue elementValue = (DoubleElementValue) elementData;
            elementValue.calculateNaturalDepletionAndGet(data.getData(), (a, b) -> a - b);
            data.recovery();
        }
    }
}
