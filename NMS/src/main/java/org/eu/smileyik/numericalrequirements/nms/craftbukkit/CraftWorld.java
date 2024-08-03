package org.eu.smileyik.numericalrequirements.nms.craftbukkit;

import org.bukkit.World;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class CraftWorld implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/craftbukkit/CraftWorld.txt";
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

    public CraftWorld(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    /**
     * 获取NMS的World
     * @param world
     * @return
     */
    public static Object getHandle(World world) {
        return CLASS.execute("getHandle", world);
    }
}
