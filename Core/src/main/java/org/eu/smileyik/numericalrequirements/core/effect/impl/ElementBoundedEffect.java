package org.eu.smileyik.numericalrequirements.core.effect.impl;

import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.effect.AbstractEffect;
import org.eu.smileyik.numericalrequirements.core.effect.EffectData;
import org.eu.smileyik.numericalrequirements.core.element.Element;
import org.eu.smileyik.numericalrequirements.core.element.ElementPlayer;
import org.eu.smileyik.numericalrequirements.core.element.data.BoundedData;
import org.eu.smileyik.numericalrequirements.core.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.element.data.RatableData;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.player.PlayerDataValue;
import org.eu.smileyik.numericalrequirements.core.util.Pair;

public class ElementBoundedEffect extends AbstractEffect {

    private static final class ElementBoundedEffectData extends DoubleEffectData {
        private double upperData;

        public synchronized void setUpperData(double upperData) {
            this.upperData = upperData;
        }

        public void setLowerData(double lowerData) {
            setData(lowerData);
        }

        public synchronized double getUpperData() {
            return upperData;
        }

        public double getLowerData() {
            return getData();
        }
    }

    private final Element element;

    public ElementBoundedEffect(Element element) {
        super(
                String.format("%s-BoundedEffect", element.getId()),
                I18N.tr("effect.bounded.normal-name", element.getName()),
                "SmileYik",
                "1.0.0",
                I18N.tr("effect.bounded.normal-description", element.getName())
        );
        this.element = element;
        ElementData elementData = element.newElementData();
        if (!(elementData instanceof BoundedData) || !(((BoundedData)elementData).getLowerBound() instanceof Number)) {
            throw new RuntimeException("This element data not implement Number BoundedData!" + element);
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
        return new ElementBoundedEffectData();
    }

    @Override
    public EffectData newEffectData(String[] args) {
        if (args.length != 3) {
            return null;
        }
        ElementBoundedEffectData elementBoundedEffectData = new ElementBoundedEffectData();
        elementBoundedEffectData.setDuration(Double.parseDouble(args[0]));
        elementBoundedEffectData.setLowerData(Double.parseDouble(args[1]));
        elementBoundedEffectData.setUpperData(Double.parseDouble(args[2]));
        return elementBoundedEffectData;
    }

    @Override
    public void handlePlayer(NumericalPlayer player, PlayerDataValue value) {

    }

    @Override
    public void onRegisterToPlayerData(NumericalPlayer player, PlayerDataValue value) {
        ElementData elementData = ElementPlayer.getElementData(player, element);
        ElementBoundedEffectData data = (ElementBoundedEffectData) value;
        if (elementData != null) {
            BoundedData<Number> boundedData = (BoundedData<Number>) elementData;
            boundedData.calculateBoundsAndGet(
                    Pair.newUnchangablePair(data.getLowerData(), data.getUpperData()),
                    (p, q) -> Pair.newUnchangablePair(
                            p.getFirst().doubleValue() + q.getFirst().doubleValue(),
                            p.getSecond().doubleValue() + q.getSecond().doubleValue()
                    )
            );
        } else {
            data.recovery();
        }
    }

    @Override
    public void onUnregisterFromPlayerData(NumericalPlayer player, PlayerDataValue value) {
        ElementData elementData = ElementPlayer.getElementData(player, element);
        ElementBoundedEffectData data = (ElementBoundedEffectData) value;
        if (elementData != null) {
            BoundedData<Number> boundedData = (BoundedData<Number>) elementData;
            boundedData.calculateBoundsAndGet(
                    Pair.newUnchangablePair(data.getLowerData(), data.getUpperData()),
                    (p, q) -> Pair.newUnchangablePair(
                            p.getFirst().doubleValue() - q.getFirst().doubleValue(),
                            p.getSecond().doubleValue() - q.getSecond().doubleValue()
                    )
            );
            data.recovery();
        }
    }
}
