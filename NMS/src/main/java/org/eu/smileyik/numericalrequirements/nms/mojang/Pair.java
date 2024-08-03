package org.eu.smileyik.numericalrequirements.nms.mojang;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class Pair implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/mojang/Pair.txt";
    private static final ReflectClass CLASS;

    static {
        try {
            String currentVersion = VersionScript.runScriptByResource(SCRIPT_PATH);
            if (currentVersion != null) {
                CLASS = MySimpleReflect.readByResource(
                        currentVersion, false,
                        "${version}", VersionScript.VERSION
                );
            } else {
                CLASS = null;
            }
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException | IOException e) {
            DebugLogger.debug(e);
            throw new RuntimeException(e);
        }
    }

    private final Object instance;

    protected Pair(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public static <T, U> Pair newPair(T first, U second) {
        if (CLASS == null) return null;
        return new Pair(CLASS.newInstance("newInstance", first, second));
    }

    public <T> T getFirst() {
        return (T) CLASS.execute("getFirst", instance);
    }

    public <U> U getSecond() {
        return (U) CLASS.execute("getSecond", instance);
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
