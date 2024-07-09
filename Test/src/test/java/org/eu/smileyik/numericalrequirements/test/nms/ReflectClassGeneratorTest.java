package org.eu.smileyik.numericalrequirements.test.nms;

import org.eu.smileyik.numericalrequirements.nms.ReflectPathGenerator;
import org.eu.smileyik.numericalrequirements.test.NeedTest;

@NeedTest
public class ReflectClassGeneratorTest {
    @NeedTest
    public void reflectClassGenerator() throws Exception {
        ReflectPathGenerator.main(new String[] {
                "ChatComponentText", "net.minecraft.network.chat.ChatComponentText",
                "ChatMessageType", "net.minecraft.network.chat.ChatMessageType",
                "PacketPlayOutChat", "net.minecraft.network.protocol.game.PacketPlayOutChat",
                "PlayerConnection", "net.minecraft.server.network.PlayerConnection",
                "ClientboundSystemChatPacket", "net.minecraft.network.protocol.game.ClientboundSystemChatPacket"
        });
    }
}
