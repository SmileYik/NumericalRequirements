package org.eu.smileyik.numericalrequirements.core.api.element.handler;

import org.bukkit.configuration.ConfigurationSection;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectPlayer;
import org.eu.smileyik.numericalrequirements.core.api.element.data.singlenumber.DoubleElementBar;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.PlayerDataValue;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * DoubleElementBar 可用。
 */
public class RangeHandler implements ElementHandler {
    private final ArrayList<Entry> entries = new ArrayList<>();
    private final ArrayList<Entry> percentageEntries = new ArrayList<>();

    private Entry prevEntry = null;

    public RangeHandler(ConfigurationSection config) {
        for (String key : config.getKeys(false)) {
            Entry entry = new Entry(config.getConfigurationSection(key));
            if (entry.percentage) {
                percentageEntries.add(entry);
            } else {
                entries.add(entry);
            }
        }

        entries.sort(Comparator.comparingDouble(it -> it.range.getFirst()));
        percentageEntries.sort(Comparator.comparingDouble(it -> it.range.getFirst()));
    }

    @Override
    public void handlePlayer(NumericalPlayer player, PlayerDataValue value) {
        if (value instanceof DoubleElementBar) {
            handlePlayer(player, (DoubleElementBar) value);
        }
    }

    @Override
    public void handlePlayer(NumericalPlayer player, DoubleElementBar value) {
        Pair<Double, Double> values = value.getValues();
        Entry entry = searchPercentageEntry(value.getUpperBound(), values.getSecond());
        if (entry == null) {
            entry = searchEntry(values.getSecond());
        }

        if (prevEntry != entry) {
            if (prevEntry != null) {
                EffectPlayer.unregisterEffectBundle(player, prevEntry.bundle);
            }
            prevEntry = entry;
        }

        if (entry == null) {
            return;
        }

        EffectPlayer.registerEffectBundle(player, entry.bundle, entry.duration, EffectPlayer.MERGE_IGNORE);
        double old = values.getFirst();
        double cur = values.getSecond();
        double upper = value.getUpperBound();

        if (
                cur >= entry.getLowerBound(upper) && cur <= entry.getUpperBound(upper) &&
                (old < entry.getLowerBound(upper) || old > entry.getUpperBound(upper))
        ) {
            EffectPlayer.registerEffect(player, entry.messageSender, entry.message, EffectPlayer.MERGE_NONE);
        }
    }

    private Entry searchEntry(double value) {
        if (entries.isEmpty()) {
            return null;
        }
        int left = 0, right = entries.size() - 1;
        while (left <= right) {
            int mid = left + ((right - left) >>> 1);
            Entry entry = entries.get(mid);
            if (entry.range.getFirst() <= value) {
                if (entry.range.getSecond() >= value) {
                    return entry;
                }
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }

    private Entry searchPercentageEntry(double max, double value) {
        int left = 0, right = percentageEntries.size() - 1;
        while (left <= right) {
            int mid = left + ((right - left) >>> 1);
            Entry entry = percentageEntries.get(mid);
            if (entry.range.getFirst() * max <= value) {
                if (entry.range.getSecond() * max >= value) {
                    return entry;
                }
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }

    private static class Entry {
        protected final Pair<Double, Double> range;
        protected final boolean percentage;
        protected final String bundle;
        protected final double duration;
        protected final String[] message;
        protected final String messageSender;

        protected Entry(ConfigurationSection section) {
            List<Double> range1 = section.getDoubleList("range");
            percentage = section.getBoolean("percentage");
            bundle = section.getString("bundle");
            duration = section.getDouble("duration");
            message = section.getStringList("message").toArray(new String[0]);
            messageSender = section.getString("message-sender");

            double value = percentage ? 100D : 1D;
            range = Pair.newUnchangablePair(range1.get(0) / value, range1.get(1) / value);
        }

        public double getLowerBound() {
            return range.getFirst();
        }

        public double getUpperBound() {
            return range.getSecond();
        }

        public double getLowerBound(double maxValue) {
            return range.getFirst() * (percentage ? maxValue : 1);
        }

        public double getUpperBound(double maxValue) {
            return range.getSecond() * (percentage ? maxValue : 1);
        }
    }
}
