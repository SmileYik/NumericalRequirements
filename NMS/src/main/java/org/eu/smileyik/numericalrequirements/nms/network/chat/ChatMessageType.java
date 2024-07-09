package org.eu.smileyik.numericalrequirements.nms.network.chat;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class ChatMessageType implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/ChatMessageType.txt";
    private static final ReflectClass CLASS;

    public static final ChatMessageType CHAT, SYSTEM, GAME_INFO;

    static {
        try {
            String currentVersion = VersionScript.runScriptByResource(SCRIPT_PATH);
            if (currentVersion != null) {
                CLASS = MySimpleReflect.readByResource(
                        currentVersion, false,
                        "${version}", VersionScript.VERSION
                );
                CHAT = new ChatMessageType(CLASS.get("CHAT", null));
                SYSTEM = new ChatMessageType(CLASS.get("SYSTEM", null));
                GAME_INFO = new ChatMessageType(CLASS.get("GAME_INFO", null));
                DebugLogger.debug("chat: %s; system: %s; game info: %s", CHAT, SYSTEM, GAME_INFO);
                DebugLogger.debug(Arrays.toString((Object[]) CLASS.execute("values", null)));
            } else {
                CLASS = null;
                CHAT = null;
                SYSTEM = null;
                GAME_INFO = null;
            }
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException | IOException e) {
            DebugLogger.debug(e);
            throw new RuntimeException(e);
        }
    }

    private final Object instance;

    protected ChatMessageType(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
        DebugLogger.debug("new ChatMessageType: %s", instance);
    }

    public static ChatMessageType byByte(byte b) {
        return new ChatMessageType(CLASS.execute("byByte", null, b));
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return Objects.toString(instance);
    }
}
