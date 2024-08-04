package org.eu.smileyik.numericalrequirements.core.item.serialization.entry;

import org.eu.smileyik.numericalrequirements.core.api.item.ItemSerializerEntry;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;

public abstract class AbstractReflectItemSerializerEntry implements ItemSerializerEntry {
    protected final ReflectClass clazz;

    public AbstractReflectItemSerializerEntry() {
        ReflectClass clazz;
        try {
            clazz = init();
        } catch (Throwable ex) {
            DebugLogger.debug(ex);
            clazz = null;
        }
        this.clazz = clazz;
    }

    protected abstract ReflectClass init() throws Throwable;

    @Override
    public boolean isAvailable() {
        return clazz != null;
    }
}
