package org.eu.smileyik.numericalrequirements.core.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.effect.Effect;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectData;
import org.eu.smileyik.numericalrequirements.core.api.effect.EffectPlayer;
import org.eu.smileyik.numericalrequirements.core.api.player.NumericalPlayer;
import org.eu.smileyik.numericalrequirements.core.command.annotation.Command;
import org.eu.smileyik.numericalrequirements.core.command.annotation.CommandI18N;

import java.util.Arrays;

@CommandI18N("command")
@Command(
        value = "effect",
        colorCode = "color",
        parentCommand = "NumericalRequirements",
        permission = "NumericalRequirements.Admin"
)
public class EffectCommand {

    @CommandI18N("command.effect")
    @Command(
            value = "add",
            colorCode = "color",
            args = {"player", "effect", "values"},
            isUnlimitedArgs = true,
            needPlayer = false
    )
    public void addEffectToPlayer(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(I18N.trp("command", "command.effect.error.no-target-player"));
            return;
        } else if (args.length == 1) {
            sender.sendMessage(I18N.trp("command", "command.effect.error.no-target-effect"));
            return;
        }
        Player player = NumericalRequirements.getPlugin().getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(I18N.trp("command", "command.effect.error.player-not-online", args[0]));
            return;
        }
        NumericalPlayer numericalPlayer = NumericalRequirements.getInstance().getPlayerService().getNumericalPlayer(player);
        if (numericalPlayer == null) {
            sender.sendMessage(I18N.trp("command", "command.effect.error.player-not-load", args[0]));
            return;
        }

        Effect effect = NumericalRequirements.getInstance().getEffectService().findEffectById(args[1]);
        if (effect == null) {
            sender.sendMessage(I18N.trp("command", "command.effect.error.not-found-effect", args[1]));
            return;
        }
        boolean valid = false;
        EffectData effectData = null;
        try {
            effectData = effect.newEffectData(Arrays.copyOfRange(args, 2, args.length));
            valid = effectData != null;
        } catch (Exception ignored) {

        }
        if (!valid) {
            sender.sendMessage(I18N.trp("command", "command.effect.error.not-valid-effect-data"));
            return;
        }
        EffectPlayer.registerEffect(numericalPlayer, effect, effectData);
    }
}
