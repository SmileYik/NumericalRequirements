package org.eu.smileyik.numericalrequirements.core.effect;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.player.NumericalPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EffectPlayer {
    static List<EffectData> getEffectData(@NotNull NumericalPlayer player, Effect effect) {
        return player.getRegisteredValues(effect, EffectData.class);
    }

    static void registerEffect(@NotNull NumericalPlayer player, Effect effect, EffectData effectData) {
        player.registerData(Effect.class, effect, effectData);
    }

    static void unregisterEffect(@NotNull NumericalPlayer player, Effect effect) {
        player.unregisterData(effect);
    }

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
