package org.eu.smileyik.numericalrequirements.nms.resources;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class MinecraftKey implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/resources/MinecraftKey.txt";
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

    public MinecraftKey(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public static Object createKey(String namespace, String key) {
        if (CLASS == null) return null;
        return CLASS.newInstance("newWithNamespace", namespace, key);
    }

    public static Object createKey(String key) {
        if (CLASS == null) return null;
        return CLASS.newInstance("new", key);
    }

    public static MinecraftKey newInstance(String namespace, String key) {
        if (CLASS == null) return null;
        return new MinecraftKey(createKey(namespace, key));
    }

    public static MinecraftKey newInstance(String key) {
        if (CLASS == null) return null;
        return new MinecraftKey(createKey(key));
    }

    public String getNamespace() {
        return (String) CLASS.execute("getNamespace", instance);
    }

    public String getKey() {
        return (String) CLASS.execute("getKey", instance);
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
