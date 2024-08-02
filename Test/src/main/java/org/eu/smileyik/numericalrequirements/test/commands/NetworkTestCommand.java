package org.eu.smileyik.numericalrequirements.test.commands;

import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.network.PacketListener;
import org.eu.smileyik.numericalrequirements.core.network.PacketToString;
import org.eu.smileyik.numericalrequirements.core.network.PlayerChannelHandler;
import org.eu.smileyik.numericalrequirements.test.TestCommand;

import java.util.HashMap;
import java.util.Map;

@TestCommand("network")
public class NetworkTestCommand {
    private final Map<String, PacketListener> map = new HashMap<>();

    /**
     * 打印指定包
     * @param args
     */
    public void print(String[] args) {
        final String packetName = args[0];
        boolean result = args.length > 1 && "true".equalsIgnoreCase(args[1]);
        System.out.println("print packet " + packetName + " cancel: " + result);
        map.putIfAbsent(packetName, new PacketListener() {
            @Override
            public String getPacketName() {
                return packetName;
            }

            @Override
            public Object handlePacketIn(PlayerChannelHandler handler, Object packet) {
                System.out.printf("%s: %s\n", handler.getPlayer().getName(), PacketToString.toString(packet));
                return result ? null : PacketListener.super.handlePacketIn(handler, packet);
            }

            @Override
            public Object handlePacketOut(PlayerChannelHandler handler, Object packet) {
                System.out.printf("%s: %s\n", handler.getPlayer().getName(), PacketToString.toString(packet));
                return result ? null : PacketListener.super.handlePacketOut(handler, packet);
            }
        });
        NumericalRequirements.getInstance().getNetworkService().addPacketListener(map.get(packetName));
    }

    /**
     * 取消打印指定包
     * @param args
     */
    public void cancel(String[] args) {
        PacketListener remove = map.remove(args[0]);
        if (remove != null)
            NumericalRequirements.getInstance().getNetworkService().removePacketListener(remove);
    }
}
