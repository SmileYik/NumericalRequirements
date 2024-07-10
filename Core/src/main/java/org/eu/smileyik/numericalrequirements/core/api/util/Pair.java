package org.eu.smileyik.numericalrequirements.core.api.util;

public class Pair <First, Second> {
    private First first;
    private Second second;


    public Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public static <First, Second> Pair<First, Second> newPair(First first, Second second) {
        return new Pair<>(first, second);
    }

    public static <First, Second> Pair<First, Second> newUnchangablePair(First first, Second value) {
        return new Pair<First, Second>(first, value) {
            @Override
            public void setFirst(First o) {
                throw new RuntimeException("Can not change key!");
            }

            @Override
            public void setSecond(Second o) {
                throw new RuntimeException("Can not change value!");
            }
        };
    }

    public static <First, Second> Pair<First, Second> newUnchangablePair(Pair<First, Second> pair) {
        return newUnchangablePair(pair.first, pair.second);
    }

    public First getFirst() {
        return first;
    }

    public Second getSecond() {
        return second;
    }

    public void setFirst(First first) {
        this.first = first;
    }

    public void setSecond(Second second) {
        this.second = second;
    }
}
