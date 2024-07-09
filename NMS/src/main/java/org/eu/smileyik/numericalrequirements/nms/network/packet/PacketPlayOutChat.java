package org.eu.smileyik.numericalrequirements.nms.network.packet;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.nms.network.chat.ChatComponentText;
import org.eu.smileyik.numericalrequirements.nms.network.chat.ChatMessageType;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;
import java.util.UUID;

public class PacketPlayOutChat implements ReflectClassBase, Packet {
    private static final String SCRIPT_PATH = "/version-script/PacketPlayOutChat.txt";
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

    protected PacketPlayOutChat(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public static PacketPlayOutChat create(ChatComponentText msg, ChatMessageType type, UUID uuid) {
        return new PacketPlayOutChat(
                CLASS.newInstance("new-type-uuid", msg.getInstance(), type.getInstance(), uuid)
        );
    }

    public static PacketPlayOutChat create(ChatComponentText msg, ChatMessageType type) {
        return new PacketPlayOutChat(
                CLASS.newInstance("new-type", msg.getInstance(), type.getInstance())
        );
    }

    public static PacketPlayOutChat create(ChatComponentText msg, byte b) {
        if (!CLASS.hasConstructor("new-byte")) {
            return create(msg);
        }
        return new PacketPlayOutChat(
                CLASS.newInstance("new-byte", msg.getInstance(), b)
        );
    }

    public static PacketPlayOutChat create(ChatComponentText msg) {
        if (!CLASS.hasConstructor("c-1")) {
            return null;
        }
        return new PacketPlayOutChat(
                CLASS.newInstance("new", msg.getInstance())
        );
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
