package org.eu.smileyik.numericalrequirements.nms.entity;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.nms.network.DataWatcher;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;
import java.util.UUID;

public class Entity implements NMSEntity, ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/entity/Entity.txt";
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

    protected final Object instance;

    public Entity(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    public int getId() {
        return (int) CLASS.execute("getId", instance);
    }

    public UUID getUniqueID() {
        return (UUID) CLASS.execute("getUniqueID", instance);
    }

    public DataWatcher getDataWatcher() {
        return new DataWatcher(CLASS.execute("getDataWatcher", instance));
    }

    public void setInvisible(boolean invisible) {
        CLASS.execute("setInvisible", instance, invisible);
    }

    public org.bukkit.entity.Entity getBukkitEntity() {
        return (org.bukkit.entity.Entity) CLASS.execute("getBukkitEntity", instance);
    }
}
