package org.eu.smileyik.numericalrequirements.nms.network.packet;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.nms.network.chat.ChatComponentText;
import org.eu.smileyik.numericalrequirements.nms.network.chat.EnumTitleAction;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class PacketPlayOutTitle implements ReflectClassBase, Packet {
    private static final String SCRIPT_PATH = "/version-script/PacketPlayOutTitle.txt";
    private static final ReflectClass CLASS;

    static {
        ReflectClass clazz;
        try {
            String currentVersion = VersionScript.runScriptByResource(SCRIPT_PATH);
            if (currentVersion != null) {
                clazz = MySimpleReflect.readByResource(
                        currentVersion, false,
                        "${version}", VersionScript.VERSION
                );
            } else {
                clazz = null;
            }
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException | IOException e) {
            DebugLogger.debug(e);
            clazz = null;
        }
        CLASS = clazz;
    }

    private final Object instance;

    protected PacketPlayOutTitle(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public static PacketPlayOutTitle newTitle(EnumTitleAction action, ChatComponentText text, int fadIn, int stay, int fadeOut) {
        return new PacketPlayOutTitle(CLASS.newInstance(
                "new", action.getInstance(), text.getInstance(), fadIn, stay, fadeOut
        ));
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
