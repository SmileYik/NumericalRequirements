package org.eu.smileyik.numericalrequirements.core.item.tag.lore;

public interface MergeableLore {
    /**
     * 将两个值合并起来，并应用在第一个形参上。
     * @param p
     * @param q
     *
     */
    default void merge(LoreValue p, LoreValue q) {
        int size = p.size();
        if (size != q.size()) {
            throw new IllegalArgumentException("Try to merge a different type value.");
        }
        for (int i = 0; i < size; i++) {
            ValueTranslator<?> translator = p.getTranslator(i);
            if (translator != q.getTranslator(i)) {
                throw new IllegalArgumentException("Try to merge a different type value.");
            }
            final int idx = i;
            ValueTranslator.VALUE_TRANSLATOR_MAP.forEach((k, v) -> {
                if (translator != v) return;
                switch (k) {
                    case "num":
                    case "int":
                    case "numf1":
                        Number x = p.get(idx);
                        Number y = q.get(idx);
                        p.set(idx, v.getTargetClass().cast(x.doubleValue() + y.doubleValue()));
                        break;
                }
            });
        }
    }
}
