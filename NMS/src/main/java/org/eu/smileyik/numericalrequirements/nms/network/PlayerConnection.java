package org.eu.smileyik.numericalrequirements.nms.network;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.nms.network.packet.Packet;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class PlayerConnection extends ServerCommonPacketListenerImpl implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/PlayerConnection.txt";
    private static final ReflectClass CLASS;

    static {
        try {
            String currentVersion = VersionScript.runScriptByResource(SCRIPT_PATH);
            if (currentVersion != null) {
                CLASS = MySimpleReflect.readByResource(
                        currentVersion, true,
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

    public PlayerConnection(Object instance) {
        super(instance);
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
    }

    public void sendPacket(Packet packet) {
        if (packet == null) return;
        CLASS.execute("sendPacket", instance, packet.getInstance());
    }

    public void sendPacket(Object packet) {
        if (packet == null) return;
        CLASS.execute("sendPacket", instance, packet);
    }

    public NetworkManager getNetworkManager() {
        if (CLASS.hasMethod("getNetworkManager")) {
            return new NetworkManager(CLASS.execute("getNetworkManager", instance));
        } else if (CLASS.hasField("networkManager")) {
            return new NetworkManager(CLASS.get("networkManager", instance));
        } else {
            return super.getNetworkManager();
        }
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
