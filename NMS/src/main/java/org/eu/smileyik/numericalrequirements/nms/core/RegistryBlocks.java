package org.eu.smileyik.numericalrequirements.nms.core;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class RegistryBlocks extends IRegistry implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/core/RegistryBlocks.txt";
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

    public RegistryBlocks(Object instance) {
        super(instance);
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
    }

    public Object get(Object minecraftKey) {
        return CLASS.execute("get", instance, minecraftKey);
    }
}
