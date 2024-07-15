package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data;

import org.eu.smileyik.numericalrequirements.core.api.Updatable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.TimeRecipe;

public interface MachineDataUpdatable extends MachineData, Updatable, MachineDataStorable {
    /**
     * 是否启用工作
     * @return
     */
    default boolean isEnable() {
        return false;
    }

    void setEnable(boolean enable);

    /**
     * 是否正在运行
     * @return
     */
    default boolean isRunning() {
        return isEnable() && getRemainingTime() > 0;
    }

    /**
     * 获取正在制作的配方。
     * @return
     */
    Recipe getRecipe();

    /**
     * 获取正在制作的配方
     * @return
     */
    default TimeRecipe getTimeRecipe() {
        return (TimeRecipe) getRecipe();
    }

    /**
     * 获取制作的时间
     * @return
     */
    double getCraftedTime();

    /**
     * 获取剩余时间。
     * @return
     */
    default double getRemainingTime() {
        return getTotalTime() - getCraftedTime();
    }

    /**
     * 获取总的运行时间
     * @return
     */
    default double getTotalTime() {
        return getTimeRecipe().getTime();
    }
}
