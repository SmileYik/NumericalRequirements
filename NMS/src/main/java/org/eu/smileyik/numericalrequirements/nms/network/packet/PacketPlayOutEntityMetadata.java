package org.eu.smileyik.numericalrequirements.nms.network.packet;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.nms.entity.Entity;
import org.eu.smileyik.numericalrequirements.nms.network.DataWatcher;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class PacketPlayOutEntityMetadata implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/packet/PacketPlayOutEntityMetadata.txt";
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

    public PacketPlayOutEntityMetadata(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public static PacketPlayOutEntityMetadata newInstance(Entity entity, boolean flag) {
        if (CLASS == null) return null;
        return newInstance(entity.getId(), entity.getDataWatcher(), flag);
    }

    public static PacketPlayOutEntityMetadata newInstance(int entityId, DataWatcher dataWatcher, boolean flag) {
        return newInstance(entityId, dataWatcher == null ? null : dataWatcher.getInstance(), flag);
    }

    public static PacketPlayOutEntityMetadata newInstance(int entityId, Object dataWatcher, boolean flag) {
        if (CLASS == null) return null;
        return new PacketPlayOutEntityMetadata(
                CLASS.newInstance("newInstance", entityId, dataWatcher, flag)
        );
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
