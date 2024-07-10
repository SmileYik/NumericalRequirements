package org.eu.smileyik.numericalrequirements.core.api.element.data;

import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;

public interface RatableData {

    /**
     * 获取值流动倍率.
     * @return
     */
    double getRate();

    /**
     * 设定倍率，值为非负数.
     * @param rate
     * @return 返回新的倍率.
     */
    double setRate(double rate);

    /**
     * 计算值. rate值作为左值，形参作为右值
     * @param num 给定的进行计算的值。
     * @param operator 自定计算方法
     * @return 计算后的值.
     */
    double calculateRateAndGet(double num, DoubleBinaryOperator operator);

    /**
     * 计算值. rate值作为左值，形参作为右值
     * @param num 给定的进行计算的值。
     * @param operator 自定计算方法
     * @return 计算后的值.
     */
    double getAndCalculateRate(double num, DoubleBinaryOperator operator);

    /**
     * 获取自然流动的值，即每秒钟自然消耗多少值.
     * @return
     */
    public double getNaturalDepletionValue();

    /**
     * 设定自然流动的值.
     * @param naturalDepletionValue
     */
    public void setNaturalDepletionValue(double naturalDepletionValue);

    public double calculateNaturalDepletionAndGet(double other, BinaryOperator<Double> operator);
}
