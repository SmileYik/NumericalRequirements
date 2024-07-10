package org.eu.smileyik.numericalrequirements.core.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.element.formatter.ElementFormatter;
import org.eu.smileyik.numericalrequirements.nms.NMSMessage;
import org.eu.smileyik.numericalrequirements.nms.network.chat.EnumTitleAction;

public interface Msg {
    static void msg(CommandSender sender, String key, Object ... objs) {
        sender.sendMessage(I18N.tr(key, objs));
    }

    static void msg(Player sender, String key, Object ... objs) {
        sender.sendMessage(ElementFormatter.replacePlaceholder(sender, I18N.tr(key, objs)));
    }

    static void actionBar(Player sender, String key, Object ... objs) {
        NMSMessage.sendActionBar(sender, ElementFormatter.replacePlaceholder(sender, I18N.tr(key, objs)));
    }

    static void mainTitle(Player sender, int fadeIn, int stay, int fadeOut, String key, Object ... objs) {
        NMSMessage.sendTitle(sender, ElementFormatter.replacePlaceholder(sender, I18N.tr(key, objs)), null, fadeIn, stay, fadeOut);
    }

    static void subTitle(Player sender, int fadeIn, int stay, int fadeOut, String key, Object ... objs) {
        NMSMessage.sendTitle(sender, (String) null, ElementFormatter.replacePlaceholder(sender, I18N.tr(key, objs)), fadeIn, stay, fadeOut);
    }

    static void title(Player sender, int fadeIn, int stay, int fadeOut, String key1, Object[] args1, String key2, Object[] args2) {
        NMSMessage.sendTitle(sender,
                ElementFormatter.replacePlaceholder(sender, I18N.tr(key1, args1)),
                ElementFormatter.replacePlaceholder(sender, I18N.tr(key2, args2)), fadeIn, stay, fadeOut);
    }

    static void resetTitle(Player sender) {
        NMSMessage.sendTitle(sender, EnumTitleAction.RESET, "", 0, 0, 0);
    }
}
