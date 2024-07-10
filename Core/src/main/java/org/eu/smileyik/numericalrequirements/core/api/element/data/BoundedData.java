package org.eu.smileyik.numericalrequirements.core.api.element.data;

import org.eu.smileyik.numericalrequirements.core.api.util.Pair;

import java.util.function.BinaryOperator;

public interface BoundedData <T> {
    /**
     * 获取下界.
     * @return
     */
    T getLowerBound();

    /**
     * 获取上界.
     */
    T getUpperBound();

    /**
     * 设置下界.
     * @param bound
     */
    void setLowerBound(T bound);

    /**
     * 设置上界.
     * @param bound
     */
    void setUpperBound(T bound);

    /**
     * 获取边界值.
     * @return 左值为下界，右值为上界
     */
    Pair<T, T> getBounds();

    /**
     * 设置边界.
     * @param bounds 左值为下界，右值为上界
     */
    void setBounds(Pair<T, T> bounds);

    Pair<T, T> getBoundsByRate(double rate);

    Pair<T, T> calculateBoundsAndGet(Pair<T, T> other, BinaryOperator<Pair<T, T>> operator);
}
