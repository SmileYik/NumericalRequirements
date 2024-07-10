package org.eu.smileyik.numericalrequirements.core.api.effect;

import org.eu.smileyik.numericalrequirements.core.api.player.PlayerDataValueUpdatable;

public interface EffectData extends PlayerDataValueUpdatable {
    double getDuration();

    void setDuration(double duration);

    /**
     * 比较两个实例是否相似。
     * @param other 另一个实例。
     * @return 若相似则返回true, 否则返回false。
     */
    default boolean isSimilar(EffectData other) {
        return false;
    }
}
