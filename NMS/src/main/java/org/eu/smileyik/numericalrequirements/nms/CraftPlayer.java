package org.eu.smileyik.numericalrequirements.nms;

import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.entity.EntityPlayer;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class CraftPlayer implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/CraftPlayer.txt";
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

    private final Player instance;

    public CraftPlayer(Player instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public Player getPlayer() {
        return instance;
    }

    public EntityPlayer getHandle() {
        return new EntityPlayer(CLASS.execute("getHandle", instance));
    }

    public static EntityPlayer getHandle(Player player) {
        return new EntityPlayer(CLASS.execute("getHandle", player));
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
