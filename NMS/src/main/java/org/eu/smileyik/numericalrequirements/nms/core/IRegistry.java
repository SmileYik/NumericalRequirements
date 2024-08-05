package org.eu.smileyik.numericalrequirements.nms.core;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class IRegistry implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/core/IRegistry.txt";
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

    private static RegistryBlocks blocks;

    protected final Object instance;

    public IRegistry(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    public static RegistryBlocks getBlocks() {
        if (blocks == null) {
            if (CLASS == null) return null;
            blocks = new RegistryBlocks(
                    CLASS.get("blocks", null)
            );
        }
        return blocks;
    }
}
