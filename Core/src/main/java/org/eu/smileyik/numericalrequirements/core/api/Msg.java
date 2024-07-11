package org.eu.smileyik.numericalrequirements.core.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementFormatter;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.nms.NMSMessage;
import org.eu.smileyik.numericalrequirements.nms.network.chat.EnumTitleAction;

public interface Msg {
    static void msg(CommandSender sender, String text) {
        sender.sendMessage(text);
    }

    static void msg(NumericalPlayer sender, String text) {
        sender.getPlayer().sendMessage(ElementFormatter.replacePlaceholder(sender, text));
    }

    static void msg(Player sender, String text) {
        sender.sendMessage(ElementFormatter.replacePlaceholder(sender, text));
    }

    static void actionBar(NumericalPlayer sender, String text) {
        NMSMessage.sendActionBar(sender.getPlayer(), ElementFormatter.replacePlaceholder(sender, text));
    }

    static void actionBar(Player sender, String text) {
        NMSMessage.sendActionBar(sender, ElementFormatter.replacePlaceholder(sender, text));
    }

    static void mainTitle(Player sender, String mainTitle, int fadeIn, int stay, int fadeOut) {
        NMSMessage.sendTitle(sender, ElementFormatter.replacePlaceholder(sender, mainTitle), null, fadeIn, stay, fadeOut);
    }

    static void mainTitle(NumericalPlayer sender, String mainTitle, int fadeIn, int stay, int fadeOut) {
        NMSMessage.sendTitle(sender.getPlayer(), ElementFormatter.replacePlaceholder(sender, mainTitle), null, fadeIn, stay, fadeOut);
    }

    static void subTitle(Player sender, String subTitle, int fadeIn, int stay, int fadeOut) {
        NMSMessage.sendTitle(sender, (String) null, ElementFormatter.replacePlaceholder(sender, subTitle), fadeIn, stay, fadeOut);
    }

    static void subTitle(NumericalPlayer sender, String subTitle, int fadeIn, int stay, int fadeOut) {
        NMSMessage.sendTitle(sender.getPlayer(), (String) null, ElementFormatter.replacePlaceholder(sender, subTitle), fadeIn, stay, fadeOut);
    }

    static void title(Player sender, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        NMSMessage.sendTitle(sender,
                ElementFormatter.replacePlaceholder(sender, title),
                ElementFormatter.replacePlaceholder(sender, subTitle), fadeIn, stay, fadeOut);
    }

    static void title(NumericalPlayer sender, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        NMSMessage.sendTitle(sender.getPlayer(),
                ElementFormatter.replacePlaceholder(sender, title),
                ElementFormatter.replacePlaceholder(sender, subTitle), fadeIn, stay, fadeOut);
    }

    static void trMsg(CommandSender sender, String key, Object ... objs) {
        sender.sendMessage(I18N.tr(key, objs));
    }

    static void trMsg(NumericalPlayer sender, String key, Object ... objs) {
        sender.getPlayer().sendMessage(ElementFormatter.replacePlaceholder(sender, I18N.tr(key, objs)));
    }

    static void trMsg(Player sender, String key, Object ... objs) {
        sender.sendMessage(ElementFormatter.replacePlaceholder(sender, I18N.tr(key, objs)));
    }

    static void trActionBar(NumericalPlayer sender, String key, Object ... objs) {
        NMSMessage.sendActionBar(sender.getPlayer(), ElementFormatter.replacePlaceholder(sender, I18N.tr(key, objs)));
    }

    static void trActionBar(Player sender, String key, Object ... objs) {
        NMSMessage.sendActionBar(sender, ElementFormatter.replacePlaceholder(sender, I18N.tr(key, objs)));
    }

    static void trMainTitle(Player sender, int fadeIn, int stay, int fadeOut, String key, Object ... objs) {
        NMSMessage.sendTitle(sender, ElementFormatter.replacePlaceholder(sender, I18N.tr(key, objs)), null, fadeIn, stay, fadeOut);
    }

    static void trMainTitle(NumericalPlayer sender, int fadeIn, int stay, int fadeOut, String key, Object ... objs) {
        NMSMessage.sendTitle(sender.getPlayer(), ElementFormatter.replacePlaceholder(sender, I18N.tr(key, objs)), null, fadeIn, stay, fadeOut);
    }

    static void trSubTitle(Player sender, int fadeIn, int stay, int fadeOut, String key, Object ... objs) {
        NMSMessage.sendTitle(sender, (String) null, ElementFormatter.replacePlaceholder(sender, I18N.tr(key, objs)), fadeIn, stay, fadeOut);
    }

    static void trSubTitle(NumericalPlayer sender, int fadeIn, int stay, int fadeOut, String key, Object ... objs) {
        NMSMessage.sendTitle(sender.getPlayer(), (String) null, ElementFormatter.replacePlaceholder(sender, I18N.tr(key, objs)), fadeIn, stay, fadeOut);
    }

    static void trTitle(Player sender, int fadeIn, int stay, int fadeOut, String key1, Object[] args1, String key2, Object[] args2) {
        NMSMessage.sendTitle(sender,
                ElementFormatter.replacePlaceholder(sender, I18N.tr(key1, args1)),
                ElementFormatter.replacePlaceholder(sender, I18N.tr(key2, args2)), fadeIn, stay, fadeOut);
    }

    static void trTitle(NumericalPlayer sender, int fadeIn, int stay, int fadeOut, String key1, Object[] args1, String key2, Object[] args2) {
        NMSMessage.sendTitle(sender.getPlayer(),
                ElementFormatter.replacePlaceholder(sender, I18N.tr(key1, args1)),
                ElementFormatter.replacePlaceholder(sender, I18N.tr(key2, args2)), fadeIn, stay, fadeOut);
    }

    static void resetTitle(Player sender) {
        NMSMessage.sendTitle(sender, EnumTitleAction.RESET, "", 0, 0, 0);
    }
}
