package org.eu.smileyik.numericalrequirements.nms.network.packet;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.nms.entity.EntityArmorStand;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class PacketPlayOutSpawnEntityLiving implements Packet, ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/packet/PacketPlayOutSpawnEntityLiving.txt";
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

    public PacketPlayOutSpawnEntityLiving(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public static PacketPlayOutSpawnEntityLiving newInstance(EntityArmorStand entityArmorStand) {
        if (CLASS == null) return null;
        return new PacketPlayOutSpawnEntityLiving(
                CLASS.newInstance("newInstance", entityArmorStand.getInstance())
        );
    }

    public static PacketPlayOutSpawnEntityLiving newInstance(Object nmsLivingEntity) {
        if (CLASS == null) return null;
        return new PacketPlayOutSpawnEntityLiving(
                CLASS.newInstance("newInstance", nmsLivingEntity)
        );
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}