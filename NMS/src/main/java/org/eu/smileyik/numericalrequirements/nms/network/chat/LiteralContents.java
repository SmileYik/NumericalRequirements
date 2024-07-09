package org.eu.smileyik.numericalrequirements.nms.network.chat;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class LiteralContents implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/LiteralContents.txt";
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

    public LiteralContents(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public static LiteralContents fromString(String text) {
        if (CLASS.hasMethod("fromString")) {
            return new LiteralContents(CLASS.execute("fromString", null, text));
        }
        return new LiteralContents(CLASS.newInstance("new", text));
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
