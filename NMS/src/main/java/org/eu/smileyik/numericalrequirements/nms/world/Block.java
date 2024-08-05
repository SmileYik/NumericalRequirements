package org.eu.smileyik.numericalrequirements.nms.world;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.nms.core.IRegistry;
import org.eu.smileyik.numericalrequirements.nms.resources.MinecraftKey;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class Block implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/world/Block.txt";
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

    public Block(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    public Object getBlockData() {
        return CLASS.execute("getBlockData", instance);
    }

    public static Object getBlock(String blockName) {
        return IRegistry.getBlocks().get(MinecraftKey.createKey(blockName));
    }

    public static Block getBlockWrapper(String blockName) {
        return new Block(getBlock(blockName));
    }
}
