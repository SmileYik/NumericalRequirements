package org.eu.smileyik.numericalrequirements.core.element.data;

import org.eu.smileyik.numericalrequirements.core.util.Pair;

import java.util.function.BiFunction;

public interface SingleElementData<T> {
    /**
     * 获取当前值.
     * @return
     */
    T getValue();

    /**
     * 获取上一个时期的值.
     * @return
     */
    T getPreviousValue();

    /**
     * 获取当前以及上一个时期的值.
     * @return
     */
    Pair<T, T> getValues();

    /**
     * 设定当前元素值为指定的值。
     * @param value
     * @return 上上一个元素值.
     */
    T setValue(T value);

    /**
     * 计算值. rate值作为左值，形参作为右值
     * @param second 给定的进行计算的值。
     * @param operator 自定计算方法
     * @return 计算后的值.
     */
    T calculateAndGet(T second, BiFunction<T, T, T> operator);

    /**
     * 计算值. rate值作为左值，形参作为右值
     * @param second 给定的进行计算的值。
     * @param operator 自定计算方法
     * @return 计算前的值.
     */
    T getAndCalculate(T second, BiFunction<T, T, T> operator);
}
