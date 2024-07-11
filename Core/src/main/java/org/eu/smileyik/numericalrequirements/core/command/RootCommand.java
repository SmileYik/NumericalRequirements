package org.eu.smileyik.numericalrequirements.core.command;

import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.Msg;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectPlayer;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementFormatter;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.command.annotation.Command;
import org.eu.smileyik.numericalrequirements.core.command.annotation.CommandI18N;
import org.eu.smileyik.numericalrequirements.core.effect.impl.EffectBundle;

import java.text.MessageFormat;
import java.util.stream.Collectors;

@CommandI18N("command")
@Command(
        value = "NumericalRequirements",
        colorCode = "color",
        aliases = {"nr", "nreq"}
)
public class RootCommand {
    @CommandI18N("command.NumericalRequirements")
    @Command(
            value = "reload",
            colorCode = "color",
            permission = "NumericalRequirements.Admin"
    )
    public void reload(CommandSender sender, String[] strs) {
        NumericalRequirements plugin = NumericalRequirements.getInstance();

        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.spawnParticle(Particle.HEART, p.getEyeLocation(), 1);
            p.playEffect(EntityEffect.HURT);
        }

        plugin.onDisable();
        plugin.onEnable();
    }

    @CommandI18N("command.NumericalRequirements")
    @Command(
            value = "status",
            args = {"player"},
            colorCode = "color",
            permission = "NumericalRequirements.Admin",
            needPlayer = false
    )
    public void statusConsole(CommandSender sender, String[] args) {
        Player player = NumericalRequirements.getInstance().getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(I18N.trp("command", "command.NumericalRequirements.error.cant-found-player", args[0]));
            return;
        }
        NumericalPlayer p = NumericalRequirements.getInstance().getPlayerService().getNumericalPlayer(player);
        if (p == null) {
            sender.sendMessage(I18N.trp("command", "command.NumericalRequirements.error.cant-find-status"));
            return;
        }
        Msg.msg(sender, getStatus(p));
    }

    @CommandI18N("command.NumericalRequirements")
    @Command(
            value = "status",
            colorCode = "color",
            needPlayer = true
    )
    public void status(Player sender, String[] args) {
        NumericalPlayer p = NumericalRequirements.getInstance().getPlayerService().getNumericalPlayer(sender);
        if (p == null) {
            sender.sendMessage(I18N.trp("command", "command.NumericalRequirements.error.cant-find-status"));
            return;
        }
        Msg.msg(p, getStatus(p));
    }

    private String getStatus(NumericalPlayer p) {
        String collect = EffectPlayer.getEffectBundleData(p)
                .stream()
                .map(it -> ((EffectBundle.EffectBundleData) it).getBundleId())
                .collect(Collectors.joining(I18N.tr("status.delimiter")));
        if (collect.isEmpty()) {
            collect = I18N.tr("status.no-effect");
        }
        String status = String.join("\n",
                NumericalRequirements.getInstance().getConfig().getStringList("status"));
        status = ChatColor.translateAlternateColorCodes('&', status);
        return MessageFormat.format(ElementFormatter.replacePlaceholder(p, status), p.getPlayer().getName(), collect);
    }
}
