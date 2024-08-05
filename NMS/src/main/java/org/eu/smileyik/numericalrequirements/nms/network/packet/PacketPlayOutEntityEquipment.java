package org.eu.smileyik.numericalrequirements.nms.network.packet;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;
import java.util.List;

public class PacketPlayOutEntityEquipment implements Packet, ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/packet/PacketPlayOutEntityEquipment.txt";
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

    public PacketPlayOutEntityEquipment(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public static PacketPlayOutEntityEquipment newInstance(int entityId, List<?> equipmentList) {
        if (CLASS == null) return null;
        return new PacketPlayOutEntityEquipment(
                CLASS.newInstance("newInstance", entityId, equipmentList)
        );
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
