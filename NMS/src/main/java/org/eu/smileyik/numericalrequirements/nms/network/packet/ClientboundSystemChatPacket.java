package org.eu.smileyik.numericalrequirements.nms.network.packet;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.nms.network.chat.IChatMutableComponent;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class ClientboundSystemChatPacket implements ReflectClassBase, Packet {
    private static final String SCRIPT_PATH = "/version-script/ClientboundSystemChatPacket.txt";
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

    protected ClientboundSystemChatPacket(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public static ClientboundSystemChatPacket create(IChatMutableComponent contents, boolean overlay) {
        return new ClientboundSystemChatPacket(CLASS.newInstance("new", contents.getInstance(), overlay));
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
