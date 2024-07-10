package org.eu.smileyik.numericalrequirements.core.api.element.data.singlenumber;

import org.eu.smileyik.numericalrequirements.core.api.element.data.ElementData;
import org.eu.smileyik.numericalrequirements.core.api.element.data.RatableData;
import org.eu.smileyik.numericalrequirements.core.api.element.data.SingleElementData;

public interface SingleElementValue<T extends Number> extends ElementData, RatableData, SingleElementData<T> {
    /**
     * 获取当前值的倍率，即为（当前值x倍率）。
     * @param rate 倍率
     * @return 当前值x倍率的乘积
     */
    default double getCurrentValueByRate(double rate) {;
        return getValue().doubleValue() * rate;
    }
}
