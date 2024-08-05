package org.eu.smileyik.numericalrequirements.nms.core;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class BlockPosition implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/core/BlockPosition.txt";
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

    public BlockPosition(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public static Object create(int x, int y, int z) {
        if (CLASS == null) return null;
        return CLASS.newInstance("newByInt", x, y, z);
    }

    public static Object create(double x, double y, double z) {
        if (CLASS == null) return null;
        return CLASS.newInstance("newByDouble", x, y, z);
    }

    public static BlockPosition newInstance(int x, int y, int z) {
        if (CLASS == null) return null;
        return new BlockPosition(create(x, y, z));
    }

    public static BlockPosition newInstance(double x, double y, double z) {
        if (CLASS == null) return null;
        return new BlockPosition(create(x, y, z));
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
