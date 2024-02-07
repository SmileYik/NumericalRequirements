package org.eu.smileyik.numericalrequirements.thirst;

import org.eu.smileyik.numericalrequirements.core.element.data.singlenumber.DoubleElementBar;
import org.eu.smileyik.numericalrequirements.core.util.Pair;

public class ThirstData extends DoubleElementBar {
    public ThirstData() {
        super(1, Pair.newPair(0D, 100D));
        setValue(100D);
    }

    @Override
    protected double calculateValue(double value, double second) {
        return value - getNaturalDepletionValue() * getRate() * second;
    }
}
