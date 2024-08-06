package org.eu.smileyik.numericalrequirements.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.Msg;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectPlayer;
import org.eu.smileyik.numericalrequirements.core.api.element.ElementFormatter;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.api.util.Pair;
import org.eu.smileyik.numericalrequirements.core.command.annotation.Command;
import org.eu.smileyik.numericalrequirements.core.command.annotation.CommandI18N;
import org.eu.smileyik.numericalrequirements.core.effect.impl.EffectBundle;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@CommandI18N("command")
@Command(
        value = "NumericalRequirements",
        colorCode = "color",
        aliases = {"nr", "nreq"}
)
public class RootCommand {

    /**
     * PlayerName: Status; Timestamp
     */
    private final Map<String, Pair<String, Long>> playerStatusCache = new HashMap<>();
    private String statusTemplate = null;
    private long cacheTime = 10000;

    @CommandI18N("command.NumericalRequirements")
    @Command(
            value = "reload",
            colorCode = "color",
            permission = "NumericalRequirements.Admin"
    )
    public void reload(CommandSender sender, String[] strs) {
        NumericalRequirements.getPlugin().onDisable();
        NumericalRequirements.getPlugin().onEnable();
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
        Player player = NumericalRequirements.getPlugin().getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(I18N.trp("command", "command.NumericalRequirements.error.cant-found-player", args[0]));
            return;
        }
        NumericalPlayer p = NumericalRequirements.getInstance().getPlayerService().getNumericalPlayer(player);
        if (p == null) {
            sender.sendMessage(I18N.trp("command", "command.NumericalRequirements.error.cant-find-status"));
            return;
        }
        Msg.msg(sender, getStatus(p, true));
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
        Msg.msg(p, getStatus(p, false));
    }



    private String getStatus(NumericalPlayer p, boolean ignoreCache) {
        ignoreCache = ignoreCache || p.getPlayer().isOp();
        if (!ignoreCache) {
            Pair<String, Long> stringLongPair = playerStatusCache.get(p.getPlayer().getName());
            if (stringLongPair != null && stringLongPair.getSecond() > System.currentTimeMillis()) {
                return String.format("%s\n%s", stringLongPair.getFirst(), I18N.tr("status.this-is-cache"));
            }
        }
        String collect = EffectPlayer.getEffectBundleData(p)
                .stream()
                .map(it -> ((EffectBundle.EffectBundleData) it).getBundleId())
                .collect(Collectors.joining(I18N.tr("status.delimiter")));
        if (collect.isEmpty()) {
            collect = I18N.tr("status.no-effect");
        }
        if (statusTemplate == null) {
            String status = String.join("\n",
                    NumericalRequirements.getPlugin().getConfig().getStringList("status.msg"));
            statusTemplate = ChatColor.translateAlternateColorCodes('&', status);
            cacheTime = NumericalRequirements.getPlugin().getConfig().getLong("status.cache", 10000);
        }

        String status = MessageFormat.format(ElementFormatter.replacePlaceholder(p, statusTemplate), p.getPlayer().getName(), collect);
        if (!ignoreCache) {
            playerStatusCache.put(p.getPlayer().getName(),
                    Pair.newPair(status, System.currentTimeMillis() + cacheTime));
        }
        return status;
    }
}
