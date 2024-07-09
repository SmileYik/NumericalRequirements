package org.eu.smileyik.numericalrequirements.nms;

import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.nms.network.chat.ChatComponentText;
import org.eu.smileyik.numericalrequirements.nms.network.chat.ChatMessageType;
import org.eu.smileyik.numericalrequirements.nms.network.chat.IChatMutableComponent;
import org.eu.smileyik.numericalrequirements.nms.network.chat.LiteralContents;
import org.eu.smileyik.numericalrequirements.nms.network.packet.ClientboundSystemChatPacket;
import org.eu.smileyik.numericalrequirements.nms.network.packet.PacketPlayOutChat;

public interface ActionBar {

    static void send(Player p, String text) {
        if (NMS.MIDDLE_VERSION >= 20) {
            sendActionBar_1_20(p, text);
        } else if (NMS.MIDDLE_VERSION == 19) {
            sendActionBar_1_19(p, text);
        } else if (NMS.MIDDLE_VERSION >= 16) {
            sendActionBar_1_16(p, text);
        } else if (NMS.MIDDLE_VERSION >= 12) {
            sendActionBar_1_12(p, text);
        } else if (NMS.MIDDLE_VERSION >= 7) {
            sendActionBar_1_7(p, text);
        }
    }

    private static void sendActionBar_1_7(Player p, String text) {
        CraftPlayer craftPlayer = new CraftPlayer(p);
        craftPlayer.getHandle().playerConnection().sendPacket(
                PacketPlayOutChat.create(
                        ChatComponentText.create(text),
                        (byte) 2
                )
        );
    }

    private static void sendActionBar_1_12(Player p, String text) {
        CraftPlayer craftPlayer = new CraftPlayer(p);
        craftPlayer.getHandle().playerConnection().sendPacket(
                PacketPlayOutChat.create(
                        ChatComponentText.create(text),
                        ChatMessageType.GAME_INFO
                )
        );
    }

    private static void sendActionBar_1_16(Player p, String text) {
        CraftPlayer craftPlayer = new CraftPlayer(p);
        craftPlayer.getHandle().playerConnection().sendPacket(
                PacketPlayOutChat.create(
                        ChatComponentText.create(text),
                        ChatMessageType.GAME_INFO,
                        p.getUniqueId()
                )
        );
    }

    private static void sendActionBar_1_19(Player p, String text) {
        CraftPlayer craftPlayer = new CraftPlayer(p);
        craftPlayer.getHandle().playerConnection().sendPacket(
                ClientboundSystemChatPacket.create(
                        IChatMutableComponent.create(LiteralContents.fromString(text)),
                        true
                )
        );
    }

    private static void sendActionBar_1_20(Player p, String text) {
        CraftPlayer craftPlayer = new CraftPlayer(p);
        craftPlayer.getHandle().playerConnection().sendCommonPacket(
                ClientboundSystemChatPacket.create(
                        IChatMutableComponent.create(LiteralContents.fromString(text)),
                        true
                )
        );
    }
}
