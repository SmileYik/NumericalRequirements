package org.eu.smileyik.numericalrequirements.core.item.tag.service;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LoreTagValue implements Iterable<LoreTagTypeValue> {
    private static final class Itr implements Iterator<LoreTagTypeValue> {
        private final Iterator<LoreTagTypeValue> iterator;

        private Itr(LoreTagValue loreTagValue) {
            this.iterator = loreTagValue.typeValues.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public LoreTagTypeValue next() {
            return iterator.next();
        }

        @Override
        public void remove() {
            iterator.remove();
        }

        @Override
        public void forEachRemaining(Consumer<? super LoreTagTypeValue> action) {
            iterator.forEachRemaining(action);
        }
    }


    private final List<LoreTagTypeValue> typeValues;

    public LoreTagValue() {
        typeValues = new ArrayList<>();
    }

    public LoreTagValue(List<LoreTagTypeValue> typeValues) {
        this.typeValues = typeValues;
    }

    public LoreTagValue merge(LoreTagValue other, BiFunction<LoreTagTypeValue, LoreTagTypeValue, String> operator) {
        int size = typeValues.size();
        if (size != other.size()) {
            return null;
        }
        LoreTagValue value = new LoreTagValue();
        for (int i = 0; i < size; ++i) {
            value.add(typeValues.get(i).merge(other.get(i), operator));
        }
        return value;
    }

    public void add(LoreTagTypeValue value) {
        typeValues.add(value);
    }

    public int size() {
        return typeValues.size();
    }

    public LoreTagTypeValue get(int idx) {
        return typeValues.get(idx);
    }

    @NotNull
    @Override
    public Iterator<LoreTagTypeValue> iterator() {
        return new Itr(this);
    }
}
