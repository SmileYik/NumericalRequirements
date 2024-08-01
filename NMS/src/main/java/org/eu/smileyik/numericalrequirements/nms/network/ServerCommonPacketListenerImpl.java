package org.eu.smileyik.numericalrequirements.nms.network;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.nms.ReflectClassBase;
import org.eu.smileyik.numericalrequirements.nms.network.packet.Packet;
import org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect;
import org.eu.smileyik.numericalrequirements.reflect.ReflectClass;
import org.eu.smileyik.numericalrequirements.versionscript.VersionScript;

import java.io.IOException;

public class ServerCommonPacketListenerImpl implements ReflectClassBase {
    private static final String SCRIPT_PATH = "/version-script/ServerCommonPacketListenerImpl.txt";
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

    protected final Object instance;

    public ServerCommonPacketListenerImpl(Object instance) {
        if (CLASS == null) {
            throw new IllegalStateException("Class not initialized");
        }
        this.instance = instance;
    }

    public void sendCommonPacket(Packet packet) {
        CLASS.execute("sendCommonPacket", instance, packet.getInstance());
    }

    public NetworkManager getNetworkManager() {
        if (CLASS.hasField("networkManager")) {
            return new NetworkManager(CLASS.get("networkManager", instance));
        } else {
            return null;
        }
    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
