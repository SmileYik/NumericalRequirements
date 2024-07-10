package org.eu.smileyik.numericalrequirements.core.api.element.data.singlenumber;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.api.AbstractUpdatable;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;

public abstract class DoubleElementValue extends AbstractUpdatable implements SingleElementValue<Double> {
    private double rate = 1;
    private double naturalDepletionValue;
    private double value;
    private double previousValue;

    @Override
    public double getRate() {
        return rate;
    }

    @Override
    public double setRate(double rate) {
        return this.rate = rate;
    }

    @Override
    public double calculateRateAndGet(double num, DoubleBinaryOperator operator) {
        return rate = operator.applyAsDouble(rate, num);
    }

    @Override
    public double getAndCalculateRate(double num, DoubleBinaryOperator operator) {
        double rate = this.rate;
        this.rate = operator.applyAsDouble(rate, num);
        return rate;
    }

    /**
     * 设定自然流动的值.
     * @param naturalDepletionValue
     */
    @Override
    public void setNaturalDepletionValue(double naturalDepletionValue) {
        this.naturalDepletionValue = naturalDepletionValue;
    }

    /**
     * 获取自然流动的值，即每秒钟自然消耗多少值.
     * @return
     */
    @Override
    public double getNaturalDepletionValue() {
        return naturalDepletionValue;
    }

    @Override
    public double calculateNaturalDepletionAndGet(double other, BinaryOperator<Double> operator) {
        naturalDepletionValue = operator.apply(naturalDepletionValue, other);
        return naturalDepletionValue;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public Double getPreviousValue() {
        return previousValue;
    }

    @Override
    public Pair<Double, Double> getValues() {
        return Pair.newUnchangablePair(previousValue, value);
    }

    @Override
    public Double setValue(Double value) {
        double ret = previousValue;
        previousValue = this.value;
        this.value = value;
        return ret;
    }

    @Override
    public Double calculateAndGet(Double second, BiFunction<Double, Double, Double> operator) {
        previousValue = this.value;
        this.value = operator.apply(this.value, second);
        return this.value;
    }

    @Override
    public Double getAndCalculate(Double second, BiFunction<Double, Double, Double> operator) {
        previousValue = this.value;
        this.value = operator.apply(this.value, second);
        return previousValue;
    }

    @Override
    public void store(ConfigurationSection section) {
        section.set("rate", rate);
        section.set("value", value);
        section.set("previous-value", previousValue);
        section.set("natural-depletion-value", naturalDepletionValue);
    }

    @Override
    public void load(ConfigurationSection section) {
        rate = section.getDouble("rate");
        value = section.getDouble("value");
        previousValue = section.getDouble("previous-value");
        naturalDepletionValue = section.getDouble("natural-depletion-value");
    }

    @Override
    protected boolean doUpdate(double second) {
        this.previousValue = this.value;
        updateValue(second);
        return true;
    }

    /**
     * 更新数据值.
     * @param seconds 距离上一次更新之间的时间（秒）
     */
    protected abstract void updateValue(double seconds);


}
