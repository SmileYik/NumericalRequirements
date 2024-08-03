package org.eu.smileyik.numericalrequirements.nms.entity;

import org.bukkit.World;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.nms.craftbukkit.CraftWorld;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class EntityArmorStand extends Entity implements NMSEntity, ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/entity/EntityArmorStand.txt";
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

    public EntityArmorStand(Object instance) {
        super(instance);
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
    }

    /**
     * 创建一个盔甲架实例.
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public EntityArmorStand(World world, double x, double y, double z) {
        this(CraftWorld.getHandle(world), x, y, z);
    }

    /**
     * 创建一个盔甲架实例.
     * @param nmsWorld
     * @param x
     * @param y
     * @param z
     */
    public EntityArmorStand(Object nmsWorld, double x, double y, double z) {
        super(CLASS.newInstance("newInstanceWithLocation", nmsWorld, x, y, z));
    }
}
