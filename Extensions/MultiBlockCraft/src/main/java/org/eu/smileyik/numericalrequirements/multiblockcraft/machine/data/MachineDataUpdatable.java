package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data;

import org.bukkit.scheduler.BukkitTask;
import org.eu.smileyik.numericalrequirements.core.api.Updatable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.TimeRecipe;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface MachineDataUpdatable extends MachineData, Updatable {
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
        return getRecipe() == null ? 0 : getTimeRecipe().getTime();
    }

    default <T> Future<T> syncCall(Callable<T> callable) {
        return MultiBlockCraftExtension.getInstance().getPlugin().getServer().getScheduler().callSyncMethod(
                MultiBlockCraftExtension.getInstance().getPlugin(), callable
        );
    }

    default BukkitTask runSync(Runnable runnable) {
        return MultiBlockCraftExtension.getInstance().getPlugin().getServer().getScheduler().runTask(
                MultiBlockCraftExtension.getInstance().getPlugin(), runnable
        );
    }
}
