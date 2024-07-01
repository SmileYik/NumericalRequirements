package org.eu.smileyik.numericalrequirements.core.effect;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EffectPlayer {

    /**
     * 效果合并模式： 直接添加需要添加的效果数据， 不做任何附加判断。
     */
    byte MERGE_NONE = 0;
    /**
     * 效果合并模式： 当有相同效果数据时， 使用新的效果数据替换旧的效果数据。
     */
    byte MERGE_REPLACE = 1;
    /**
     * 效果合并模式： 当有相同效果数据时， 忽略此次注册效果操作。
     */
    byte MERGE_IGNORE = 2;

    static List<EffectData> getEffectData(@NotNull NumericalPlayer player, Effect effect) {
        return player.getRegisteredValues(effect, EffectData.class);
    }

    /**
     * 给玩家注册一个新的效果。效果将会直接加入到玩家数据中。
     * @param player 玩家。
     * @param effect 效果类型。
     * @param effectData 效果数据。
     */
    static void registerEffect(@NotNull NumericalPlayer player, Effect effect, EffectData effectData) {
        registerEffect(player, effect, effectData, MERGE_NONE);
    }

    /**
     * 给玩家注册一个新的效果。
     * @param player 玩家
     * @param effect 效果类型
     * @param effectData 效果数据
     * @param mergeMode 效果合并模式
     */
    static void registerEffect(@NotNull NumericalPlayer player, Effect effect, EffectData effectData, byte mergeMode) {
        if (mergeMode == MERGE_NONE) {
            player.registerData(Effect.class, effect, effectData);
            return;
        }

        boolean hasSimilarEffects = false;
        List<EffectData> effectDataList = getEffectData(player, effect);
        for (EffectData registered : effectDataList) {
            if (effectData.isSimilar(registered)) {
                hasSimilarEffects = true;

                if (mergeMode == MERGE_REPLACE) {
                    removeEffectData(player, effect, registered);
                } else {
                    break;
                }
            }
        }

        if (hasSimilarEffects && mergeMode == MERGE_IGNORE) {
            return;
        }

        player.registerData(Effect.class, effect, effectData);
    }

    /**
     * 取消注册某一类型效果的所有数据。
     * @param player 玩家
     * @param effect 效果类型
     */
    static void unregisterEffect(@NotNull NumericalPlayer player, Effect effect) {
        player.unregisterData(effect);
    }

    /**
     * 取消注册某一效果的效果数据。
     * @param player 玩家。
     * @param effect 效果类型。
     * @param data 要取消的效果数据实例。
     */
    static void removeEffectData(@NotNull NumericalPlayer player, Effect effect, EffectData data) {
        player.unregisterData(effect, (arg) -> arg == data);
    }

    static Map<Effect, EffectData> getDisabledEffectMap(@NotNull NumericalPlayer player) {
        Map<Effect, EffectData> map = new HashMap<>();
        Map<Effect, List<EffectData>> disabledDataMap = player.getDisabledDataMap(Effect.class, EffectData.class);
        disabledDataMap.forEach((k, v) -> {
            if (v != null && !v.isEmpty()) {
                map.put(k, v.get(0));
            }
        });
        return map;
    }

    static void load(@NotNull NumericalPlayer player, ConfigurationSection section, Effect effect) {
        load(player, section, effect, null);
    }

    static void load(@NotNull NumericalPlayer player,
                     ConfigurationSection section, Effect effect, EffectData defaultData) {
        player.load(section, Effect.class, effect, defaultData);
    }
}
