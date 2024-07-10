package org.eu.smileyik.numericalrequirements.nms.network.chat;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class EnumTitleAction implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/EnumTitleAction.txt";
    private static final ReflectClass CLASS;

    public static final EnumTitleAction TITLE, SUBTITLE, TIMES, CLEAR, RESET;

    static {
        try {
            String currentVersion = VersionScript.runScriptByResource(SCRIPT_PATH);
            if (currentVersion != null) {
                CLASS = MySimpleReflect.readByResource(
                        currentVersion, false,
                        "${version}", VersionScript.VERSION
                );
                TITLE = new EnumTitleAction(CLASS.get("TITLE", null));
                SUBTITLE = new EnumTitleAction(CLASS.get("SUBTITLE", null));
                TIMES = new EnumTitleAction(CLASS.get("TIMES", null));
                CLEAR = new EnumTitleAction(CLASS.get("CLEAR", null));
                RESET = new EnumTitleAction(CLASS.get("RESET", null));
            } else {
                CLASS = null;
                TITLE = null;
                SUBTITLE = null;
                TIMES = null;
                CLEAR = null;
                RESET = null;
            }
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException | IOException e) {
            DebugLogger.debug(e);
            throw new RuntimeException(e);
        }
    }

    private final Object instance;

    protected EnumTitleAction(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
