package org.eu.smileyik.numericalrequirements.nms.entity;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

public class EnumItemSlot implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/entity/EnumItemSlot.txt";
    private static final ReflectClass CLASS;
    private static final Map<String, EnumItemSlot> cache;

    public static final String SLOT_MAINHAND = "mainhand";
    public static final String SLOT_OFFHAND = "offhand";
    public static final String SLOT_FEET = "feet";
    public static final String SLOT_LEGS = "legs";
    public static final String SLOT_CHEST = "chest";
    public static final String SLOT_HEAD = "head";

    static {
        try {
            String currentVersion = VersionScript.runScriptByResource(SCRIPT_PATH);
            if (currentVersion != null) {
                CLASS = MySimpleReflect.readByResource(
                        currentVersion, false,
                        "${version}", VersionScript.VERSION
                );
                cache = new WeakHashMap<>();
            } else {
                CLASS = null;
                cache = null;
            }
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException | IOException e) {
            DebugLogger.debug(e);
            throw new RuntimeException(e);
        }
    }


    private final Object instance;

    protected EnumItemSlot(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public static EnumItemSlot fromName(String name) {
        if (CLASS == null) return null;
        if (cache.containsKey(name)) return cache.get(name);
        if (!CLASS.hasField(name)) return null;
        EnumItemSlot slot = new EnumItemSlot(
                CLASS.get(name, null)
        );
        cache.put(name, slot);
        return slot;
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
