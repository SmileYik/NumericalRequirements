package org.eu.smileyik.numericalrequirements.nms.network.packet;

import org.bukkit.World;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.nms.core.BlockPosition;
import org.eu.smileyik.numericalrequirements.nms.craftbukkit.CraftWorld;
import org.eu.smileyik.numericalrequirements.nms.world.Block;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class PacketPlayOutBlockChange implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/packet/PacketPlayOutBlockChange.txt";
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

    public PacketPlayOutBlockChange(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public static Object createByWorld(Object nmsWorld, Object nmsBlockPosition) {
        if (CLASS == null) return null;
        return CLASS.newInstance("newByWorld", nmsWorld, nmsBlockPosition);
    }

    public static Object createByWorld(World world, BlockPosition blockPosition) {
        return createByWorld(CraftWorld.getHandle(world), blockPosition.getInstance());
    }

    public static Object createByBlockData(Object nmsBlockPosition, Object nmsBlockData) {
        if (CLASS == null) return null;
        return CLASS.newInstance("newByBlockData", nmsBlockPosition, nmsBlockData);
    }

    public static Object createByBlock(BlockPosition blockPosition, Block block) {
        if (CLASS == null) return null;
        return createByBlockData(blockPosition.getInstance(), block.getBlockData());
    }

    public static PacketPlayOutBlockChange newInstance(World world, BlockPosition blockPosition) {
        Object byWorld = createByWorld(world, blockPosition);
        return byWorld == null ? null : new PacketPlayOutBlockChange(byWorld);
    }

    public static PacketPlayOutBlockChange newInstance(BlockPosition blockPosition, Block block) {
        Object byBlock = createByBlock(blockPosition, block);
        return byBlock == null ? null : new PacketPlayOutBlockChange(byBlock);
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
