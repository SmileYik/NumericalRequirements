package org.eu.smileyik.numericalrequirements.core.api.item.tag.lore;

import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class LoreValue implements Iterable<Pair<ValueTranslator<?>, String>> {
    private final List<Pair<ValueTranslator<?>, String>> values;

    public LoreValue() {
        values = new ArrayList<>();
    }

    public LoreValue(List<Pair<ValueTranslator<?>, String>> values) {
        this.values = new ArrayList<>(values);
    }

    @NotNull
    @Override
    public Iterator<Pair<ValueTranslator<?>, String>> iterator() {
        return new Itr(this);
    }

    public int size() {
        return values.size();
    }

    public <T> T get(int idx) {
        if (idx < 0 || idx >= values.size()) {
            throw new IndexOutOfBoundsException("idx: " + idx + ", size: " + values.size());
        }
        Pair<ValueTranslator<?>, String> pair = values.get(idx);
        return (T) pair.getFirst().cast(pair.getSecond());
    }

    public String getString(int idx) {
        if (idx < 0 || idx >= values.size()) {
            throw new IndexOutOfBoundsException("idx: " + idx + ", size: " + values.size());
        }
        return values.get(idx).getSecond();
    }

    public void set(int idx, String value) {
        if (idx < 0 || idx >= values.size()) {
            throw new IndexOutOfBoundsException("idx: " + idx + ", size: " + values.size());
        }
        values.get(idx).setSecond(value);
    }

    public <T> void set(int idx, T value) {
        if (idx < 0 || idx >= values.size()) {
            throw new IndexOutOfBoundsException("idx: " + idx + ", size: " + values.size());
        }
        Pair<ValueTranslator<?>, String> pair = values.get(idx);
        pair.setSecond(((ValueTranslator<T>) pair.getFirst()).asString(value));
    }

    public <T> ValueTranslator<T> getTranslator(int idx) {
        if (idx < 0 || idx >= values.size()) {
            throw new IndexOutOfBoundsException("idx: " + idx + ", size: " + values.size());
        }
        return (ValueTranslator<T>) values.get(idx).getFirst();
    }

    private static class Itr implements Iterator<Pair<ValueTranslator<?>, String>> {
        private final Iterator<Pair<ValueTranslator<?>, String>> it;

        Itr(LoreValue loreValue) {
            this.it = loreValue.values.iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Pair<ValueTranslator<?>, String> next() {
            return it.next();
        }

        @Override
        public void remove() {
            it.remove();
        }

        @Override
        public void forEachRemaining(Consumer<? super Pair<ValueTranslator<?>, String>> action) {
            it.forEachRemaining(action);
        }
    }
}
