package org.eu.smileyik.numericalrequirements.core.element.data.singlenumber;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.element.data.BoundedData;
import org.eu.smileyik.numericalrequirements.core.util.Pair;

import java.util.function.BinaryOperator;

public abstract class DoubleElementBar extends DoubleElementValue implements BoundedData<Double> {
    private final long period;
    private Pair<Double, Double> bounds;

    public DoubleElementBar(long period, Pair<Double, Double> bounds) {
        this.period = period;
        setBounds(bounds);
    }

    @Override
    public Double getLowerBound() {
        return bounds.getFirst();
    }

    @Override
    public Double getUpperBound() {
        return bounds.getSecond();
    }

    @Override
    public void setLowerBound(Double bound) {
        bounds.setFirst(bound);
    }

    @Override
    public void setUpperBound(Double bound) {
        bounds.setSecond(bound);
    }

    @Override
    public Pair<Double, Double> getBounds() {
        return Pair.newUnchangablePair(bounds);
    }

    @Override
    public void setBounds(Pair<Double, Double> bounds) {
        this.bounds = Pair.newPair(
                bounds.getFirst(), bounds.getSecond()
        );
    }

    @Override
    public Pair<Double, Double> getBoundsByRate(double rate) {
        return Pair.newUnchangablePair(
                rate * bounds.getFirst(),
                rate * bounds.getSecond()
        );
    }

    @Override
    public Pair<Double, Double> calculateBoundsAndGet(Pair<Double, Double> other,
                                                                   BinaryOperator<Pair<Double, Double>> operator) {
        Pair<Double, Double> apply = operator.apply(bounds, other);
        bounds = Pair.newPair(apply.getFirst(), apply.getSecond());
        return Pair.newUnchangablePair(apply.getFirst(), apply.getSecond());
    }

    @Override
    public long period() {
        return period;
    }

    @Override
    public Double setValue(Double value) {
        if (value < bounds.getFirst()) {
            return super.setValue(bounds.getFirst());
        } else if (value > bounds.getSecond()) {
            return super.setValue(bounds.getSecond());
        }
        return super.setValue(value);
    }

    @Override
    public void store(ConfigurationSection section) {
        super.store(section);
        section.set("lower-bound", bounds.getFirst());
        section.set("upper-bound", bounds.getSecond());
    }

    @Override
    public void load(ConfigurationSection section) {
        super.load(section);
        bounds = Pair.newPair(
                section.getDouble("lower-bound"),
                section.getDouble("upper-bound")
        );
    }

    @Override
    protected void updateValue(double seconds) {
        setValue(calculateValue(getValue(), seconds));
    }

    /**
     * 计算最终数据值.
     * @param value 当前值.
     * @param seconds 距离上一次计算过程中所经过的秒数.
     * @return
     */
    protected abstract double calculateValue(double value, double seconds);

    public double getValueOfUpperBound() {
        return getValue() / getUpperBound();
    }
}
