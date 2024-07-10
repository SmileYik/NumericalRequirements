package org.eu.smileyik.numericalrequirements.nms;

import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.nms.network.chat.*;
import org.eu.smileyik.numericalrequirements.nms.network.packet.ClientboundSystemChatPacket;
import org.eu.smileyik.numericalrequirements.nms.network.packet.PacketPlayOutChat;
import org.eu.smileyik.numericalrequirements.nms.network.packet.PacketPlayOutTitle;

public interface NMSMessage {
    static void sendActionBar(Player p, String text) {
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

    static void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (NMS.MIDDLE_VERSION >= 17) {
            p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            return;
        }

        sendTitle(p, EnumTitleAction.TITLE, title, fadeIn, stay, fadeOut);
        sendTitle(p, EnumTitleAction.SUBTITLE, subtitle, fadeIn, stay, fadeOut);
    }

    static void sendTitle(Player p, EnumTitleAction action, String text, int fadeIn, int stay, int fadeOut) {
        if (NMS.MIDDLE_VERSION >= 17) {
            sendTitle_1_17(p, action, text, fadeIn, stay, fadeOut);
        } else if (NMS.MIDDLE_VERSION >= 8) {
            sendTitle_1_8(p, action, text, fadeIn, stay, fadeOut);
        }
    }

    private static void sendTitle_1_8(Player p, EnumTitleAction action, String text, int fadeIn, int stay, int fadeOut) {
        new CraftPlayer(p).getHandle().playerConnection().sendPacket(
                PacketPlayOutTitle.newTitle(
                        action, ChatComponentText.create(text), fadeIn, stay, fadeOut
                )
        );
    }

    private static void sendTitle_1_17(Player p, EnumTitleAction action, String text, int fadeIn, int stay, int fadeOut) {
        if (action == EnumTitleAction.RESET) {
            p.resetTitle();
            return;
        }

        String first = text;
        String second = text;
        if (action == EnumTitleAction.SUBTITLE) {
            first = null;
        } else if (action == EnumTitleAction.TITLE) {
            second = null;
        }
        p.sendTitle(first, second, fadeIn, stay, fadeOut);
    }
}
